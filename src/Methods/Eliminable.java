package Methods;
import java.util.List;
import Infrastructure.Node;

/**
 * This interface represents the VE algorithms.
 * @author Yahav Karpel
 */

public interface Eliminable extends Inferable {
	public void order(List<Node> hidden);
}
