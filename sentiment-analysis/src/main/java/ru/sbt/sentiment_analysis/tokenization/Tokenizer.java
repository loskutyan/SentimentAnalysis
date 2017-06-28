package ru.sbt.sentiment_analysis.tokenization;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import ru.yandex.bolts.collection.Cf;
import ru.yandex.bolts.collection.ListF;
import ru.yandex.bolts.collection.SetF;
import ru.yandex.bolts.collection.Tuple2;
import ru.yandex.bolts.collection.Tuple2List;

/*
 * @author loskutyan
 */
public final class Tokenizer {

    private Tokenizer() {}

    private static final String SENTENCE_DIVIDER = "^[\\.+|!+|?+|\\)+|\\(+]\\s*";
    private static final String ILLEGAL_CHARACTERS_RE = "[\\000-\\007]|[\\013-\\014]|[\\016-\\037]";

    private static final SetF<String> STOP_WORDS = Cf.set();
    private static final SetF<String> WHITE_LIST = Cf.hashSet(
            "вкс",
            "ца",
            "it",
            "crm",
            "sap",
            "сбт",
            "всп",
            "service",
            "sd",
            "outlook",
            "кэ",
            "sm",
            "manager",
            "hr",
            "bankhelper",
            "знд",
            "desk",
            "cio",
            "госб",
            "срм",
            "web",
            "ip",
            "sla",
            "helper",
            "bank",
            "пр",
            "мсц",
            "alpha",
            "email",
            "excel",
            "cib",
            "отп",
            "осб",
            "ariadna",
            "бкп",
            "тд",
            "лвс",
            "нпз",
            "ms",
            "android",
            "иб",
            "doctrix",
            "всочл",
            "знр",
            "office",
            "helpdesk",
            "bhelper",
            "transact",
            "sberbank",
            "kpi",
            "сд",
            "id",
            "mail",
            "microsoft",
            "тп",
            "зп",
            "rtf",
            "itsm",
            "уб",
            "внд",
            "уп",
            "word",
            "sigma",
            "citrix",
            "internet",
            "omega",
            "тб",
            "логин",
            "сервис",
            "хантер","рабочие","рабочий","место","аиб","домен","пара",
            "старый","страница","почтовый","банковский","тригер","ссылка",
            "разный","выбор","старший","да","не","нет","зайти","последний",
            "перенос","чек","шаг","пункт","первый","цифра","новый","вид",
            "почта","половина","филиал","момент","путь","штатный","окошко",
            "контур","цель","сторона","минута","фабрика","слово","пароль","зона",
            "пул","закрытый","отдел","список","смена","бюро","роль","жалоба",
            "почта","урок","линия","работник","критерий","средний","зао","надо",
            "граф","графа","строка","ооо","звонок","ас","ранний","хост","логинов",
            "низкий","чужой","копия","группа","переход","номер","работа","база",
            "поля","риски","договоры","близкий","идея","долгая","идея","фактор",
            "иногда","поле","лист","договор","тэг","файл","риск","потеря","верный",
            "блок","символ","дата","лог","авто","залог","начальник","тысяч","код",
            "свободная","модуль","среда","тверская","год","время","третий","лицо",
            "защита","глобальный","отдельный","общий","центр","москва","россия",
            "настоящий","причина","сервер","неверный","ящик","конец","лист","сектор","высокий"
    );

    private static Token getToken(JSONObject object) {
        JSONArray analysis = (JSONArray) object.get("analysis");
        String text = (String) object.get("text");
        if (analysis == null || analysis.size() == 0) {
            return new Token(text.toLowerCase().replaceAll("[0-9]", "9"), text);
        }
        JSONObject analysisMap = (JSONObject) analysis.get(0);
        String lex = (String) analysisMap.get("lex");
        String gr = (String) analysisMap.get("gr");
        String qual = (String) analysisMap.get("qual");
        return new Token(lex, text, qual, gr);
    }

    public static ListF<Token> tokenize(ListF<JSONObject> analyzedText) {
        ListF<Token> tokens = analyzedText.map(Tokenizer::getToken);
        ListF<Token> abbrTokensStack = Cf.arrayList();
        ListF<Token> result = Cf.arrayList();
        for (int i = 0; i + 1 < tokens.length(); ++i) {
            Token token = tokens.get(i);
            if (token.getGr().isDefined() && token.getGr().get().contains("сокр")) {
                abbrTokensStack.add(token);
            } else {
                if (abbrTokensStack.isNotEmpty()) {
                    if (token.getLex().trim().equals(".")) {
                        result.add(abbrTokensStack.first());
                        abbrTokensStack.clear();
                    } else {
                        result.add(abbrTokensStack.first());
                        result.add(token);
                        abbrTokensStack.clear();
                    }
                } else {
                    result.add(token);
                }
            }
        }
        return result;
    }

    public static ListF<Tuple2List<String, String>> splitTokensToSentences(ListF<Token> tokens) {
        ListF<Tuple2List<String, String>> result = Cf.arrayList();
        Tuple2List<String, String> currentSentence = Tuple2List.arrayList();
        for (Token token : tokens) {
            if (token.getLex().matches(SENTENCE_DIVIDER)) {
                result.add(Tuple2List.tuple2List(currentSentence));
                currentSentence.clear();
            }
            if ((token.getGr().isDefined() || WHITE_LIST.contains(token.getLex()))
                    && !STOP_WORDS.contains(token.getLex())) {
                if (currentSentence.isNotEmpty() && currentSentence.last()._1.equals("не")) {
                    currentSentence.set(currentSentence.length() - 1, Tuple2.tuple(
                            "не" + token.getLex(), "не" + token.getText().replaceAll(ILLEGAL_CHARACTERS_RE, "").trim().toLowerCase()
                    ));
                } else {
                    currentSentence.add(token.getLex(), token.getText().replaceAll(ILLEGAL_CHARACTERS_RE, "").trim().toLowerCase());
                }
            }
        }
        result.add(currentSentence);
        return result;
    }

    private static int countAbbreviations(ListF<Token> tokens) {
        return tokens.filter(token -> token.getGr().getOrElse("").toLowerCase().contains("сокр")).length();
    }
}
