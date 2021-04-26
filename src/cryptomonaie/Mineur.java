package cryptomonaie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 *
 * @author Rami
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
    private final int clientPort = 3335;
    volatile Queue<MiningTask> clientRequests = new LinkedList<>();
    ServerSocket clientRequestSocket;
    Thread clientRequestThread;
    private final ExecutorService clientRequestExceutor;

    private final ExecutorService validationExceutor;

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

    public Mineur() throws IOException {
        this.clientRequestSocket = new ServerSocket(clientPort);
        this.clientRequestExceutor = Executors.newSingleThreadExecutor();
        this.validationExceutor = Executors.newSingleThreadExecutor();
    }

    void connectMulticast() {

        this.multicastThread = new Thread(() -> {

            try {
                Socket socket = new Socket(this.serverHost, this.multicastPort);
                while (true) {
                    ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
                    TransactionResponse response = (TransactionResponse) oi.readObject();
                    this.blockchaine.add(response.transaction, response.sel);
                    this.blockchaine.setDifficulte(response.diffculte);
                    
                    
                    
                }

            } catch (IOException ex) {
                Util.debug(this, ex, "Le mineur a déconnecté de mutlicast cannal");
            } catch (NonInserableException ex) {
                 Util.debug(this, ex, "Le mineur n'a pas reussi à inserer un block venant de multicast ");
            } catch (ClassNotFoundException ex) {
                 Util.debug(this, ex, "Le mineur n'a pas reussi à comprende un message de multicast ");
            }finally{
                this.close();
            }

        }
        );

        this.multicastThread.start();

    }

    void submitMiningTask(MiningTask task) {
        this.clientRequestExceutor.submit(task);
    }

    void submitValidationTask(MiningTask task) {
        this.validationExceutor.submit(new ValidationTask(task));
    }

    void listenClients() {
        this.clientRequestThread = new Thread(() -> {
            try {
                while (true) {
                    Socket client = this.clientRequestSocket.accept();
                    this.submitMiningTask(new MiningTask(this, client));
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

    public static void printMenu() {
        System.out.println("Sassir 1 pour afficher le hash de blockchaine");
    }

    void start() {
        this.closed = false;
        this.connectMulticast();
        this.listenClients();

    }

    public static void main(String[] args) {
        try {
            Mineur mineur = new Mineur();
            mineur.init();
            mineur.start();
            
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
        this.clientRequestExceutor.shutdown();
        this.validationExceutor.shutdown();
        this.closed = true;
    }

}
