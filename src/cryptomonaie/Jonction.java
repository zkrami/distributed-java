package cryptomonaie;

import java.io.Serializable;

/**
 *
 * La jonction de la blockchaine il se consiste d'un block et un sel.
 */
public class Jonction implements Serializable {

    Jonction precedent;
    public Bloc bloc;
    int sel;

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

    long getHash() {

        if (this.precedent == null) { // si premier block 
            return 0;
        }
        return precedent.getHash() * 31L + bloc.hashCode() * 31L * 31L + sel * 31L * 31L * 31L;
    }


    @Override
    public int hashCode() {
        return (int)this.getHash();
    }

}
