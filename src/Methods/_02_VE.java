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
			System.out.println(df.format(Service.calculateProbability(query)) + "," + 0 + "," + 0);
			return df.format(Service.calculateProbability(query)) + "," + 0 + "," + 0;
		}

		Queue<Cpt> minHeap = new PriorityQueue<>();

		// Eliminate in alphabetical order
		Collections.sort(hidden);

		// The factors of the nodes
		Stack<Cpt> factors = Service.getFactors(query);

		while (!hidden.isEmpty()) {
			String X = hidden.remove(0);
			Stack<Cpt> stack = new Stack<>();

			while (!factors.isEmpty()) {
				stack.push(factors.pop());
				Iterator<String> iterator = stack.peek().iterator();

				while (iterator.hasNext()) {
					if (iterator.next().contains(X)) {
						minHeap.add(stack.pop());
						break;
					}
				}
			}

			factors = stack;

			// Join
			minHeap = Service.mulFactors(minHeap);

			// Eliminate
			Cpt top = minHeap.remove();
			Iterator<String> iterator = top.iterator();

			Cpt cpt = new Cpt();

			while (iterator.hasNext()) {
				String value = BN.getInstance().getNode(X).valuesIterator().next();

				String current = iterator.next();
				Set<String> ordered = Extract.ordered(current);

				double probability = 0;

				if (current.contains(X + "=" + value)) {
					Iterator<String> valuesIterator = BN.getInstance().getNode(X).valuesIterator();

					while (valuesIterator.hasNext()) {
						String candidate = valuesIterator.next();

						Set<String> temp = Extract.ordered(current.replace(X + "=" + value, X + "=" + candidate));
						probability += top.get(temp.toString());
					}

					ordered.remove(X + "=" + value);
					cpt.put(ordered.toString(), probability);
				}
			}

			minHeap.add(cpt);
		}

		//System.out.println(minHeap.peek());

		while (!factors.isEmpty()) /* Join all remaining factors */ {
			minHeap.add(factors.pop());
			minHeap = Service.mulFactors(minHeap);
		}

		Map<String, String> qn = Extract.queryNode(query);
		String qnode = qn.keySet().iterator().next();
		qnode += "=" + qn.get(qnode);

		double result = 0;
		double sum = 0;

		while (!minHeap.isEmpty()) {
			Cpt cpt = minHeap.remove();
			Iterator<String> iterator = cpt.iterator();

			while (iterator.hasNext()) {
				String next = iterator.next();
				if (next.contains(qnode)) {
					result = cpt.get(next);
				}

				sum += cpt.get(next);
			}
		}

		System.out.println(df.format(result / sum) + "," + additions + "," + multiplications);
		return df.format(result / sum) + "," + additions + "," + multiplications;
	}
}
