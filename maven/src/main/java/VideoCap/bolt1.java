package VideoCap;

import org.apache.storm.drpc.LinearDRPCTopologyBuilder;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.apache.storm.tuple.Values;

import java.util.Map;

class bolt1 extends BaseRichBolt {
    private OutputCollector collector;
    private int frame_number = 0;

    @Override
    public void prepare(Map<String, Object> map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String framePath = "output_blurred";

        // Load the frame
        Mat frame = (Mat) tuple.getValueByField("frame");

        // Blur the frame
        Mat blurredFrame = new Mat();
        Imgproc.blur(frame, blurredFrame, new Size(10, 10));

        String blurredPath = framePath + "/blurred_" + frame_number + ".jpg";
        Imgcodecs.imwrite(blurredPath, blurredFrame);

        collector.emit(new Values("Blur", frame_number, blurredFrame));
        frame_number++;

        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        // Declare the output fields as "tag," "index," and "frame"
        declarer.declare(new Fields("tag", "index", "frame"));
    }
}
