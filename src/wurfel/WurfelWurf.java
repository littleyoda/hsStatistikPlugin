package wurfel;

import java.util.Random;


public class WurfelWurf {

    int w20;
    int w6;
    int zusatz;
    Random rnd;
    
    public WurfelWurf(int w20, int w6, int zusatz) {
        this.w20 = w20;
        this.w6 = w6;
        this.zusatz = zusatz;
    }
    
    public int getWert() {
        int x = 0;
        for (int i=0; i < w20; i++) {
            x += Wurfeln.w20();
        }
        for (int i=0; i < w6; i++) {
            x += Wurfeln.w6();
        }
        x += zusatz;
        return x;
    }
    
}
