/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.sesbeans;

import com.pgu.entidades.Matricula;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cristhian
 */
@Stateless
public class MatriculaFacade extends AbstractFacade<Matricula> {
    @PersistenceContext(unitName = "PGU-ejbPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MatriculaFacade() {
        super(Matricula.class);
    }
    
}
