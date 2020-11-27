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
		StringTokenizer st = new StringTokenizer(Input.nodes.remove(0).substring(11), ",");
		while (st.hasMoreTokens()) /* Initialize the nodes in the network */ {
			Node node = new Node(st.nextToken());
			network.put(node.getName(), node);
		}

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
					current.insertParent(token);

					Iterator<String> ancestorsIterator = network.get(token).ancestorsIterator();
					while (ancestorsIterator.hasNext()) {
						current.insertAncestor(ancestorsIterator.next());
					}

					current.insertAncestor(token);
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
				Set<String> set = new HashSet<>();

				Iterator<String> iterator = current.parentsIterator();
				while (iterator.hasNext()) {
					set.add(iterator.next() + "=" + st.nextToken());
				}

				while (st.hasMoreTokens()) {
					current.getCpt().put(current.getName() + st.nextToken() + "|" + set, Double.parseDouble(st.nextToken()));
				}

				st = new StringTokenizer(Input.nodes.remove(0), ",");
			}
		}

		return network;
	}
}
