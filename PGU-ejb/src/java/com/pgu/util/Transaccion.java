/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.util;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
@Stateless
@LocalBean
public class Transaccion {
    @PersistenceContext(unitName = "PGU-ejbPU")
    private EntityManager em;
    
    @Resource
    UserTransaction ut;
    
    public EntityManager getEntityManager(){
        return em;
    }
    
    public UserTransaction getUserTransaction(){
        return ut;
    }
    
    public EntityManagerFactory getEntityManagerFactory(){
        return em.getEntityManagerFactory();
    }
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")

}
