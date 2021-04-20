package cryptomonaie;

import java.util.ArrayList;

/**
 *
 * @author Rami
 */
public class Blockchaine {

    ArrayList<Jonction> chaine;

    void add(Transaction transaction, int sel, int difficulte) throws NonInserableException {
        Etat etat = new Etat(this.getLast().bloc.etat);
        Bloc bloc = new Bloc(etat, transaction);
        if (!validate(transaction)) {
            throw new NonInserableException();
        }
        bloc.applyTransaction();

//        if (!verif(bloc)) { // @TODO enleve cette condition, elle est redondante 
//            throw new NonInserableException();
//        }
        
        Jonction jon = new Jonction(this.getLast(), bloc);
        jon.setSel(sel);
        if (!inserable(jon, difficulte)) {
            throw new NonInserableException();
        }
        chaine.add(jon);
    }

    Blockchaine(ArrayList<Jonction> chaine) {
        this.chaine = chaine;
    }

    Blockchaine() {
        chaine = new ArrayList<>();
    }

    void init() {
        // l’initialisation de la répartition initiale  
        // chaque individue commence avec un solde de 100 monnaie 

        ArrayList<Integer> individues = new ArrayList<>(100);
        for (int i = 0; i < individues.size(); i++) {
            individues.set(i, 100);
        }

        Etat etat = new Etat(individues);
        this.chaine.add(new Jonction(null, new Bloc(etat, null)));
    }

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
    // le bloc cohérent a une diffculté égale à celui du paramètre. 
    static boolean inserable(Jonction jonction, int diffculte) {
        return countFirstZeros(jonction.getHash()) == diffculte;
    }

    // verifie si la transaction est valide 
    // (verifie si le bloc est cohérent avec le dernier état)
    boolean validate(Transaction transaction) {
        Jonction last = this.getLast();

        if (!checkRange(transaction.payeur)) {
            return false;
        }
        if (!checkRange(transaction.receveur)) {
            return false;
        }
        // vaider si le payeur a le montant correspondant 
        if (last.bloc.etat.monaie.get(transaction.payeur) < transaction.somme) {
            return false;
        }
        return true;
    }

    boolean checkRange(int index) {
        return index >= 0 && index < this.getLast().bloc.etat.monaie.size();
    }

    // verifie si le bloc est cohérent avec le dernier état 
    // (en appliquant la transaction on aura un état égale à l'état de bloc)
//    boolean verif(Bloc bloc) {
//        Jonction last = this.getLast();
//
//        // verifie si la transaction est valid 
//        if (!validate(bloc.transaction)) {
//            return false;
//        }
//
//        if (bloc.etat.monaie.size() != last.bloc.etat.monaie.size()) {
//            return false;
//        }
//
//        // verifie si l'état est coherent après la transaction 
//        for (int i = 0; i < last.bloc.etat.monaie.size(); i++) {
//
//            int current = last.bloc.etat.monaie.get(i);
//
//            if (i == bloc.transaction.payeur) {
//
//                current -= bloc.transaction.somme;
//
//            } else if (i == bloc.transaction.receveur) {
//
//                current += bloc.transaction.receveur;
//
//            }
//
//            if (current != bloc.etat.monaie.get(i)) {
//                return false;
//            }
//
//        }
//        return true;
//
//    }

    @Override
    public int hashCode() {
        return this.chaine.hashCode();
    }

}
