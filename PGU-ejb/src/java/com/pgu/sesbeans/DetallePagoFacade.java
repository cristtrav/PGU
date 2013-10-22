/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.sesbeans;

import com.pgu.entidades.DetallePago;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cristhian
 */
@Stateless
public class DetallePagoFacade extends AbstractFacade<DetallePago> {
    @PersistenceContext(unitName = "PGU-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DetallePagoFacade() {
        super(DetallePago.class);
    }
    
}
