package ru.sbt.sentiment_analysis.model;

import ru.yandex.bolts.collection.Cf;
import ru.yandex.bolts.collection.ListF;
import ru.yandex.bolts.collection.MapF;

/*
 * @author loskutyan
 */
public class WeightedNgramModel {

    private static final double MIN_NEUTRAL_WEIGHT = -4.38;
    private static final double MAX_NEUTRAL_WEIGHT = 5.05;

    private final MapF<String, Double> ngramWeights;

    public WeightedNgramModel(MapF<String, Double> ngramWeights) {
        this.ngramWeights = ngramWeights;
    }

    private MapF<String, Integer> countOneTwoGrams(ListF<String> words) {
        MapF<String, Integer> ngramsCounter = Cf.hashMap();
        words.forEach(onegram -> ngramsCounter.compute(onegram, (k, v) -> v == null ? 1 : v + 1));
        if (words.length() < 2) {
            return ngramsCounter;
        }
        Cf.range(0, words.length() - 1)
                .map(i -> String.format("%s %s", words.get(i), words.get(i + 1)))
                .forEach(twogram -> ngramsCounter.compute(twogram, (k, v) -> v == null ? 1 : v + 1));
        return ngramsCounter;
    }

    public double getSentiment(ListF<String> normalizedText) {
        MapF<String, Integer> oneTwoGramsCounter = countOneTwoGrams(normalizedText);
        double result = ngramWeights.entries()
                .map1(ngram -> oneTwoGramsCounter.getOrElse(ngram, 0))
                .map(tup -> tup._1.doubleValue() * tup._2)
                .foldLeft(0.0, Double::sum);
        if (result > MIN_NEUTRAL_WEIGHT && result < MAX_NEUTRAL_WEIGHT) {
            return 0.0;
        }
        return result;
    }
}
