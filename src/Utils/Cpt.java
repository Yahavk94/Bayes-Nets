package Utils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class represents a conditional probability table.
 * @author Yahav Karpel
 */

public class Cpt implements Comparable<Cpt> {
	private Map<String, Double> table = new HashMap<>();

	/**
	 * This method returns true if this cpt contains a mapping for the specified query.
	 */
	public boolean containsKey(String query) {
		return table.containsKey(query);
	}

	/**
	 * This method returns the probability to which the specified query is mapped.
	 */
	public double get(String query) {
		return table.get(query);
	}

	/**
	 * This method returns true if this cpt contains no mappings.
	 */
	public boolean isEmpty() {
		return table.isEmpty();
	}

	/**
	 * This method returns an iterator over the queries in this cpt.
	 */
	public Iterator<String> iterator() {
		return table.keySet().iterator();
	}

	/**
	 * This method associates the specified probability with the specified query in this cpt.
	 */
	public void put(String query, double probability) {
		table.put(query, probability);
	}

	public String toString() {
		return table.toString();
	}

	/**
	 * For sorting purpose.
	 */
	@Override
	public int compareTo(Cpt cpt) {
		if (table.size() == cpt.table.size()) {
			if (Extract.asciiSum(iterator().next()) > Extract.asciiSum(cpt.iterator().next())) {
				return 1;
			}
		}

		else if (table.size() > cpt.table.size()) {
			return 1;
		}

		return -1;
	}
}
