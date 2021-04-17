package cryptomonaie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author Rami
 */
public class Serveur {

    Blockchaine blockchaine;
    int difficulte = 3; // la difficulte courante de la blockchaine initializé à 3 

    // la porte que le serveur va utiliser pour rajouter des mineurs pour les messages multicast 
    private final int multicastPort = 3333;
    volatile ArrayList<Socket> mineurs = new ArrayList<>();
    ServerSocket multicastSocket;
    Thread multicastThread;

    // la porte que le serveur va utiliser pour écouter les nouvelles transactions 
    private final int transactionPort = 3332;
    ServerSocket transactionSocket;
    Thread transactionThread;
    // la porte que le serveur va utiliser pour initialiser les blockchaine des mineurs 
    // (utile au cas où le mineur n'a pas commencé avec les autres où il a été crashé) 
    private final int initPort = 3331;
    ServerSocket initSocket;
    Thread initThread;

    volatile boolean interrupt = false;

    Serveur() throws IOException {
        blockchaine = new Blockchaine();
        blockchaine.init();
        this.transactionSocket = new ServerSocket(this.transactionPort);
        this.initSocket = new ServerSocket(this.initPort);
        this.multicastSocket = new ServerSocket(this.multicastPort);
    }

    void listenMulticast() {

        try {

            this.multicastSocket.setSoTimeout(100);
            while (!interrupt) {

                try {

                    Socket socket = this.multicastSocket.accept();
                    System.out.println("Un nouveau mineur a été connecté");
                    this.mineurs.add(socket);

                } catch (SocketTimeoutException ex) {

                }

            }

        } catch (IOException exception) {

            Util.debug(this, exception);
        } finally {
            try {
                if (!this.multicastSocket.isClosed()) {
                    this.multicastSocket.close();
                }
            } catch (IOException ex) {
                Util.debug(this, ex);
            }
        }
    }

    void listenInit() {

        try {

            this.initSocket.setSoTimeout(100);
            while (!interrupt) {

                try {

                    Socket socket = this.initSocket.accept();
                    System.out.println("Une mineur demande l'état actuel de la blocchaine ");
                    ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
                    // ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
                    os.writeObject(this.blockchaine.chaine);
                    System.out.println("La blockchaine a été envoyée ");

                    socket.close();

                } catch (SocketTimeoutException ex) {

                }

            }

        } catch (IOException exception) {

            Util.debug(this, exception);
        } finally {
            try {
                if (!this.initSocket.isClosed()) {
                    this.initSocket.close();
                }
            } catch (IOException ex) {
                Util.debug(this, ex);
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

                } catch (SocketTimeoutException ex) {

                }

            }

        } catch (IOException exception) {

            Util.debug(this, exception);
        } finally {
            try {
                if (!this.transactionSocket.isClosed()) {
                    this.transactionSocket.close();
                }
            } catch (IOException ex) {
                Util.debug(this, ex);
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
                }else if(chocie == 1){
                     System.out.println("La blockchaine a un hash de " + serveur.blockchaine.hashCode());
                }

            }

            System.out.println("Le serveur s'est arrété ");

        } catch (IOException ex) {
            System.err.println("Le serveur ne réussie pas à ouvrir les portes suviantes: 3331,3332,3333");
        }

    }

}
