package Tools;
import java.util.ArrayList;
import java.util.List;
import Infrastructure.Node;

public class Backup {
	/**
	 * This method returns a cartesian product of the values of the nodes.
	 */
	private static List<List<String>> cartesianProduct(List<Node> nodes) {
		List<List<String>> cartesian = new ArrayList<>();

		if (nodes.size() == 1) {
			for (int i = 0; i < nodes.get(0).getValues().size(); i += 1) {
				List<String> element = new ArrayList<>();
				element.add(nodes.get(0).getValues().get(i));
				cartesian.add(element);
			}

			return cartesian;
		}

		for (int first = 0; first < nodes.get(0).getValues().size(); first += 1) {
			for (int second = 0; second < nodes.get(1).getValues().size(); second += 1) {
				List<String> element = new ArrayList<>();
				element.add(nodes.get(0).getValues().get(first));
				element.add(nodes.get(1).getValues().get(second));
				cartesian.add(element);
			}
		}

		for (int i = 2; i < nodes.size(); i += 1) {
			List<List<String>> temp = new ArrayList<>();

			while (!cartesian.isEmpty()) {
				for (int v = 0; v < nodes.get(i).getValues().size(); v += 1) {
					List<String> lst = clone(cartesian.get(0));
					lst.add(nodes.get(i).getValues().get(v));
					temp.add(lst);
				}

				cartesian.remove(0);
			}

			cartesian = temp;
		}

		return cartesian;
	}
}
