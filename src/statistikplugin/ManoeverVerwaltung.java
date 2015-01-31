package statistikplugin;

import java.util.ArrayList;


public class ManoeverVerwaltung {

    public enum MANOEVER { FINTE, WUCHTSCHLAG, GEGENHALTEN, WUNDE }

    public class manoeverInfo {
        public MANOEVER manoever;
        public  Integer wert;
        
        manoeverInfo(MANOEVER m, Integer w) {
            manoever = m;
            wert = w;
        }
    }


    public void addAll(ManoeverVerwaltung quelle) {
        for (manoeverInfo mi : quelle.angreifManeover) {
            add(mi);
        }
    }
    ArrayList<manoeverInfo> angreifManeover; 

    public ManoeverVerwaltung() {
        angreifManeover = new ArrayList<manoeverInfo>();    
    }
    
    public void clear() {
        angreifManeover.clear();
    }

    public Integer getManoeverWert(MANOEVER m, Integer defaultWert) {
        for (manoeverInfo mi : angreifManeover) {
            if (mi.manoever.equals(m)) {
                return mi.wert;
            }
        }
        return defaultWert;
    }

    public void add(MANOEVER m, Integer wert) {
        add(new manoeverInfo(m, wert));
    }
    
    public void add(manoeverInfo mi) {
        if (mi != null) {
            angreifManeover.add(mi);
        }
    }
    public manoeverInfo getManoeverByName(MANOEVER m) {
        for (manoeverInfo mi : angreifManeover) {
            if (mi.manoever.equals(m)) {
                return mi;
            }
        }
        return null;
    }
    
    public boolean hatManoever(MANOEVER m) {
        for (manoeverInfo mi : angreifManeover) {
            if (mi.manoever.equals(m)) {
                return true;
            }
        }
        return false;
    }
    
    public int calcAndAddToLog(String desc, ArrayList<String> kampflog) {
        int attckeErschwernis = 0;
        for (manoeverInfo mi : angreifManeover) {
            if (kampflog != null) {
                kampflog.add("      " + desc + " " + mi.manoever + " " 
                        + ((mi.wert == null) ? "" : mi.wert));
            }
            if (mi.wert != null) {
                attckeErschwernis += mi.wert;
            }
        }
        return attckeErschwernis;
    }

    public void add(MANOEVER m) {
        if (m.equals(MANOEVER.GEGENHALTEN)) {
            add(m, 4);
        } else {
            add(m, null);
        }
    }
}
