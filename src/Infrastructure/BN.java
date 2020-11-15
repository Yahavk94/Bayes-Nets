package Infrastructure;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a Bayesian Network.
 * @author Yahav Karpel
 */

public class BN {
	private Map<String, Node> network;
	private static BN instance = new BN();

	/**
	 * This method returns a single instance of this network.
	 */
	public static BN getInstance() {
		return instance;
	}

	/**
	 * This method constructs a new network.
	 */
	private BN() {
		network = Init.initFromFile(new HashMap<>());
	}

	/**
	 * This method returns the corresponding node.
	 */
	public Node getNode(String name) {
		return network.get(name);
	}

	/**
	 * This method returns an iteration of the nodes in this network.
	 */
	public Iterator<Node> nodesIterator() {
		return network.values().iterator();
	}
}
