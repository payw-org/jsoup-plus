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
}
