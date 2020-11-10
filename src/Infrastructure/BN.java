package Infrastructure;
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents a Bayesian Network.
 * @author Yahav Karpel
 */

public class BN {
	private Map<String, Node> network = new HashMap<>();
	private static BN instance = new BN();

	/**
	 * This method returns a single instance of the network.
	 */
	public static BN getBN() {
		return instance;
	}

	/**
	 * This method constructs a new network.
	 */
	private BN() {
		network = Init.initFromFile(network);
	}

	/**
	 * This method returns the corresponding node.
	 */
	public Node getNode(String name) {
		return network.get(name);
	}
}
