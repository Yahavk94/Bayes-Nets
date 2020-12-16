package Infrastructure;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import Utils.Cpt;

/**
 * This class represents a node.
 * @author Yahav Karpel
 */

public class Node implements Comparable<Node> {
	private String name;

	private Set<String> values = new HashSet<>();
	private Set<String> parents = new LinkedHashSet<>();

	private Cpt cpt = new Cpt();

	private Set<String> ancestors = new HashSet<>();
	private Set<String> children = new HashSet<>();

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
	 * This method returns a random value from this values set.
	 */
	public String getRandomValue() {
		return values.iterator().next();
	}

	/**
	 * This method returns the size of the domain.
	 */
	public int domainSize() {
		return values.size();
	}

	/**
	 * This method adds the specified value to this values set if it is not already present.
	 */
	protected void insertValue(String value) {
		values.add(value);
	}

	/**
	 * This method returns an iterator over the values of this node.
	 */
	public Iterator<String> valuesIterator() {
		return values.iterator();
	}

	/**
	 * This method adds the specified node to this parents set if it is not already present.
	 */
	protected void insertParent(Node node) {
		parents.add(node.name);
		node.children.add(name);
		update(node);
	}

	/**
	 * This method updates the ancestors set of this node.
	 */
	private void update(Node node) {
		ancestors.add(node.name);
		ancestors.addAll(node.ancestors);
	}

	/**
	 * This method returns an iterator over the parents of this node.
	 */
	public Iterator<String> parentsIterator() {
		return parents.iterator();
	}

	/**
	 * This method returns the conditional probability table of this node.
	 */
	public Cpt getCpt() {
		return cpt;
	}

	/**
	 * This method returns true if this set contains the specified ancestor.
	 */
	public boolean containsAncestor(String ancestor) {
		return ancestors.contains(ancestor);
	}

	/**
	 * For sorting purpose.
	 */
	@Override
	public int compareTo(Node node) {
		if (computeHeuristic() == node.computeHeuristic()) {
			return cpt.compareTo(node.cpt);
		}

		else if (computeHeuristic() > node.computeHeuristic()) {
			return 1;
		}

		return -1;
	}

	/**
	 * This method computes and returns the heuristic evaluation of this node.
	 */
	private int computeHeuristic() {
		int size = cpt.size();

		Iterator<String> iterator = parents.iterator();
		while (iterator.hasNext()) {
			size += BN.getInstance().getNode(iterator.next()).cpt.size();
		}

		iterator = children.iterator();
		while (iterator.hasNext()) {
			size += BN.getInstance().getNode(iterator.next()).cpt.size();
		}

		return size;
	}
}
