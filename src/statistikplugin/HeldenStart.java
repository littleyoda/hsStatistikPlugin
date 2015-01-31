/*
 * (c) Copyright 2002-2006 Andreas Sch�nknecht
 */
package statistikplugin;

import helden.plugin.HeldenXMLDatenPlugin;
import helden.plugin.PluginTabDarstellung;
import helden.plugin.datenxmlplugin.DatenAustauschImpl;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;


/**
 * Startet das Plugin von der Helden-Software aus.
 */
public class HeldenStart implements HeldenXMLDatenPlugin, PluginTabDarstellung {

    /**
     * 
     */
    public static final String VERSIONNR = "0.06";
    
    /**
     * 
     */
    public static final String VERSIONDATUM = "15.09.2012";
    /**
     * 
     */
    public static final  String VERSION = VERSIONNR + " (" + VERSIONDATUM + ")";


    //private JScrollPane panel;
    private JPanel panel;


    /**
     * Defaultconstructor
     * Gefordert von der Helden-Software
     */
    public HeldenStart() {
        super();
        //panel = new JScrollPane();
        panel = new JPanel();
    }

    /**
     * @return Liste mit den Strings für die Untemenüs 
     */
    public ArrayList<String> getUntermenus() {
        return new ArrayList<String>();
    }

    /**
     * Gibt den Namen des Plugins an.
     * Wird verwendet um das Menu auf zu bauen.
     * @return name
     */
    public String getMenuName() {
        return "Statistik (" + VERSIONNR + ")";
    }

    /**
     * Wird angezeigt, wenn die maus �ber den Menu schwebt.
     * @return tooltip
     */
    public String getToolTipText() {
        return "";
    }


    /**
     * Liest ein Attribut aus einem XML-NOde
     * @param n Node
     * @param s Attribute Name
     * @return Inhalt des Attributes. ggf. ""
     */
//    private String getAtt(Node n, String s) {
//        if (n.getAttributes().getNamedItem(s) == null) {
//            return "";
//        }
//        return n.getAttributes().getNamedItem(s).getNodeValue();
//    }



    /**
     * Liefert das ImageIcon f�r das Menu
     * @return ImageIcon oder null 
     */
    public ImageIcon getIcon() {
        return null;
    }


    /**
     * @param f Frames des Heldenprogramms.
     */
    public void doWork(JFrame f) {
    }





    private  void init(DatenAustauschImpl dai) {
        org.w3c.dom.Document request;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            request = factory.newDocumentBuilder().newDocument();
        } catch (Exception ex) {
            request = null;
        }
        Element requestElement = request.createElement("action");
        request.appendChild(requestElement);
        requestElement.setAttribute("action", "held");
        requestElement.setAttribute("id", "selected");
        requestElement.setAttribute("format", "xml");
        org.w3c.dom.Document xmlDoc = (org.w3c.dom.Document) dai.exec(request);
        Utils.daten = xmlDoc;
        //save(xmlDoc);

    }

    /**
     * Wird von Helden Aufgerufen
     * @param frame parent Frame
     * @param menuIdx x
     * @param dai Datenstruktur für die Daten
     */
    public void doWork(JFrame frame, Integer menuIdx, DatenAustauschImpl dai) {
        Utils.setFrame(frame);
        init(dai);
        Statistikplugin hs = new Statistikplugin();
        if (menuIdx == -1) {
            panel.removeAll();
            panel.setLayout(new BorderLayout());
            if (Utils.daten != null) {
                JTabbedPane tb = hs.getTabbedPane();
                tb.setTabPlacement(JTabbedPane.LEFT);
                panel.add(new JScrollPane(tb), BorderLayout.CENTER);
            }
            panel.revalidate();
        } else {
            hs.run();
        }
    }

    /**
     * Gibt helden den Typ dieses Plugins
     * @return SIMPLE
     */
    public String getType() {
        return DATEN;
    }

    public JComponent getPabel() {
        return panel;
    }


}
