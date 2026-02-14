package VideoCap;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;


class Videospout implements IRichSpout, Serializable {
    private SpoutOutputCollector collector;
    private String folderPath = "output";
    private File[] frameFiles;
    private int currentIndex=0;


    @Override
    public void open(Map<String, Object> map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        VideoCap();
        // Assuming your frames are stored as image files in the specified folder
        frameFiles = new File(folderPath).listFiles();

        // Sort frame files based on their numerical order in the filename
        Arrays.sort(this.frameFiles, Comparator.comparingInt(file -> extractNumber(file.getName())));
    }


    private int extractNumber(String name) {
        try {
            int s = name.indexOf('_') + 1;
            int e = name.lastIndexOf('.');
            String number = name.substring(s, e);
            return Integer.parseInt(number);
        } catch (Exception e) {
            return 0;
        }


    }

    @Override
    public void close() {

    }

    @Override
    public void activate() {

    }

    @Override
    public void deactivate() {

    }

    @Override
    public void nextTuple() {
        if (currentIndex < frameFiles.length) {
            Mat frame= Imgcodecs.imread(frameFiles[currentIndex].getPath());
            collector.emit(new Values(frame));
            currentIndex++;
        }
    }

    @Override
    public void ack(Object o) {

    }

    @Override
    public void fail(Object o) {

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
//        System.out.println("sahar3"+frameFiles.length);
        outputFieldsDeclarer.declare(new Fields("frame"));

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
    public static void VideoCap(){
        VideoCapture cap = new VideoCapture();

        String input = "video/1.mp4";

        String output = "output";

        cap.open(input);

        int video_length = (int) cap.get(Videoio.CAP_PROP_FRAME_COUNT);
        int frames_per_second = (int) cap.get(Videoio.CAP_PROP_FPS);
        int frame_number = 0;

        int resizedWidth = 320;
        int resizedHeight = 240;

        Mat frame = new Mat();

        if (cap.isOpened()) {
            while (cap.read(frame)) {
                // Convert the frame to grayscale
                Mat grayFrame = new Mat();
                Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

                // Resize the grayscale frame
                Mat resizedGrayFrame = new Mat();
                Imgproc.resize(grayFrame, resizedGrayFrame, new Size(resizedWidth, resizedHeight));

                // Save the resized grayscale frame
                Imgcodecs.imwrite(output + "/" + frame_number + ".jpg", resizedGrayFrame);
                frame_number++;
            }
            cap.release();
        } else {
            System.out.println("Fail");
        }


    }
}
