package ru.sbt.sentiment_analysis.stemming;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import ru.yandex.bolts.collection.Cf;
import ru.yandex.bolts.collection.ListF;

/*
 * @author loskutyan
 */
public class Stemmer {

    private final ProcessBuilder mystemBuilder;

    public Stemmer(String path) {
        String mystemPath = Paths.get(path, "mystem").toString();
        File dir = new File(Paths.get(mystemPath).getParent().toString());
        this.mystemBuilder = new ProcessBuilder()
                .command(mystemPath, "-igdc", "--format", "json")
                .directory(dir);
    }

    private int writeOneLineTexts(BufferedWriter writer, ListF<String> texts) throws IOException {
        int notEmptyTexts = 0;

        for (String text : texts) {
            String oneLineText = text.replaceAll("\n", " ") + "\n";
            writer.write(oneLineText);
            notEmptyTexts += 1;
        }
        return notEmptyTexts;
    }

    private ListF<ListF<JSONObject>> parseJsonArrays(BufferedReader reader) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        ListF<ListF<JSONObject>> result = Cf.arrayList();

        String line;
        while ((line = reader.readLine()) != null) {
            ListF<JSONObject> analyzedTextJson = Cf.arrayList();
            JSONArray jsonArray = (JSONArray) parser.parse(line);
            for (int i = 0; i < jsonArray.size(); ++i) {
                analyzedTextJson.add((JSONObject) jsonArray.get(i));
            }
            result.add(analyzedTextJson);
        }
        return result;
    }

    public ListF<ListF<JSONObject>> analyzeTexts(ListF<String> texts) throws IOException, ParseException {
        Process mystem = mystemBuilder.start();
        InputStream stdout = mystem.getInputStream();
        OutputStream stdin = mystem.getOutputStream();
        InputStream stderr = mystem.getErrorStream();

        BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stdin));

        int notEmptyTexts = writeOneLineTexts(writer, texts);
        writer.close();

        ListF<ListF<JSONObject>> result = parseJsonArrays(reader);
        reader.close();

        if (result.length() != notEmptyTexts) {
            throw new IllegalArgumentException("Some unknown newline symbols");
        }

        mystem.destroy();
        return result;
    }
}
