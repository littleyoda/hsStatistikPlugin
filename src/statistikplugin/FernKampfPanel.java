package statistikplugin;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class FernKampfPanel extends JScrollPane {
    private static JPanel jp = new JPanel();
    
    private void uebernehmeWaffenwerte(KaempferGUI held, int waffe) {
        held.at.setValue((Integer)Utils.getDatenAsInt("daten/fernkampfwaffen/fernkampfwaffe[" 
               + waffe + "]/at"));
        String tp = Utils.getDatenAsString("daten/fernkampfwaffen/fernkampfwaffe[" 
                + waffe + "]/tpinkl");
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
        
        
    }
    
    
    public FernKampfPanel() {
        super(jp);
        jp.setLayout(new GridBagLayout());
        int maxbreite = 17; 

        getHorizontalScrollBar().setUnitIncrement(10);
        getVerticalScrollBar().setUnitIncrement(10);

        final KaempferGUI held = KaempferGUI.createKampfGUI("Held", jp, 0, false, false);
        
        held.rs.setValue(Integer.parseInt(Utils.getDatenAsString("daten/angaben/rsgesamt")));
        held.le.setValue(Integer.parseInt(Utils.getDatenAsString("daten/eigenschaften/lebensenergie/akt")));
        final JComboBox cb = new JComboBox();
        for (int i = 1; i < 6; i++) {
            String wnr = Utils.getDatenAsString("daten/fernkampfwaffen/fernkampfwaffe[" + i + "]/name");
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

        final KaempferGUI gegner = KaempferGUI.createKampfGUI("Gegner", jp, 1, true, false);

        jp.validate();
        /*final JTabbedPane tabbedPane = new JTabbedPane();
        jp.add(tabbedPane, Utils.getGridBagConstraints(0, 3, maxbreite));

        tabbedPane.addTab("Kampfsimulation", getKampfPanel(maxbreite, jp, held, gegner));



        tabbedPane.addTab("Finte", getPanelOpt(maxbreite, held, gegner, 0));
        tabbedPane.addTab("Wuchtschlag", getPanelOpt(maxbreite, held, gegner, 1));
        tabbedPane.addTab("Finte / Wuchtschlag",
                new JScrollPane(
                        getPanelOptFinteWuchtschlag(maxbreite, held, gegner)));

        tabbedPane.addChangeListener( new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                boolean enabled = true;
                switch (tabbedPane.getSelectedIndex()) {
                case 0: enabled = true; break;
                case 1:
                case 2: enabled = false; break;
                }
                held.wuchtschlag.setEnabled(enabled);
                gegner.wuchtschlag.setEnabled(enabled);
                held.finte.setEnabled(enabled);
                gegner.finte.setEnabled(enabled);
            }

        });

        JTextArea t2 = new JTextArea("Es kommen nur einfache Attacken und Paraden zum Einsatz\n"
                + "Nicht beachtet werden:\n"
                + "- Wunden\n"
                + "- Dass der Rustungsschutz den AT- und PA-Wert verändern. (Manuelle Änderung notwendig)\n"
                + "- Patzer");
        t2.setEditable(false);
        t2.setOpaque(false);
        jp.add(t2, Utils.getGridBagConstraints(0, 2, maxbreite));

*/
    }
}
