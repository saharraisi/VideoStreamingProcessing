package VideoCap;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VideoBrightnessAnalyzer {

    private static final String VIDEO_PATH = "video/1.mp4";
    private static final String ORIGINAL_FRAMES_PATH = "original-frames";
    private static final String OUTPUT_COMBINED_PATH = "output_combined";
    private static final String FRAME_AVG_FILE = "Frame_AVG.txt";
    private static final String FINAL_AVG_FILE = "Final_AVG.txt";


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        createDirectoryIfNeeded(ORIGINAL_FRAMES_PATH);
        createDirectoryIfNeeded(OUTPUT_COMBINED_PATH);

        try (FileWriter frameFileWriter = new FileWriter(FRAME_AVG_FILE);
             FileWriter finalFileWriter = new FileWriter(FINAL_AVG_FILE)) {

            // Extract frames from the video and save to original-frames directory
            extractFramesFromVideo(VIDEO_PATH, ORIGINAL_FRAMES_PATH);

            // Process original frames and write to Frame_AVG.txt
            processFramesAndWriteResults(ORIGINAL_FRAMES_PATH, frameFileWriter);

            // Process combined frames and write to Final_AVG.txt
            processFramesAndWriteResults(OUTPUT_COMBINED_PATH, finalFileWriter);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void extractFramesFromVideo(String videoPath, String outputFramesPath) {
        VideoCapture videoCapture = new VideoCapture(videoPath);

        if (videoCapture.isOpened()) {
            Mat frame = new Mat();
            int frameNumber = 0;

            while (videoCapture.read(frame)) {
                String outputImagePath = outputFramesPath + "/" + frameNumber + ".jpg";
                Imgcodecs.imwrite(outputImagePath, frame);
                frameNumber++;
            }

            videoCapture.release();
        } else {
            System.err.println("Error opening video file: " + videoPath);
        }
    }

    private static void processFramesAndWriteResults(String framesPath, FileWriter fileWriter) throws IOException {
        // Initialize variables for overall average calculation
        double totalBrightness = 0.0;
        int totalFrames = 0;
        int frameNumber = 0;

        // Process each frame in the directory
        File directory = new File(framesPath);
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // Read the frame
                    Mat frame = Imgcodecs.imread(file.getAbsolutePath());

                    // Calculate average brightness
                    double avgBrightness = Core.mean(frame).val[0];

                    System.out.println("Frame: " + frameNumber + " - Average brightness: " + avgBrightness);
                    fileWriter.write("Frame: " + frameNumber + " - Average brightness: " + avgBrightness + "\n");

                    totalBrightness += avgBrightness;
                    totalFrames++;

                    frameNumber++;
                }
            }

            // Calculate and write frame count and overall average to the file
            double overallAverage = totalFrames > 0 ? totalBrightness / totalFrames : 0.0;
            fileWriter.write("Number of Frames Processed: " + totalFrames + "\n");
            fileWriter.write("Overall Average Brightness: " + overallAverage + "\n");
        } else {
            System.err.println("Error reading frames from directory: " + framesPath);
        }
    }

    private static void createDirectoryIfNeeded(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}
