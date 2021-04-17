/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cryptomonaie;

/**
 *
 * @author Rami
 */
public class NonInserableException extends Exception {

    public NonInserableException() {
        super("Le bloc n'est pas valide ");
    }
    
}
