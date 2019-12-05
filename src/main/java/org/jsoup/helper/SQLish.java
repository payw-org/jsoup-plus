package org.jsoup.helper;

import org.jsoup.select.Elements;

import java.util.ArrayList;

public class SQLish {
    private Elements elements;

    private TextExtractor extractor;

    private ArrayList<SQLCommand> commands;

    public SQLish(Elements elements) {
        this.elements = elements;
        this.extractor = new TextExtractor.WholeText();
        this.commands = new ArrayList<>();
    }

    public SQLish(Elements elements, TextExtractor extractor) {
        this.elements = elements;
        this.extractor = extractor;
        this.commands = new ArrayList<>();
    }

    public void setExtractor(TextExtractor extractor) {
        this.extractor = extractor;
    }

    public SQLish orderByTextAsc() {
        this.commands.add(new SQLCommand.OrderByTextAscCommand(this.extractor));
        return this;
    }

    public SQLish orderByTextDesc() {
        this.commands.add(new SQLCommand.OrderByTextDescCommand(this.extractor));
        return this;
    }

    public SQLish startsWithText(String prefix) {
        this.commands.add(new SQLCommand.StartsWithText(this.extractor, prefix));
        return this;
    }

    public SQLish endsWithText(String suffix) {
        this.commands.add(new SQLCommand.EndsWithText(this.extractor, suffix));
        return this;
    }

    public SQLish gteByText(int number) {
        this.commands.add(new SQLCommand.GTEByText(this.extractor, number));
        return this;
    }

    public SQLish lteByText(int number) {
        this.commands.add(new SQLCommand.LTEByText(this.extractor, number));
        return this;
    }

    public SQLish limit(int count) {
        this.commands.add(new SQLCommand.LimitCommand(count));
        return this;
    }

    public SQLish limit(int index, int count){
        this.commands.add(new SQLCommand.LimitCommand(index, count));
        return this;
    }

    public SQLish flushQueries() {
        this.commands.clear();
        return this;
    }

    public SQLish popQuery() {
        this.commands.remove(this.commands.size() - 1);
        return this;
    }

    public Elements exec() {
        Elements copyElements = this.elements.clone();

        for (SQLCommand command: this.commands) {
            command.execute(copyElements);
        }

        return copyElements;
    }
}
