package cryptomonaie;

import cryptomonaie.client.Client;
import cryptomonaie.mining.Mineur;
import cryptomonaie.serveur.Serveur;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rami
 */
public class Tester {

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {

            Serveur.main(args);
        }).start();
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {

            }
            Mineur.main(new String[]{"", "8777"});
        }).start();
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {

            }
            Mineur.main(new String[]{"", "8778"});
        }).start();
        Thread.sleep(100);
        Client.main(args);

    }
}
