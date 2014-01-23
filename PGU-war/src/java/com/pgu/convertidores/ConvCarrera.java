/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pgu.convertidores;

import com.pgu.sesbeans.CarreraFacade;
import com.pgu.util.StringEntityUtil;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author serql
 */
@Named(value = "ConvCarreraBean")
@RequestScoped
@FacesConverter("ConvCarrera")

public class ConvCarrera implements Converter{
    @EJB
    private CarreraFacade carreraFacade;
    private static final Logger LOG = Logger.getLogger(ConvCarrera.class.getName());
    /**
     * Creates a new instance of ConvCarrera
     */
    public ConvCarrera() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
         System.out.println("##A convertir getAsobjet:"+value);
         return this.carreraFacade.find(StringEntityUtil.getIdEntidad(value));
        
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        System.out.println("###getAsString a convertir:"+value);
        return value.toString();    
    }
    
}
