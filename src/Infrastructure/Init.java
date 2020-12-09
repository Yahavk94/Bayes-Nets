package Infrastructure;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.TreeSet;
import Utils.Input;

/**
 * This class initializes the network.
 * @author Yahav Karpel
 */

public class Init {
	protected static Map<String, Node> initFromFile() {
		SortedMap<String, Node> temp = new TreeMap<>();

		// Initialize the network nodes in lexicographic order
		StringTokenizer st = new StringTokenizer(Input.nodes.remove(0).substring(11), ",");
		while (st.hasMoreTokens()) {
			Node node = new Node(st.nextToken());
			temp.put(node.getName(), node);
		}

		Map<String, Node> network = new LinkedHashMap<>(temp);

		// Epsilon
		Input.nodes.remove(0);

		while (!Input.nodes.isEmpty()) {
			Node current = network.get(Input.nodes.remove(0).substring(4));

			// Initialize the values of the current node
			st = new StringTokenizer(Input.nodes.remove(0).substring(8), ",");
			while (st.hasMoreTokens()) {
				current.insertValue(st.nextToken());
			}

			// Initialize the parents of the current node
			st = new StringTokenizer(Input.nodes.remove(0).substring(9), ",");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (!token.equals("none")) {
					current.insertParent(network.get(token));
				}
			}

			// Initialize the conditional probability table of the current node
			if (!current.parentsIterator().hasNext()) {
				st = new StringTokenizer(Input.nodes.remove(0), ",");
				while (st.hasMoreTokens()) {
					current.getCpt().put(current.getName() + st.nextToken(), Double.parseDouble(st.nextToken()));
				}

				// Epsilon
				Input.nodes.remove(0);
				continue;
			}

			st = new StringTokenizer(Input.nodes.remove(0), ",");
			while (st.hasMoreTokens()) {
				SortedSet<String> parents = new TreeSet<>();

				Iterator<String> currentParentsIterator = current.parentsIterator();
				while (currentParentsIterator.hasNext()) {
					parents.add(currentParentsIterator.next() + "=" + st.nextToken());
				}

				while (st.hasMoreTokens()) {
					current.getCpt().put(current.getName() + st.nextToken() + "|" + parents, Double.parseDouble(st.nextToken()));
				}

				st = new StringTokenizer(Input.nodes.remove(0), ",");
			}
		}

		return network;
	}
}
