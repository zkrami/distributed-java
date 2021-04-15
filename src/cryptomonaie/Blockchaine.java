package cryptomonaie;

import java.util.ArrayList;

/**
 *
 * @author Rami
 */
public class Blockchaine {

    ArrayList<Jonction> chaine;

    Jonction getLast() {
        return this.chaine.get(this.chaine.size() - 1);
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
    static boolean inserable(Jonction jonction, int diffculte) {
        return countFirstZeros(jonction.getHash()) == diffculte;
    }

    boolean checkRange(int index) {
        return index >= 0 && index < this.getLast().bloc.etat.monaie.size();
    }
    // verifie si le bloc est cohérent avec le dernier état 
    // (en appliquant la transaction on aura un état égale à l'état de bloc)

    boolean verif(Bloc bloc) {
        Jonction last = this.getLast();

        // verifie si la transaction est valide 
        if (!checkRange(bloc.transaction.payeur)) {
            return false;
        }
        if (!checkRange(bloc.transaction.receveur)) {
            return false;
        }
        // on peut aussi vaider si le payeur a le montant correspondant 
        // ou on suppose que la dette est autorisé 

        if (bloc.etat.monaie.size() != last.bloc.etat.monaie.size()) {
            return false;
        }

        // verifie si l'état est coherent après la transaction 
        for (int i = 0; i < last.bloc.etat.monaie.size(); i++) {

            int current = last.bloc.etat.monaie.get(i);

            if (i == bloc.transaction.payeur) {

                current -= bloc.transaction.somme;

            } else if (i == bloc.transaction.receveur) {

                current += bloc.transaction.receveur;

            }

            if (current != bloc.etat.monaie.get(i)) {
                return false;
            }

        }
        return true;

    }
}
