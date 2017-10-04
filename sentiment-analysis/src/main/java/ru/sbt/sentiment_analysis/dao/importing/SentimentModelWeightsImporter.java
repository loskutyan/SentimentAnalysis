package ru.sbt.sentiment_analysis.dao.importing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.yandex.bolts.collection.Cf;
import ru.yandex.bolts.collection.ListF;
import ru.yandex.bolts.collection.MapF;
import ru.yandex.bolts.collection.Tuple2;
import ru.yandex.bolts.collection.Tuple2List;
import ru.yandex.jackson.datatype.bolts.BoltsModule;

/*
 * @author loskutyan
 */
public class SentimentModelWeightsImporter {

    private static final double NEGATIVE_COEFFICIENT = -1.0;
    private static final String SEPARATOR = ";";

    public void read(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new BoltsModule());

        Tuple2List<String, Double> positiveNgramsWeights = Tuple2List.arrayList();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(
                "raw/positive.csv"))))
        {
            String positivesHeader = bufferedReader.readLine();
            Tuple2<Integer, Integer> positiveWordAndWeightPositions = getWordAndWeightPositions(positivesHeader);

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                String[] tokens = line.split(SEPARATOR);
                String ngram = tokens[positiveWordAndWeightPositions._1];
                double weight = Double.valueOf(tokens[positiveWordAndWeightPositions._2]);
                positiveNgramsWeights.add(ngram, weight);
            }
        }

        Tuple2List<String, Double> negativeNgramsWeights = Tuple2List.arrayList();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(
                "raw/negative.csv"))))
        {
            String negativesHeader = bufferedReader.readLine();
            Tuple2<Integer, Integer> negativesHeaderWordAndWeightPositions = getWordAndWeightPositions(negativesHeader);

            while (bufferedReader.ready()) {
                String line = bufferedReader.readLine();
                String[] tokens = line.split(SEPARATOR);
                String ngram = tokens[negativesHeaderWordAndWeightPositions._1];
                double weight = Double.valueOf(tokens[negativesHeaderWordAndWeightPositions._2]);
                negativeNgramsWeights.add(ngram, weight * NEGATIVE_COEFFICIENT);
            }
        }
        MapF<String, Double> ngramsWeights = positiveNgramsWeights.toMap();
        ngramsWeights.putAll(negativeNgramsWeights.filterNot(tup -> ngramsWeights.containsKeyTs(tup._1)));
        mapper.writeValue(Paths.get(path, "sentiment_model_weights.json").toFile(), ngramsWeights);
    }

    private Tuple2<Integer, Integer> getWordAndWeightPositions(String header) {
        ListF<String> headerTokens = Cf.list(header.split(SEPARATOR));
        int wordPosition = headerTokens.indexOfTs("word");
        int weightPosition = headerTokens.indexOfTs("weight");
        return Tuple2.tuple(wordPosition, weightPosition);
    }
}
