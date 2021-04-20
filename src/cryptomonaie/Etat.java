package cryptomonaie;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Rami
 */
public class Etat implements Serializable, Cloneable {

    ArrayList<Integer> monaie; // dont les indicies sont des individues

    Etat(ArrayList<Integer> monaie) {
        this.monaie = monaie;
    }

    Etat(Etat etat) { // copy constructor 
        this((ArrayList<Integer>) etat.monaie.clone());
    }


    @Override
    public int hashCode() {
        return this.monaie.hashCode();
    }

}
