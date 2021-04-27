package cryptomonaie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * Minor serveur: Wrapper de la connexion entre le mineur et le serveur. Le
 * mineur communique avec le serveur un utilisant cette classe.
 */
public class MineurServeur {

    private Socket socket;

    public MineurServeur(Socket socket) {
        this.socket = socket;
    }

    public void close() {

        try {
            socket.close();
        } catch (Exception ex) {
        }
    }

    public TransactionResponse readTransactionResponse() throws IOException, ClassNotFoundException {
        ObjectInputStream oi = new ObjectInputStream(socket.getInputStream());
        return (TransactionResponse) oi.readObject();
    }

    void sendTransactionRequest(TransactionRequest transactionRequest) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        os.writeObject(transactionRequest); // send request 
        os.flush();
    }

    String readResponse() throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return is.readLine();
    }
}
