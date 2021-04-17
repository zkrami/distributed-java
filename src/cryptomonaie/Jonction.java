package cryptomonaie;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Rami
 */
public class Jonction implements Serializable {

    Jonction precedent;
    Bloc bloc;
    int sel;
    int hash = -1;

    public Jonction(Jonction precedent, Bloc bloc) {
        this.bloc = bloc;
        this.precedent = precedent;
    }

    public void setSel(int sel) {
        this.sel = sel;
    }

    public int getSel() {
        return sel;
    }

    int getHash() {
        if (this.hash == -1) { //  calcule le hash si il n'est pas calculé 
            this.calcHash();
        }
        return this.hash;
    }

    void calcHash() {

        this.hash = 0;
        if (this.precedent == null) { // si premier block 
            return;
        }

        this.hash = precedent.hash * 31 + bloc.hashCode() * 31 * 31 + sel * 31 * 31 * 31;

    }

    @Override
    public int hashCode() {
        return this.getHash();
    }

}
