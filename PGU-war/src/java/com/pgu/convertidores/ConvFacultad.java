/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pgu.convertidores;

import com.pgu.sesbeans.FacultadFacade;
import com.pgu.util.StringEntityUtil;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author serql
 */
@Named(value = "ConvFacultadBean")
@RequestScoped
@FacesConverter("ConvFacultad")
public class ConvFacultad implements Converter{
    
    @EJB
    private FacultadFacade facultadFacade;

    private static final Logger LOG = Logger.getLogger(ConvFacultad.class.getName());
    
    
    /**
     * Creates a new instance of ConvCarrera
     */
    
    
    public ConvFacultad() {
    }
    
    

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("A convertir getAsObject:"+value);
        return this.facultadFacade.find(StringEntityUtil.getIdEntidad(value));
    }

    
    
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
    System.out.println("getAsString a convertir: "+value);
        return value.toString();
    }
    
}
