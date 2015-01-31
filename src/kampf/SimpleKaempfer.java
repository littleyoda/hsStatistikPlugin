package kampf;

import statistikplugin.ManoeverVerwaltung;


public class SimpleKaempfer extends Kaempfer {
    
    public ManoeverVerwaltung liste = new ManoeverVerwaltung(); 
    
    
    protected void setzeAngreifMaenover() {
        angreifManeover.clear();
        angreifManeover.addAll(liste);
    }


    @Override
    protected void setzeParadeManoever() {
        // TODO Auto-generated method stub
        
    }
};
