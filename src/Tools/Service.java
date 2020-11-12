package Tools;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import Infrastructure.BN;

/**
 * This class represents the methods which are used by the algorithms.
 * @author Yahav Karpel
 */

public class Service {
	/**
	 * This method converts and returns the given query as a BN formula.
	 */
	public static List<List<String>> getBNFormula(String query) {
		List<List<String>> formula = new ArrayList<>();

		List<Map<String, String>> tpr = Utensil.totalProbabilityRule(query);
		while (!tpr.isEmpty()) {
			Map<String, String> map = tpr.remove(0);
			List<String> list = new ArrayList<>();

			Iterator<String> iterator = map.keySet().iterator();
			while (iterator.hasNext()) {
				String X = iterator.next();
				if (BN.getInstance().getNode(X).getParents().size() == 0) {
					list.add("P(" + X + "=" + map.get(X) + ")");
					continue;
				}

				String cp = "P(" + X + "=" + map.get(X) + "|";

				int i = 0;
				while (i < BN.getInstance().getNode(X).getParents().size() - 1) {
					String parent = BN.getInstance().getNode(X).getParents().get(i++);
					cp += parent + "=" + map.get(parent) + ",";
				}

				String parent = BN.getInstance().getNode(X).getParents().get(i);
				list.add(cp + parent + "=" + map.get(parent) + ")");
			}

			formula.add(list);
		}

		return formula;
	}
}
