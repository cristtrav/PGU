/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pgu.convertidores;

import com.pgu.sesbeans.SemestreFacade;
import com.pgu.util.StringEntityUtil;
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
@Named(value = "covSemestreBean")
@RequestScoped
@FacesConverter("convSemestre")
public class CovSemestre implements Converter{
    @EJB
    private SemestreFacade semestreFacade;

    /**
     * Creates a new instance of CovSemestre
     */
    public CovSemestre() {
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        System.out.println("getAsObject"+value);
        return this.semestreFacade.find(StringEntityUtil.getIdEntidad(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        System.out.println("getAsString"+value);
        return value.toString();
    }
    
}
