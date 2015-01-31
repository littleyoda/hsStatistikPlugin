package statistikplugin;

import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import kampf.Kaempfer;
import kampf.KaempferFromGUI;
import kampf.Kampf;
import kampf.SimpleKaempfer;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import statistikplugin.ManoeverVerwaltung.MANOEVER;


public class Statistikplugin {
    
    public static Random rnd = new Random();
    
    public JScrollPane get(JComponent jl) {
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp.add(jl, BorderLayout.NORTH);
        JScrollPane j = new JScrollPane(jp);
        j.getHorizontalScrollBar().setUnitIncrement(10);
        j.getVerticalScrollBar().setUnitIncrement(10); 
        return j;
    }

    public static Color toRGB(float h, float s, float l) {
        if (s < 0.0f || s > 100.0f) {
            return Color.red;
        }

        if (l < 0.0f || l > 100.0f) {
            return Color.red;       
        }


        //  Formula needs all values between 0 - 1.

        h = h % 360.0f;
        h /= 360f;
        s /= 100f;
        l /= 100f;

        float q = 0;

        if (l < 0.5) {
            q = l * (1 + s);
        } else {
            q = (l + s) - (s * l);
        }

        float p = 2 * l - q;

        float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
        float g = Math.max(0, HueToRGB(p, q, h));
        float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

        return new Color(r, g, b);
    }

    private static float HueToRGB(float p, float q, float h)
    {
        if (h < 0) {
            h += 1;
        }

        if (h > 1) {
            h -= 1;
        }

        if (6 * h < 1) {
            return p + ((q - p) * 6 * h);
        }

        if (2 * h < 1) {
            return  q;
        }

        if (3 * h < 2)
        {
            return p + ( (q - p) * 6 * ((2.0f / 3.0f) - h) );
        }

        return p;
    }

    private void uebernehmeWaffenwerte(KaempferGUI held, int waffe) {
        String swaffe = "daten/nahkampfwaffen/nahkampfwaffe[" + waffe + "]";
        held.at.setValue((Integer)Utils.getDatenAsInt(swaffe + "/at"));
        held.pa.setValue((Integer) Utils.getDatenAsInt(swaffe + "/pa"));
        String tp = Utils.getDatenAsString(swaffe +  "/tpinkl");
        if (tp.length() > 0 && tp.charAt(1) == 'W') {
            held.schadenW6.setValue((Integer) Integer.parseInt("" + tp.charAt(0)));
            if (tp.length() > 2) {
                int i = Integer.parseInt(tp.substring(3));
                if (tp.charAt(2) == '-') {
                    i = -i;
                }
                held.schadenKonstant.setValue((Integer) i);
            }
        }
        int ini = Utils.getDatenAsInt("daten/eigenschaften/initiative/akt") +
                  Utils.getDatenAsInt(swaffe + "/ini");
        held.ini.setValue(ini);
    }
    
    private JComponent getKampfPanel() {
        int maxbreite = 17; 
        JPanel jp = new JPanel();
        jp.setLayout(new GridBagLayout());

        JScrollPane j = new JScrollPane(jp);
        j.getHorizontalScrollBar().setUnitIncrement(10);
        j.getVerticalScrollBar().setUnitIncrement(10);

        final KaempferGUI held = KaempferGUI.createKampfGUI("Held", jp, 0, true, true);
        try {
        held.rs.setValue(Integer.parseInt(Utils.getDatenAsString("daten/angaben/rsgesamt")));
        held.le.setValue(Integer.parseInt(Utils.getDatenAsString("daten/eigenschaften/lebensenergie/akt")));
        held.ini.setValue(Integer.parseInt(Utils.getDatenAsString("daten/eigenschaften/initiative/akt")));
        } catch (Exception e) {
            
        }
        try {
            held.wundschwelle.setValue(Integer.parseInt(Utils.getDatenAsString("daten/angaben/wundschwelle")));
        } catch (Exception e) {
                
        }
        final JComboBox cb = new JComboBox();
        for (int i = 1; i < 6; i++) {
            String wnr = Utils.getDatenAsString("daten/nahkampfwaffen/nahkampfwaffe[" + i + "]/name");
            if (wnr.equals("")) {
                continue;
            }
            wnr = i + " " + wnr;
            cb.addItem(wnr);
        }
        cb.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                uebernehmeWaffenwerte(held, cb.getSelectedIndex() + 1);
            }
            
        });
        if (cb.getItemCount() > 0) {
            cb.setSelectedIndex(0);
            jp.add(cb, Utils.getGridBagConstraints(1, 0));
        }
        jp.add(new JSeparator(JSeparator.HORIZONTAL), Utils.getGridBagConstraints(0, 2, maxbreite));
        jp.add(new JSeparator(JSeparator.HORIZONTAL), Utils.getGridBagConstraints(0, 5, maxbreite));
        final KaempferGUI gegner = KaempferGUI.createKampfGUI("Gegner", jp, 3, true, true);
