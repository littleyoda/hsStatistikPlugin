package kampf;

import java.util.ArrayList;
import java.util.WeakHashMap;


public class Kampf {
    
    private int winK1;
    private int winK2;
    private int wundenK1;
    private int wundenK2;
    private int gesamtLepK1;
    private int gesamtLepK2;
    private int gesamtRunden;
    private int durchgefuehrteKaempfe;
    private int zeitueberschreitungen;
    public WeakHashMap<Integer,ArrayList<String>> kampfliste = new WeakHashMap<Integer,ArrayList<String>>();
    
    
    public double getSiegwahrscheinlichkeit() {
        return ((double) winK1 * 100.0) / ((double) winK1 + (double) winK2);
    }
    

    public int getZeitueberschreitungen() {
        return zeitueberschreitungen;
    }
    
    public int getDurchschnittsLepK1() {
        if (winK1 == 0) {
            return 0;
        }
        return gesamtLepK1 / winK1;
    }
    
    public int getDurchschnittsLepK2() {
        if (winK2 == 0) {
            return 0;
        }
        return gesamtLepK2 / winK2;
    }

    public int getDurchschnittsWundenK1() {
        return wundenK1/ durchgefuehrteKaempfe;
    }
    
    public int getDurchschnittsWundenK2() {
        return wundenK2 / durchgefuehrteKaempfe;
    }
    public double kaempfe(int anz, Kaempfer k1, Kaempfer k2, boolean log) {
        winK1 = 0;
        winK2 = 0;
        wundenK1 = 0;
        wundenK2 = 0;
        durchgefuehrteKaempfe = anz;
        gesamtLepK1 = 0;
        gesamtLepK2 = 0;
        gesamtRunden = 0;
        zeitueberschreitungen = 0;
        for (int i = 0; i < anz; i++) {
            do {
                k1.wuerfeleIni();
                k2.wuerfeleIni();
            } while ((k1.iniAkt == k2.iniAkt) && (k1.iniBasis == k2.iniBasis));
            boolean k1AmZug = (k1.iniAkt > k2.iniAkt) 
                               || ((k1.iniAkt == k2.iniAkt) && (k1.iniBasis > k2.iniBasis));
            if (k1AmZug) {
                k1.setReihenfolge(1);
                k2.setReihenfolge(2);
            } else {
                k1.setReihenfolge(2);
                k2.setReihenfolge(1);
            }
            ArrayList<String> kampflog = new ArrayList<String>();
            if (log) {
                kampfliste.put(i, kampflog);
            }
            kampflog.add("Held: Ini: " + k1.iniAkt + " Basis: " + k1.iniBasis);
            kampflog.add("Gegner: Ini: " + k2.iniAkt + " Basis: " + k2.iniBasis);
            kampflog.add("================");
            k1.setLog(kampflog);
            k2.setLog(kampflog);
            k1.reset();
            k2.reset();
            int runden = 0;
            while (k1.getLP() > 0 && k2.getLP() > 0 && runden < 100) {
                gesamtRunden++;
                Kaempfer a1;
                Kaempfer a2;
                if (k1AmZug) {
                    a1 = k1;
                    a2 = k2;
                } else {
                    a2 = k1;
                    a1 = k2;
                }
                a1.greifeAn(a2);
                k1AmZug = !k1AmZug;
                runden++;
            }
            if (runden == 100) {
                zeitueberschreitungen++;
            }
            if (k1.getLP() > 0) {
                gesamtLepK1 += k1.getLP();
                winK1++;
            }
            if (k2.getLP() > 0) {
                gesamtLepK2 += k2.getLP();
                winK2++;
            }
            wundenK1 += k1.wunden;
            wundenK2 += k2.wunden;
            if (!log) {
                kampflog.clear(); // speed up gc
                kampflog = null;
            }
        }
        return getSiegwahrscheinlichkeit();
    }

    public int getDurchschnittlicheKaempflaenge() {
        return gesamtRunden / durchgefuehrteKaempfe;
    }


    public int getDurchgefuehrteKaempfe() {
        return durchgefuehrteKaempfe;
    }
    public String getKampfZusammenfassung() {
        return String.format("Siegeswahrscheinlichkeit: %3.2f%%\n"
                ,getSiegwahrscheinlichkeit())
   + String.format("Durchschnittliche Kampflänge: %d Aktionen\n"
           ,getDurchschnittlicheKaempflaenge())
            + String.format("Wegen Zeitüberschreitung abgebrochene Kämpfe: %d von %d\n"
          ,getZeitueberschreitungen(), getDurchgefuehrteKaempfe())
           + String.format("Wenn Held gewinnt, durchschnittliche Anzahl an verbleibenden LeP: %d\n",
                   getDurchschnittsLepK1())
           + String.format("Durschnittliche Anzahl an Wunden für Held: %d\n", getDurchschnittsWundenK1())
                   + String.format("Wenn Gegner gewinnt, durchschnittliche Anzahl an verbleibenden LeP: %d\n",
                           getDurchschnittsLepK2())
           + String.format("Durschnittliche Anzahl an Wunden für Gegner: %d\n", getDurchschnittsWundenK2())
                          ;
    }


    public String getKampfZusammenfassungHtml() {
        String s = getKampfZusammenfassung();
        return "<html>" +s.replace("\n","<br>");
    }
}
