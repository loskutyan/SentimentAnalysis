package ru.sbt.sentiment_analysis.singleton;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import ru.sbt.sentiment_analysis.Context;
import ru.sbt.sentiment_analysis.model.ModelRepository;
import ru.sbt.sentiment_analysis.model.WeightedNgramModel;
import ru.sbt.sentiment_analysis.stemming.Stemmer;
import ru.sbt.sentiment_analysis.tokenization.TextPreprocessor;
import ru.sbt.sentiment_analysis.tokenization.Token;
import ru.sbt.sentiment_analysis.tokenization.Tokenizer;

import ru.yandex.bolts.collection.Cf;
import ru.yandex.bolts.collection.ListF;
import ru.yandex.bolts.collection.Tuple2;
import ru.yandex.bolts.collection.Tuple2List;

/*
 * @author loskutyan
 */
public class SentimentAnalyzerSingleton {

    private static final ListF<String> NEUTRAL_PATTERNS = Cf.list(
            "взаимодействие незначительное",
            "не пересекались",
            "мало взаимодействия",
            "не взаимодействовал",
            "не было коммуникаций",
            "не было взаимодействия",
            "не было общения",
            "не было контактов"
    );

    private final WeightedNgramModel model;
    private final Stemmer stemmer;
    private final TextPreprocessor preprocessor;

    private SentimentAnalyzerSingleton(String path) throws IOException {
        Context context = new Context();
        ModelRepository modelRepository = new ModelRepository(context);
        this.model = modelRepository.getWeightedNgramModel();
        this.stemmer = new Stemmer(path);
        this.preprocessor = new TextPreprocessor();
    }

    private static String resultConverter(Tuple2<Double, Boolean> result) {
        double coef = result._2 ? 0.0 : result._1;
        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append("{").append("\"class\":")
                .append(coef == 0.0 ? "\"neutral\"" : (coef > 0.0 ? "\"positive\"" : "\"negative\""));
        if (coef != 0.0) {
            resultBuilder.append(",").append("\"confident\":").append(coef);
        }
        resultBuilder.append("}");
        return resultBuilder.toString();
    }

    private static boolean containsNeutralPatterns(String rawText) {
        return NEUTRAL_PATTERNS.map(rawText.replaceAll("\\s+", " ").toLowerCase()::contains).reduceLeft(Boolean::logicalOr);
    }

    public String[] predict(String[] texts) throws IOException, ParseException {
        ListF<String> preprocessedTexts = Cf.list(texts).map(preprocessor::process);
        ListF<ListF<JSONObject>> analyzedTexts = stemmer.analyzeTexts(preprocessedTexts);
        ListF<ListF<Token>> allTokens = analyzedTexts.map(Tokenizer::tokenize);
        ListF<ListF<Tuple2List<String, String>>> allSentences = allTokens.map(Tokenizer::splitTokensToSentences);
        ListF<ListF<String>> normalizedTexts = allSentences.map(sentence -> sentence.map(Tuple2List::get1).flatMap(a -> a));

        ListF<Boolean> isNeutral = preprocessedTexts.map(SentimentAnalyzerSingleton::containsNeutralPatterns);
        ListF<Double> coefficients = normalizedTexts.map(model::getSentiment);
        return coefficients.zip(isNeutral).map(SentimentAnalyzerSingleton::resultConverter).stream().toArray(String[]::new);
    }

    public String predict(String text) throws IOException, ParseException {
        String[] texts = new String[1];
        texts[0] = text;
        return this.predict(texts)[0];
    }

    public static SentimentAnalyzerSingleton getInstance() throws IOException {
        String path = SentimentAnalyzerSingleton.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String decodedPath = URLDecoder.decode(path, "UTF-8");
        File jarFile = new File(decodedPath);
        String jarDir = jarFile.getParent();
        return new SentimentAnalyzerSingleton(jarDir);
    }
}
