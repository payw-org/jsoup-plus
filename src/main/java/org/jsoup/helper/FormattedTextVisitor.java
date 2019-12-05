package org.jsoup.helper;

import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;

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

    public void visit(Comment comment) {}

    public void visit(DocumentType documentType) {}

    public void visit(XmlDeclaration xmlDeclaration) {}

    public void visit(DataNode dataNode) {}
}
