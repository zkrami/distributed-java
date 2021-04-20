package cryptomonaie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rami
 */
public class Mineur {

    Blockchaine blockchaine;
    final String serverHost = "127.0.0.1";
    private final int initPort = 3331;
    private final int transactionPort = 3332;
    private final int multicastPort = 3333;
    private final int clientPort = 3335;
    boolean interrupt;

    Thread multicastThread;

    void connectMulticast() {
        this.multicastThread = new Thread(() -> {

            try {
                Socket socket = new Socket(this.serverHost, this.multicastPort);

            } catch (IOException ex) {
                Logger.getLogger(Mineur.class.getName()).log(Level.SEVERE, null, ex);
            }

        });
        this.multicastThread.start();

    }

    void init() throws IOException, ClassNotFoundException {
        this.interrupt = false;
        Socket socket = new Socket(this.serverHost, this.initPort);
        System.out.println("Connecté à la porte d'initialisation");
        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
        ArrayList<Jonction> chaine = (ArrayList<Jonction>) is.readObject();
        System.out.println("La blockchaine a été reçue ");
        this.blockchaine = new Blockchaine(chaine);
        System.out.println("La blockchaine a un hash de " + this.blockchaine.hashCode());

    }

    public static void printMenu() {
        System.out.println("Sassir 1 pour afficher le hash de blockchaine");
    }

    public static void main(String[] args) {
        try {
            Mineur mineur = new Mineur();
            mineur.init();
            Scanner scan = new Scanner(System.in);
            while (true) {
                printMenu();
                int chocie = scan.nextInt();
                if (chocie == 0) {
                    mineur.close();
                    break;
                } else if (chocie == 1) {
                    System.out.println("La blockchaine a un hash de " + mineur.blockchaine.hashCode());
                }

            }
        } catch (Exception ex) {
            System.err.println("une erreur s'est produite");
        }

    }

    private void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
