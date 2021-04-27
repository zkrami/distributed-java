
package cryptomonaie;

import java.net.Socket;
/*
* La tache pour envoyer une transaction demandée par le client au mineur 
*/
public class ClientTransactionTask implements  Runnable{

    Transaction transaction; 

    public ClientTransactionTask(Transaction transaction) {
        this.transaction = transaction;
    }
    
    @Override
    public void run() {
        Socket socket = new Socket();
        
        
    }
    
}
