package kampf;

import java.util.ArrayList;
import statistikplugin.ManoeverVerwaltung;
import statistikplugin.ManoeverVerwaltung.MANOEVER;

import wurfel.WurfelWurf;
import wurfel.Wurfeln;


public abstract class Kaempfer {


    int wunden;
    int origLebenspunkte;
    int lebenspunkte;
    int at;
    int pa;
    int iniBasis;
    int iniAkt;
    int wundschwelle;
    //int erschwernisNaechsteAktion;
    int ruestung;
    int reihenfolge;
    int gueteAttacke;
    WurfelWurf schaden;
    //protected int paradeErschwernis;
    protected int basisSchaden;
    protected int aktSchaden;
    protected String name;
    protected ArrayList<String> kampflog;
    protected ManoeverVerwaltung angreifManeover;
    protected ManoeverVerwaltung paradeErschwernis;
    protected ManoeverVerwaltung erschwernisNaechsteAktion;
    protected ManoeverVerwaltung paradeManoever;

    public Kaempfer() {

    }

    public Kaempfer(String name, int lebenspunkte, int at, int pa, WurfelWurf schaden_, int ruestung
            , int iniBasis, int wundschwelle) {
        setValues(name, lebenspunkte, at, pa, schaden_, ruestung, iniBasis, wundschwelle);
    }

    public void setValues(String name, int lebenspunkte, int at, int pa, WurfelWurf schaden_, int ruestung
            , int iniBasis, int wundschwelle) {
        angreifManeover = new ManoeverVerwaltung(); 
        paradeErschwernis = new ManoeverVerwaltung();
        paradeManoever = new ManoeverVerwaltung();
        erschwernisNaechsteAktion = new ManoeverVerwaltung();
        this.iniBasis = iniBasis;
        this.name = name;
        this.ruestung = ruestung;
        this.lebenspunkte = lebenspunkte;
        this.origLebenspunkte = lebenspunkte;
        this.at = at;
        this.pa = pa;
        this.schaden = schaden_;
        this.wundschwelle = wundschwelle;
        reset();
    }

    public void setReihenfolge(int r) {
        reihenfolge = r;
    }
    public void reset() {
        lebenspunkte = origLebenspunkte;
        resetNaechsteAktionErschwernis();
        wunden = 0;

    }

    public void wuerfeleIni() {
        iniAkt = iniBasis + Wurfeln.w6(); 
    }


    public boolean attackeErfolgreich() {
        aktSchaden = 0;
        basisSchaden = 0;
        int w = Wurfeln.w20();
        // Berechne Erschwernis der Attacke un dokumente
        int atnew = at - 2 * wunden - angreifManeover.calcAndAddToLog("Manöver", kampflog) 
        - erschwernisNaechsteAktion.calcAndAddToLog("Erschwernis", kampflog);
        resetNaechsteAktionErschwernis();
        boolean erf = ((w <= atnew) && w != 20) || w == 1;
        if (erf) {
            gueteAttacke = atnew - w;
            kampflog.add("      Attacke erfolgreich (AT: " + atnew + " Gewürfelt: " + w + ")");
            wuerfeleSchadenAus();
        } else {
            kampflog.add("      Attacke nicht erfolgreich (" + w + " gewürfelt; " + atnew + " AT)");
            erschwernisNaechsteAktion.addAll(angreifManeover);
        }
        return erf;
    }

    private void wuerfeleSchadenAus() {
        // Schaden ist normaler Waffenschaden + ggf. Wuchtschlag
        basisSchaden = schaden.getWert(); 
        aktSchaden =  basisSchaden
        + angreifManeover.getManoeverWert(MANOEVER.WUCHTSCHLAG, 0); 
    }

    public void resetNaechsteAktionErschwernis() {
        erschwernisNaechsteAktion.clear();
    }

    public boolean paradeErfolgreich() {
        int w = Wurfeln.w20();
        kampflog.add("");
        int panew = pa - paradeErschwernis.calcAndAddToLog("Erschwernis", kampflog) 
                       - paradeManoever.calcAndAddToLog("Parademanoever", kampflog)
                       - erschwernisNaechsteAktion.calcAndAddToLog("Erschwernis", kampflog)
                       - 2 * wunden;
        resetNaechsteAktionErschwernis();
        boolean erf = ((w <= panew) && w != 20) || w == 1;
        String ausgang;
        if (!erf) {
            ausgang = "misslungen";
        } else {
            ausgang = "erfolgreich";
        }
        paradeErschwernis.clear();
        kampflog.add("      Parade " + ausgang + " (PA: " + panew + " Gewürfelt: "+ w + ")");
        return erf;
    }

