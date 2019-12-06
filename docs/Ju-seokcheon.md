This page is written and managed by @smallfish06.

## New Features

- [Get Iframe elements and merge into original document](#Get-Iframe-elements-and-merge-into-original-document)



## What we tried

- [Response from website not getting appropriate encoding](#Response-from-website-not-getting-appropriate-encoding)

- [Converting html to plain text, line brokes!](#Converting-html-to-plain-text,-line-brokes!)

- [Get only text from element](#Get-only-text-from-element)


## Get Iframe elements and merge into original document

### Idea

There is no implement to get iframe's elements

Jsoup focused on static html. For the elements loaded dynamically in runtime. 

So it is time-spending work to look reference and read document only for this small function.

### Implementation

With this feature you can get `Document` with all `Element` including Element inside `iframe`


```Java

    public Document iframeExtract(String link) throws IOException {
        Document doc = Jsoup.connect(link).get();
        for (Element things : doc.select("iframe")) {
            if (things.attr("src").startsWith("http")) {
                Document docu=null;
                try {
                    docu = Jsoup.connect(things.attr("src")).get(); //parse()

                    things.prependElement(docu.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }
        System.out.print(doc.toString());
        return doc;
    }
```

## Response from website not getting appropriate encoding

### Idea

when getting response, some web site doesn't return character-set. due to unmatching character-set stream broke.


### Problem

Some old website's response doesn't include charset. Jsoup do send request with execute() method. But in execute() it only read for the response, not checking META. 

So "requester" doesn't have any way to find out it's original char-set. So we thought it is nessesary to add parsing method to execute() method. 

We started to find META parsing part and tried to add it in execute(). But we thought this action was not the one 'Maker intended'.

Because execute() is only for requesting data to server. So not parsing or additional action required. So we stopped.

in order to change encoding we used META data. so add parse() method to execute() method or just do parse() after execute().


## Converting html to plain text, line brokes!

### Idea

When convert html to plain text, line brokes. 

### Problem

With this feature you can get `Document` with all `Element` including Element inside `iframe`

``` HTML
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


## Get only text from element

### Idea

Sometimes when 'GET' we only need to extract element. But only extract can be pretty hard if we manually find strings and iterating objects. 

### Problem

We found ownText() method. 

```Java

    Element p = doc.select("p").first();
    System.out.println(p.ownText());

    for (Node node :p.childNodes()){
        if (node instanceof TextNode){
            System.out.println(((TextNode)node).text()); 
        }
}

```
