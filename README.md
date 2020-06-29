![github-hero](https://user-images.githubusercontent.com/19797697/70528724-382efc80-1b92-11ea-9fc1-9eef7213edd9.png)

It is a forked version of jsoup for research and study. We are going to inspect the design patterns applied to jsoup, enhance them if possible, and extend the features. Every development process or discussion history will be recorded in this document.

**Manifesto**

We're trying to focus on **jsoup** itself. It is a powerful HTML parser, not a headless browser, web driver or anything else like those. So the extended features are mostly binded to what it does well. And the author of this software currently works at Amazon. It means and it is obvious that the code structure or the code quality are so good that we cannot easily assert our opinion about them.

**Postscript**

We've learnt a lot of things from this project. We found out that design patterns are not always being used, and sometimes in many different ways. Also it was really hard to inspect and extend source codes written by other programmers.

## Members

- [@ihooni](https://github.com/ihooni)
- [@jhaemin](https://github.com/jhaemin)
- [@smallfish06](https://github.com/smallfish06)

## Documentation (deprecated)

~~We're writing member-specific ideas and notes in [docs](https://github.com/payw-org/jsoup-plus/tree/master/docs). This markdown is aimed to converge and share those ideas in more general form.~~

> ⚠️ All the contents written inside docs directory have been merged to here.

## Design patterns found in jsoup

- [org.jsoup.Jsoup](#orgjsoupJsoup)
- [org.jsoup.internal.ConstrainableInputStream](#orgjsoupinternalConstrainableInputStream)
- [org.jsoup.parser.CharacterReader](#orgjsoupparserCharacterReader)
- [org.jsoup.parser.Parser](#orgjsoupparserParser)
- [org.jsoup.select.Collector.Accumulator](#orgjsoupselectCollectorAccumulator)
- [org.jsoup.parser.HtmlTreeBuilder](#orgjsoupparserHtmlTreeBuilder)
- [org.jsoup.parser.Tokeniser](#orgjsoupparserTokeniser)
- [org.jsoup.nodes.Node](#orgjsoupnodesNode)
- [org.jsoup.select.NodeVisitor](#orgjsoupselectNodeVisitor)

### org.jsoup.Jsoup

**Facade**

Provides a unified interface to a set of interfaces in a subsystem. It defines a higher-level interface that makes a subsystem easier to use.

**Why?**

Jsoup core features are available from this class. It depends on many subsystem and also all the elements don't depend on this. See the below comments on this class.

| Role   | Class                                                                                          |
| ------ | ---------------------------------------------------------------------------------------------- |
| Facade | [Jsoup](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/Jsoup.java) |

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

| Role              | Class                                                                                                                                         |
| ----------------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| Component         | [InputStream]()                                                                                                                               |
| ConcreteDecorator | [ConstrainableInputStream](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/internal/ConstrainableInputStream.java) |

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

| Role             | Class                                                                                                                     |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------- |
| Context          | [CharacterReader](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/CharacterReader.java) |
| Strategy         | Reader                                                                                                                    |
| ConcreteStrategy | StringReader BufferedReader                                                                                               |

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

| Role             | Class                                                                                                                                                                                                                                             |
| ---------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Context          | [Parser](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/CharacterReader.java)                                                                                                                                  |
| Strategy         | [TreeBuilder](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/TreeBuilder.java)                                                                                                                                 |
| ConcreteStrategy | [HtmlTreeBuilder](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/HtmlTreeBuilder.java) [XmlTreeBuilder](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/XmlTreeBuilder.java) |

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

| Role             | Class                                                                                                           |
| ---------------- | --------------------------------------------------------------------------------------------------------------- |
| Context          | [Accumulator](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/select/Collector.java) |
| Strategy         | [Evaluator](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/select/Evaluator.java)   |
| ConcreteStrategy | Check out the screenshot below                                                                                  |

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

| Role          | Class                                                                                                                               |
| ------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| Context       | [HtmlTreeBuilder](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/HtmlTreeBuilder.java)           |
| State         | [HtmlTreeBuilderState](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/HtmlTreeBuilderState.java) |
| ConcreteState | [Many nested states](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/HtmlTreeBuilderState.java)   |

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

**Builder**

Builder pattern allow for dynamic creation of objects based upon easily interchangeable algorithms. It is used when runtime control over the creation process is required and the addition of new creation functionality without changing the core code is necessary.

There are director, builder and concrete builder in this pattern. Director knows what parts are needed for the final product. And concrete builder knows how to produce the part and add it to the final product.

In jsoup, a Parser parses the HTML with an HtmlTreeBuilder which extends an abstract class TreeBuilder. Then it returns a Document which is a product of the builder.

| Role             | Class                                                                                                                                                                                                                                              |
| ---------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Director         | [Parser](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/Parser.java)                                                                                                                                            |
| Builder          | [TreeBuilder](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/TreeBuilder.java)                                                                                                                                  |
| Concrete Builder | [HtmlTreeBuilder](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/HtmlTreeBuilder.java), [XmlTreeBuilder](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/XmlTreeBuilder.java) |
| Product          | [Document](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Document.java)                                                                                                                                         |

```java
// Parser.java
public Document parseInput(String html, String baseUri) {
    return treeBuilder.parse(new StringReader(html), baseUri, this);
}

...

public static Document parse(String html, String baseUri) {
    TreeBuilder treeBuilder = new HtmlTreeBuilder();
    return treeBuilder.parse(new StringReader(html), baseUri, new Parser(treeBuilder));
}
```

```java
// TreeBuilder.java
Document parse(Reader input, String baseUri, Parser parser) {
    initialiseParse(input, baseUri, parser);
    runParser();
    return doc;
}
```

```java
// HtmlTreeBuilder.java
public class HtmlTreeBuilder extends TreeBuilder {

    ...

}
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/70318883-6d6ede00-1864-11ea-9ff0-a226f2513523.png" width="400" />
</p>

### org.jsoup.parser.Tokeniser

**State**

[Same as above.](#orgjsoupparserHtmlTreeBuilder)

**Why?**

This class has the member variable `state`, which is `TokeniserState` type. The `TokeniserState` declare abstract method and its subtypes implement this method. And subtypes of `TokeniserState` call `transition` method for transiting to another state.

| Role          | Class                                                                                                                       |
| ------------- | --------------------------------------------------------------------------------------------------------------------------- |
| Context       | [Tokeniser](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/Tokeniser.java)               |
| State         | [TokeniserState](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/TokeniserState.java)     |
| ConcreteState | [Many nested states](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/parser/TokeniserState.java) |

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

### org.jsoup.nodes.Node

**Composite**

Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly.

**Why?**

`Node` is the parent class of `LeafNode` and `Element`. `Element` delegates to multiple `Node`. And this composite member variable name is childNodes. It can be `LeafNode` or `Element` recursively.

| Role      | Class                                                                                                      |
| --------- | ---------------------------------------------------------------------------------------------------------- |
| Component | [Node](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Node.java)         |
| Composite | [Element](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Element.java)   |
| Leaf      | [LeafNode](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/LeafNode.java) |

```java
// Element.java
public class Element extends Node {
    ...
    List<Node> childNodes;
```

```java
// LeafNode.java
abstract class LeafNode extends Node {
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/70317090-68a82b00-1860-11ea-86e8-f256c422449d.png" width="400" />
</p>

### org.jsoup.select.NodeVisitor

**Visitor**

Visitor perform an operation on a group of similar kind of Objects. By using Visitor we can move the operational logic from the objects to another class.

**Why?**

So many class in this project used NodeVisitor.

Example) Cleaner class

`Cleaner` class is `Client` that accesses data structure objects of other class by using `Visitor`,`CleaningVisitor`. `NodeVisitor` is `ConcreteVisitor`interface which is type of visitor `CleaningVisitor`. this pattern can be also seen in [W3CBuilder](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/W3CDom.java)

| Role            | Class                                                                                                             |
| --------------- | ----------------------------------------------------------------------------------------------------------------- |
| Client          | [Cleaner](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/safty/Cleaner.java)          |
| Visitor         | [CleaningVisitor](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/safty/Cleaner.java)  |
| ConcreteVisitor | [NodeVisitor](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/select/NodeVisitor.java) |

```java
// NodeVisitor.java
public class Cleaner {
...
   private final class CleaningVisitor implements NodeVisitor {
           private CleaningVisitor(Element root, Element destination) { ...
           }
           public void head(Node source, int depth) { ...
           }
           public void tail(Node source, int depth) { ...
           }
   }
   private int copySafeNodes(Element source, Element dest) {
        CleaningVisitor cleaningVisitor = new CleaningVisitor(source, dest);
        NodeTraversor.traverse(cleaningVisitor, source);
        return cleaningVisitor.numDiscarded;
    }
}
```

```java
// NodeVisitor.java

public interface NodeVisitor {
    void head(Node node, int depth);
    void tail(Node node, int depth);
}
```

## New features

- [Get elements by inline style properties](#get-elements-by-inline-style-css-properties)
- [Get text content in an element while keeping HTML default block level line breaks](#Get-text-content-in-an-element-while-keeping-HTML-default-block-level-line-breaks)
- [Element inspection](#Element-inspection)
  - [Frame cloning](#Frame-Cloning)
  - [HTML minifying](#HTML-Minifying)
- [Get Iframe elements and merge into original document](#Get-Iframe-elements-and-merge-into-original-document)
- [SQLish: SQL-like utility for elements](#SQLish-SQL-like-utility-for-elements)

### Get elements by inline style CSS properties

**Idea**

With this feature you can directly find elements with CSS properties inside inline style attribute.

It is already possible with CSS selector implemented in jsoup, for example, if you would like to select `div` tag with a style `display: block` you can simply achieve this by

```Java
element.select("div[style*=\"display: block\"]")
```

Unfortunately it only matches to the exact string `display: block`, while not working with `display : block` or `display:block`. So the codes can easily become fragile and you may not get the results as you expected.

This problem happens because unlike most other attributes, style attribute contains a set of CSS key/value pairs in just a single line of string. So it is reasonable to separate, parse and store them in another form of structure to improve utility and usability.

**Implementation**

First of all, we need a new class called `Style` alongside the `style` attribute. This object would have `key` and `value` as its properties. Each of them matches to CSS' key/value. What we're going to do is that when an Element is created with the given attributes, parse style attribute's string (only if style attribute exists to improve performance), and then create `Style` instance with the key/value and store them in an Element property `styles` which is an `ArrayList<Style>`. Now you can find elements with inline styles using `getElementsByInlineStyle()` without writing messy, tedious queries.

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

**Changelogs**

- [#12](https://github.com/payw-org/jsoup-plus/pull/12)
- [#7](https://github.com/payw-org/jsoup-plus/pull/7)

### Get text content in an element while keeping HTML default block level line breaks

**Related source codes**

- [`FormattedTextVisitor.java`](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/FormattedTextVisitor.java)
- [`Node.java`: accept method](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Node.java#)
- [`TextNode.java`: accept method](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/TextNode.java)
- [`Element.java`: accept method](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Element.java)
- [`Element.java`: formattedText method](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Element.java)

**Idea**

When we get text content inside elements, currently `text()` or `wholeText()` methods concatenate all of them in a single line by unrespecting HTML's block-level line breaks. For example, suppose there is a DOM tree like below.

```html
<div>
  <h1>My First Program</h1>
  <p>
    <span>Hello</span>
    World
  </p>
</div>
```

What the form of text we want is like this since the `div`, `h1`, `p` tags are block-level and `span` is inline(or no default display).

```
My First Program
Hello World
```

However, **jsoup**'s two mostly used getting text methods don't work as we expected.

```
My First Program Hello World // Result of Element.text()
My First ProgramHello World // Result of Element.wholeText()
```

**First implementation**

On the first shot, we implemented this feature in a single method in [Element](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Element.java). However, we found that visitor pattern can be applied to this feature by creating a visitor class and accept methods in each [Node](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Node.java)'s subclasses.

**Visitor pattern**

Related to this pull request [#12](https://github.com/payw-org/jsoup-plus/pull/12).

We created a concrete class called `FormattedTextVisitor` and here is the core part of this class.

> [FormattedTextVisitor.java](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/FormattedTextVisitor.java)

```java
public class FormattedTextVisitor {
    private String formattedText = "";

    public String text() {
        return this.formattedText;
    }

    public void visit(Element element) {
        if (element.tagName().equals("br")) {
            this.formattedText += "\n";
        }
    }

    public void visit(TextNode textNode) {
        this.formattedText += textNode.text();

        if (textNode.parentNode() instanceof Element) {
            Element parentElement = (Element) textNode.parentNode();
            if (parentElement.isBlock()) {
                // Block level
                this.formattedText += "\n";
            } else {
                // No block level
            }
        }
    }

    ...

}
```

Then added the accept method to Node.

```java
public void accept(FormattedTextVisitor visitor) {
    visitor.visit(this);
}
```

As a result of visitor pattern, the implementation becomes much simpler than the first approach. From now on, `formattedText()` method doesn't have to know each node's type anymore. All it has to do is create a visitor instance and accept it to each node inside NodeTraversor. Then the visitor will accumulate strings and we can simply get the result using `text()` method.

```java
public String formattedText() {
    final FormattedTextVisitor visitor = new FormattedTextVisitor();
    NodeTraversor.traverse(new NodeVisitor() {
        @Override
        public void head(Node node, int depth) {
            node.accept(visitor);
        }

        @Override
        public void tail(Node node, int depth) {
        }
    }, this);

    return visitor.text();
}
```

### Element inspection

- [`Node.java`](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Node.java#)

**Idea**

Most of the time, when you are trying to crawl something, the crawling targets are repeating elements. Suppose there an HTML looks like this.

```html
<div>
  <ul>
    <div>Included div element</div>
    <li>item 1</li>
    <li>item 2</li>
    <li>item 3</li>
  </ul>
  <div class="car">Tesla</div>
  <div class="car">Jaguar</div>
  <div class="car">Lexus</div>
  <div class="car">Chevrolet</div>
</div>
```

We can assume that item 1, item 2..., and cars are similar elements which are repeating several times. There may be a high chance to crawl data from them. To do that, a programmer or an user must inspect the page using browsers or other tools and traverse through them to identify what is repeating and how to get it by making queries targetting them. This is very tedious work and this feature could be an aid for this situation.

If you run `inspect()` on the element above, you will get the result of this.

```
## This kind of element has been repeated 4 times ##

Query Recommendation: div.car

<div class="car">
 Tesla
</div>

## This kind of element has been repeated 3 times ##

Query Recommendation: li

<li>item 1</li>
```

It simply tells you what are repeating and how to get them by recommending a query.

### Frame cloning

- [`Node.java`](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Node.java#)

Both jsoup's `clone()` method in deep or shallow return a cloned node with all the text nodes and attributes included. But sometimes we just want to get only the structure of elements to see the appearance of them or to create reusable components.

```java
public Element frameClone(final String[] preservingAttrs) {
    Element clone = this.clone();
    final ArrayList<TextNode> textNodes = new ArrayList<>();
    NodeTraversor.traverse(new NodeVisitor(){
        @Override
        public void head(Node node, int depth) {
            if (node instanceof TextNode) {
                TextNode textNode = (TextNode) node;
                textNodes.add(textNode);
            } else if (node instanceof Element) {
                Element element = (Element) node;
                Attributes attrs = element.attributes();
                for (Attribute attr : attrs) {
                    if (!Arrays.asList(preservingAttrs).contains(attr.getKey())) {
                        attr.setValue("");
                    }
                }
            }
        }
    }, clone);

    for (TextNode node : textNodes) {
        node.parentNode.removeChild(node);
    }

    return clone;
}
```

Additionally, you can pass a list of strings(attribute names) to be preserved, for example, class or id.

```java
Element.frameClone(new String[]{"class", "id"})
```

**Comparison with `clone()`**

```html
<!-- clone() -->
<div id="wrapper">
  <h1 class="title typography-big" data-title-id="123">
    This is title
  </h1>
</div>

<!-- frameClone() -->
<div id="">
  <h1 class="" data-title-id=""></h1>
</div>

<!-- frameClone(new String[]{"id", "class"}) -->
<div id="wrapper">
  <h1 class="title typography-big" data-title-id=""></h1>
</div>
```

### HTML minifying

- [`Node.java`](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/nodes/Node.java#)

There are `html()` and `outerHtml()` methods to be used readily when you need to get an HTML string from an element. In default, jsoup's **_pretty_** output configuration is set to true, so the result string looks great keeping all the indentations.

This feature post-processes the result of `outerHtml()` to be minified.

Its implementation is very simple. We achieved the result by just removing all white spaces and line breaks. Still it conserves the HTML syntax and keeps all content intact.

```java
public String outerHtml(Boolean minify) {
    if (!minify) return this.outerHtml();
    return this.outerHtml()
        .replace("\n", "")
        .replace("\r", "")
        .replaceAll("  ","")
        .replace("> <", "><");
}
```

We created this method by overriding the exising method `outerHtml()` and all you have to do is pass a boolean argument to it.

**Comparison between the original `outerHtml()`**

```html
<!-- Default -->
<div id="div1">
  <p>Hello</p>
  <p>
    Another
    <b>element</b>
  </p>
  <div id="div2">
    <img src="foo.png" />
  </div>
</div>

<!-- Minified -->
<div id="div1">
  <p>Hello</p>
  <p>
    Another
    <b>element</b>
  </p>
  <div id="div2"><img src="foo.png" /></div>
</div>
```

**Updates**

We found `TextUtil.stripNewlines()`.

### Get Iframe elements and merge into original document

**Idea**

There is no implement to get iframe's elements. Jsoup focused on static html. For the elements loaded dynamically in runtime. So it is time-spending work to look reference and read document only for this small function.

**Implementation**

To get every detail from iframe, first we need to find iframe elements in document. Simply we got every iframe and extract `src` attribute from element. After we extract src, we call it's document and prepend it to original element. Because Jsoup only look for a HTML things. So we have to manually call it. So node is generated, and matches with original tree. But we append whole text including META. Because we shouldn't give any restriction to user.

With this feature you can get `Document` with all `Element` including Element inside `iframe`

```Java
public static Document nestedConnect(String url) throws IOException {
    Document doc = Jsoup.connect(url).get();
    Elements iframes = doc.select("iframe");
    for (Element iframe : iframes) {
        if (iframe.attr("src").startsWith("http")) {
            try {
                String source = iframe.attr("src");
                Document iframeDoc = Jsoup.connect(source).get();

                iframe.prependElement(iframeDoc.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
    return doc;
}
```

### SQLish: SQL-like utility for elements

**Idea**

jsoup is already a very powerful tool to parse HTML and crawl data from any website (except dynamically rendered pages like Single Page Application). What most people do with this library is crawl data just I said. However, everytime users or programmers trying to achieve that purpose, they might code tedious work. SQLish is an answer for this situation. It is packed with many useful tools for crawling and you can use this in a SQL-like ways. Crawling data is highly close to data things, anyone can use this tool without knowing the all API of jsoup.

**Implementation**

- [Members](#members)
- [Documentation (deprecated)](#documentation-deprecated)
- [Design patterns found in jsoup](#design-patterns-found-in-jsoup)
  - [org.jsoup.Jsoup](#orgjsoupjsoup)
  - [org.jsoup.internal.ConstrainableInputStream](#orgjsoupinternalconstrainableinputstream)
  - [org.jsoup.parser.CharacterReader](#orgjsoupparsercharacterreader)
  - [org.jsoup.parser.Parser](#orgjsoupparserparser)
  - [org.jsoup.select.Collector.Accumulator](#orgjsoupselectcollectoraccumulator)
  - [org.jsoup.parser.HtmlTreeBuilder](#orgjsoupparserhtmltreebuilder)
  - [org.jsoup.parser.Tokeniser](#orgjsoupparsertokeniser)
  - [org.jsoup.nodes.Node](#orgjsoupnodesnode)
  - [org.jsoup.select.NodeVisitor](#orgjsoupselectnodevisitor)
- [New features](#new-features)
  - [Get elements by inline style CSS properties](#get-elements-by-inline-style-css-properties)
  - [Get text content in an element while keeping HTML default block level line breaks](#get-text-content-in-an-element-while-keeping-html-default-block-level-line-breaks)
  - [Element inspection](#element-inspection)
  - [Frame cloning](#frame-cloning)
  - [HTML minifying](#html-minifying)
  - [Get Iframe elements and merge into original document](#get-iframe-elements-and-merge-into-original-document)
  - [SQLish: SQL-like utility for elements](#sqlish-sql-like-utility-for-elements)
    - [Sort elements as ascending order of its text](#sort-elements-as-ascending-order-of-its-text)
    - [Sort elements as descending order of its text](#sort-elements-as-descending-order-of-its-text)
    - [Get the only elements which are starts with the specified prefix](#get-the-only-elements-which-are-starts-with-the-specified-prefix)
    - [Get the only elements which are ends with the specified suffix](#get-the-only-elements-which-are-ends-with-the-specified-suffix)
    - [Get the only elements which text integer are greater than or equal to specified number](#get-the-only-elements-which-text-integer-are-greater-than-or-equal-to-specified-number)
    - [Get the only elements which text integer are less than or equal to specified number](#get-the-only-elements-which-text-integer-are-less-than-or-equal-to-specified-number)
    - [Returns the portion of these elements](#returns-the-portion-of-these-elements)
- [What we tried](#what-we-tried)
  - [Response from website not getting appropriate encoding](#response-from-website-not-getting-appropriate-encoding)
  - [Converting html to plain text, line brokes!](#converting-html-to-plain-text-line-brokes)
  - [Get only text from element](#get-only-text-from-element)

**Command pattern**

Queries are generated with command objects and every time you call SQL methods the commands are stored sequentially. After that, you can get the result by executing all the queries you just accumulated using exec() method. Also, for a duplicate query, you can reduce the code duplication by popping out the previous command inside the oboject instead of generating another cloned object. Now as you know, the commands are objects we could get many advantages from this like applying other design patterns or something like that.

| Role            | Class                                                                                                                    |
| --------------- | ------------------------------------------------------------------------------------------------------------------------ |
| Command         | [SQLCommand](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/SQLCommand.java)          |
| ConcreteCommand | [Many nested classes](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/SQLCommand.java) |
| Receiver        | [Elements](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/select/Elements.java)              |
| Invoker         | [SQLish](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/SQLish.java)                  |

**Strategy pattern**

There are many ways to extract texts from Elements. You can extract them including every child text nodes in the element(by using text() method) or you can extract only the text content which is related directly to that element (by using ownText() method). Queries are different for each method and both of them are plentifully used as users needed. Furthermore, since the texts should be extracted at run time, we used strategy pattern.

| Role             | Class                                                                                                                   |
| ---------------- | ----------------------------------------------------------------------------------------------------------------------- |
| Context          | [SQLCommand](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/SQLCommand.java)         |
| Strategy         | [TextExtractor](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/TextExtractor.java)   |
| ConcreteStrategy | [Nested classes](hhttps://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/TextExtractor.java) |

**Facade pattern**

Classes related to SQL are implemented with command pattern and strategy pattern. So the clients need to know what are them and how to use/apply them. For the users who don’t have knowledge base on these, we prepared facade pattern to enable them easily use our utility without knowing the whole notions.

| Role   | Class                                                                                                   |
| ------ | ------------------------------------------------------------------------------------------------------- |
| Facade | [SQLish](https://github.com/payw-org/jsoup-plus/blob/master/src/main/java/org/jsoup/helper/SQLish.java) |

**Test elements 1**

```html
<!-- test elements -->
<p>
  hello
  <span>mango</span>
</p>
<p>
  hello
  <span>ironman</span>
</p>
<p>
  hello
  <span>nobody</span>
</p>
<p>
  hello
  <span>food</span>
</p>
<p>
  hello
  <span>programmer</span>
</p>
<p>
  hello
  <span>love</span>
</p>
<p>
  hello
  <span>ice</span>
</p>
<p>
  hello
  <span>apple</span>
</p>
<p>
  hello
  <span>human</span>
</p>
<p>
  hello
  <span>zoo</span>
</p>
<p>
  hello
  <span>solo</span>
</p>
<p>
  hello
  <span>banana</span>
</p>
<p>
  hello
  <span>melon</span>
</p>
<p>
  hello
  <span>apology</span>
</p>
<p>
  hello
  <span>for</span>
</p>
<p>
  hello
  <span>prolong</span>
</p>
```

**Test elements 2**

```html
<!-- test elements -->
<p>
  23
  <span>human</span>
</p>
<p>
  18
  <span>cat</span>
</p>
<p>
  4939
  <span>nobody</span>
</p>
<p>
  19
  <span>food</span>
</p>
<p>
  293
  <span>dog</span>
</p>
<p>
  174
  <span>love</span>
</p>
<p>
  3942
  <span>lion</span>
</p>
<p>
  92
  <span>elephant</span>
</p>
<p>
  12
  <span>human</span>
</p>
<p>
  443
  <span>giraffe</span>
</p>
```

---

#### Sort elements as ascending order of its text

**Method**

`SQLish#orderByTextAsc()`

**Results** (Test elements 1)

```
hello apology
hello apple
hello banana
hello food
hello for
hello human
hello ice
hello ironman
hello love
hello mango
hello melon
hello nobody
hello programmer
hello prolong
hello solo
hello zoo
```

#### Sort elements as descending order of its text

**Method**

`SQLish#orderByTextDesc()`

**Results** (Test elements 1)

```
hello zoo
hello solo
hello prolong
hello programmer
hello nobody
hello melon
hello mango
hello love
hello ironman
hello ice
hello human
hello for
hello food
hello banana
hello apple
hello apology
```

#### Get the only elements which are starts with the specified prefix

**Method**

`SQLish#startsWithText("hello pro")`

**Results** (Test elements 1)

```
hello programmer
hello prolong
```

#### Get the only elements which are ends with the specified suffix

**Method**

`SQLish#endsWithText("man")`

**Results** (Test elements 1)

```
hello ironman
hello human
```

#### Get the only elements which text integer are greater than or equal to specified number

**Method**

`SQLish#gteByText(200)`

**Results** (Test elements 2)

```
293 dog
443 giraffe
3942 lion
4939 nobody
```

#### Get the only elements which text integer are less than or equal to specified number

**Method**

`SQLish#lteByText(100)`

**Results** (Test elements 2)

```
92 elephant
23 human
19 food
18 cat
12 human
```

#### Returns the portion of these elements

**Method**

`SQLish#limit(3, 5)` `SQLish#limit(2)`

**Results** (Test elements 1)

```
hello food
hello for
hello human
hello ice
hello ironman
```

```
hello food
hello for
```

## What we tried

- [Response from website not getting appropriate encoding](#Response-from-website-not-getting-appropriate-encoding)
- [Converting html to plain text, line brokes!](#Converting-html-to-plain-text,-line-brokes!)
- [Get only text from element](#Get-only-text-from-element)

### Response from website not getting appropriate encoding

**Idea**

when getting response, some web site doesn't return character-set. due to unmatching character-set stream broke.

**Problem**

Some old website's response doesn't include charset. Jsoup do send request with execute() method. But in execute() it only read for the response, not checking META.

So "requester" doesn't have any way to find out it's original char-set. So we thought it is nessesary to add parsing method to execute() method.

We started to find META parsing part and tried to add it in execute(). But we thought this action was not the one 'Maker intended'.

Because execute() is only for requesting data to server. So not parsing or additional action required. So we stopped.

In order to change encoding we used META data. so add parse() method to execute() method or just do parse() after execute().

### Converting html to plain text, line brokes!

**Idea**

When convert html to plain text, line brokes.

**Problem**

With this feature you can get `Document` with all `Element` including Element inside `iframe`

```HTML
         <html>
             <head>
                 <title>
                 </title>
                 <style>body{ font-size: 12px;font-family: verdana, arial, helvetica, sans-serif;}
                 </style>
             </head>
             <body><p><b>hello world</b></p><p><br><b>yo</b> <a href=\"http://google.com\">googlez</a></p>
             </body>
```

when extracting 'hello world yo googlez' traditional way of Jsoup makes it intoline.

    hello world
    yo googlez

if we want to get things in this format we should prepend "\\n" things. At first. we tried to build function.

But figured out there already function ''Jsoup.parse().wholeText()'' exist. So we stopped

**Updates**

The `wholeText()` also does not return the result as we expected. So we created a [feature](#Get-text-content-in-an-element-while-keeping-HTML-default-block-level-line-breaks) for that.

### Get only text from element

**Idea**

Sometimes when 'GET' we only need to extract element. But only extract can be pretty hard if we manually find strings and iterating objects.

**Problem**

We found `ownText()` method.

```Java

    Element p = doc.select("p").first();
    System.out.println(p.ownText());

    for (Node node :p.childNodes()){
        if (node instanceof TextNode){
            System.out.println(((TextNode)node).text());
        }
}

```
