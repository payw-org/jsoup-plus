This page is written and managed by @smallfish06.

## New Features

- [Get Iframe elements and merge into original document](#Get-Iframe-elements-and-merge-into-original-document)

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
                    docu = Jsoup.connect(things.attr("src")).get();

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