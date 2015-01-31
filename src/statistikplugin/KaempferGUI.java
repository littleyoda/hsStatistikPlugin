package statistikplugin;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import kampf.Kaempfer;

public class KaempferGUI {
        JSpinner at;
        JSpinner pa;
        JSpinner le;
        JSpinner schadenW6;
        JSpinner schadenKonstant;
        JSpinner wundschwelle;
        public JSpinner finte;
        public JSpinner wuchtschlag;
        JSpinner ini;
        public JComboBox gegenhalten;
        String name;
        static String[] anwendung = new String[]{"Nie", "Immer"/*, "E == 0", "E <= 1", "E <= 2",
            "E <= 3","E <= 4","E <= 5","E <= 6","E <= 7"*/};
        public JSpinner rs;

        public void getKaempfer(Kaempfer k) {
            k.setValues(name, (Integer) le.getValue(), (Integer) at.getValue(),
                    (Integer) pa.getValue(), new wurfel.WurfelWurf(0, (Integer) schadenW6.getValue(), 
                            (Integer) schadenKonstant.getValue()), (Integer) rs.getValue(),
                                    (Integer) ini.getValue(),
                                    (Integer) wundschwelle.getValue());
        }

        
        
        public static KaempferGUI createKampfGUI(String p, JPanel jp, int y, boolean nahkampf,
                boolean showWsFinte) {
            KaempferGUI kg = new KaempferGUI();
            kg.name = p;
            int x = 0;
            jp.add(new JLabel(p), Utils.getGridBagConstraints(x, y));
            x++; x++;
            kg.at = Utils.getSpinner(jp, "AT", x, y, 10, 1, 25);
            x++; x++;

            if (nahkampf) {
                kg.pa = Utils.getSpinner(jp, "PA", x, y, 10, 1, 25);
            }
            x++; x++;

            kg.ini = Utils.getSpinner(jp, "INI", x, y, 10, 1, 25);
            x++; x++;

            kg.le = Utils.getSpinner(jp, "LE", x, y, 35, 0, 60);
            x++; x++;

            kg.wundschwelle = Utils.getSpinner(jp, "WS", x, y, 8, 0, 20);
            x++; x++;

            kg.rs = Utils.getSpinner(jp, "RS", x, y, 0, 0, 10);
            x++; x++;

            kg.schadenW6 = Utils.getSpinner(jp, "Waffenschaden", x, y, 1, 0, 3);
            x++; x++;

            kg.schadenKonstant = Utils.getSpinner(jp, "W6 + ", x, y, 4, -2, 12);
            x++; x++;

            if (nahkampf && showWsFinte) {
                x = 2;
                y++;
                kg.wuchtschlag = Utils.getSpinner(jp, "Wucht", x, y, 0, 0, 20);
                x++;
                x++;

                kg.finte = Utils.getSpinner(jp, "Finte", x, y, 0, 0, 20);
                x++; x++;

                kg.gegenhalten = Utils.getComboBox(jp, "Gegenhalten", x, y, anwendung);
                x++; x++;
            }
            return kg;
        }
        
        
}
