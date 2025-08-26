import warnings
warnings.filterwarnings("ignore", category=FutureWarning)  # Suppress FutureWarning
import cv2
import numpy as np
import time
import os
from picamera import PiCamera

# Create temp directory if it doesn't exist
TEMP_DIR = '/tmp/vehicle_detection'
os.makedirs(TEMP_DIR, exist_ok=True)
FRAME_PATH = os.path.join(TEMP_DIR, 'frame.jpg')

# Load the Tiny-YOLOv4 model
net = cv2.dnn.readNetFromDarknet('yolov4-tiny.cfg', 'yolov4-tiny.weights')

# use OpenCV's DNN with CPU optimization
net.setPreferableBackend(cv2.dnn.DNN_BACKEND_OPENCV)
net.setPreferableTarget(cv2.dnn.DNN_TARGET_CPU)

# Load class names
with open('coco.names', 'r') as f:
    classes = [line.strip() for line in f.readlines()]

# Initialize the PiCamera
camera = PiCamera()
camera.resolution = (640, 480)
camera.framerate = 10

# Allow the camera to warm up
time.sleep(0.1)

# ROI size
roi_width_percent = 0.3  # 30% of the frame width
roi_start_y_percent = 0.3  # Start from 30% down the frame

# Updated reference values based on measurements
REFERENCE_DISTANCE_1 = 12.0  # meters
REFERENCE_CAR_WIDTH_PIXELS_1 = 71.4  # Width of car at 12m
REFERENCE_CAR_HEIGHT_PIXELS_1 = 53.2  # Height of car at 12m

REFERENCE_DISTANCE_2 = 20.0  # meters
REFERENCE_CAR_WIDTH_PIXELS_2 = 36.2  # Width of car at 20m
REFERENCE_CAR_HEIGHT_PIXELS_2 = 27.1  # Height of car at 20m

REFERENCE_DISTANCE_3 = 30.0  # meters
REFERENCE_CAR_WIDTH_PIXELS_3 = 27.9  # Width of car at 30m
REFERENCE_CAR_HEIGHT_PIXELS_3 = 22.5  # Height of car at 30m

# Vehicle class IDs in COCO dataset
VEHICLE_CLASSES = ['car', 'truck', 'bus', 'motorcycle']

def estimate_distance(bbox_width, bbox_height):
    # Width-based estimates
    dist_w1 = REFERENCE_DISTANCE_1 * (REFERENCE_CAR_WIDTH_PIXELS_1 / bbox_width)
    dist_w2 = REFERENCE_DISTANCE_2 * (REFERENCE_CAR_WIDTH_PIXELS_2 / bbox_width)
    dist_w3 = REFERENCE_DISTANCE_3 * (REFERENCE_CAR_WIDTH_PIXELS_3 / bbox_width)
    
    # Height-based estimates
    dist_h1 = REFERENCE_DISTANCE_1 * (REFERENCE_CAR_HEIGHT_PIXELS_1 / bbox_height)
    dist_h2 = REFERENCE_DISTANCE_2 * (REFERENCE_CAR_HEIGHT_PIXELS_2 / bbox_height)
    dist_h3 = REFERENCE_DISTANCE_3 * (REFERENCE_CAR_HEIGHT_PIXELS_3 / bbox_height)
    
    # Calculate weighted average based on proximity to reference points
    # This gives more weight to the reference point closest to the current measurement
    distances = [dist_w1, dist_w2, dist_w3, dist_h1, dist_h2, dist_h3]
    
    # Simple average of all estimates
    estimated_distance = sum(distances) / len(distances)
    
    return estimated_distance

# Get the output layer names
layer_names = net.getLayerNames()
output_layers = [layer_names[i - 1] for i in net.getUnconnectedOutLayers().flatten()]

