package Infrastructure;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import Utils.Cpt;

/**
 * This class represents a node.
 * @author Yahav Karpel
 */

public class Node implements Comparable<Node> {
	private String name;

	private Set<String> values = new HashSet<>();
	private Set<String> parents = new HashSet<>();
	private Set<String> ancestors = new HashSet<>();

	private Cpt cpt = new Cpt();

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
	 * This method returns an iterator over the values of this node.
	 */
	public Iterator<String> valuesIterator() {
		return values.iterator();
	}

	/**
	 * This method adds the specified value to this values set if it is not already present.
	 */
	protected void insertValue(String value) {
		values.add(value);
	}

	/**
	 * This method returns an iterator over the parents of this node.
	 */
	public Iterator<String> parentsIterator() {
		return parents.iterator();
	}

	/**
	 * This method adds the specified parent to this parents set if it is not already present.
	 */
	protected void insertParent(String parent) {
		parents.add(parent);
	}

	/**
	 * This method returns an iterator over the ancestors of this node.
	 */
	public Iterator<String> ancestorsIterator() {
		return ancestors.iterator();
	}

	/**
	 * This method adds the specified ancestor to this ancestors set if it is not already present.
	 */
	protected void insertAncestor(String ancestor) {
		ancestors.add(ancestor);
	}

	/**
	 * This method returns true if this set contains the specified ancestor.
	 */
	public boolean containsAncestor(String ancestor) {
		return ancestors.contains(ancestor);
	}

	/**
	 * This method returns the conditional probability table of this node.
	 */
	public Cpt getCpt() {
		return cpt;
	}

	/**
	 * For sorting purpose.
	 */
	@Override
	public int compareTo(Node node) {
		if (parents.size() > node.parents.size()) {
			return 1;
		}

		return -1;
	}
}
