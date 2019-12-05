# JSouffle

It is a forked version of jsoup for research and study. We are going to inspect the design patterns applied to jsoup, enhance them if possible, and extend the features.

## Members

- [@ihooni](https://github.com/ihooni)
- [@jhaemin](https://github.com/jhaemin)
- [@smallfish06](https://github.com/smallfish06)

## Dcoumentation

We're writing member-specific ideas and notes in [docs](https://github.com/ihooni/jsouffle/tree/master/docs). This markdown is aimed to converge and share those ideas in more general form.

> All the contents written inside docs directory have been merged to here.

## Design patterns found in jsoup

This section is written and managed by [@ihooni](https://github.com/ihooni)

> It describe the inspection of all about Jsoup.

### org.jsoup.Jsoup

**Facade**

Provides a unified interface to a set of interfaces in a subsystem. It defines a higher-level interface that makes a subsystem easier to use.

**Why?**

Jsoup core features are available from this class. It depends on many subsystem and also all the elements don't depend on this. See the below comments on this class.

```java
/**
 The core public access point to the jsoup functionality.

 @author Jonathan Hedley */
public class Jsoup {
  ...
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/69477441-c92f7500-0e29-11ea-92bb-64d0c4ce905e.png" width="400" />
</p>

### org.jsoup.internal.ConstrainableInputStream

**Decorator**

Attaches additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.

**Why?**

This class have the same super type as the object it decorate. And `BufferedInputStream`, its parent class, is one of the most famous representative of Decorator pattern. Also we can pass around a decorated object in place of the original(wrapped) object. See the below codes.

```java
private ConstrainableInputStream(InputStream in, ...) {
  super(in, bufferSize);
  ...
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/69499220-3e3aa180-0f33-11ea-8d92-37f7aeeb6f40.png" width="400" />
</p>

### org.jsoup.parser.CharacterReader

**Strategy**

Defines a set of encapsulated algorithms that can be swapped to carry out a specific behavior.

**Why?**

This class use `java.io.Reader` by object composition. The `Reader` is abstract class. And `Reader`'s concrete type is decided dynamically at run-time when `CharacterReader` is initialized. So `CharacterReader` is client and `Reader` is encapsulated algorithm in the strategy pattern.

```java
public final class CharacterReader {
  ...
  private final Reader reader;
  ...
  public CharacterReader(Reader input, int sz) {
    Validate.notNull(input);
    Validate.isTrue(input.markSupported());
    reader = input;
  ...
      final long skipped = reader.skip(pos);
      reader.mark(maxBufferLen);
      final int read = reader.read(charBuf);
      reader.reset();
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/70133717-03223600-16ca-11ea-95ba-7dee8e09f321.png" width="400" />
</p>

### org.jsoup.parser.Parser

**Strategy**

[Same as above.](#orgjsoupparserCharacterReader)

**Why?**

This class use `org.jsoup.parser.TreeBuilder` by object composition. The `TreeBuilder` is abstract class. And `TreeBuilder`'s concrete type is decided dynamically at run-time when `Parser` is initialized or call by `setTreeBuilder` method. So `Parser` is client and `TreeBuilder` is encapsulated algorithm in the strategy pattern.

```java
public class Parser {
  private TreeBuilder treeBuilder;
  ...
  public Parser(TreeBuilder treeBuilder) {
    this.treeBuilder = treeBuilder;
  ...
  public Parser setTreeBuilder(TreeBuilder treeBuilder) {
    this.treeBuilder = treeBuilder;
  ...
  public Document parseInput(String html, String baseUri) {
    return treeBuilder.parse(new StringReader(html), baseUri, this);
  }
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/70135301-24385600-16cd-11ea-9ddf-030eedad9c31.png" width="400" />
</p>

### org.jsoup.select.Collector.Accumulator

**Strategy**

[Same as above.](#orgjsoupparserCharacterReader)

**Why?**

This class use `org.jsoup.select.Evaluator` by object composition. The `Evaluator` is abstract class. And `Evaluator`'s concrete type is decided dynamically at run-time when `Accumulator` is initialized. So `Accumulator` is client and `Evaluator` is encapsulated algorithm in the strategy pattern.

```java
Accumulator(Element root, Elements elements, Evaluator eval) {
  this.root = root;
  this.elements = elements;
  this.eval = eval;
}
...
    if (eval.matches(root, el))
      elements.add(el);
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/70141245-fa396080-16d9-11ea-83ff-79712037319b.png" width="600" />
</p>

### org.jsoup.parser.HtmlTreeBuilder

**State**

Ties object circumstances to its behavior, allowing the object to behave in different ways based upon its internal state.

**Why?**

This class has the member variable `state`, which is `HtmlTreeBuilderState` type. The `HtmlTreeBuilderState` declare abstract method and its subtypes implement this method. And subtypes of `HtmlTreeBuilderState` call `transition` method for transiting to another state.

```java
private HtmlTreeBuilderState state; // the current state
...
protected boolean process(Token token) {
  currentToken = token;
  return this.state.process(token, this);
}
...
void transition(HtmlTreeBuilderState state) {
  this.state = state;
}
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/70142981-ceb87500-16dd-11ea-9dce-a79670c8ad08.png" width="400" />
</p>

### org.jsoup.parser.Tokeniser

**State**

[Same as above.](#orgjsoupparserHtmlTreeBuilder)

**Why?**

This class has the member variable `state`, which is `TokeniserState` type. The `TokeniserState` declare abstract method and its subtypes implement this method. And subtypes of `TokeniserState` call `transition` method for transiting to another state.

```java
private TokeniserState state = TokeniserState.Data; // current tokenisation state
...
Token read() {
  while (!isEmitPending)
    state.read(this, reader);
...
void transition(TokeniserState state) {
  this.state = state;
}
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/70143605-71bdbe80-16df-11ea-80ec-7ea3a0b256d9.png" width="400" />
</p>

## New features

---

Below is the original README.md from jsoup repository.

---

# jsoup: Java HTML Parser

**jsoup** is a Java library for working with real-world HTML. It provides a very convenient API for extracting and manipulating data, using the best of DOM, CSS, and jquery-like methods.

**jsoup** implements the [WHATWG HTML5](http://whatwg.org/html) specification, and parses HTML to the same DOM as modern browsers do.

- scrape and [parse](https://jsoup.org/cookbook/input/parse-document-from-string) HTML from a URL, file, or string
- find and [extract data](https://jsoup.org/cookbook/extracting-data/selector-syntax), using DOM traversal or CSS selectors
- manipulate the [HTML elements](https://jsoup.org/cookbook/modifying-data/set-html), attributes, and text
- [clean](https://jsoup.org/cookbook/cleaning-html/whitelist-sanitizer) user-submitted content against a safe white-list, to prevent XSS attacks
- output tidy HTML

jsoup is designed to deal with all varieties of HTML found in the wild; from pristine and validating, to invalid tag-soup; jsoup will create a sensible parse tree.

See [**jsoup.org**](https://jsoup.org/) for downloads and the full [API documentation](https://jsoup.org/apidocs/).

[![Build Status](https://travis-ci.org/jhy/jsoup.svg?branch=master)](https://travis-ci.org/jhy/jsoup)

## Example

Fetch the [Wikipedia](http://en.wikipedia.org/wiki/Main_Page) homepage, parse it to a [DOM](https://developer.mozilla.org/en-US/docs/Web/API/Document_Object_Model/Introduction), and select the headlines from the _In the News_ section into a list of [Elements](https://jsoup.org/apidocs/index.html?org/jsoup/select/Elements.html):

```java
Document doc = Jsoup.connect("http://en.wikipedia.org/").get();
log(doc.title());
Elements newsHeadlines = doc.select("#mp-itn b a");
for (Element headline : newsHeadlines) {
  log("%s\n\t%s",
    headline.attr("title"), headline.absUrl("href"));
}
```

[Online sample](https://try.jsoup.org/~LGB7rk_atM2roavV0d-czMt3J_g), [full source](https://github.com/jhy/jsoup/blob/master/src/main/java/org/jsoup/examples/Wikipedia.java).

## Open source

jsoup is an open source project distributed under the liberal [MIT license](https://jsoup.org/license). The source code is available at [GitHub](https://github.com/jhy/jsoup/tree/master/src/main/java/org/jsoup).

## Getting started

1. [Download](https://jsoup.org/download) the latest jsoup jar (or add it to your Maven/Gradle build)
2. Read the [cookbook](https://jsoup.org/cookbook/)
3. Enjoy!

## Development and support

If you have any questions on how to use jsoup, or have ideas for future development, please get in touch via the [mailing list](https://jsoup.org/discussion).

If you find any issues, please file a [bug](https://jsoup.org/bugs) after checking for duplicates.

The [colophon](https://jsoup.org/colophon) talks about the history of and tools used to build jsoup.

## Status

jsoup is in general, stable release.
