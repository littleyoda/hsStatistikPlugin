package wurfel;

import java.util.Random;


public class Wurfeln {
    static Random rnd = new Random();

    public static int w6() {
        return rnd.nextInt(6) + 1;
    }

    public static int w20() {
        return rnd.nextInt(20) + 1;
    }
}
