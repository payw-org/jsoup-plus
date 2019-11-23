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

Unfortunately it only matches to the exact string `display: block`, while not working with `display : block` or `display:block`.

### Implementation
