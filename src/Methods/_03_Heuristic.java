package Methods;
import java.util.Collections;
import java.util.List;
import Infrastructure.Node;

/**
 * This class implements heuristic VE algorithm.
 * @author Yahav Karpel
 */

public class _03_Heuristic extends _02_VE implements Sortable {
	@Override
	public void sort(List<Node> HN) {
		Collections.sort(HN);
	}
}
