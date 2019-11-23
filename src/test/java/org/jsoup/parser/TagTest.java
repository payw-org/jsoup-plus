package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.jsoup.select.Evaluator;
import org.jsoup.MultiLocaleRule;
import org.jsoup.MultiLocaleRule.MultiLocaleTest;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 Tag tests.
 @author Jonathan Hedley, jonathan@hedley.net */
public class TagTest {
    @Rule public MultiLocaleRule rule = new MultiLocaleRule();

    @Test public void myTest() {
        try {
            Document doc = Jsoup.connect("https://google.com").get();
            Element textarea = doc.getElementsByTag("textarea").get(0);
            String style = textarea.attr("style");
            style = "display:none; text-align: center;margin-top:10px";
            String[] properties = style.split(";");
            System.out.println(style);
            for (Integer i = 0; i < properties.length; i += 1) {
                String[] keyValue = properties[i].split(":");
                String key = keyValue[0].trim();
                String value = keyValue[1].trim();
                System.out.println(key + " : " + value);
            }
        } catch (Exception e) {
            //TODO: handle exception
            System.out.println(e);
        }
    }

    @Test public void iframe() {
        try {
            Document doc = Jsoup.connect("https://naver.com").get();
            Element iframe = doc.getElementsByTag("iframe").get(1);
            // System.out.println(iframe);
            String iframeSrc = iframe.attr("src");

            // System.out.println(iframeSrc.toString());

            Document iframeDoc = Jsoup.connect(iframeSrc).get();
            System.out.println(iframeDoc);
            iframe.appendChild(iframeDoc);

            System.out.println(iframe);

            Element iframeFirstElement = iframe.children().get(0);
            // System.out.println(iframeFirstElement);

            // System.out.println(doc);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test public void booleanAttribute() {
        try {
            Document doc = Jsoup.parse("<div contenteditable=\"\" async=\"what\"></div>");
            System.out.println(doc);
            Attributes attrs = doc.attributes();
            System.out.println(attrs);
            for (Attribute attr : attrs) {
                System.out.println(attr);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Test public void isCaseSensitive() {
        Tag p1 = Tag.valueOf("P");
        Tag p2 = Tag.valueOf("p");
        assertFalse(p1.equals(p2));
    }

    @Test @MultiLocaleTest public void canBeInsensitive() {
        Tag script1 = Tag.valueOf("script", ParseSettings.htmlDefault);
        Tag script2 = Tag.valueOf("SCRIPT", ParseSettings.htmlDefault);
        assertSame(script1, script2);
    }

    @Test public void trims() {
        Tag p1 = Tag.valueOf("p");
        Tag p2 = Tag.valueOf(" p ");
        assertEquals(p1, p2);
    }

    @Test public void equality() {
        Tag p1 = Tag.valueOf("p");
        Tag p2 = Tag.valueOf("p");
        assertTrue(p1.equals(p2));
        assertTrue(p1 == p2);
    }

    @Test public void divSemantics() {
        Tag div = Tag.valueOf("div");

        assertTrue(div.isBlock());
        assertTrue(div.formatAsBlock());
    }

    @Test public void pSemantics() {
        Tag p = Tag.valueOf("p");

        assertTrue(p.isBlock());
        assertFalse(p.formatAsBlock());
    }

    @Test public void imgSemantics() {
        Tag img = Tag.valueOf("img");
        assertTrue(img.isInline());
        assertTrue(img.isSelfClosing());
        assertFalse(img.isBlock());
    }

    @Test public void defaultSemantics() {
        Tag foo = Tag.valueOf("FOO"); // not defined
        Tag foo2 = Tag.valueOf("FOO");

        assertEquals(foo, foo2);
        assertTrue(foo.isInline());
        assertTrue(foo.formatAsBlock());
    }

    @Test(expected = IllegalArgumentException.class) public void valueOfChecksNotNull() {
        Tag.valueOf(null);
    }

    @Test(expected = IllegalArgumentException.class) public void valueOfChecksNotEmpty() {
        Tag.valueOf(" ");
    }
}
