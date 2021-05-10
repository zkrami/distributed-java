package cryptomonaie;

/**
 *
 * Debug utils 
 */
public abstract class Util {

    /**
     * Pour les exceptions connues
     */
    static public void debug(Object ob, Exception ex, String message) {
        System.err.println(message);
    }

    /**
     * Pour les exceptions non connues
     */
    static public void debug(Object ob, Exception ex) {
        System.err.println(ob.getClass());
        System.err.println(ex);

    }
}
