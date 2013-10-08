/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.depManBean;

import com.pgu.entidades.Departamento;
import com.pgu.sesbeans.DepartamentoFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;


/**
 *
 * @author cristhian
 */
@Named(value = "depManBean")
@RequestScoped
public class DepManBean {
    @EJB
    private DepartamentoFacade departamentoFacade;

    /**
     * Creates a new instance of NewJSFManagedBean
     */
    public DepManBean() {
        
    }
    
    public List<Departamento> getDeps(){
        return this.departamentoFacade.findAll();
    }
    
    
}
