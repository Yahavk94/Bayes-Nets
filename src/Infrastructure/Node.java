package Infrastructure;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a node.
 * @author Yahav Karpel
 */

public class Node {
	private String name;

	private Map<String, Double> cpt = new LinkedHashMap<>();
	private Map<String, Boolean> values = new LinkedHashMap<>();

	private List<String> parents = new ArrayList<>();

	/**
	 * This method constructs a new node.
	 */
	protected Node(String name) {
		this.name = name;
	}

	/**
	 * This method returns the name of this node.
	 */
	public String getName() {
		return name;
	}

	/**
	 * This method returns the conditional probability table of this node.
	 */
	public Map<String, Double> getCpt() {
		return cpt;
	}

	/**
	 * This method returns the values of this node.
	 */
	public Map<String, Boolean> getValues() {
		return values;
	}

	/**
	 * This method returns the parents of this node.
	 */
	public List<String> getParents() {
		return parents;
	}
}
