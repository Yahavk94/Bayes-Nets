package Methods;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import Tools.Service;

/**
 * This class implements a simple probabilistic inference algorithm.
 * @author Yahav Karpel
 */

public class _01_Simple implements Probable {
	@Override
	public String inference(String query) {
		DecimalFormat df = new DecimalFormat("0.00000");

		int additions = 0;
		int multiplications = 0;

		// The sample set
		Stack<String> samples = Service.getSamples(query);

		// The probabilities of the queries in the sample set
		List<Double> probabilities = new ArrayList<>();

		while (!samples.isEmpty()) {
			String sample = samples.pop();

			// The given query as a BN formula
			List<List<String>> formula = Service.getBNFormula(sample);

			if (formula == null) /* The formula could not be created */ {
				System.out.println(df.format(Service.calculateProbability(sample)) + "," + 0 + "," + 0);
				return df.format(Service.calculateProbability(sample)) + "," + 0 + "," + 0;
			}

			double result = 0;
			additions += formula.size() - 1;

			while (!formula.isEmpty()) /* Calculate the probability of each part in the formula */ {
				double probability = 1;
				multiplications += formula.get(0).size() - 1;

				while (!formula.get(0).isEmpty()) {
					probability = probability * Service.calculateProbability(formula.get(0).remove(0));
				}

				formula.remove(0);
				result += probability;
			}

			probabilities.add(result);
		}

		// Normalization
		double result = probabilities.remove(0);
		double sum = result;
		while (!probabilities.isEmpty()) {
			sum += probabilities.remove(0);
			additions += 1;
		}

		result /= sum;

		System.out.println(df.format(result) + "," + additions + "," + multiplications);
		return df.format(result) + "," + additions + "," + multiplications;
	}
}
