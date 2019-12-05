package org.jsoup.helper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public abstract class SQLCommand {
    public abstract void execute(Elements elements);

    public abstract static class TextCommand extends SQLCommand {
        protected TextExtractor extractor;

        public TextCommand(TextExtractor extractor) {
            this.extractor = extractor;
        }
    }

    public static final class OrderByTextAscCommand extends TextCommand {
        public OrderByTextAscCommand(TextExtractor extractor) {
            super(extractor);
        }

        @Override
        public void execute(Elements elements) {
            Collections.sort(elements, new Comparator<Element>() {
                @Override
                public int compare(Element e1, Element e2) {
                    String str1 = extractor.extract(e1).trim();
                    String str2 = extractor.extract(e2).trim();

                    if (str1.matches("-?\\d+") && str2.matches("-?\\d+")) {
                        return Integer.compare(Integer.parseInt(str1), Integer.parseInt(str2));
                    } else {
                        return str1.compareTo(str2);
                    }
                }
            });
        }
    }

    public static final class OrderByTextDescCommand extends TextCommand {
        public OrderByTextDescCommand(TextExtractor extractor) {
            super(extractor);
        }

        @Override
        public void execute(Elements elements) {
            Collections.sort(elements, new Comparator<Element>() {
                @Override
                public int compare(Element e1, Element e2) {
                    String str1 = extractor.extract(e1).trim();
                    String str2 = extractor.extract(e2).trim();

                    if (str1.matches("-?\\d+") && str2.matches("-?\\d+")) {
                        return Integer.compare(Integer.parseInt(str2), Integer.parseInt(str1));
                    } else {
                        return str2.compareTo(str1);
                    }
                }
            });
        }
    }

    public static final class StartsWithText extends TextCommand {
        private String prefix;

        public StartsWithText(TextExtractor extractor, String prefix) {
            super(extractor);
            this.prefix = prefix;
        }

        @Override
        public void execute(Elements elements) {
            for(Iterator<Element> it = elements.iterator(); it.hasNext();) {
                Element element = it.next();

                if(!extractor.extract(element).startsWith(this.prefix)) {
                    it.remove();
                }
            }
        }
    }

    public static final class EndsWithText extends TextCommand {
        private String suffix;

        public EndsWithText(TextExtractor extractor, String suffix) {
            super(extractor);
            this.suffix = suffix;
        }

        @Override
        public void execute(Elements elements) {
            for(Iterator<Element> it = elements.iterator(); it.hasNext();) {
                Element element = it.next();

                if(!extractor.extract(element).endsWith(this.suffix)) {
                    it.remove();
                }
            }
        }
    }

    public static final class GTEByText extends TextCommand {
        private int number;

        public GTEByText(TextExtractor extractor, int number) {
            super(extractor);
            this.number = number;
        }

        @Override
        public void execute(Elements elements) {
            for(Iterator<Element> it = elements.iterator(); it.hasNext();) {
                Element element = it.next();

                if(Integer.parseInt(extractor.extract(element)) < number) {
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
