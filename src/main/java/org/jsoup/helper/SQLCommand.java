package org.jsoup.helper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public abstract class SQLCommand {
    public abstract void execute(Elements elements);

    public static final class OrderByTextAscCommand extends SQLCommand {
    public abstract static class TextCommand extends SQLCommand {
        protected TextExtractor extractor;

        public TextCommand(TextExtractor extractor) {
            this.extractor = extractor;
        }
    }

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

    public static final class OrderByTextDescCommand extends SQLCommand {
        @Override
        public void execute(Elements elements) {
            Collections.sort(elements, new Comparator<Element>() {
                @Override
                public int compare(Element e1, Element e2) {
                    return e2.text().compareTo(e1.text());
                }
            });
        }
    }

    public static final class StartsWithText extends SQLCommand {
        private String prefix;

        public StartsWithText(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public void execute(Elements elements) {
            for(Iterator<Element> it = elements.iterator(); it.hasNext();) {
                Element element = it.next();

                if(!element.text().startsWith(this.prefix)) {
                    it.remove();
                }
            }
        }
    }

    public static final class EndsWithText extends SQLCommand {
        private String suffix;

        public EndsWithText(String suffix) {
            this.suffix = suffix;
        }

        @Override
        public void execute(Elements elements) {
            for(Iterator<Element> it = elements.iterator(); it.hasNext();) {
                Element element = it.next();

                if(!element.text().endsWith(this.suffix)) {
                    it.remove();
                }
            }
        }
    }

    public static final class LimitCommand extends SQLCommand {
        private int index;

        private int count;

        public LimitCommand(int count) {
            this.index = 0;
            this.count = count;
        }

        public LimitCommand(int index, int count) {
            this.index = index;
            this.count = count;
        }

        @Override
        public void execute(Elements elements) {
            if (this.index >= elements.size()) {
                elements.clear();
            } else {
                elements.subList(0, this.index).clear();
                elements.subList(Math.min(this.count, elements.size()), elements.size()).clear();
                elements.trimToSize();
            }
        }
    }
}
