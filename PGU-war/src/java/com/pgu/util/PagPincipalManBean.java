/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.util;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author cristhian
 */
@Named(value = "pagPincipalManBean")
@SessionScoped
public class PagPincipalManBean implements Serializable {

    /**
     * Creates a new instance of PagPincipalManBean
     */
    public PagPincipalManBean() {
    }
    
    public void theMetodo(){
        for(int i=0; i<500000;i++){
            System.out.println("Repeticion No. "+i);
        }
    }
    
    public void mostrarMensaje(){
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Mensaje del sistema","Probando los mensajes"));
    }
}