    public boolean doGegenhalten() {
        int w = Wurfeln.w20();
        kampflog.add("");
        int panew = at - paradeErschwernis.calcAndAddToLog("Parade", kampflog) 
                       - paradeManoever.calcAndAddToLog("Parade", kampflog)
                       - erschwernisNaechsteAktion.calcAndAddToLog("Erschwernis", kampflog)
                       - 2 * wunden;
        resetNaechsteAktionErschwernis();
        boolean erf = ((w <= panew) && w != 20) || w == 1;
        gueteAttacke = panew - w;
        String ausgang;
        if (!erf) {
            ausgang = "misslungen";
        } else {
            ausgang = "erfolgreich";
        }
        paradeErschwernis.clear();
        kampflog.add("      Gegenhalten Attacke " + ausgang + " (AT: " + panew + " Gewürfelt: "+ w + ")");
        return erf;
    }

    public int getWaffenSchaden() {
        return aktSchaden;
    }

    public void setTP(int x) {
        int s = Math.max(0, x - ruestung);
        int neueWunden = s / wundschwelle;
        kampflog.add("      " + name + " LEP: " + lebenspunkte + " => " + (lebenspunkte - s)
                + "  [TP " + x + " => " + s + " SP]");
        if (neueWunden > 0) {
            kampflog.add("      Wunden: " + wunden + " => " + (neueWunden + wunden));
        }
        wunden += neueWunden;
        lebenspunkte -= s;
    }

    public int getLP() {
        return lebenspunkte;
    }



    public void greifeAn(Kaempfer k2) {
        setzeAngreifMaenover();
        k2.setzeParadeManoever();
        kampflog.add(this.name + " greift " + k2.name + " an");
        // Führe Attacke durch
        if (k2.paradeManoever.hatManoever(MANOEVER.GEGENHALTEN)) {
            boolean b1 = attackeErfolgreich();
            boolean b2 = k2.doGegenhalten();
            wuerfeleSchadenAus();
            k2.wuerfeleSchadenAus();                    
            if (b1 ^ b2) {
                if (b1) {
                    k2.setTP(getWaffenSchaden());
                    resetNaechsteAktionErschwernis();
                    k2.erschwernisNaechsteAktion.addAll(k2.paradeManoever);
                } 
                if (b2) {
                    setTP(k2.getWaffenSchaden());
                    k2.resetNaechsteAktionErschwernis(); 
                    erschwernisNaechsteAktion.addAll(angreifManeover);
                } 
            } else if (b1 && b2) {
                int g1 = gueteAttacke;
                int g2 = k2.gueteAttacke;
                kampflog.add("      K1: " + g1 + "/" + iniAkt 
                                + " K2: " + g2 + "/" + k2.iniAkt);
                if (iniAkt > k2.iniAkt) {
                    g1 += (iniAkt - k2.iniAkt);
                }
                if (iniAkt < k2.iniAkt) {
                    g2 += (k2.iniAkt - iniAkt);
                }
                if (g1 > g2) {
                   setTP(Math.round(k2.basisSchaden / 2));
                   k2.setTP(getWaffenSchaden());
                } else if (g1 < g2) {
                   k2.setTP(Math.round(basisSchaden / 2));
                   setTP(k2.getWaffenSchaden());
                } else {
                    k2.setTP(getWaffenSchaden());                   
                    setTP(k2.getWaffenSchaden());                    
                }
            } else {
                erschwernisNaechsteAktion.addAll(angreifManeover);
                k2.erschwernisNaechsteAktion.addAll(k2.paradeManoever);
            }
        } else {
            if (attackeErfolgreich()) {
                // ggf. Erschwernis aus Finte hinzufügen
                k2.paradeErschwernis.clear();
                k2.paradeErschwernis.add(angreifManeover.getManoeverByName(MANOEVER.FINTE));
                if (k2.paradeErfolgreich()) {

                } else {
                    k2.setTP(this.getWaffenSchaden());
                }
            } else {
                k2.resetNaechsteAktionErschwernis(); // Gegner hat nun keine Erschwernis mehr
            }
            angreifManeover.clear();
        }
        kampflog.add("====================================================");
    }



    protected abstract void setzeParadeManoever();

    protected abstract void setzeAngreifMaenover();



    public void setLog(ArrayList<String> kampfliste) {
        this.kampflog = kampfliste;
    }

}
