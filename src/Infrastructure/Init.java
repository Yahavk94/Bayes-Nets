package Infrastructure;
import java.util.Map;
import java.util.StringTokenizer;
import Utils.Input;

/**
 * This class initializes the network.
 * @author Yahav Karpel
 */

public class Init {
	protected static Map<String, Node> initFromFile(Map<String, Node> network) {
		String input = Input.nodes.remove(0);
		StringTokenizer tokenizer = new StringTokenizer(input.substring(input.indexOf(" ") + 1), " ,");
		while (tokenizer.hasMoreTokens()) /* Initialize the nodes in the network */ {
			Node node = new Node(tokenizer.nextToken());
			network.put(node.getName(), node);
		}

		// Epsilon
		Input.nodes.remove(0);

		while (!Input.nodes.isEmpty()) {
			input = Input.nodes.remove(0);
			Node current = network.get(input.substring(input.indexOf(" ") + 1));

			// Initialize the values of the current node
			input = Input.nodes.remove(0);
			tokenizer = new StringTokenizer(input.substring(input.indexOf(" ") + 1), " ,");
			while (tokenizer.hasMoreTokens()) {
				current.getValues().add(tokenizer.nextToken());
				//current.getValues().put(tokenizer.nextToken(), false);
			}

			// Initialize the parents of the current node
			input = Input.nodes.remove(0);
			tokenizer = new StringTokenizer(input.substring(input.indexOf(" ") + 1), " ,");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (!token.equals("none")) {
					current.getParents().add(token);
				}
			}

			if (current.getParents().size() == 0) {
				tokenizer = new StringTokenizer(Input.nodes.remove(0), ",");
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					//current.getValues().replace(value.substring(1), true);
					current.getCpt().put("P(" + current.getName() + value + ")", Double.parseDouble(tokenizer.nextToken()));
				}

				// Epsilon
				Input.nodes.remove(0);
				continue;
			}

			// Initialize the conditional probabilities of the current node
			tokenizer = new StringTokenizer(Input.nodes.remove(0), ",");
			while (tokenizer.hasMoreTokens()) {
				String cp = "|";

				int i = 0;
				while (i < current.getParents().size() - 1) {
					cp += current.getParents().get(i++) + "=" + tokenizer.nextToken() + ",";
				}

				cp += current.getParents().get(i) + "=" + tokenizer.nextToken() + ")";

				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					//current.getValues().replace(value.substring(1), true);
					current.getCpt().put("P(" + current.getName() + value + cp, Double.parseDouble(tokenizer.nextToken()));
				}

				tokenizer = new StringTokenizer(Input.nodes.remove(0), ",");
			}
		}

		return network;
	}
}
