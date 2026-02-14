package VideoCap;

import org.opencv.core.Core;

public class start {
    public static void main(String[] args) throws Exception {
        // Load OpenCV native libraries
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        LocalTopology localTopology = new LocalTopology();
//        LocalTopology.VideoCap();
        localTopology.main(args);
        VideoFromFrames videoFromFrames = new VideoFromFrames();
        videoFromFrames.createVideo("output_combined", "output_video.mp4", 30);
        VideoBrightnessAnalyzer.main(args);
    }
}
