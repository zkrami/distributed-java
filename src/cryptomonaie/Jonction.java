
package cryptomonaie;

import java.util.ArrayList;

/**
 *
 * @author Rami
 */
public class Jonction {

    Jonction precedent;
    Bloc bloc;
    int sel;
    int hash = -1;

    public Jonction(Jonction precedent, Bloc bloc) {
        this.bloc = bloc;
        this.precedent = precedent;
    }

    static int countFirstZeros(int number) {
        if (number == 0) {
            return 0;
        }
        int cnt = 0;
        while (number % 2 == 0) {
            cnt++;
            number /= 2;
        }
        return cnt;
    }

    // verifie si le hash de la jonction avec le sel qu'elle a et avec 
    // le bloc cohérent a une diffculté égale à celui de parmètre. 
    boolean inserable(int diffculte) {
        return countFirstZeros(this.getHash()) == diffculte;
    }

    // verifie si le bloc est cohérent avec le dernier état 
    // (en appliquant la transaction on aura un état égale à l'état de bloc)
    boolean verif(Bloc bloc) {
        
        
        return true;
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

}
