package cryptomonaie;

/**
 *
 * @author Rami
 */
public abstract class Util {

    static void debug(Object ob, Exception ex, String message) {
        debug(ob, ex);
        System.err.println(message);
    }

    static void debug(Object ob, Exception ex) {
        System.err.println(ob.getClass());
        System.err.println(ex);

    }
}
