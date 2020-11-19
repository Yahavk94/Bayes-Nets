package Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Clone {
	/**
	 * This method returns a deep copy of the given list.
	 */
	public static List<String> cloneList(List<String> list) {
		List<String> clone = new ArrayList<>();
		Iterator<String> iterator = list.iterator();

		while (iterator.hasNext()) {
			clone.add(iterator.next());
		}

		return clone;
	}

	/**
	 * This method returns a deep copy of the given map.
	 */
	public static Map<String, String> cloneMap(Map<String, String> map) {
		Map<String, String> clone = new LinkedHashMap<>();
		Iterator<String> iterator = map.keySet().iterator();

		while (iterator.hasNext()) {
			String X = iterator.next();
			clone.put(X, map.get(X));
		}

		return clone;
	}
}
