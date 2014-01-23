/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pgu.convertidores;

import com.pgu.sesbeans.AlumnoFacade;
import com.pgu.util.StringEntityUtil;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import java.util.logging.Logger;

/**
 *
 * @author serql
 */
@Named(value = "ConvDocentesBean")
@RequestScoped
@FacesConverter("ConvDocentes")
public class ConvDocentes implements Converter{
    @EJB
    private AlumnoFacade alumnoFacade;
    private static final Logger LOG = Logger.getLogger(ConvAlumnos.class.getName());

    /**
     * Creates a new instance of ConvDocentes
     */
    public ConvDocentes() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("getAsObject"+value);
        return this.alumnoFacade.find(StringEntityUtil.getIdEntidad(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        System.out.println("getAsString"+value);
        return value.toString();
    }
    
}
