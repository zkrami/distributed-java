package cryptomonaie.client;

import cryptomonaie.Transaction;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* Le programme de client 
 */
public class Client {

    private final ExecutorService transactionExecutor;
    static final String mineurHost = "127.0.0.1";
    volatile boolean closed = false;

    public Client() {
        this.transactionExecutor = Executors.newFixedThreadPool(5);
    }

    public void submitTransaction(Transaction transaction, int mineurPort) {
        System.out.println("Envois d'une transaction au mineur ");
        this.transactionExecutor.submit(new ClientTransactionTask(transaction, mineurPort));
    }

    public void close() {
        this.closed = true;
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

    public static int readInt() {
        while (true) {
            try {
                return scan.nextInt();
            } catch (InputMismatchException ex) {
                System.err.println("Veuillez saisir seulement des entier merci pour ressayer ");
            }
        }
    }
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {

        Client client = new Client();
        (new Thread(() -> {
            int tries = 0;
            while (tries != 10) {
                client.submitTransaction(new Transaction(10, 1, 2), 8777);
                client.submitTransaction(new Transaction(10, 1, 2), 8778);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                }
                tries++;
            }
        })).start();
        while (true) {

            printMenu();
            int choice = readInt();

            if (choice == 0) {

                break;
            } else if (choice == 1) {
                Transaction tr = readTransaction();
                if (tr == null) {
                    continue;
                }

                client.submitTransaction(tr, 8777);

            } else if (choice == 2) { // for speed test 
                client.submitTransaction(new Transaction(10, 1, 2), 8777);
            }

        }
    }
}
