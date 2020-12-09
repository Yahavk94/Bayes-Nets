package Infrastructure;
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
	 * This method constructs a new network.
	 */
	private BN() {
		network = Init.initFromFile();
	}

	/**
	 * This method returns a single instance of this network.
	 */
	public static BN getInstance() {
		return instance;
	}

	/**
	 * This method returns the node to which the specified name is mapped.
	 */
	public Node getNode(String name) {
		return network.get(name);
	}

	/**
	 * This method returns an iterator over the nodes in this network.
	 */
	public Iterator<Node> iterator() {
		return network.values().iterator();
	}
}
