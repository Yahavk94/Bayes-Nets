package Methods;
import java.util.Iterator;
import java.util.List;
import Infrastructure.BN;
import Infrastructure.Node;
import Tools.Service;

/**
 * This class implements a simple probabilistic inference algorithm.
 * @author Yahav Karpel
 */

public class _01_Simple implements Probable {
	@Override
	public String inference(String query) {
		double probability = 0;
		int muls = 0;
		int adds = 0;

		List<List<String>> formula = Service.getBNFormula(query);
		while (!formula.isEmpty()) {
			List<String> element = formula.remove(0);
			while (!element.isEmpty()) {
				String e = element.remove(0);
				Node node = BN.getInstance().getNode(Service.getLX(e));
				if (node.getCpt().containsKey(e)) {
					probability += node.getCpt().get(e);
				} else {
					Iterator<String> iterator = node.getValues().iterator();
					while (iterator.hasNext()) {
						String value = iterator.next();
						System.out.println(value);
					}
					
					System.out.println("Write the service method");
				}
			}
		}

		return null;
	}
}