try:
    # Track FPS
    fps_start_time = time.time()
    fps_counter = 0
    fps = 0
    
    print("Starting detection. Press Ctrl+C to exit or 'q' to quit...")
    running = True
    
    while running:
        # Capture frame to file
        camera.capture(FRAME_PATH, format="jpeg", use_video_port=True)
        
        # Read the frame from file
        frame = cv2.imread(FRAME_PATH)
        
        if frame is None:
            print("Failed to read frame, retrying...")
            time.sleep(0.1)
            continue
        
        # Calculate FPS
        fps_counter += 1
        if (time.time() - fps_start_time) > 1.0:
            fps = fps_counter / (time.time() - fps_start_time)
            fps_counter = 0
            fps_start_time = time.time()
        
        # Get frame dimensions
        height, width, _ = frame.shape
        
        # Define the Region of Interest
        roi_width = int(width * roi_width_percent)
        roi_x = (width - roi_width) // 2  # Centered horizontally
        roi_y = int(height * roi_start_y_percent)  # Start from 30% down the frame
        roi_height = int(height - roi_y)  # Extend to bottom of frame
        
        # Draw the ROI on the frame (for visualization)
        cv2.rectangle(frame, (roi_x, roi_y), (roi_x + roi_width, roi_y + roi_height), (255, 0, 0), 2)
        
        # Crop the frame to the ROI (Ensure it stays within bounds)
        roi_x_end = min(roi_x + roi_width, width)
        roi_y_end = min(roi_y + roi_height, height)
        roi_frame = frame[roi_y:roi_y_end, roi_x:roi_x_end]
        
        # Create a blob from the ROI frame
        blob = cv2.dnn.blobFromImage(roi_frame, 1/255.0, (416, 416), swapRB=True, crop=False)
        
        # Set the input to the network
        net.setInput(blob)
        
        # Forward pass through the network
        start_time = time.time()
        outputs = net.forward(output_layers)
        inference_time = time.time() - start_time
        
        # Initialize lists to hold detection results
        boxes = []
        confidences = []
        class_ids = []
        
        # Process each detection
        for output in outputs:
            for detection in output:
                scores = detection[5:]
                class_id = np.argmax(scores)
                confidence = scores[class_id]
                
                if confidence > 0.5:  # Confidence threshold
                    # Scale the bounding box coordinates back to the ROI image size
                    roi_height, roi_width, _ = roi_frame.shape
                    center_x = int(detection[0] * roi_width)
                    center_y = int(detection[1] * roi_height)
                    w = int(detection[2] * roi_width)
                    h = int(detection[3] * roi_height)
                    
                    # Calculate top-left corner of the bounding box
                    x = int(center_x - w / 2)
                    y = int(center_y - h / 2)
                    
                    boxes.append([x, y, w, h])
                    confidences.append(float(confidence))
                    class_ids.append(class_id)
        
        # Apply non-maximum suppression to remove overlapping bounding boxes
        if len(boxes) > 0:
            indices = cv2.dnn.NMSBoxes(boxes, confidences, 0.5, 0.4)
            
            # Draw bounding boxes and labels
            for i in indices.flatten():
                x, y, w, h = boxes[i]
                label = classes[class_ids[i]]
                confidence = confidences[i]
                
                # Only process vehicles
                if label.lower() in VEHICLE_CLASSES:
                    # Adjust coordinates to the full frame
                    x = x + roi_x
                    y = y + roi_y
                    
                    # Estimate distance
                    distance = estimate_distance(w, h)
                    
                    # Draw bounding box
                    cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 255, 0), 2)
                    
                    # Display label with distance
                    label_text = f"{label}: {distance:.1f}m"
                    cv2.putText(frame, label_text, (x, y - 10), 
                               cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 255, 0), 2)
        
        # Display FPS and inference time
        cv2.putText(frame, f"FPS: {fps:.1f}", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
        cv2.putText(frame, f"Inference: {inference_time*1000:.1f}ms", (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
        
        # Display the frames
        cv2.imshow('Vehicle Detection & Distance', frame)
        cv2.imshow('ROI Window', roi_frame)
        
        # Exit on 'q' key press
        if cv2.waitKey(1) & 0xFF == ord('q'):
            running = False

except KeyboardInterrupt:
    print("Interrupted by user")

finally:
    # Release the camera and close windows
    camera.close()
    cv2.destroyAllWindows()
    
    # Clean up temporary file
    if os.path.exists(FRAME_PATH):
        try:
            os.remove(FRAME_PATH)
        except:
            pass