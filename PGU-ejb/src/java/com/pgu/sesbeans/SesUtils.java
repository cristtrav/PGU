/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.sesbeans;

import java.util.Iterator;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cristhian
 */
@Stateless
@LocalBean
public class SesUtils {
    @PersistenceContext(unitName = "PGU-ejbPU")
    private EntityManager em;

    public void refrescarEntidad(Object e) {
        em.refresh(e);
    }
    
    public void refrescarEntidad(List le){
        Iterator it=le.iterator();
        while(it.hasNext()){
            em.refresh(it.next());
        }
            
    }
      
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")


}
