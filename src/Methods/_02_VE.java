package Methods;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import Infrastructure.BN;
import Tools.Service;
import Utils.Cpt;
import Utils.Extract;

/**
 * This class implements Variable Elimination algorithm.
 * @author Yahav Karpel
 */

public class _02_VE implements Probable {
	@Override
	public String inference(String query) {
		DecimalFormat df = new DecimalFormat("0.00000");

		int additions = 0;
		int multiplications = 0;

		// The hidden nodes of the given query
		List<String> hidden = Extract.hiddenNodes(query);

		if (hidden.isEmpty()) {
			return df.format(Service.calculateProbability(query)) + "," + 0 + "," + 0;
		}

		Queue<Cpt> minHeap = new PriorityQueue<>();

		// Eliminate in alphabetical order
		Collections.sort(hidden);

		// The factors of the nodes
		Stack<Cpt> factors = Service.getFactors(query);

		while (!hidden.isEmpty()) {
			String node = hidden.remove(0);
			Stack<Cpt> temp = new Stack<>();

			while (!factors.isEmpty()) {
				temp.push(factors.pop());
				Iterator<String> iterator = temp.peek().iterator();

				while (iterator.hasNext()) {
					if (iterator.next().contains(node)) {
						minHeap.add(temp.pop());
						break;
					}
				}
			}

			factors = temp;

			// Join
			minHeap = Service.joinFactors(minHeap);

			// Eliminate
			Cpt first = minHeap.remove();
			multiplications += first.getCpt().size();

			Cpt cpt = new Cpt();
			System.out.println(multiplications);

			Iterator<String> iterator = first.getCpt().keySet().iterator();

			for (int i = 0; i < first.getCpt().keySet().size(); i += 1) {
				String val = BN.getInstance().getNode(node).valuesIterator().next();

				String q = iterator.next();
				Set<String> ordered = Extract.orderedEvidence(q);

				double prob = 0;

				if (q.contains(node + "=" + val)) {
					Iterator<String> valuesIterator = BN.getInstance().getNode(node).valuesIterator();
					while (valuesIterator.hasNext()) {
						String value = valuesIterator.next();
						if (q.contains(node + "=" + value)) {
							prob += first.getCpt().get(ordered.toString());
							continue;
						}

						Set<String> tempSet = Extract.orderedEvidence(q.replace(node + "=" + val, node + "=" + value));
						prob = prob + first.getCpt().get(tempSet.toString());
						additions += 1;

						ordered.remove(node + "=" + val);
						cpt.getCpt().put(ordered.toString(), prob);
					}
				}
			}

			first = cpt;
			minHeap.add(first);
			//System.out.println(first);
		}

		while (!factors.isEmpty()) {
			System.out.println(factors.peek());
			minHeap.add(factors.pop());
			minHeap = Service.joinFactors(minHeap);
			multiplications += minHeap.peek().getCpt().size();
		}

		System.out.println(minHeap);

		Map<String, String> qn = Extract.queryNode(query);
		Iterator<String> iterator = qn.keySet().iterator();

		String qnode = iterator.next();
		qnode += "=" + qn.get(qnode);

		double result = 0;
		double sum = 0;

		while (!minHeap.isEmpty()) {
			Cpt cpt = minHeap.remove();
			Iterator<String> iter = cpt.iterator();

			while (iter.hasNext()) {
				String next = iter.next();
				if (next.contains(qnode)) {
					result = cpt.getCpt().get(next);
				}

				sum += cpt.getCpt().get(next);
			}
		}

		System.out.println(df.format(result / sum) + "," + additions + "," + multiplications);
		return df.format(result / sum) + "," + additions + "," + multiplications;
	}
}
