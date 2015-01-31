package kampf;

import statistikplugin.KaempferGUI;
import statistikplugin.ManoeverVerwaltung.MANOEVER;

public  class KaempferFromGUI extends Kaempfer {

    private KaempferGUI kGUI;

    public KaempferFromGUI(KaempferGUI kGUI) {
        this.kGUI = kGUI;
        kGUI.getKaempfer(this);
    }
    
    @Override
    protected void setzeAngreifMaenover() {
        //                     public void setzeAngreifMaenover() {
        angreifManeover.clear();
        if ((Integer) kGUI.finte.getValue() > 0) {
            angreifManeover.add(MANOEVER.FINTE, (Integer) kGUI.finte.getValue());
        }
        if ((Integer)  kGUI.wuchtschlag.getValue() > 0) {
            angreifManeover.add(MANOEVER.WUCHTSCHLAG, 
                                (Integer)  kGUI.wuchtschlag.getValue());
        }
    }


    @Override
    protected void setzeParadeManoever() {
        paradeManoever.clear();
        switch (kGUI.gegenhalten.getSelectedIndex()) {
            case 0: break; // nie
            case 1: paradeManoever.add(MANOEVER.GEGENHALTEN); break;
            default: 
                int istErs = erschwernisNaechsteAktion.calcAndAddToLog(null, null);
                int maxSollErs = kGUI.gegenhalten.getSelectedIndex() - 2;
                kampflog.add("      Erschwernis ist " + istErs + " und darf maximal sein: " + maxSollErs);
                
                if (istErs <= maxSollErs) {
                    paradeManoever.add(MANOEVER.GEGENHALTEN); break;
                }
                break;
        }
        
    }

}
