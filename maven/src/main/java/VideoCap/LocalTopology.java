package VideoCap;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.opencv.core.Core;

public class LocalTopology {

    public static void main(String[] args) throws Exception {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // Create a Storm topology
        TopologyBuilder builder = new TopologyBuilder();

        // Add spouts and bolts to the topology
        builder.setSpout("video-spout", new Videospout());
//        bolt1 and bolt2
        builder.setBolt("blur-bolt", new bolt1()).shuffleGrouping("video-spout");
        builder.setBolt("sharpening-bolt", new bolt2(), 1).shuffleGrouping("video-spout");

        // Define a bolt to combine the output of Gaussian blur and sharpening bolts
        builder.setBolt("combine-bolt", new CombinedBolt(), 1).shuffleGrouping("blur-bolt").shuffleGrouping("sharpening-bolt");

        // Set up any additional configuration
        Config config = new Config();
        config.setDebug(true);

        // Create a LocalCluster instance for testing
        LocalCluster cluster = new LocalCluster();

            cluster.submitTopology("local-topology", config, builder.createTopology());

            Thread.sleep(10000);

            cluster.shutdown();


    }

}
