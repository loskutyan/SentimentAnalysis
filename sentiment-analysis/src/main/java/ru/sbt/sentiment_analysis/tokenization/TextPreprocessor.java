package ru.sbt.sentiment_analysis.tokenization;

import ru.yandex.bolts.collection.Cf;
import ru.yandex.bolts.collection.MapF;
import ru.yandex.bolts.collection.Tuple2;
import ru.yandex.bolts.collection.Tuple2List;

/*
 * @author loskutyan
 */
public class TextPreprocessor {

    private final static MapF<String, String> SMILES = Cf.map(Tuple2List.fromPairs(
            "\\:\\)", "спасибо спасибо спасибо спасибо спасибо спасибо",
            "\\;\\)", "спасибо спасибо спасибо спасибо спасибо спасибо",
            "\\:\\(", "необходимо необходимо необходимо необходимо необходимо"
    ));

    private String replaceSmiles(String text) {
        String resultText = text;
        for (Tuple2<String, String> tup : SMILES.entries()) {
            resultText = resultText.replaceAll(tup._1, tup._2);
        }
        return resultText;
    }

    public String process(String text) {
        return replaceSmiles(text);
    }
}
