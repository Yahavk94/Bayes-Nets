package Methods;
import java.util.List;
import Infrastructure.Node;

/**
 * This interface imposes a total ordering on the hidden nodes of each class that implements it.
 * @author Yahav Karpel
 */

public interface Sortable extends Inferable {
	public void sort(List<Node> HN);
}
