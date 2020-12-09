package Utils;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

public class Extract {
	/**
	 * This method extracts the query variable of the given query.
	 */
	public static String QX(String query) {
		return query.substring(0, query.indexOf("="));
	}

	/**
	 * This method extracts the value of the query variable of the given query.
	 */
	public static String QV(String query) {
		if (!query.contains("|")) {
			return query.substring(query.indexOf("=") + 1);
		}

		return query.substring(query.indexOf("=") + 1, query.indexOf("|"));
	}

	/**
	 * This method extracts the query node of the given query.
	 */
	public static String QN(String query) {
		if (!query.contains("|")) {
			return query;
		}

		return query.substring(0, query.indexOf("|"));
	}

	/**
	 * This method extracts the evidence nodes of the given query.
	 */
	public static Map<String, String> getEvidenceNodes(String query) {
		Map<String, String> evidence = new HashMap<>();

		if (!query.contains("|")) {
			return evidence;
		}

		StringTokenizer st = new StringTokenizer(query.substring(query.indexOf("|") + 1), "[]= ,");
		while (st.hasMoreTokens()) {
			evidence.put(st.nextToken(), st.nextToken());
		}

		return evidence;
	}

	/**
	 * This method extracts the nodes of the given query into a sorted set.
	 */
	public static SortedSet<String> getSortedSet(String query) {
		SortedSet<String> set = new TreeSet<>();

		StringTokenizer st = new StringTokenizer(query, "|[] ,");
		while (st.hasMoreTokens()) {
			set.add(st.nextToken());
		}

		return set;
	}

	/**
	 * This method calculates and returns the ascii sum of the nodes of the given query.
	 */
	public static int asciiSum(String query) {
		int ascii = 0;

		Iterator<String> iterator = getSortedSet(query).iterator();
		while (iterator.hasNext()) {
			String X = QX(iterator.next());
			while (!X.isEmpty()) {
				ascii += (int)X.charAt(0);
				X = X.substring(1);
			}
		}

		return ascii;
	}
}
