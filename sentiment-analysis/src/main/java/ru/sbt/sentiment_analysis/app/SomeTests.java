package ru.sbt.sentiment_analysis.app;

import java.io.IOException;

import org.json.simple.parser.ParseException;
import ru.sbt.sentiment_analysis.singleton.SentimentAnalyzerSingleton;

/*
 * @author loskutyan
 */
public class SomeTests {

    public static void main(String[] args) throws ParseException, IOException {

        String testOutput = args[0];
//        String s = "мужчину, который будет обеспечивать меня? Добрый день. Я поняла, что когда выйду замуж - работать не хочу. Хочу заниматься семьей, варить борщи, заниматься домом и детьми. Но где найти такого мужика, чтоб обеспечивал? С кем не познакомлюсь, все хотят или напополам (что ещё конечно приемлемый вариант, но это на крайний случай уж), или чтоб женщина после официальной работы ещё вставала во вторую смену стирать/готовить/убирать/детьми заниматься. Объясняется всё это тем что это женские обязанности, что всегда женщины этим занимались и типо у него это всё мама делает же. Я согласна их выполнять, но чтоб тогда я не работала на работе. Я не готова взвалить на себя и то и другое. Вот где и как найти себе мужчину, хотящего комфорта и уюта в семье и при этом, не заставляющего жену работать??Почему российские мужики не умеют обеспечивать свою семью? Почему женщина должна как конь впрячься и в работу и в быт и тянуть эту лямку, потому что мужчина этого сделать не в состоянии. даже деньги зарабатывать, ему приходится помогать. Где найти нормального мужчину?\n" +
//                "Здравствуйте. Пишу сюда чтобы выговориться. Может, станет легче и кто что посоветует. Муж у меня работает по вахте 15/15. Сегодня приехал домой. Подготовились ко сну, уложили ребенка спать, повздорили, т.к я ему рассказывала кое что о планах на завтра, он прослушал, меня это взбесило, любовь-секс все дела. На часах 12 ночи, муж говорит я схожу покурю. Ну думаю ок. Просыпаюсь от шума теливизора одна. Его нет. Спускаюсь вниз (на 1 этаже баня и комната отдыха, сверху вот 2 комнаты, кухня и прочее где мы живем) иду в соседний дом родителей там он сидит с моим братом которому сегодня в 3 на сессию ехать лясы точит. Так меня это взбесило просто сил нет. Выкинула ему постельное белье в коридор и сказала что пускай веселится сколько хочет и спит как хочет если не хочет спать со мной. Просто достало. Ждешь по 15 а то и 18 дней, спишь одна в холодной постели. Потом хвала небесам приедет Его Величество Муж и начинается вот это вот. То он в бане сидит парится, пиво хлещет а тож на работе совсем не пьет, то с братом общается и я молодая женщина засыпаю одна буквально каждую ночь, а мне всего 27. И я думаю нахрена мне такая жизнь? Так меня одолело это бесконечное ожидание Его Величество Мужа. Иногда думаю что лучше вообще быть одной. Т.к по сути я итак одна.";
        String[] test_strings = new String[]{
                "Спасибо, все хорошо, мне все понравилось",
                "Общение получилось не очень продуктивным",
                "К сожалению, ничего не получилось",
                "Узнал много нового",
                "Хотелось бы больше конкретики",
                "Хороший доклад, но хотелось бы больше конкретики",
                "Хороший доклад",
                "результат не получили"
        };

        SentimentAnalyzerSingleton singleton = SentimentAnalyzerSingleton.getInstance();
        String[] predictions = singleton.predict(test_strings);
        for (int i = 0; i < predictions.length; ++i) {
            System.out.println(test_strings[i] + '\t' + predictions[i]);
        }

//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(testOutput)));
//        writer.write(singleton.predict(s));
//        writer.close();
    }
}
