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
	private static List<String> queries = readLines();
	private static List<String> network = initNList();

	private static List<String> readLines() {
		try {
			return Files.readAllLines(Paths.get("input.txt"));
		}

		catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private static List<String> initNList() {
		List<String> net = new LinkedList<>();
		queries.remove(0);

		for (int i = 0; i < queries.size(); i += 1) {
			if (queries.get(i).equals("Queries")) {
				queries.remove(i);
				initQList();
				return net;
			}

			if (queries.get(i).equals("") || queries.get(i).equals("CPT:")) {
				queries.remove(i--);
				continue;
			}

			net.add(queries.remove(i--));
		}

		return null;
	}

	private static void initQList() {
		for (int i = 0; i < queries.size(); i += 1) {
			if (queries.get(i).equals("")) {
				queries.remove(i--);
			}
		}
	}

	/**
	 * This method returns the network as a list.
	 */
	public static List<String> getNList() {
		return network;
	}

	/**
	 * This method returns the queries as a list.
	 */
	public static List<String> getQList() {
		return queries;
	}
}
