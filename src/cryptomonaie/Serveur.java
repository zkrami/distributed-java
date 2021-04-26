package cryptomonaie;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Rami
 */
public class Serveur {

    volatile Blockchaine blockchaine;
    volatile int difficulte = 3; // la difficulte courante de la blockchaine initializé à 3 

    // la porte que le serveur va utiliser pour rajouter des mineurs pour les messages multicast 
    private final int multicastPort = 3333;
    volatile ArrayList<Socket> mineurs = new ArrayList<>();
    ServerSocket multicastSocket;
    Thread multicastThread;

    // la porte que le serveur va utiliser pour écouter les nouvelles transactions 
    private final int transactionPort = 3332;
    ServerSocket transactionSocket;
    Thread transactionThread;
    private final ExecutorService transactionExecutor; // une service pour ne pas bloquer l'ecoute de requetes 

    // la porte que le serveur va utiliser pour initialiser les blockchaine des mineurs 
    // (utile au cas où le mineur n'a pas commencé avec les autres où il a été crashé) 
    private final int initPort = 3331;
    ServerSocket initSocket;
    Thread initThread;
    private final ExecutorService initExceutor;  // une service pour ne pas bloquer l'ecoute de requetes 

    volatile boolean interrupt = false;
    private final ExecutorService multicastExceutor;

    Serveur() throws IOException {
        blockchaine = new Blockchaine();
        blockchaine.init();

        this.transactionSocket = new ServerSocket(this.transactionPort);
        this.transactionExecutor = Executors.newSingleThreadExecutor(); // Tasks are guaranteed to execute * sequentially, and no more than one task will be active at any * given time.

        this.initSocket = new ServerSocket(this.initPort);
        this.initExceutor = Executors.newSingleThreadExecutor(); 

        this.multicastSocket = new ServerSocket(this.multicastPort);
        this.multicastExceutor = Executors.newSingleThreadExecutor(); 
    }

    Date lastInsert = new java.util.Date();
    synchronized void nextDifficulte() {
        Date now = new java.util.Date();
        long diff = now.getTime() - lastInsert.getTime(); 
        diff /= (1e9 * 60); // difference in minutes 
        if(diff <= 10){
            difficulte++;            
        }else {
           difficulte -- ; 
        }
        blockchaine.setDifficulte(difficulte);
        lastInsert = now ; 
    }

    void multicast(TransactionResponse response) {
        System.out.println("Muticast d'une nouvelle transaction");
        this.multicastExceutor.submit(new MulticastTask(this, response));
    }

    void listenMulticast() {

        try {

            this.multicastSocket.setSoTimeout(100);
            while (!interrupt) {

                try {

                    Socket socket = this.multicastSocket.accept();
                    System.out.println("Un nouveau mineur a été connecté au canal de multitask");
                    this.mineurs.add(socket);

                } catch (SocketTimeoutException ex) {

                }

            }

        } catch (IOException exception) {

            Util.debug(this, exception);
        } finally {
            try {
                this.multicastSocket.close();
            } catch (IOException ex) {
            }
        }
    }

    void listenInit() {

        try {

            this.initSocket.setSoTimeout(100);
            while (!interrupt) {

                try {

                    Socket socket = this.initSocket.accept();
                    System.out.println("Un mineur demande l'état actuel de la blocchaine ");
                    this.initExceutor.submit(new InitTask(this, socket));

                } catch (SocketTimeoutException ex) {

                }

            }

        } catch (IOException exception) {
            Util.debug(this, exception);
        } finally {
            try {
                this.initSocket.close();
                this.initExceutor.shutdownNow();
            } catch (IOException ex) {

            }
        }
    }

    void listenTransaction() {

        try {

            this.transactionSocket.setSoTimeout(100);
            while (!interrupt) {

                try {

                    Socket socket = this.transactionSocket.accept();
                    System.out.println("Treatement d'une nouvelle transaction");
                    this.transactionExecutor.submit(new TransactionTask(this, socket));

                } catch (SocketTimeoutException ex) {

                }

            }

        } catch (IOException exception) {

            Util.debug(this, exception);
        } finally {
            try {
                this.transactionSocket.close();
                this.transactionExecutor.shutdownNow();
            } catch (IOException ex) {

            }
        }

    }

    void listen() { // listen to all sockets 
        this.interrupt = false;
        this.initThread = new Thread(() -> {
            this.listenInit();
        });
        this.transactionThread = new Thread(() -> {
            this.listenTransaction();
        });
        this.multicastThread = new Thread(() -> {
            this.listenMulticast();
        });

        this.initThread.start();
        this.transactionThread.start();
        this.multicastThread.start();

    }

    public static void printMenu() {

        System.out.println("Sassir 1 pour afficher le hash de blockchaine");
    }

    public void close() {
        this.interrupt = true;

        this.mineurs.forEach(mineurs -> {
            try {
                mineurs.close();
            } catch (Exception ex) {
            }
        });
        this.mineurs.clear();
    }

    public static void main(String[] args) {
        try {
            Serveur serveur = new Serveur();
            System.out.println("Serveur a été initializé ");
            System.out.println("La blockchaine a un hash de " + serveur.blockchaine.hashCode());
            Scanner scan = new Scanner(System.in);
            serveur.listen();
            while (true) {
                printMenu();
                int chocie = scan.nextInt();
                if (chocie == 0) {
                    serveur.close();
                    break;
                } else if (chocie == 1) {
                    System.out.println("La blockchaine a un hash de " + serveur.blockchaine.hashCode());
                }

            }

            System.out.println("Le serveur s'est arrété ");

        } catch (IOException ex) {
            System.err.println("Le serveur ne réussie pas à ouvrir les portes suviantes: 3331,3332,3333");
        } catch (Exception ex) {
            System.err.println("une erreur s'est produite");
        }

    }

}
