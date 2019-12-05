package org.jsoup.helper;

import org.jsoup.select.Elements;

import java.util.ArrayList;

public class SQLish {
    private Elements elements;

    private ArrayList<SQLCommand> commands;

    public SQLish(Elements elements) {
        this.elements = elements;
        this.commands = new ArrayList<>();
    }

    public SQLish orderByTextAsc() {
        this.commands.add(new SQLCommand.OrderByTextAscCommand());
        return this;
    }

    public SQLish orderByTextDesc() {
        this.commands.add(new SQLCommand.OrderByTextDescCommand());
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

    public Elements exec() {
        Elements copyElements = this.elements.clone();

        for (SQLCommand command: this.commands) {
            command.execute(copyElements);
        }

        return copyElements;
    }
}
