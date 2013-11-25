/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.util;

import com.pgu.entidades.Ciudad;
import com.pgu.sesbeans.CiudadFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author cristhian
 */
@Named(value = "datosTablasManBean")
@RequestScoped
public class DatosTablasManBean {
    @EJB
    private CiudadFacade ciudadFacade;

    /**
     * Creates a new instance of DatosTablasManBean
     */
    public DatosTablasManBean() {
    }
    
    public List<Ciudad> getListaCiudades(){
        return this.ciudadFacade.findAll();
    }
}
