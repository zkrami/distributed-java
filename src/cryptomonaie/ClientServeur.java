package cryptomonaie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * Client serveur: Wrapper de la connexion entre le client et le mineur. Le
 * client communique avec le mineur un utilisant cette classe.
 */
public class ClientServeur {

    private Socket socket;

    public ClientServeur(Socket socket) {
        this.socket = socket;
    }

    public void close() {

        try {
            socket.close();
        } catch (Exception ex) {
        }
    }

    void sendTransaction(Transaction transaction) throws IOException {
        ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
        os.writeObject(transaction); // send request 
        os.flush();
    }

    String readResponse() throws IOException {
        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        return is.readLine();
    }
}
