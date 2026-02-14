package VideoCap;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.Map;

class bolt2 extends BaseRichBolt {

    private OutputCollector collector;
    private int frameNumber = 0;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        Mat frame = (Mat) tuple.getValueByField("frame");

        // Sharpen the frame
        Mat sharpenedFrame = new Mat();
        Imgproc.GaussianBlur(frame, sharpenedFrame, new Size(0, 0), 5);
        Core.addWeighted(frame, 2.0, sharpenedFrame, -1.0, 0, sharpenedFrame);


        // Save the sharpened frame
        String outputFolderPath = "output_Sharpened";
        String sharpenedFramePath = outputFolderPath + "/sharpened_" + frameNumber + ".jpg";
        Imgcodecs.imwrite(sharpenedFramePath, sharpenedFrame);

        collector.emit(new Values("Sharp", frameNumber, sharpenedFrame));
        frameNumber++;

        collector.ack(tuple);

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Declare the output fields as "tag," "index," and "frame"
        declarer.declare(new Fields("tag", "index", "frame"));
    }
}
