package Methods;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import Infrastructure.Node;
import Tools.Service;
import Utils.Cpt;
import Utils.Extract;

/**
 * This class implements heuristic Variable Elimination algorithm.
 * @author Yahav Karpel
 */

public class _03_Heuristic implements Probable {
	@Override
	public String inference(String query) {
		DecimalFormat df = new DecimalFormat("0.00000");

		// The hidden nodes of the given query
		List<Node> hidden = Extract.hiddenNodes(query);

		if (hidden.isEmpty()) {
			System.out.println(df.format(Service.calculateProbability(query)) + "," + Service.getComplexity());
			return null;
		}

		Queue<Cpt> minHeap = new PriorityQueue<>();

		// Eliminate in alphabetical order
		Collections.sort(hidden);

		// The factors of the nodes
		Stack<Cpt> factors = Service.getFactors(query);

		while (!hidden.isEmpty()) {
			Node node = hidden.remove(0);
			Stack<Cpt> stack = new Stack<>();

			while (!factors.isEmpty()) {
				stack.push(factors.pop());
				Iterator<String> iterator = stack.peek().iterator();

				while (iterator.hasNext()) {
					if (iterator.next().contains(node.getName())) {
						minHeap.add(stack.pop());
						break;
					}
				}
			}

			factors = stack;

			// Join
			minHeap = Service.mulFactors(minHeap);

			// Eliminate
			minHeap.add(Service.eliminateFactor(minHeap.remove(), node));
		}

		while (!factors.isEmpty()) {
			minHeap.add(factors.pop());
			minHeap = Service.mulFactors(minHeap);
		}

		String queryNode = Extract.QN(query);

		Stack<Double> stack = new Stack<>();

		// The probabilities of the queries in the samples set
		Queue<Double> probabilities = new LinkedList<>();

		while (!minHeap.isEmpty()) {
			Cpt cpt = minHeap.remove();
			Iterator<String> iterator = cpt.iterator();

			while (iterator.hasNext()) {
				String next = iterator.next();
				if (next.contains(queryNode)) {
					probabilities.add(cpt.get(next));
					continue;
				}

				stack.push(cpt.get(next));
			}
		}

		while (!stack.isEmpty()) {
			probabilities.add(stack.pop());
		}

		System.out.println(df.format(Service.normalization(probabilities)) + "," + Service.getComplexity());
		return null;
	}
}
