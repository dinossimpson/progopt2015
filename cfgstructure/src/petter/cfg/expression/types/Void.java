/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package petter.cfg.expression.types;

/**
 *
 * @author petter
 */
public class Void extends Type {
    private Void() {}
    private static Void singleton = new Void();
    public static Void create(){
        return singleton;
    }

    @Override
    public boolean isBasicType() {
        return true;
    }
   
}