//        System.out.println("Waffen: " + Utils.getDatenAsInt("count(daten/nahkampfwaffen/nahkampfwaffe)"));




        final JTabbedPane tabbedPane = new JTabbedPane();
        jp.add(tabbedPane, Utils.getGridBagConstraints(0, 6, maxbreite));

        final JPanel kp = getKampfPanel(maxbreite, jp, held, gegner);
        tabbedPane.addTab("Kampfsimulation", get(kp));



//        tabbedPane.addTab("Finte", getPanelOpt(maxbreite, held, gegner, 0));
  //      tabbedPane.addTab("Wuchtschlag", getPanelOpt(maxbreite, held, gegner, 1));
        tabbedPane.addTab("Finte / Wuchtschlag",
                new JScrollPane(
                getPanelOptFinteWuchtschlag(maxbreite, held, gegner)));
        
        tabbedPane.addChangeListener( new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                boolean akt = held.wuchtschlag.isEnabled();
                boolean enabled = false;
                switch (tabbedPane.getSelectedIndex()) {
                    case 0: enabled = true; break;
                }
                if (akt != enabled) {
                    held.wuchtschlag.setEnabled(enabled);
                    held.finte.setEnabled(enabled);
                    
                    gegner.wuchtschlag.setEnabled(enabled);
                    gegner.finte.setEnabled(enabled);
                    kp.validate();
                }
                
            }
            
        });
