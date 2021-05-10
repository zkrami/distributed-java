package cryptomonaie;

import java.util.ArrayList;

/*
*   Blockchaine se consiste d'une list de jonction 
 */
public class Blockchaine {

    int difficulte = 3; // la difficulte courante de la blockchaine initializé à 3 

    public void setDifficulte(int difficulte) {
        this.difficulte = difficulte;
    }

    public int getDifficulte() {
        return difficulte;
    }

    public ArrayList<Jonction> chaine;

    public static Jonction newJonction(Jonction last, Transaction transaction) {

        Etat etat = new Etat(last.bloc.etat);
        Bloc bloc = new Bloc(etat, transaction);

        bloc.applyTransaction();

        return new Jonction(last, bloc);

    }

    public void add(Transaction transaction, int sel) throws NonInserableException {
        if (!validate(transaction)) {
            throw new NonInserableException();
        }
        int difficulte = this.getDifficulte();

        Jonction jon = newJonction(this.getLast(), transaction);
        jon.setSel(sel);
        if (!inserable(jon, difficulte)) {
            throw new NonInserableException();
        }
        chaine.add(jon);
    }

    public Blockchaine(ArrayList<Jonction> chaine) {
        this.chaine = chaine;
    }

    public Blockchaine() {
        chaine = new ArrayList<>();
    }

    public void init() {
        // l’initialisation de la répartition initiale  
        // chaque individue commence avec un solde de 100 monnaie 

        ArrayList<Integer> individues = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            individues.add(100);
        }

        Etat etat = new Etat(individues);
        this.chaine.add(new Jonction(null, new Bloc(etat, null)));
    }

    public Jonction getLast() {
        return this.chaine.get(this.chaine.size() - 1);
    }

    static int countFirstZeros(long number) {
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
    public static boolean inserable(Jonction jonction, int diffculte) {
        return countFirstZeros(jonction.getHash()) == diffculte;
    }

    // verifie si la transaction est valide 
    // (verifie si le bloc est cohérent avec le dernier état)
    public static boolean validate(Transaction transaction, Jonction last) {

        if (!checkRange(transaction.payeur, last)) {
            return false;
        }
        if (!checkRange(transaction.receveur, last)) {
            return false;
        }
        // vaider si le payeur a le montant correspondant 
        if (last.bloc.etat.monaie.get(transaction.payeur) < transaction.somme) {
            return false;
        }
        return true;
    }

    boolean validate(Transaction transaction) {
        return validate(transaction, this.getLast());
    }

    static boolean checkRange(int index, Jonction last) {
        return index >= 0 && index < last.bloc.etat.monaie.size();
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
