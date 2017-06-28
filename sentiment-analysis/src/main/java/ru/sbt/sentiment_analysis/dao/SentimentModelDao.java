package ru.sbt.sentiment_analysis.dao;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import ru.yandex.bolts.collection.MapF;

/*
 * @author loskutyan
 */
public class SentimentModelDao {

    private final MapF<String, Double> ngramsWeights;

    public SentimentModelDao(ObjectMapper mapper) throws IOException {
        this.ngramsWeights = mapper.readValue(ClassLoader.getSystemResourceAsStream("dict/sentiment_model_weights.json"),
                mapper.getTypeFactory().constructMapType(MapF.class, String.class, Double.class));
    }

    public MapF<String, Double> getNgramsWeights() {
        return ngramsWeights;
    }
}
