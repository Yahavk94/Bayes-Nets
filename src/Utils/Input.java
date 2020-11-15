package Utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * This class represents the input file.
 * @author Yahav Karpel
 */

public class Input {
	public static List<String> queries = readLines();
	public static List<String> nodes = initNodes();

	private static List<String> readLines() {
		try {
			return Files.readAllLines(Paths.get("input.txt"));
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static List<String> initNodes() throws RuntimeException {
		List<String> nodes = new LinkedList<>();
		queries.remove(0);
		for (int i = 0; i < queries.size(); i += 1) {
			if (queries.get(i).equals("Queries")) {
				queries.remove(i);
				initQueries();
				return nodes;
			}

			if (queries.get(i).equals("CPT:")) {
				queries.remove(i--);
				continue;
			}

			nodes.add(queries.remove(i--));
		}

		throw new RuntimeException("ERROR");
	}

	private static void initQueries() {
		for (int i = 0; i < queries.size(); i += 1) {
			if (queries.get(i).equals("")) {
				queries.remove(i--);
			} else {
				queries.set(i, process(queries.get(i)));
			}
		}
	}

	/**
	 * This method returns a processed format of the given query.
	 */
	private static String process(String query) {
		if (!query.contains("|")) {
			return query;
		}

		// The query
		String temp = query.substring(2, query.lastIndexOf(",") - 1);

		Set<String> set = new HashSet<>();

		StringTokenizer st = new StringTokenizer(temp.substring(temp.indexOf("|") + 1), ",");
		while (st.hasMoreTokens()) {
			set.add(st.nextToken());
		}

		return temp.substring(0, temp.indexOf("|") + 1) + set + query.substring(query.lastIndexOf(","));
	}
}
