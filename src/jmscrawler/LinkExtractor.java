/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jmscrawler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.*;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author herberth
 */
public class LinkExtractor {
    public static List<String> Extract(String location) throws MalformedURLException, IOException
    {
        HashSet urls = new HashSet();
        URL url = new URL( location );
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument(); 
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        
        Reader HTMLReader = new InputStreamReader(url.openConnection().getInputStream());
        LinkExtractorCallback callback = new LinkExtractorCallback();
        ParserDelegator delegator = new ParserDelegator();
        delegator.parse(HTMLReader, callback, true);



        return null;
    }
}

/**
 *
 * @author Herberth
 */
class LinkExtractorCallback extends HTMLEditorKit.ParserCallback {

    private Boolean inA=false;
    private String link="";
    private HashSet e = new HashSet();

    @Override
    public void handleStartTag(HTML.Tag tag,MutableAttributeSet a, int pos)
    {
        if(tag== HTML.Tag.A)
        {
            Object attr = a.getAttribute(HTML.Attribute.HREF);
            link = a.getAttribute(HTML.Attribute.HREF).toString();
            e.add(link);
        }
        
    }

    @Override
    public void handleText(char[] data,int pos)
    {

    }

    @Override
    public void handleEndTag(HTML.Tag tag, int pos)
    {

    }

    @Override
    public void handleSimpleTag(HTML.Tag t,MutableAttributeSet a, int pos)
    {

    }

    public String getLink()
    {
        return this.link;
    }

    public HashSet getLinks()
    {
        return e;
    }
}