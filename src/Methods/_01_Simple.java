package Methods;
import java.util.ArrayList;
import java.util.List;
import Tools.Service;

/**
 * This class implements a simple probabilistic inference algorithm.
 * @author Yahav Karpel
 */

public class _01_Simple implements Probable {
	@Override
	public String inference(String query) {
		int additions = 0;
		int multiplications = 0;

		// The sample set
		List<String> space = Service.getSampleSet(query);

		// The probabilities of the queries in the sample space
		List<Double> probabilities = new ArrayList<>();

		while (!space.isEmpty()) {
			double result = 0;

			// Represent the given query as a BN formula.
			List<List<String>> formula = Service.getBNFormula(space.remove(0));
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

		System.out.println(result / sum + "," + additions + "," + multiplications);

		return result / sum + "," + additions + "," + multiplications;
	}
}
