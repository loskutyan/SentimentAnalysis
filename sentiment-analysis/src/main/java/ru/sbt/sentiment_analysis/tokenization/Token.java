package ru.sbt.sentiment_analysis.tokenization;

import ru.yandex.bolts.collection.Option;

/*
 * @author loskutyan
 */
public class Token {

    private final String lex;
    private final String text;
    private final Option<String> qual;
    private final Option<String> gr;

    public Token(String lex, String text, String qual, String gr) {
        this.lex = lex;
        this.text = text;
        this.qual = Option.notNull(qual);
        this.gr = Option.notNull(gr);
    }

    public Token(String lex, String text) {
        this.lex = lex;
        this.text = text;
        this.qual = Option.none();
        this.gr = Option.none();
    }

    public String getLex() {
        return lex;
    }

    public String getText() {
        return text;
    }

    public Option<String> getQual() {
        return qual;
    }

    public Option<String> getGr() {
        return gr;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("(lex = %s, ", lex));
        stringBuilder.append(String.format("text = %s, ", text));
        stringBuilder.append(String.format("qual = %s, ", qual.isDefined() ? qual.get() : ""));
        stringBuilder.append(String.format("gr = %s)", gr.isDefined() ? gr.get() : ""));
        return stringBuilder.toString();
    }
}
