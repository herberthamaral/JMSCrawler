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
import javax.swing.text.html.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.parser.ParserDelegator;

/**
 *
 * @author herberth
 */
public class LinkExtractor {
    public static void Extract(String location) throws MalformedURLException, IOException
    {
        System.out.println("Iniciando o download de uma nova URL: "+location);
        HashSet urls = new HashSet();
        URL url = new URL( location );
        
        HTMLEditorKit kit = new HTMLEditorKit();
        HTMLDocument doc = (HTMLDocument) kit.createDefaultDocument(); 
        doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
        
        Reader HTMLReader = new InputStreamReader(url.openConnection().getInputStream());
        LinkExtractorCallback callback = new LinkExtractorCallback();
        ParserDelegator delegator = new ParserDelegator();
        delegator.parse(HTMLReader, callback, true);
        Main.reportDownloadedURL(location, "OK"); // ver como pega o conteúdo do arquivo...
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
            if(attr!=null)
            {
                link = attr.toString();
                Main.addLink(link); // todo: refatorar isto. Classe separada para links
            }
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
}