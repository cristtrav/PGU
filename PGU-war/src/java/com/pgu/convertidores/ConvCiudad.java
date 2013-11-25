/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.convertidores;

import com.pgu.entidades.Ciudad;
import com.pgu.sesbeans.AlumnoFacade;
import com.pgu.sesbeans.CiudadFacade;
import com.pgu.util.StringEntityUtil;
import java.util.logging.Level;
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
 * @author cristhian
 */
@Named(value = "convCiudadBean")
@RequestScoped
@FacesConverter("convCiudad")
public class ConvCiudad implements Converter {
    @EJB
    private CiudadFacade ciudadFacade;
    
    private static final Logger LOG = Logger.getLogger(ConvCiudad.class.getName());
    /**
     * Creates a new instance of ConvAlumno
     */
    
    public ConvCiudad() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("A convertir getAsObject: "+value);
        return this.ciudadFacade.find(StringEntityUtil.getIdEntidad(value));
    }
    

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        System.out.println("getAsString a convertir: "+value);
        return value.toString();
        
    }
}
