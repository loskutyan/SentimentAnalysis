package ru.sbt.sentiment_analysis.dao.importing;

/*
 * @author loskutyan
 */
public class Import {

    public static void main(String[] args) throws Exception {
        for (String arg : args) {
            System.out.println(arg);
        }
        switch (args[0]) {
            case "sentiment_model_weights":
                new SentimentModelWeightsImporter().read(args[1]);
                break;
            default:
                throw new IllegalArgumentException("Uknown dict: " + args[0]);
        }
    }
}
