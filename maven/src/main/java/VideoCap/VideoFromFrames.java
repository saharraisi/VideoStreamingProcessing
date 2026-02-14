package VideoCap;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoWriter;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class VideoFromFrames {

    public void createVideo(String folderPath, String outputFile, int fps) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        File[] files = new File(folderPath).listFiles();
        if (files == null || files.length == 0) {
            System.err.println("No frames found in " + folderPath);
            return;
        }

        // Sort filenames
        Arrays.sort(files, Comparator.comparingInt(f -> Integer.parseInt(f.getName().split("_")[1].split("\\.")[0])));

        // Get first frame size
        Mat firstFrame = Imgcodecs.imread(files[0].getAbsolutePath());
        Size frameSize = firstFrame.size();

        // Create video writer with error handling
        try {
            VideoWriter writer = new VideoWriter(outputFile, VideoWriter.fourcc('m', 'p', '4', 'v'), fps, frameSize, true);

            // Write all frames (assuming valid image formats)
            for (File file : files) {
                Mat frame = Imgcodecs.imread(file.getAbsolutePath());
                writer.write(frame);
            }

            writer.release();
            firstFrame.release();

            System.out.println("Video created: " + outputFile);
        } catch (Exception e) {
            System.err.println("Error creating video: " + e.getMessage());
        }
    }
}
