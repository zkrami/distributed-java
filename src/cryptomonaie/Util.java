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
public abstract class Util {

    static void debug(Object ob, Exception ex) {
        System.err.println(ob.getClass());
        System.err.println(ex);
    }
}
