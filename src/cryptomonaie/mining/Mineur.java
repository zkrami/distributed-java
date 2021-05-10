package cryptomonaie.mining;

import cryptomonaie.Blockchaine;
import cryptomonaie.Jonction;
import cryptomonaie.NonInserableException;
import cryptomonaie.TransactionResponse;
import cryptomonaie.Util;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * Le programme du mineur
 */
public class Mineur {

    // Reliable blockchaine only appended by multicast messages 
    Blockchaine blockchaine;
    final String serverHost = "127.0.0.1";

    // server ports 
    // init port 
    private final int initPort = 3331;

    // transaction port 
    final int transactionPort = 3332;

    // multicast port 
    Thread multicastThread;
    private final int multicastPort = 3333;

    // minor ports 
    // la port sur laquelle le mineur va reçevoir de requetes des clients 
    private int clientPort = 0;
    volatile Queue<MiningTask> clientRequests = new LinkedList<>();
    ServerSocket clientRequestSocket;
    Thread clientRequestThread;
    private final ExecutorService miningExceutor;

    private ExecutorService validationExceutor;

    volatile boolean interrupt;

    // _last et _difficulte sont utilisés pour continuer le calcul localement 
    // au cas ou le serveur n'as pas validé une transaction de mineur, ils sont réinitialisé à null 
    volatile Jonction _last = null;
    private boolean closed;

    /**
     *
     * Soit le pointeur _last (cad) la dernière jonction qui a été rajouté
     * localement sinon le dernier du blockchaine
     *
     * @return Jonction
     */
    public Jonction getLast() {
        if (_last == null) {
            return this.blockchaine.getLast();
        }
        return _last;
    }

    void setLast(Jonction jon) {
        this._last = jon;
        this.nextDifficulte();
    }

    // estimated difficulty used to process the transaction without the need to wait for the servce difficulty response 
    volatile Integer _difficulte = null;
    Date lastInsert = new java.util.Date();

    public int getDifficulte() {

        if (_difficulte == null) {
            return this.blockchaine.getDifficulte();
        }
        return _difficulte;
    }

    synchronized void nextDifficulte() {
        if (_difficulte == null) {
            _difficulte = this.blockchaine.getDifficulte();
        }
        Date now = new java.util.Date();
        long diff = now.getTime() - lastInsert.getTime();
        diff /= (1e9 * 60); // difference in minutes 
        if (diff <= 10) {
            _difficulte++;
        } else {
            _difficulte--;
        }
        lastInsert = now;
    }

    void reset() {
        this._last = null;
        this._difficulte = null;
    }

    void resubmitValidation() {
        // refaise toutes les taches dans la queue 
        List<Runnable> tasks = this.validationExceutor.shutdownNow();
        this.validationExceutor = Executors.newSingleThreadExecutor();
        tasks.stream().forEach(
                r -> {
                    this.submitValidationTask(((ValidationTask) r).task);
                }
        );
    }

    /**
     * La port sur laquelle le mineur va opérer 0 pour laisser le système
     * choisir le port
     *
     * @param port
     * @throws IOException
     */
    public Mineur(int port) throws IOException {
        this.clientPort = port;
        this.clientRequestSocket = new ServerSocket(clientPort);
        this.miningExceutor = Executors.newSingleThreadExecutor();
        this.validationExceutor = Executors.newSingleThreadExecutor();
    }

    void connectMulticast() {

        this.multicastThread = new Thread(() -> {

            try {
                MineurServeur serveur = new MineurServeur(new Socket(this.serverHost, this.multicastPort));
                while (!closed) {
                    TransactionResponse response = (TransactionResponse) serveur.readTransactionResponse();

                    this.blockchaine.add(response.transaction, response.sel);
                    this.blockchaine.setDifficulte(response.diffculte);
                    System.out.println("(Multicast): Le serveur a inséré une nouvelle jonction ");
                    System.out.println(blockchaine.hashCode());

                }

            } catch (IOException ex) {
                Util.debug(this, ex, "Le mineur a déconnecté du canal mutlicast");
            } catch (NonInserableException ex) {
                Util.debug(this, ex, "Le mineur n'a pas reussi à inserer un block venant du canal multicast ");
            } catch (ClassNotFoundException ex) {
                Util.debug(this, ex, "Le mineur n'a pas reussi à comprende un message de multicast ");
            } finally {
                this.close();
            }

        }
        );

        this.multicastThread.start();

    }

    void submitMiningTask(MiningTask task) {
        this.miningExceutor.submit(task);
    }

    void submitValidationTask(MiningTask task) {
        this.validationExceutor.submit(new ValidationTask(task));
    }

    void listenClients() {
        this.clientRequestThread = new Thread(() -> {
            System.out.println("Le mineur attends des clients sur le port: " + this.clientRequestSocket.getLocalPort());
            try {
                while (!closed) {
                    Socket client = this.clientRequestSocket.accept();
                    System.out.println("Un nouveau client s'est connecté ");
                    this.submitMiningTask(new MiningTask(this, new MineurClient(client)));
                }

            } catch (IOException ex) {
                Util.debug(this, ex);

            } finally {

                this.close();

            }

        });
        this.clientRequestThread.start();
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

    void start() throws IOException, ClassNotFoundException {
        this.closed = false;
        this.init();
        this.connectMulticast();
        this.listenClients();

    }

    public static void printMenu() {
        System.out.println("Sassir 1 pour afficher le hash de blockchaine");
    }

    public static void main(String[] args) {
        try {
            int port = 0;
            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
            Mineur mineur = new Mineur(port);
            mineur.start();

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
                    mineur.close();
                    break;
                } else if (choice == 1) {
                    System.out.println("La blockchaine a un hash de " + mineur.blockchaine.hashCode());
                }

            }
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Le mineur n'a pas réussi à se connecter au serveur ");
        }

    }

    private synchronized void close() {

        if (this.closed) {
            return;
        }

        try {
            this.clientRequestSocket.close();
        } catch (IOException ex) {
        }
        this.interrupt = true;
        this.clientRequestThread.interrupt();
        this.multicastThread.interrupt();
        this.miningExceutor.shutdown();
        this.validationExceutor.shutdown();
        this.closed = true;
    }

}
