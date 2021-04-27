package cryptomonaie;

public class NonInserableException extends Exception {

    public NonInserableException() {
        super("Le bloc n'est pas valide ");
    }
    
}
