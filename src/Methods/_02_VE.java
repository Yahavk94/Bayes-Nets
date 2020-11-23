package Methods;
import java.text.DecimalFormat;
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
 * This class implements Variable Elimination algorithm.
 * @author Yahav Karpel
 */

public class _02_VE implements Probable {
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

		// The initial factors
		Stack<Cpt> factors = Service.getFactors(query);

		while (!hidden.isEmpty()) {
			Node node = hidden.remove(0);
			Stack<Cpt> temp = new Stack<>();

			while (!factors.isEmpty()) {
				temp.push(factors.pop());
				Iterator<String> iterator = temp.peek().iterator();

				while (iterator.hasNext()) {
					if (iterator.next().contains(node.getName())) {
						minHeap.add(temp.pop());
						break;
					}
				}
			}

			factors = temp;

			// Join
			minHeap = Service.mulFactors(minHeap);

			// Eliminate
			minHeap.add(Service.eliminateFactor(minHeap.remove(), node));
		}

		while (!factors.isEmpty()) {
			minHeap.add(factors.pop());
			minHeap = Service.mulFactors(minHeap);
		}

		Stack<Double> stack = new Stack<>();
		Queue<Double> results = new LinkedList<>();

		String queryNode = Extract.QN(query);

		Cpt cpt = minHeap.remove();
		Iterator<String> iterator = cpt.iterator();

		while (iterator.hasNext()) {
			String next = iterator.next();
			if (next.contains(queryNode)) {
				results.add(cpt.get(next));
				continue;
			}

			stack.push(cpt.get(next));
		}

		while (!stack.isEmpty()) {
			results.add(stack.pop());
		}

		System.out.println(df.format(Service.normalization(results)) + "," + Service.getComplexity());
		return null;
	}
}
