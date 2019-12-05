package org.jsoup.helper;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public abstract class SQLCommand {
    public abstract void execute(Elements elements);
}
