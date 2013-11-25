/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.util;

/**
 *
 * @author cristhian
 */
public class StringEntityUtil {
    
    public static int getIdEntidad(String entity){
        int theid=0;
        String auxId=entity.substring(entity.indexOf('=')+1, entity.indexOf(']')-1);
        System.out.println("La cadena :'"+auxId+"'");
        try{
            theid=Integer.parseInt(auxId);
        }catch(Exception ex){
            System.out.println("Error al convertir cadena de entidad a int: "+ex);
        }
        return theid;
    }
    
}
