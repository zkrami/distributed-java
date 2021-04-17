package cryptomonaie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 *
 * @author Rami
 */
public class Serveur {

    Blockchaine blockchaine;
    int difficulte = 3; // la difficulte courante de la blockchaine initializé à 3 

    // la porte que le serveur va utiliser pour rajouter des mineurs pour les messages multicast 
    private int multicastPort = 3333;
    volatile ArrayList<Socket> mineurs = new ArrayList<>();
    ServerSocket multicastSocket;
    Thread multicastThread;

    // la porte que le serveur va utiliser pour écouter les nouvelles transactions 
    private int transactionPort = 3332;
    ServerSocket transactionSocket;
    Thread transactionThread;
    // la porte que le serveur va utiliser pour initialiser les blockchaine des mineurs 
    // (utile au cas où le mineur n'a pas commencé avec les autres où il a été crashé) 
    private int initPort = 3331;
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
                this.multicastSocket.close();
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
                    ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
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
                this.initSocket.close();
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
                this.transactionSocket.close();
            } catch (IOException ex) {
                Util.debug(this, ex);
            }
        }

    }

    void listen() { // listen to all sockets 

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
        this.multicastThread.run();

    }

    public static void main(String[] args) {
        try {
            Serveur serveur = new Serveur();

            serveur.listen();

        } catch (IOException ex) {
            System.err.println("Le serveur ne réussie pas à ouvrir les portes suviantes: 3331,3332,3333");
        }

    }

}
