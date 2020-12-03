package Methods;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import Tools.Service;

/**
 * This class implements a simple probabilistic inference algorithm.
 * @author Yahav Karpel
 */

public class _01_Simple implements Inferable {
	@Override
	public String inference(String query) {
		DecimalFormat df = new DecimalFormat("0.00000");

		// Reset the number of additions and multiplications
		Service.reset();

		try {
			return df.format(Service.computeProbability(query)) + "," + Service.getComplexity();
		}

		catch (Exception e) {
			Stack<String> samples = Service.getSamples(query);

			// The results of the queries in the sample set
			Queue<Double> results = new LinkedList<>();

			while (!samples.isEmpty()) {
				double result = 0;

				// The given query as a BN formula
				Queue<Queue<String>> formula = Service.getBNFormula(samples.pop());

				while (!formula.isEmpty()) /* Calculate the probability of each part in the formula */ {
					double probability = 1;

					Queue<String> queue = formula.remove();
					while (!queue.isEmpty()) {
						probability = probability * Service.computeProbability(queue.remove());
					}

					result += probability;
				}

				results.add(result);
			}

			return df.format(Service.normalization(results)) + "," + Service.getComplexity();
		}
	}
}
