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

		// The sample set
		Stack<String> samples = Service.getSamples(query);

		// The results of the queries in the sample set
		Queue<Double> results = new LinkedList<>();

		while (!samples.isEmpty()) {
			String sample = samples.pop();

			// The given query as a BN formula
			Queue<Queue<String>> formula = Service.getBNFormula(sample);

			if (formula == null) {
				return df.format(Service.calculateProbability(sample)) + "," + Service.getComplexity();
			}

			double result = 0;

			while (!formula.isEmpty()) /* Calculate the probability of each part in the formula */ {
				double probability = 1;

				Queue<String> queue = formula.remove();
				while (!queue.isEmpty()) {
					probability = probability * Service.calculateProbability(queue.remove());
				}

				result += probability;
			}

			results.add(result);
		}

		return df.format(Service.normalization(results)) + "," + Service.getComplexity();
	}
}
