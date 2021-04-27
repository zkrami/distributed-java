package cryptomonaie;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/*
* Le programme de client 
*/
public class Client {

    private final ExecutorService transactionExecutor;

    volatile boolean closed = false;

    public Client() {
        this.transactionExecutor = Executors.newFixedThreadPool(5);
    }

    public void close() {
        this.closed = true ; 
        this.transactionExecutor.shutdown();
    }

    public static void printMenu() {
        System.out.println("Sassir 1 pour faire une nouvelle transaction");
        System.out.println("Sassir 0 pour fermer");
    }

    public static Transaction readTransaction() {
        try {
            Scanner scan = new Scanner(System.in);
            int somme, payeur, receveur;
            do {
                System.out.println("Veuillez saisir le montant de la transaction");
                somme = scan.nextInt();
                if (somme <= 0) {
                    System.err.println("Veuillez saisir un montant strictement positif ");
                }
            } while (somme <= 0);

            do {
                System.out.println("Veuillez saisir l'indice du payeur");
                payeur = scan.nextInt();
                if (payeur < 0) {
                    System.err.println("Veuillez saisir une indice positive ");
                }
            } while (payeur < 0);

            do {
                System.out.println("Veuillez saisir l'indice du receveur");
                receveur = scan.nextInt();
                if (receveur < 0) {
                    System.err.println("Veuillez saisir une indice positive ");
                }
            } while (receveur < 0);

            Transaction transaction = new Transaction(somme, payeur, receveur);
            return transaction;
        } catch (InputMismatchException ex) {
            System.err.println("Veuillez saisir seulement des entier merci pour ressayer ");
        }
        return null;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        while (true) {

            printMenu();
            int choice;
            try {
                choice = scan.nextInt();
            } catch (InputMismatchException ex) {
                System.err.println("Veuillez saisir seulement des entier merci pour ressayer ");
                continue;
            }
            if (choice == 0) {

                break;
            } else if (choice == 1) {
                Transaction tr = readTransaction();
                if (tr == null) {
                    continue;
                }

            }

        }
    }
}
