package org.jsoup.helper;

import org.jsoup.nodes.Element;

/**
 * Text extractor for Element.
 *
 * @author ihooni, to@ihooni.com
 */
public interface TextExtractor {
    /**
     * Extract text from Element.
     *
     * @param element
     * @return
     */
    public String extract(Element element);

    /**
     * Gets the whole text of the element and all its children.
     */
    public static final class WholeText implements TextExtractor {
        @Override
        public String extract(Element element) {
            return element.text();
        }
    }

    /**
     * Gets the text owned by the element only.
     */
    public static final class OwnText implements TextExtractor {
        @Override
        public String extract(Element element) {
            return element.ownText();
        }
    }
}
