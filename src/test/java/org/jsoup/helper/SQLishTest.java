package org.jsoup.helper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SQLishTest {
    private String ref =
            "<p>hello <span>mango</span></p>" +
            "<p>hello <span>ironman</span></p>" +
            "<p>hello <span>nobody</span></p>" +
            "<p>hello <span>food</span></p>" +
            "<p>hello <span>programmer</span></p>" +
            "<p>hello <span>love</span></p>" +
            "<p>hello <span>ice</span></p>" +
            "<p>hello <span>apple</span></p>" +
            "<p>hello <span>human</span></p>" +
            "<p>hello <span>zoo</span></p>" +
            "<p>hello <span>solo</span></p>" +
            "<p>hello <span>banana</span></p>" +
            "<p>hello <span>melon</span></p>";

    private String[] orderedStrings = {
            "hello apple",
            "hello banana",
            "hello food",
            "hello human",
            "hello ice",
            "hello ironman",
            "hello love",
            "hello mango",
            "hello melon",
            "hello nobody",
            "hello programmer",
            "hello solo",
            "hello zoo"
    };

    @Test
    public void orderByTextAsc() {
        Document doc = Jsoup.parse(this.ref);
        List<String> ascStrings = Arrays.asList(orderedStrings);

        Elements ascElements = new SQLish(doc.select("p")).orderByTextAsc().exec();
        for (int i = 0; i < ascElements.size(); i++) {
            assertEquals(ascElements.get(i).text(), ascStrings.get(i));
        }
    }

    @Test
    public void orderByTextDesc() {
        Document doc = Jsoup.parse(this.ref);
        List<String> descStrings = Arrays.asList(orderedStrings);
        Collections.reverse(descStrings);

        Elements descElements = new SQLish(doc.select("p")).orderByTextDesc().exec();
        for (int i = 0; i < descElements.size(); i++) {
            assertEquals(descElements.get(i).text(), descStrings.get(i));
        }
    }

    @Test
    public void limit() {
        Document doc = Jsoup.parse(this.ref);
        List<String> ascStrings = Arrays.asList(orderedStrings);
        List<String> expected1 = ascStrings.subList(3, 8);
        List<String> expected2 = ascStrings.subList(3, 5);

        SQLish sql = new SQLish(doc.select("p")).orderByTextAsc();

        Elements elements1 = sql.limit(3, 5).exec();
        assertEquals(elements1.size(), expected1.size());
        for (int i = 0; i < elements1.size(); i++) {
            assertEquals(elements1.get(i).text(), expected1.get(i));
        }

        Elements elements2 = sql.limit(2).exec();
        assertEquals(elements2.size(), expected2.size());
        for (int i = 0; i < elements2.size(); i++) {
            assertEquals(elements2.get(i).text(), expected2.get(i));
        }
    }

    @Test
    public void flushQueries() {
        Document doc = Jsoup.parse(this.ref);

        Elements elements1 = new SQLish(doc.select("p")).exec();
        Elements elements2 = new SQLish(doc.select("p")).orderByTextAsc().limit(3).flushQueries().exec();

        assertEquals(elements1.size(), elements2.size());
        for (int i = 0; i < elements1.size(); i++) {
            assertEquals(elements1.get(i).text(), elements2.get(i).text());
        }
    }

    @Test
    public void popQuery() {
        Document doc = Jsoup.parse(this.ref);

        Elements elements1 = new SQLish(doc.select("p")).orderByTextAsc().exec();
        Elements elements2 = new SQLish(doc.select("p")).orderByTextAsc().limit(3).popQuery().exec();

        assertEquals(elements1.size(), elements2.size());
        for (int i = 0; i < elements1.size(); i++) {
            assertEquals(elements1.get(i).text(), elements2.get(i).text());
        }
    }
}