//        jp.add(t2, Utils.getGridBagConstraints(0, 2, maxbreite));
                JTextArea t2 = new JTextArea("Es kommen nur einfache Attacken und Paraden zum Einsatz\n"
                        + "Nicht beachtet werden:\n"
                        + "- Dass der Rustungsschutz den AT- und PA-Wert verändern. (Manuelle Änderung notwendig)\n"
                        + "- Patzer\n"
                        + "- Trefferzonenmodel inkl. Rüstungszonen\n"
                        );
                t2.setEditable(false);
                t2.setOpaque(false);
                tabbedPane.addTab("Anmerkungen", get(t2));



        return j;
    }

    private Kampf kampfFuerKampfPanel;
    private JPanel getKampfPanel(int maxbreite, JPanel jp, final KaempferGUI held,
            final KaempferGUI gegner) {
        // Panel1
        JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());

        final JTextArea ta = new JTextArea("", 20, 60);
        ta.setEditable(false);
        ta.setFont(new java.awt.Font("Monospaced", Font.BOLD, 15));
        panel1.add(ta, Utils.getGridBagConstraints(0, 1, 2));

        final JButton showKampf = new JButton("Einen Kampf anzeigen");
        panel1.add(showKampf, Utils.getGridBagConstraints(1, 0, 1));
        showKampf.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (kampfFuerKampfPanel.kampfliste.size() == 0){
                    showKampf.setEnabled(false);
                    return;
                }
                Integer r;
                ArrayList<String> x;
                System.out.println("Save fights: " + kampfFuerKampfPanel.kampfliste.size());
                do {
                    r = rnd.nextInt(kampfFuerKampfPanel.kampfliste.size());
                    System.out.println(r);
                    x = kampfFuerKampfPanel.kampfliste.get(r);
                } while (x == null);
                StringBuilder t = new StringBuilder();
                t.append("Kampf ");
                t.append(r);
                t.append("\n");
                for (String s : x) {
                    t.append(s);
                    t.append("\n");
                }
                JTextArea ta = new JTextArea(t.toString());
                ta.setEditable(false);
                JDialog jd = new JDialog(Utils.getFrame(), "", true);
                jd.add(get(ta), BorderLayout.CENTER);
                //JDialog edit = new JDialog(Utils.getFrame(), "Statistik für " + Utils.getDatenAsString("daten/angaben/name"), true);
                jd.setSize(900, 450);
                jd.setModal(true);
                jd.setVisible(true);
                
            }});
        showKampf.setEnabled(false);
        
        JButton jb = new JButton("Kämpfen");
        panel1.add(jb, Utils.getGridBagConstraints(0, 0, 1));
        jb.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Kaempfer k1 = new KaempferFromGUI(held);                
                Kaempfer k2 = new KaempferFromGUI(gegner);
                kampfFuerKampfPanel = new Kampf();
                kampfFuerKampfPanel.kaempfe(10000, k1, k2, true);
                ta.setText(kampfFuerKampfPanel.getKampfZusammenfassung());
                // TODO Auto-generated method stub
                showKampf.setEnabled(true);

            }});
        return panel1;
    }

    

    /**
     * 
     * @param maxbreite
     * @param held
     * @param gegner
     * @param art 0=Finte; 1=Wuchtschlag
     * @return
     */
    /*private JPanel getPanelOpt(int maxbreite, final KaempferGUI held, final KaempferGUI gegner, final int art) {
        JButton jb;
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());

        final JTextArea ta2 = new JTextArea("", 20, 60);
        ta2.setEditable(false);
        ta2.setFont(new java.awt.Font("Monospaced", Font.BOLD, 15));
        panel2.add(ta2, Utils.getGridBagConstraints(0, 3, maxbreite));
        if (art == 0) {
        jb = new JButton("Optimale Finte berechnen");
        } else if (art == 1) {
            jb = new JButton("Optimalen Wuchtschlag berechnen");
        } else {
            jb = new JButton("Missing");
        }
        panel2.add(jb, Utils.getGridBagConstraints(4, 2, 3));
        jb.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MANOEVER m;
                SimpleKaempfer k1 = new SimpleKaempfer();
                Kaempfer k2 = new Kaempfer() {

                    protected void setzeAngreifMaenover() {
                    }

                    @Override
                    protected void setzeParadeManoever() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                };
                held.getKaempfer(k1);
                gegner.getKaempfer(k2);
                String s = "";
                Kampf k = new Kampf();
                String man;
                if (art == 0) {
                    man = "Finte";
                    m = MANOEVER.FINTE;
                } else {
                    man = "Wuchtschlag";
                    m = MANOEVER.WUCHTSCHLAG;
                }
                for (int i = 0; i < 15; i++) {
                    k1.liste.clear();
                    k1.liste.add(m, i);
                    s += String.format("Siegeswahrscheinlichkeit bei " + man + " %2d: %3.2f%%\n", 
                            i, k.kaempfe(10000, k1, k2, false));
                }
                ta2.setText(s);
                // TODO Auto-generated method stub

            }});
        return panel2;
    }*/

    private JPanel getPanelOptFinteWuchtschlag(int maxbreite, final KaempferGUI held, final KaempferGUI gegner) {
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());

        JPanel subpanel = new JPanel();
        subpanel.setLayout(new GridBagLayout());
        

        JButton jb = new JButton("Berechnen");
        subpanel.add(jb, Utils.getGridBagConstraints(0, 2, 1));
        final JSpinner anz = Utils.getSpinner(subpanel, "Simulierte Kämpfe", 1, 2, 1000, 500, 10000, 500);
        
        panel2.add(subpanel, BorderLayout.NORTH);

        final JPanel panelUnten = new JPanel();
        panelUnten.setLayout(new GridBagLayout());
        panel2.add(panelUnten, BorderLayout.CENTER);
        
        jb.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                SimpleKaempfer k1 = new SimpleKaempfer();
                Kaempfer k2 = new Kaempfer() {
                    protected void setzeAngreifMaenover() {
                    }

                    @Override
                    protected void setzeParadeManoever() {
                        // TODO Auto-generated method stub
                        
                    }
                };
                held.getKaempfer(k1);
                gegner.getKaempfer(k2);
                Kampf[][] x = new Kampf[20][20];
                double max = -1;
                int maxErsch = (Integer) held.at.getValue();
                for (int f = 0; f < 20; f++) {
                    for (int w = 0; w < 20; w++) {
                        System.out.println(f + " " + w);
                        if (f + w > maxErsch) {
                            x[f][w] = null;
                        } else {
                            x[f][w] = new Kampf();
                            k1.liste.clear();
                            k1.liste.add(MANOEVER.FINTE, f);
                            k1.liste.add(MANOEVER.WUCHTSCHLAG, w);
                            x[f][w].kaempfe((Integer) anz.getValue(), k1, k2, false);
                            if (x[f][w].getSiegwahrscheinlichkeit() > max) {
                                max = x[f][w].getSiegwahrscheinlichkeit();
                            }
                        }
                    }
                }
                panelUnten.removeAll();
                // Header
                for (int f = 0; f < Math.min(20, maxErsch + 1); f++) {
                    getTextFieldSimple("W " + f, panelUnten, f + 1, 3, null);
                }
                max = Math.round(max);
                for (int f = 0; f < Math.min(20, maxErsch + 1); f++) {
                    // Am Anfang der Zeile jeweils den Fintenwert angeben
                    getTextFieldSimple("F " + f, panelUnten, 0, f + 4, null);
                    for (int w = 0; w < Math.min(20, maxErsch + 1); w++) {
                        String s = "";
                        String tt = "";
                        Color c = null;
                        if (x[f][w] == null) {
                            //
                        } else {
                            double xx = x[f][w].getSiegwahrscheinlichkeit(); 
                            s = "" + Math.round(xx);
                            tt = x[f][w].getKampfZusammenfassungHtml();
                            c = toRGB(204, (float) (double) xx, 65); 
                            if (Math.round(xx) > max - 2) {
                                c = Color.red;
                            }
                        }
                        getTextFieldSimple(s, panelUnten, w + 1, f + 4, c).setToolTipText(tt);
                    }
                }
                panelUnten.validate();
                panelUnten.getParent().validate();
                panelUnten.getParent().getParent().validate();
                panelUnten.getParent().getParent().repaint();
            }});
        return panel2;
    }


   
    
    public void run() {
        JTabbedPane tab = getTabbedPane();
        JFrame edit = new JFrame();
        edit.setTitle("Statistik für " + Utils.getDatenAsString("daten/angaben/name"));
        edit.getContentPane().add(tab);
        edit.setSize(1000, 650);
        edit.setVisible(true);
    }

    public JTabbedPane getTabbedPane() {
        JTabbedPane tab = new JTabbedPane();

        if (Utils.getDaten("daten/angaben/name").getLength() > 0) {
            tab.addTab("Talente", get(getWahr2(Utils.getDaten("daten/talente/talent"), false)));
            tab.addTab("Zauber", get(getWahr2(Utils.getDaten("daten/zauber/zauber"), true)));
            tab.addTab("Eigenschaften", 
                        get(new JLabel(
                        "<html><h1>Talente</h1>" 
                        + getZauberNachEigenschaften(Utils.getDaten("daten/talente/talent/probe")) 
                        + "<h1>Zauber</h1>"
                        + getZauberNachEigenschaften(Utils.getDaten("daten/zauber/zauber/probe"))
                        )));
            tab.addTab("Merkmale", get(new JLabel(getZauberNachMerkmalen())));
            tab.addTab("Waffen", get(new JLabel(getWaffen())));
        }
        tab.addTab("Kampf", get(getKampfPanel()));
        return tab;
    }


    private int getW20Wahrscheinlichkeit(int x) {
        if (x < 0) {
            return 0;
        }
        if (x == 0) {
            return 1;
        }
        if (x > 19) {
            return 95;
        }
        return (int) Math.round((((float) x) * 0.05) * 100);
    }
    private String getWaffen() {
        String s = "<html><table border=1><tr><td>Name</td><td>Wert</td><td>0</td><td>+3</td><td>+6</td><td>+9</td></tr>";
        NodeList l = Utils.getDaten("daten/nahkampfwaffen/nahkampfwaffe");
        String name;
        String at;
        String pa;
        for (int i = 0; i < l.getLength(); i++) {
            Node ns = l.item(i);
            NodeList n = ns.getChildNodes();
            name = "";
            at = "";
            pa = "";
            for (int j = 0; j < n.getLength(); j++) {
                Node info = n.item(j);
                String nodeName = info.getNodeName();
                if (nodeName.equals("name")) {
                    name = info.getChildNodes().item(0).getNodeValue();
                } else if (nodeName.equals("at")) {
                    at = info.getChildNodes().item(0).getNodeValue();
                } else if (nodeName.equals("pa")) {
                    pa = info.getChildNodes().item(0).getNodeValue();
                }  
            }
            s += "<tr><td>" + name +" (Attacke)</td><td>" + at + "</td>";
            for (int w = 0; w < 10; w = w + 3) {
                s += "<td>" + getW20Wahrscheinlichkeit(Integer.parseInt(at) - w) + "</td>";
            }
            s += "</tr>";

            s += "<tr><td>" + name +" (Parade)</td><td>" + pa + "</td>";
            for (int w = 0; w < 10; w = w + 3) {
                s += "<td>" + getW20Wahrscheinlichkeit(Integer.parseInt(pa) - w) + "</td>";
            }
            s += "</tr>";
        }
        l = Utils.getDaten("daten/fernkampfwaffen/fernkampfwaffe");
        for (int i = 0; i < l.getLength(); i++) {
            Node ns = l.item(i);
            NodeList n = ns.getChildNodes();
            name = "";
            at = "";
            pa = "";
            for (int j = 0; j < n.getLength(); j++) {
                Node info = n.item(j);
                String nodeName = info.getNodeName();
                if (nodeName.equals("name")) {
                    name = info.getChildNodes().item(0).getNodeValue();
                } else if (nodeName.equals("at")) {
                    at = info.getChildNodes().item(0).getNodeValue();
                }  
            }
            s += "<tr><td>" + name +" (Attacke)</td><td>" + at + "</td>";
            for (int w = 0; w < 10; w = w + 3) {
                s += "<td>" + getW20Wahrscheinlichkeit(Integer.parseInt(at) - w) + "</td>";
            }
            s += "</tr>";
        }
        return s;
        //        <nahkampfwaffen>
        //        <nahkampfwaffe>
        //        <nummer>1</nummer>
        //        <name>Jagdspieß</name>
        //        <spalte2>Sp/BE-3</spalte2>
        //        <dk>S </dk>
        //        <tp>1W+6</tp>
        //        <tpkk>12 / 4</tpkk>
        //        <ini>-1</ini>
        //        <wm>0 / -1</wm>
        //        <at>15</at>
        //        <pa>13</pa>
        //        <tpinkl>1W+6</tpinkl>

    }

    
    private String getZauberNachEigenschaften(NodeList l) {
        SortedMap<String, Integer> anzahl = new TreeMap<String, Integer>();
        for (int j = 0; j < l.getLength(); j++) {
            String[] merkmale = l.item(j).getChildNodes().item(0).getNodeValue().split("/");
            for (String merkmal : merkmale) {
                Integer anz = anzahl.get(merkmal.trim());
                if (anz == null) {
                    anz = new Integer(0);
                }
                anz++;
                anzahl.put(merkmal.trim(), anz);
            }
        }
        String s = "<table border=1>";
        Iterator<Entry<String, Integer>> sItr = anzahl.entrySet().iterator();
        while (sItr.hasNext()) {
            Entry<String, Integer> e = sItr.next();
            s += "<tr><td>" 
                + e.getKey() + "</td><td>" + e.getValue() + "</td></tr>";
        }
        s += "</table>";        
        return s;
    }

    private String getZauberNachMerkmalen() {
        SortedMap<String, Integer> anzahl = new TreeMap<String, Integer>();
        NodeList l = Utils.getDaten("daten/zauber/zauber/merkmale");
        for (int j = 0; j < l.getLength(); j++) {
            String[] merkmale = l.item(j).getChildNodes().item(0).getNodeValue().split(",");
            for (String merkmal : merkmale) {
                Integer anz = anzahl.get(merkmal.trim());
                if (anz == null) {
                    anz = new Integer(0);
                }
                anz++;
                anzahl.put(merkmal.trim(), anz);
            }
        }
        String s = "<html><table border=1>";
        Iterator<Entry<String, Integer>> sItr = anzahl.entrySet().iterator();
        while (sItr.hasNext()) {
            Entry<String, Integer> e = sItr.next();
            s += "<tr><td>" + e.getKey() + "</td><td>" + e.getValue() + "</td></tr>";
        }
        s += "</table>";        
        return s;
    }


    private void getTextField(String s, JPanel jp, int x, int y) {
        getTextField(s, jp, x, y, null, false);
    }
    
    private JTextField getTextField(String s, JPanel jp, int x, int y, Color color, Boolean border) {
        JTextField tf = new JTextField(s);
        tf.setEditable(false);
        if (color != null) {
            tf.setBackground(color);
        }
        tf.setFont(new java.awt.Font("Dialog", Font.BOLD, 15));
        GridBagConstraints c = Utils.getGridBagConstraints(x, y);
        if (border) {
            tf.setBorder(BorderFactory.createLineBorder(Color.black, 5));
        }
        c.ipady = 30;
        c.ipadx = 10;
        jp.add(tf, c);
        return tf;
    }

    private JTextField getTextFieldSimple(String s, JPanel jp, int x, int y, Color color) {
        JTextField tf = new JTextField(s);
        tf.setEditable(false);
        if (color != null) {
            tf.setBackground(color);
        }
        tf.setFont(new java.awt.Font("Dialog", Font.BOLD, 15));
        GridBagConstraints c = Utils.getGridBagConstraints(x, y);
        c.insets = new Insets(1,1,1,1);
        jp.add(tf, c);
        return tf;
    }


    private int countEigenschaften(String e,String[] proben) {
        int count = 0;
        for (int i = 0; i< 3; i++) {
            if (proben.equals(e)) {
                count++;
            }
        }
        return count;
    }
    String[] eigenschaften = {"MU","KL","CH","IN","FF","GE","KO","KK"};
    
    /*
    Festgefügtes Denken (Magischer Nachteil;
    ZH; -1 GP pro Punkt): Der Zauberer ist
    ohne Ordnung und vorgegebene Formalismen
    verloren; Improvisation und spontan
    richtige Entscheidungen sind nicht sein
    Ding: Jede Zauber- oder Ritualkenntnis-Probe
    , bei der einmal auf die Eigenschaft Intuition
    gewürfelt wird, ist um die Anzahl an
    Punkten in Festgefügtem Denken (maximal 5)
    erschwert. Kommt IN zweimal in der Probe
    vor, verdoppelt sich der Malus; solche Zauber
    (mit zweimal IN in der Probe) sind um eine
    Spalte schwieriger zu steigern. Dieser Nachteil
    kann nur von Zauberkundigen gewählt
    werden, die Spruchzauberei beherrschen.

    Schwache Ausstrahlung (Magischer Nachteil;
    ZH; -1 GP pro Punkt): Die magische
    Aura des Helden ist wenig 'leuchtkräftig',
    koppelt schlecht an andere Auren an und
    erweckt keine Sympathie bei magischen
    Wesenheiten: Jede Zauberprobe oder Probe
    auf die Ritualkenntnis, bei der einmal auf die
    Eigenschaft Charisma gewürfelt wird, ist um
    die Anzahl an Punkten in SchwacherAusstrahlung
    (maximal 5) erschwert. Kommt Charisma
    zweimal in der Probe vor, verdoppelt sich
    der Malus; solche Zauber (mit zweimal eH
    in der Probe) sind um eine Spalte schwieriger
    zu steigern

    Spruchhemmung (Magischer Nachteil; ZH;
    -10 GP): Die Zauberei des Helden versagt
    gelegentlich (und nicht einmal selten: etwa
    jedes siebte Mal). Zeigen bei einer Zauberprobe
    zwei (oder drei) der drei beteiligten
    W20 dieselbe Zahl, so ist die entsprechende
    Zauberhandlung gehemmt: Sie


         */
    
    private JPanel getWahr2(NodeList talente, boolean isZauber) {
        JPanel jp = new JPanel();
        jp.setLayout(new GridBagLayout());
        int xx = 0;
        getTextField("Name", jp, xx++, 0);
        getTextField("Probe", jp, xx++, 0);
        getTextField("Probe", jp, xx++, 0);
        getTextField("Taw", jp, xx++, 0);
        for (int i = -9; i < 15; i += 3) {
            int fontsize = 0;
            if (i==0) {
                fontsize =3;
            }
            getTextField("" + i,jp, xx++,0);
        }


        String name;
        String probe;
        String wert;
        String rep;
        String bereich = "";
        int y = 1;
        boolean elfRep = 
                Utils.getDatenAsInt("count(daten/sonderfertigkeiten/sonderfertigkeit/nameausfuehrlich[text() = 'Repräsentation: Elf'])")
                > 0 &&
                (Utils.getDatenAsInt("daten/eigenschaften/"+ getAbkuerzung("KL") +"/akt") 
                 < Utils.getDatenAsInt("daten/eigenschaften/"+ getAbkuerzung("IN") +"/akt"));
        for (int i = 0; i < talente.getLength(); i++) {
            String bemerkung = "";
            Node talentNodes = talente.item(i);
            NodeList talentInfos = talentNodes.getChildNodes();
            name = "";
            probe = "";
            wert = "";
            bereich = "";
            rep = "";
            for (int j = 0; j < talentInfos.getLength(); j++) {
                Node talentInfo = talentInfos.item(j);
                String nodeName = talentInfo.getNodeName();
                if (nodeName.equals("name")) {
                    name = talentInfo.getChildNodes().item(0).getNodeValue();
                } else if (nodeName.equals("probe")) {
                    probe = talentInfo.getChildNodes().item(0).getNodeValue();
                } else if (nodeName.equals("wert")) {
                    wert = talentInfo.getChildNodes().item(0).getNodeValue();
                } else if (nodeName.equals("bereich")) {
                    bereich = talentInfo.getChildNodes().item(0).getNodeValue();
                } else if (nodeName.equals("repräsentation")) {
                    rep = talentInfo.getChildNodes().item(0).getNodeValue();
                }
            }
            if (bereich.toLowerCase().equals("kampf")) {
                continue;
            }
            if (wert.equals("?")) {
            	System.out.println(name);
            	continue;
            }
            int taw = Integer.parseInt(wert);
            
            String[] proben = probe.split("/");
            boolean elf =  elfRep && rep.equals("Elf") && bereich.equals("Zauber") 
                                  && countEigenschaften("IN", proben) < 2;
            if (!rep.equals("")) {
                name = name + " (" + rep + ")";
            }
            // Sonderfall Attributo
            if (proben[2].equals("**")) {
                for (String s : eigenschaften) {
                    proben = probe.split("/");
                    proben[2] =  s;
                    showZauber(jp, name, y, bemerkung, taw, proben, elf);
                    y++;
                }
            } else {
                showZauber(jp, name, y, bemerkung, taw, proben, elf);
                y++;
            }

        }
        return jp;
    }

    private void showZauber(JPanel jp, String name, int y, String bemerkung, int taw,
            String[] proben, boolean elfRep) {
        String[] probenM = {"", "" , ""};
        
        // Sonderfall Elfische Repräsentation
        if (elfRep) {
            for (int ec = 0; ec < 3; ec++) {
                probenM[ec] = "";
                if (proben[ec].equals("KL")) {
                    proben[ec] = "IN";
                    bemerkung = "Elfen: KL => IN";
                    probenM[ec] = "*";
                    break;
                }
            }
        }
        int xx;
        xx = 0;
        getTextField(name, jp, xx++, y);
        getTextField(proben[0] + probenM[0] + "/" + proben[1] + probenM[1] 
                     + "/" + proben[2] + probenM[2], jp, xx++, y);
        int e1 = Utils.getDatenAsInt("daten/eigenschaften/" + getAbkuerzung(proben[0]) + "/akt");
        int e2 = Utils.getDatenAsInt("daten/eigenschaften/" + getAbkuerzung(proben[1]) + "/akt");
        int e3 = Utils.getDatenAsInt("daten/eigenschaften/" + getAbkuerzung(proben[2]) + "/akt");
        getTextField(e1 + "/" + e2 + "/" + e3, jp, xx++, y);
        getTextField("" + taw, jp, xx++, y);
        for (int x = -9; x < 15; x += 3) {
            int w = (int)(test(e1, e2, e3, taw - x) * 100.0d);
            int font=0;
            if (x==0) {
                font = 3;
            }
            Color color = null;
            if (w > 66) {
                color = Color.green;
            } else  if (w > 33) {
                color = Color.yellow; 
            } else {
                color = Color.red;
            }
            getTextField("" + w, jp, xx++, y, color, x == 0);
        }
        getTextField(bemerkung, jp, xx++, y, null, false);
    }


    public String getAbkuerzung(String t) {
        if (t.equals("MU")) {
            return "mut";
        }
        if (t.equals("KL")) {
            return "klugheit";
        }
        if (t.equals("CH")) {
            return "charisma";
        }
        if (t.equals("IN")) {
            return "intuition";
        }
        if (t.equals("FF")) {
            return "fingerfertigkeit";
        }
        if (t.equals("GE")) {
            return "gewandtheit";
        }
        if (t.equals("KO")) {
            return "konstitution";
        }
        if (t.equals("KK")) {
            return "koerperkraft";
        }
        return "FEHLT";
    }
    
    public static double test(int e1, int e2, int e3, int taw) {

        int success, restTaP;
        if (taw < 0) {
            return test(e1+taw, e2+taw, e3+taw, 0) ;
        }

        success = 0;
        for (int w1=1;w1<= 20;w1++) {
            for (int w2=1;w2<= 20;w2++){
                for (int w3=1;w3<= 20;w3++){
                    if (meisterhaft(w1,w2,w3)){
                        success++;
                    }else{ 
                        if (patzer(w1,w2,w3)){

                        } else {
                            // schauen, ob die Rest-TaP nicht unter 0 fallen
                            restTaP = taw-Math.max(0, w1-e1)-Math.max(0, w2-e2)-Math.max(0, w3-e3);
                            if (restTaP >= 0) {
                                // hat gereicht
                                success++;
                            }
                        }
                    }
                }
            }
        }
        return (1d/8000d*(success));


    }


    private static boolean meisterhaft(int w1, int w2, int w3) {
        return (w1==1)&&(w2==1)||
        (w2==1)&&(w3==1)||
        (w1==1)&&(w3==1);
    }

    private static boolean patzer(int w1, int w2, int w3) {
        return (w1==20)&&(w2==20)||
        (w2==20)&&(w3==20)||
        (w1==20)&&(w3==20);
    }

    private static boolean patzerWM(int w1, int w2, int w3) {
        int count = 0;
        if ((w1 == 19) || (w1 ==20)) {
            count++;
        }
        if ((w2 == 19) || (w2 ==20)) {
            count++;
        }
        if ((w3 == 19) || (w3 ==20)) {
            count++;
        }
        return count >= 2;
    }

    public static void main(String [] args) {
        try {
            Utils.daten = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("input.xml");
        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                Utils.daten = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        Statistikplugin hs = new Statistikplugin();
        hs.run();

    }

    
}
