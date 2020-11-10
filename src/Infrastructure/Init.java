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
		StringTokenizer tokenizer = new StringTokenizer(Input.getNList().remove(0).split(":")[1], " ,");
		int size = tokenizer.countTokens();

		for (int i = 0; i < size; i += 1) {
			Node node = new Node(Input.getNList().remove(0).split(" ")[1]);

			tokenizer = new StringTokenizer(Input.getNList().remove(0).split(":")[1], " ,");
			while (tokenizer.hasMoreTokens()) {
				node.getValues().put(tokenizer.nextToken(), false);
			}

			tokenizer = new StringTokenizer(Input.getNList().remove(0).split(":")[1], " ,");
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (!token.equals("none")) {
					node.getParents().add(token);
				}
			}

			if (node.getParents().size() == 0) {
				tokenizer = new StringTokenizer(Input.getNList().remove(0), ",");
				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					node.getValues().replace(value.substring(1), true);
					node.getCpt().put("P(" + node.getName() + value + ")", Double.parseDouble(tokenizer.nextToken()));
				}
			}

			for (int lines = 0; lines < node.getParents().size() * node.getValues().size(); lines += 1) {
				String cp = "|";

				int d = 0;
				tokenizer = new StringTokenizer(Input.getNList().remove(0), ",");
				while (d < node.getParents().size() - 1) {
					cp += node.getParents().get(d++) + "=" + tokenizer.nextToken() + ",";
				}

				cp += node.getParents().get(d) + "=" + tokenizer.nextToken() + ")";

				while (tokenizer.hasMoreTokens()) {
					String value = tokenizer.nextToken();
					node.getValues().replace(value.substring(1), true);
					node.getCpt().put("P(" + node.getName() + value + cp, Double.parseDouble(tokenizer.nextToken()));
				}
			}

			network.put(node.getName(), node);
		}

		return network;
	}
}
