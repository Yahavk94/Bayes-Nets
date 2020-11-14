package Tools;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import Infrastructure.BN;
import Infrastructure.Node;

/**
 * This class represents the methods which are used by the algorithms.
 * @author Yahav Karpel
 */

public class Service {
	/**
	 * This method returns the given query as a BN formula.
	 */
	public static List<List<String>> getBNFormula(String query) {
		List<List<String>> formula = new ArrayList<>();

		List<Map<String, String>> tpr = Utensil.completeProbabilityFormula(query);
		while (!tpr.isEmpty()) {
			List<String> list = new ArrayList<>();
			Map<String, String> map = tpr.remove(0);
			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				Node node = BN.getInstance().getNode(iterator.next());

				if (node.getParents().size() == 0) {
					list.add(node.getName() + "=" + map.get(node.getName()));
					continue;
				}

				Set<String> set = new HashSet<>();
				for (int i = 0; i < node.getParents().size(); i += 1) {
					set.add(node.getParents().get(i) + "=" + map.get(node.getParents().get(i)));
				}

				list.add(node.getName() + "=" + map.get(node.getName()) + "|" + set);
			}

			formula.add(list);
		}

		return formula;
	}

	/**
	 * This method returns the sample set of the given query.
	 */
	public static List<String> getSampleSet(String query) {
		List<String> sample = new ArrayList<>();
		sample.add(query);
		sample.addAll(Utensil.getComplementaryEvents(query));
		return sample;
	}

	/**
	 * This method calculates the probability of the given query.
	 */
	public static double calculateProbability(String query) {
		Node node = BN.getInstance().getNode(query.substring(0, query.indexOf("=")));
		if (node.getCpt().containsKey(query)) {
			return node.getCpt().get(query);
		}

		double probability = 1;
		List<String> ce = Utensil.getComplementaryEvents(query);

		while (!ce.isEmpty()) {
			probability = probability * (1 - node.getCpt().get(ce.remove(0)));
		}

		return probability;
	}
}
