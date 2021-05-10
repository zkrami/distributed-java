package cryptomonaie;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * L'etat de la chaine il se consiste des individues et leurs montants. 
 */
public class Etat implements Serializable, Cloneable {

    public ArrayList<Integer> monaie; // dont les indicies sont des individues

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
