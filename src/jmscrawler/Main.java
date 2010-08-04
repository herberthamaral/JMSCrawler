package jmscrawler;

/**
 *
 * @author Herberth Amaral & Denison Wallace
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Thread(new Crawler("http://www.uol.com.br/")).start();
    }

}
