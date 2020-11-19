package Utils;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * This class represents a conditional probability table.
 * @author Yahav Karpel
 */

public class Cpt implements Comparable<Cpt> {
	private Map<String, Double> table = new TreeMap<>();

	/**
	 * This method returns this conditional probability table.
	 */
	public Map<String, Double> getCpt() {
		return table;
	}

	public boolean isEmpty() {
		return table.isEmpty();
	}

	/**
	 * This method returns true if this cpt contains a mapping for the specified query.
	 */
	public boolean contains(String query) {
		return table.containsKey(query);
	}

	/**
	 * This method returns the probability to which the specified query is mapped.
	 */
	public double get(String query) {
		return table.get(query);
	}

	/**
	 * This method associates the specified probability with the specified query in this cpt.
	 */
	public void insert(String query, double probability) {
		table.put(query, probability);
	}

	public String remove() {
		String st = ((TreeMap<String, Double>)table).firstKey();
		table.remove(st);
		return st;
	}

	public void remove(String query) {
		table.remove(query);
	}

	/**
	 * This method returns an iterator over the queries in this cpt.
	 */
	public Iterator<String> iterator() {
		return table.keySet().iterator();
	}

	/**
	 * This method returns a set view of the queries contained in this cpt.
	 */
	public Set<String> keySet() {
		return table.keySet();
	}

	/**
	 * This method returns the string representation of this cpt.
	 */
	@Override
	public String toString() {
		return table.toString();
	}

	/**
	 * For sorting purpose.
	 */
	@Override
	public int compareTo(Cpt cpt) {
		if (table.size() == cpt.table.size()) {
			return 1;
		}

		else if (table.size() > cpt.table.size()) {
			return 1;
		}

		return -1;
	}
}
