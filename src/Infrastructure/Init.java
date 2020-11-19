package Infrastructure;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import Utils.Input;

/**
 * This class initializes the network.
 * @author Yahav Karpel
 */

public class Init {
	protected static Map<String, Node> initFromFile(Map<String, Node> network) {
		String input = Input.nodes.remove(0);

		// Initialize the nodes in the network
		StringTokenizer st = new StringTokenizer(input.substring(input.indexOf(" ") + 1), " ,");
		while (st.hasMoreTokens()) {
			Node node = new Node(st.nextToken());
			network.put(node.getName(), node);
		}

		// Epsilon
		Input.nodes.remove(0);

		while (!Input.nodes.isEmpty()) {
			input = Input.nodes.remove(0);
			Node current = network.get(input.substring(input.indexOf(" ") + 1));

			// Initialize the values of the current node
			input = Input.nodes.remove(0);
			st = new StringTokenizer(input.substring(input.indexOf(" ") + 1), " ,");
			while (st.hasMoreTokens()) {
				current.insertValue(st.nextToken());
			}

			// Initialize the parents of the current node
			input = Input.nodes.remove(0);
			st = new StringTokenizer(input.substring(input.indexOf(" ") + 1), " ,");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (!token.equals("none")) {
					current.insertParent(token);
				}
			}

			if (!current.parentsIterator().hasNext()) {
				st = new StringTokenizer(Input.nodes.remove(0), ",");
				while (st.hasMoreTokens()) {
					current.getCpt().put(current.getName() + st.nextToken(), Double.parseDouble(st.nextToken()));
				}

				// Epsilon
				Input.nodes.remove(0);
				continue;
			}

			// Initialize the conditional probabilities of the current node
			st = new StringTokenizer(Input.nodes.remove(0), " ,");
			while (st.hasMoreTokens()) {
				Set<String> ordered = new HashSet<>();
				Iterator<String> iterator = current.parentsIterator();

				while (iterator.hasNext()) {
					ordered.add(iterator.next() + "=" + st.nextToken());
				}

				while (st.hasMoreTokens()) {
					current.getCpt().put(current.getName() + st.nextToken() + "|" + ordered, Double.parseDouble(st.nextToken()));
				}

				st = new StringTokenizer(Input.nodes.remove(0), " ,");
			}
		}

		return network;
	}
}
