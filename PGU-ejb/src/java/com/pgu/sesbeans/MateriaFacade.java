/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.sesbeans;

import com.pgu.entidades.Materia;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cristhian
 */
@Stateless
public class MateriaFacade extends AbstractFacade<Materia> {
    @PersistenceContext(unitName = "PGU-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MateriaFacade() {
        super(Materia.class);
    }
    
}
