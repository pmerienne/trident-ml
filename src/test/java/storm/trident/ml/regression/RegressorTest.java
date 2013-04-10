package storm.trident.ml.regression;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import storm.trident.ml.testing.data.Sample;

public class RegressorTest {

	private final static File WINE_FILE = new File("src/test/resources/wine.csv");

	private final static List<Sample<Double, Double>> WINE_SAMPLES = new ArrayList<Sample<Double, Double>>();

	static {
		try {
			loadWineData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected double eval(Regressor regressor, List<Sample<Double, Double>> samples, double trainingPercent) {
		int maxTrainingCount = (int) (samples.size() * trainingPercent);
		List<Sample<Double, Double>> training = new ArrayList<Sample<Double, Double>>(samples.subList(0, maxTrainingCount));
		List<Sample<Double, Double>> eval = new ArrayList<Sample<Double, Double>>(samples.subList(maxTrainingCount, samples.size()));

		// Train
		for (Sample<Double, Double> sample : training) {
			regressor.update(sample.label, sample.features);
		}

		// Evaluate
		double rmse = 0.0;
		Double actualPrediction;
		for (Sample<Double, Double> sample : eval) {
			actualPrediction = regressor.predict(sample.features);
			rmse += Math.pow(actualPrediction - sample.label, 2);
			System.out.println("Was : " + sample.label + ", predict : " + actualPrediction + ", error : " + Math.pow(actualPrediction - sample.label, 2));
		}

		return Math.sqrt(rmse / eval.size());
	}

	protected List<Sample<Double, Double>> getWineSamples() {
		return WINE_SAMPLES;
	}

	private static void loadWineData() throws IOException {
		FileInputStream is = new FileInputStream(WINE_FILE);
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		try {
			String line;
			while ((line = br.readLine()) != null) {
				try {
					String[] values = line.split(";");

					Double label = Double.parseDouble(values[11]);
					List<Double> features = new ArrayList<Double>();
					features.add(1.0);
					for (int i = 0; i < 11; i++) {
						features.add(Double.parseDouble(values[i]));
					}

					WINE_SAMPLES.add(new Sample<Double, Double>(label, features));
				} catch (Exception ex) {
					System.out.println("Skipped health sample : " + line);
				}
			}

			Collections.shuffle(WINE_SAMPLES);
		} finally {
			is.close();
			br.close();
		}
	}
}
