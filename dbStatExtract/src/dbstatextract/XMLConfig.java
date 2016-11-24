package dbstatextract;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLConfig {

    private static HashMap<String, String> imeiName = null;

    public boolean containsImei(String imei) {
        boolean res = false;

        if (imeiName != null) {
            res = imeiName.containsKey(imei);
        }
        return res;
    }

    public String getNameByImei(String imei) {
        return imeiName.get(imei);
    }

    public HashMap getImeiName() {
        return imeiName;
    }

    XMLConfig() {

        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setValidating(false);
            DocumentBuilder builder = f.newDocumentBuilder();
            Document doc = builder.parse(new File("/home/adoborin/work/config/imei.xml"));
            //Document doc = builder.parse(XMLConfig.class.getResourceAsStream("/imei.xml"));
            Element rootElement = doc.getDocumentElement();
            NodeList nl = rootElement.getElementsByTagName("Imei");

            if (nl != null && nl.getLength() > 0) {

                imeiName = new HashMap<>();

                for (int i = 0; i < nl.getLength(); i++) {

                    String name = null;
                    String imei = null;
                    //get the employee element
                    Element el = (Element) nl.item(i);

                    NodeList el_nl = el.getElementsByTagName("Name");

                    if (el_nl != null && el_nl.getLength() > 0) {
                        name = el_nl.item(0).getFirstChild().getNodeValue();                        
                    }

                    NodeList v_nl = el.getElementsByTagName("Value");

                    if (v_nl != null && v_nl.getLength() > 0) {
                        imei = v_nl.item(0).getFirstChild().getNodeValue();                        
                    }

                    if (name != null && imei != null) {
                        imeiName.put(imei, name);
                    }
                }
            }

            System.out.println(imeiName);

        } catch (SAXException ex) {
            Logger.getLogger(XMLConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XMLConfig.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLConfig.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

  /*  public static void main(String[] args) throws Exception {

        XMLConfig conf = new XMLConfig();

    }
*/
}
