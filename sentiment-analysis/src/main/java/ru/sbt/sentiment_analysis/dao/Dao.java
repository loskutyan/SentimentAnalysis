package ru.sbt.sentiment_analysis.dao;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * @author loskutyan
 */
public class Dao {

    private final SentimentModelDao sentimentModelDao;

    public Dao(ObjectMapper mapper) throws IOException {
        this.sentimentModelDao = new SentimentModelDao(mapper);
    }

    public SentimentModelDao getSentimentModelDao() {
        return sentimentModelDao;
    }
}
