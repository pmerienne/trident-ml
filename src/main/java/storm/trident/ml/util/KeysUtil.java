package storm.trident.ml.util;

import java.util.Arrays;
import java.util.List;

public class KeysUtil {

	@SuppressWarnings("unchecked")
	public static List<List<Object>> toKeys(Object singleKey) {
		List<List<Object>> keys = Arrays.asList(Arrays.asList(singleKey));
		return keys;
	}
}
