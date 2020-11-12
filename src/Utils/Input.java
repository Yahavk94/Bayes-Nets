package Utils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

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
			}
		}
	}
}
