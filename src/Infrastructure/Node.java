package Infrastructure;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a node.
 * @author Yahav Karpel
 */

public class Node implements Comparable<Node> {
	private String name;

	private List<String> values = new ArrayList<>();
	private List<String> parents = new ArrayList<>();

	private Map<String, Double> cpt = new LinkedHashMap<>();

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
	 * This method returns true if the cpt of this node contains the probability of the given query.
	 */
	public boolean containsProbability(String query) {
		return cpt.containsKey(query);
	}

	/**
	 * This method returns the probability of the given query.
	 */
	public double getProbability(String query) {
		return cpt.get(query);
	}

	/**
	 * This method returns the values of this node.
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * This method returns an iteration of the values of this node.
	 */
	public Iterator<String> valuesIterator() {
		return values.iterator();
	}

	/**
	 * This method returns the parents of this node.
	 */
	public List<String> getParents() {
		return parents;
	}

	/**
	 * This method returns an iteration of the parents of this node.
	 */
	public Iterator<String> parentsIterator() {
		return parents.iterator();
	}

	/**
	 * For sorting purpose.
	 */
	@Override
	public int compareTo(Node node) {
		if (values.size() > node.values.size()) {
			return 1;
		}

		return -1;
	}
}
