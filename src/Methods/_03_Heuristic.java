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

		// Reset the number of additions and multiplications
		Service.reset();

		// The hidden nodes of the given query
		List<Node> hidden = Extract.hiddenNodes(query);

		if (hidden.isEmpty()) {
			return df.format(Service.calculateProbability(query)) + "," + Service.getComplexity();
		}

		// Choose the node with the fewest dependant nodes
		Collections.sort(hidden);

		Queue<Cpt> minHeap = new PriorityQueue<>();

		// The initial factors
		Stack<Cpt> factors = Service.getFactors(query);

		while (!hidden.isEmpty()) /* Join and eliminate the factors of the hidden nodes */ {
			Node current = hidden.remove(0);
			Stack<Cpt> temp = new Stack<>();

			while (!factors.isEmpty()) {
				temp.push(factors.pop());
				Iterator<String> iterator = temp.peek().iterator();

				while (iterator.hasNext()) {
					if (iterator.next().contains(current.getName())) {
						minHeap.add(temp.pop());
						break;
					}
				}
			}

			factors = temp;

			// Join
			minHeap = Service.mulFactors(minHeap);

			// Eliminate
			minHeap.add(Service.eliminateFactor(minHeap.remove(), current));
		}

		while (!factors.isEmpty()) /* Join the remaining factors */ {
			minHeap.add(factors.pop());
			minHeap = Service.mulFactors(minHeap);
		}

		Cpt cpt = minHeap.remove();
		Iterator<String> iterator = cpt.iterator();

		// The query node
		String qn = Extract.QN(query);

		// The results of the queries in the cpt
		Queue<Double> results = new LinkedList<>();
		Stack<Double> temp = new Stack<>();

		while (iterator.hasNext()) {
			String current = iterator.next();
			if (current.contains(qn)) {
				results.add(cpt.get(current));
				continue;
			}

			temp.push(cpt.get(current));
		}

		while (!temp.isEmpty()) {
			results.add(temp.pop());
		}

		return df.format(Service.normalization(results)) + "," + Service.getComplexity();
	}
}
