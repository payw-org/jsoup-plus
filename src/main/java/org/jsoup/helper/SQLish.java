package org.jsoup.helper;

import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * The core public access point to the SQLish functionality.
 *
 * @author ihooni, to@ihooni.com
 */
public class SQLish {
    private Elements elements;  // SQL target elements

    private TextExtractor extractor;    // text extractor from element

    private ArrayList<SQLCommand> commands; // sql command list

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

    /**
     * Change extractor dynamically at run-time.
     *
     * @param extractor
     */
    public void setExtractor(TextExtractor extractor) {
        this.extractor = extractor;
    }

    /**
     * Sort elements as ascending order of its text.
     *
     * @return
     */
    public SQLish orderByTextAsc() {
        this.commands.add(new SQLCommand.OrderByTextAscCommand(this.extractor));
        return this;
    }

    /**
     * Sort elements as descending order of its text.
     *
     * @return
     */
    public SQLish orderByTextDesc() {
        this.commands.add(new SQLCommand.OrderByTextDescCommand(this.extractor));
        return this;
    }

    /**
     * Get the only elements which are starts with the specified prefix.
     *
     * @param prefix
     * @return
     */
    public SQLish startsWithText(String prefix) {
        this.commands.add(new SQLCommand.StartsWithText(this.extractor, prefix));
        return this;
    }

    /**
     * Get the only elements which are ends with the specified suffix.
     *
     * @param suffix
     * @return
     */
    public SQLish endsWithText(String suffix) {
        this.commands.add(new SQLCommand.EndsWithText(this.extractor, suffix));
        return this;
    }

    /**
     * Get the only elements which text integer are greater than or equal to specified number.
     *
     * @param number
     * @return
     */
    public SQLish gteByText(int number) {
        this.commands.add(new SQLCommand.GTEByText(this.extractor, number));
        return this;
    }

    /**
     * Get the only elements which text integer are less than or equal to specified number.
     *
     * @param number
     * @return
     */
    public SQLish lteByText(int number) {
        this.commands.add(new SQLCommand.LTEByText(this.extractor, number));
        return this;
    }

    /**
     * Returns the portion of these elements.
     *
     * @param count
     * @return
     */
    public SQLish limit(int count) {
        this.commands.add(new SQLCommand.LimitCommand(count));
        return this;
    }

    /**
     * Returns the portion of these elements.
     *
     * @param index
     * @param count
     * @return
     */
    public SQLish limit(int index, int count){
        this.commands.add(new SQLCommand.LimitCommand(index, count));
        return this;
    }

    /**
     * Flush all SQL commands.
     *
     * @return
     */
    public SQLish flushQueries() {
        this.commands.clear();
        return this;
    }

    /**
     * Pop the last SQL command.
     *
     * @return
     */
    public SQLish popQuery() {
        this.commands.remove(this.commands.size() - 1);
        return this;
    }

    /**
     * Execute all SQL commands.
     *
     * @return
     */
    public Elements exec() {
        Elements copyElements = this.elements.clone();

        for (SQLCommand command: this.commands) {
            command.execute(copyElements);
        }

        return copyElements;
    }
}
