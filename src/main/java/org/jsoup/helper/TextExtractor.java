package org.jsoup.helper;

import org.jsoup.nodes.Element;

public interface TextExtractor {
    public String extract(Element element);

    public static final class WholeText implements TextExtractor {
        @Override
        public String extract(Element element) {
            return element.text();
        }
    }

    public static final class OwnText implements TextExtractor {
        @Override
        public String extract(Element element) {
            return element.ownText();
        }
    }
}
