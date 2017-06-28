package ru.sbt.sentiment_analysis;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ru.sbt.sentiment_analysis.dao.Dao;

import ru.yandex.jackson.datatype.bolts.BoltsModule;

/*
 * @author loskutyan
 */
public class Context {

    private final ObjectMapper mapper;
    private final Dao dao;

    public Context() throws IOException {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new BoltsModule());
        this.mapper.registerModule(new Jdk8Module());
        this.mapper.registerModule(new JavaTimeModule());
        this.dao = new Dao(mapper);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public Dao getDao() {
        return dao;
    }
}
