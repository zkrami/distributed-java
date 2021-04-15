package cryptomonaie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Rami
 */
public class Etat {

    ArrayList<Integer> monaie; // dont les indicies sont des individues

    Etat(ArrayList<Integer> monaie) {
        this.monaie = monaie;
    }

    @Override
    public int hashCode() {
        return this.monaie.hashCode();
    }

}
