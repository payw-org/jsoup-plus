This page is written and managed by @jhaemin.

## New Features

- [Get elements by inline style properties](#get-elements-by-inline-style-properties)

## Get elements by inline style CSS properties

### Idea

With this feature you can directly find elements with CSS properties inside inline style attribute.

It is already possible with CSS selector implemented in jsoup, for example, if you would like to select `div` tag with a style `display: block` you can simply achieve this by

```Java
element.select("div[style*=\"display: block\"]")
```

Unfortunately it only matches to the exact string `display: block`, while not working with `display : block` or `display:block`. So the codes can easily become fragile and you may not get the results as you expected.

This problem happens because unlike most other attributes, style attribute contains a set of CSS key/value pairs in just a single. So it is reasonable to separate, parse and store them in another form to improve utility.

### Implementation

First of all, we need a new class called `Style` alongside the `style` attribute. This object would have `key` and `value` as its properties. Each of them matches to CSS' key/value. What we're going to do is that when an Element is created with the given attributes, parse style attribute's string (only if style attribute exists to improve performance), and then create `Style` instance with the key/value and store them in an Element property `styles` which is an `ArrayList<Style>`. Now you can find elements with inline styles using `getElementsByInlineStyle()` without writing messy queries.

> Style.java

```Java
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

    ...

}
```

> Element.java

```Java
public class Element extends Node {

    ...

    private ArrayList<Style> styles = null;

    ...

}
```

```Java
public Elements getElementsByInlineStyle(String key, String val) {
    Validate.notEmpty(key);

    Elements results = new Elements();
    Elements children = this.getAllElements();
    for (Integer i = 0; i < children.size(); i += 1) {
        Element child = children.get(i);
        if (child.hasInlineStyles()) {
            for (Integer j = 0; j < child.styles.size(); j += 1) {
                Style style = child.styles.get(j);
                if (style.getKey().equals(key) && style.getValue().equals(val)) {
                    results.add(child);
                }
            }
        }
    }
    return results;
}
```
