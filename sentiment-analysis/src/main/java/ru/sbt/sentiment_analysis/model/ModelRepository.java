package ru.sbt.sentiment_analysis.model;

import ru.sbt.sentiment_analysis.Context;

/*
 * @author loskutyan
 */
public class ModelRepository {

    private final WeightedNgramModel weightedNgramModel;

    public ModelRepository(Context context) {
        this.weightedNgramModel = new WeightedNgramModel(context.getDao().getSentimentModelDao().getNgramsWeights());
    }

    public WeightedNgramModel getWeightedNgramModel() {
        return weightedNgramModel;
    }
}
