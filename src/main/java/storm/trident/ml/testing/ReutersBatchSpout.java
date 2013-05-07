package storm.trident.ml.testing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import storm.trident.testing.FixedBatchSpout;
import backtype.storm.tuple.Fields;

public class ReutersBatchSpout extends FixedBatchSpout {

	private static final long serialVersionUID = 2484759216530963284L;

	private final static File REUTEURS_FILE = new File("src/test/resources/reuters.csv");

	private static final List<List<Object>> REUTEURS_TRAINING_SAMPLES = new ArrayList<List<Object>>();
	public static final Map<Integer, String> REUTEURS_EVAL_SAMPLES = new HashMap<Integer, String>();

	static {
		try {
			loadReutersData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public ReutersBatchSpout() {
		super(new Fields("label", "text"), 300, (List<Object>[]) REUTEURS_TRAINING_SAMPLES.toArray(new List[0]));
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static void loadReutersData() throws IOException {
		Map<String, Integer> topics = new HashMap<String, Integer>();

		FileInputStream is = new FileInputStream(REUTEURS_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					// Get class index
					String topic = line.split(",")[0];
					if (!topics.containsKey(topic)) {
						topics.put(topic, topics.size());
					}
					Integer classIndex = topics.get(topic);

					// Get text
					int startIndex = line.indexOf(" - ");
					String text = line.substring(startIndex, line.length() - 1);

					if (REUTEURS_EVAL_SAMPLES.containsKey(classIndex)) {
						REUTEURS_TRAINING_SAMPLES.add((List) Arrays.asList(classIndex, text));
					} else {
						REUTEURS_EVAL_SAMPLES.put(classIndex, text);
					}
				} catch (Exception ex) {
					System.err.println("Skipped Reuters sample because it can't be parsed : " + line);
				}
			}

			Collections.shuffle(REUTEURS_TRAINING_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}

}
