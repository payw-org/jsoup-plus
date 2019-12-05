package org.jsoup.nodes;

import org.jsoup.helper.Validate;

import java.util.Map;

public class Style implements Map.Entry<String, String>, Cloneable {
    private String key;
    private String val;

    public Style(String key, String val) {
        Validate.notNull(key);
        key = key.trim().toLowerCase();
        Validate.notEmpty(key);
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public String setKey(String key) {
        this.key = key;
        return this.key;
    }

    public String getValue() {
        return val;
    }

    public String setValue(String val) {
        this.val = val;
        return this.val;
    }
}
