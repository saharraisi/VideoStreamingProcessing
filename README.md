# VideoStreamingProcessing
# Distributed Video Stream Processing with Apache Storm

## Overview

This project implements a distributed video processing topology using **Apache Storm** and **OpenCV**.The system is designed to ingest a video file, break it down into frames, and distribute the processing workload across multiple nodes (Bolts).
The topology splits the video stream into two parallel processing branches—one for **blurring** and one for **sharpening**—before aggregating the results into a single, combined output video. Additionally, the project includes an analysis module to calculate and compare the average brightness of the original versus the processed frames.


<img width="512" height="190" alt="Screenshot 2026-02-14 at 18 41 40" src="https://github.com/user-attachments/assets/f386f149-beba-4ca3-9a3e-940e310d4594" />



## Table of Contents

* [Overview](#overview)
* [Key Features](#key-features)
* [Components](#components)
* [Getting Started](#getting-started)
    * [Requirements](#requirements)
    * [Installation](#installation)

## Key Features

* **Distributed Stream Processing:** Uses Apache Storm's Spout/Bolt architecture to handle video frames efficiently in a distributed environment.
* **Preprocessing:** Automatically converts input video frames to grayscale and resizes them to 320x240 pixels for standardized processing.
* **Parallel Image Effects:**
    * **Blurring:** Applies standard box filtering to frames.
    * **Sharpening:** Applies Gaussian blur and weighted subtraction to enhance frame details.
* **Frame Synchronization & Fusion:** A specialized coordination bolt ensures that corresponding blurred and sharpened frames (matching Frame IDs) are synchronized and merged with equal weight (50/50).
* **Video Reconstruction:** Recompiles processed frames into a seamless MP4 video file.
* **Brightness Analysis:** Calculates the average brightness of frames before and after processing to generate statistical reports (`FRAME_AVG.txt` and `FINAL_AVG.txt`).

## Components

The project consists of the following core Java classes:

* **VideoSpout (Master Node):**
    * Reads the pre-processed/resized grayscale frames from the input directory.
    * Sorts frames numerically to prevent video jitter.
    * Emits tuples containing the frame data to the topology.
* **bolt1 (Blurring Worker):**
    * Receives tuples from the Spout.
    * Applies a Blur filter using OpenCV (`Imgproc.blur`).
    * Tags the output as "Blur" and emits the processed frame.
* **bolt2 (Sharpening Worker):**
    * Receives tuples from the Spout.
    * Applies a sharpening effect using `Imgproc.GaussianBlur` and `Core.addWeighted`.
    * Tags the output as "Sharp" and emits the processed frame.
* **CombinedBolt (Aggregator):**
    * Uses a `HashMap` to buffer incoming frames from both bolts.
    * Waits until both the "Blur" and "Sharp" versions of a specific Frame ID are received.
    * Merges the two images into a single frame and saves the result.
* **VideoFromFrames:**
    * Reads the final combined frames and reconstructs them into an `.mp4` video file using `VideoWriter`.
* **VideoBrightnessAnalyzer:**
    * A utility class that computes the pixel intensity (brightness) of the original frames vs. the final output frames for analysis.

## Getting Started

### Requirements

Please ensure the following tools and libraries are installed and configured on your system (Linux recommended, as used in development):

* **Java JDK:** (Project developed using OpenJDK, compatible with version 8+).
* **Apache Storm:** A distributed real-time computation system (e.g., version 2.x).
* **Apache Zookeeper:** Required for coordinating the Storm cluster (e.g., version 3.8.x).
* **OpenCV:** Required for all image processing tasks.
    * *Note:* Ensure you install `opencv_contrib` modules if necessary.
    * *Tip:* If building from source, ensure your CMake configuration matches your Java version to avoid `UnsatisfiedLinkError`.

### Installation

1.  **Install Dependencies:** Set up Java, Zookeeper, Storm, and OpenCV as per the **Requirements** section.
2.  **Clone the Repository:**
    ```bash
    git clone [https://github.com/your-username/your-repo-name.git](https://github.com/your-username/your-repo-name.git)
    ```
3.  **Prepare the Environment:**
    * Ensure your native OpenCV library path is accessible to the Java application.
    * Place your input video file in the `video/` directory (default input is `1.mp4`).
4.  **Run the Topology:**
    * Navigate to the `src` folder.
    * Compile and run the **`start`** class.This class acts as the main entry point; it orchestrates the frame extraction, topology submission, video reconstruction, and brightness analysis.
    ```bash
    javac -cp ".:/path/to/storm/*:/path/to/opencv/jar" VideoCap/start.java
    java -Djava.library.path="/path/to/opencv/lib" -cp ".:/path/to/storm/*:/path/to/opencv/jar" VideoCap.start
    ```
5.  **Check Output:**
    * **Video:** The final video will be generated as `output_video.mp4`.
    * **Stats:** Check `FRAME_AVG.txt` and `FINAL_AVG.txt` for brightness analysis.

Have fun with it! 😊
