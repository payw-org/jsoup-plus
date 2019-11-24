This page is written and managed by @ihooni.

> It describe the inspection of all about Jsoup.

## Design pattern

### org.jsoup.Jsoup

**Facade**

Provides a unified interface to a set of interfaces in a subsystem. It defines a higher-levl interface that makes a subsystem easier to use.

**Why?**

Jsoup core features are available from this class. It depends on many subsystem and also all the elements don't depend on this.

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/69477441-c92f7500-0e29-11ea-92bb-64d0c4ce905e.png" width="400" />
</p>

### org.jsoup.internal.ConstrainableInputStream

**Decorator**

Attaches additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.

**Why?**

This class have the same super type as the object it decorate. And `BufferedInputStream`, its parent class, is one of the most famous representative example of Decorator pattern. Also we can pass around a decorated object in place of the original(wrapped) object. See the below codes.

```java
private ConstrainableInputStream(InputStream in, ...) {
        super(in, bufferSize);
        ...
```

<p align="center">
  <img src="https://user-images.githubusercontent.com/37792049/69499220-3e3aa180-0f33-11ea-8d92-37f7aeeb6f40.png" width="400" />
</p>
