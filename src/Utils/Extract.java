package Utils;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * This class represents data extraction.
 * @author Yahav Karpel
 */

public class Extract {
	/**
	 * This method extracts the query variable of the given query.
	 */
	public static String QX(String query) {
		if (query.charAt(0) == '[') {
			return query.substring(1, query.indexOf("="));
		}

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
	 * This method extracts the names of the nodes in the given query.
	 */
	public static Stack<String> getNames(String query) {
		Stack<String> names = new Stack<>();

		StringTokenizer st = new StringTokenizer(query, "[] ,");
		while (st.hasMoreTokens()) {
			names.push(QX(st.nextToken()));
		}

		return names;
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
	 * This method extracts and flattens the nodes of the given query into a sorted set.
	 */
	public static SortedSet<String> getNodes(String query) {
		SortedSet<String> set = new TreeSet<>();

		StringTokenizer st = new StringTokenizer(query, "[]| ,");
		while (st.hasMoreTokens()) {
			set.add(st.nextToken());
		}

		return set;
	}
}
