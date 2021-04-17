/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptomonaie;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Rami
 */
public class Client {

    Blockchaine blockchaine;
    final String serverHost = "127.0.0.1";
    private final int initPort = 3331;
    private final int transactionPort = 3332;
    private final int multicastPort = 3333;

    void init() throws IOException, ClassNotFoundException {

        Socket socket = new Socket(this.serverHost, this.initPort);
        System.out.println("Connecté à la porte d'initialisation");
        ObjectInputStream is = new ObjectInputStream(socket.getInputStream());
        ArrayList<Jonction> chaine = (ArrayList<Jonction>) is.readObject();
        System.out.println("La blockchaine a été reçue ");
        this.blockchaine = new Blockchaine(chaine);
        System.out.println("La blockchaine a un hash de " + this.blockchaine.hashCode());
    }

    public static void main(String[] args) {
        try {
            Client client =  new Client(); 
            client.init(); 
        } catch (Exception ex) {
            System.err.println("une erreur s'est produite");
        }

    }
}
