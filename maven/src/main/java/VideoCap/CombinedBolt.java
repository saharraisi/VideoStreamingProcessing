package VideoCap;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.HashMap;
import java.util.Map;

public class CombinedBolt extends BaseRichBolt {

    private OutputCollector collector;
    private Map<Integer, Mat> blurredFrames = new HashMap<>();
    private Map<Integer, Mat> sharpenedFrames = new HashMap<>();
    private int frameIndex = 0;

    @Override
    public void prepare(Map conf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    @Override
    public void execute(Tuple combine) {
        String framePath = "output_combined";
        try {
            String tag = combine.getStringByField("tag");
            int index = combine.getIntegerByField("index");
            Mat frame = (Mat) combine.getValueByField("frame");

            Map<Integer, Mat> targetFrames = (tag.equals("Blur")) ? blurredFrames : sharpenedFrames;
            targetFrames.put(index, frame);

            if (blurredFrames.containsKey(frameIndex) && sharpenedFrames.containsKey(frameIndex)) {
                Mat combinedFrame = new Mat();
                Core.addWeighted(blurredFrames.remove(frameIndex), 0.5, sharpenedFrames.remove(frameIndex), 0.5, 0, combinedFrame);
                String combinedPath = framePath + "/combined_" + frameIndex + ".jpg";
                Imgcodecs.imwrite(combinedPath, combinedFrame);
                frameIndex++;
            }
        } catch (Exception e) {
            System.err.println("CombinedBolt Error " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Declare the output fields for the next bolt
    }
}
