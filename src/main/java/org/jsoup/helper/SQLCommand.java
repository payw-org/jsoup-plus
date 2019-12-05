package org.jsoup.helper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public abstract class SQLCommand {
    public abstract void execute(Elements elements);

    public static final class OrderByTextAscCommand extends SQLCommand {
        @Override
        public void execute(Elements elements) {
            Collections.sort(elements, new Comparator<Element>() {
                @Override
                public int compare(Element e1, Element e2) {
                    return e1.text().compareTo(e2.text());
                }
            });
        }
    }
}
