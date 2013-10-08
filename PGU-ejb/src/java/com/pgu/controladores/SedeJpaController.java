/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.controladores;

import com.pgu.controladores.exceptions.NonexistentEntityException;
import com.pgu.controladores.exceptions.PreexistingEntityException;
import com.pgu.controladores.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.pgu.entidades.Facultad;
import com.pgu.entidades.Ciudad;
import com.pgu.entidades.Sede;
import com.pgu.entidades.SedePK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class SedeJpaController implements Serializable {

    public SedeJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sede sede) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (sede.getSedePK() == null) {
            sede.setSedePK(new SedePK());
        }
        sede.getSedePK().setIdFacultad(sede.getFacultad().getIdFacultad());
        sede.getSedePK().setIdCiudad(sede.getCiudad().getIdCiudad());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facultad facultad = sede.getFacultad();
            if (facultad != null) {
                facultad = em.getReference(facultad.getClass(), facultad.getIdFacultad());
                sede.setFacultad(facultad);
            }
            Ciudad ciudad = sede.getCiudad();
            if (ciudad != null) {
                ciudad = em.getReference(ciudad.getClass(), ciudad.getIdCiudad());
                sede.setCiudad(ciudad);
            }
            em.persist(sede);
            if (facultad != null) {
                facultad.getSedeList().add(sede);
                facultad = em.merge(facultad);
            }
            if (ciudad != null) {
                ciudad.getSedeList().add(sede);
                ciudad = em.merge(ciudad);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findSede(sede.getSedePK()) != null) {
                throw new PreexistingEntityException("Sede " + sede + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sede sede) throws NonexistentEntityException, RollbackFailureException, Exception {
        sede.getSedePK().setIdFacultad(sede.getFacultad().getIdFacultad());
        sede.getSedePK().setIdCiudad(sede.getCiudad().getIdCiudad());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Sede persistentSede = em.find(Sede.class, sede.getSedePK());
            Facultad facultadOld = persistentSede.getFacultad();
            Facultad facultadNew = sede.getFacultad();
            Ciudad ciudadOld = persistentSede.getCiudad();
            Ciudad ciudadNew = sede.getCiudad();
            if (facultadNew != null) {
                facultadNew = em.getReference(facultadNew.getClass(), facultadNew.getIdFacultad());
                sede.setFacultad(facultadNew);
            }
            if (ciudadNew != null) {
                ciudadNew = em.getReference(ciudadNew.getClass(), ciudadNew.getIdCiudad());
                sede.setCiudad(ciudadNew);
            }
            sede = em.merge(sede);
            if (facultadOld != null && !facultadOld.equals(facultadNew)) {
                facultadOld.getSedeList().remove(sede);
                facultadOld = em.merge(facultadOld);
            }
            if (facultadNew != null && !facultadNew.equals(facultadOld)) {
                facultadNew.getSedeList().add(sede);
                facultadNew = em.merge(facultadNew);
            }
            if (ciudadOld != null && !ciudadOld.equals(ciudadNew)) {
                ciudadOld.getSedeList().remove(sede);
                ciudadOld = em.merge(ciudadOld);
            }
            if (ciudadNew != null && !ciudadNew.equals(ciudadOld)) {
                ciudadNew.getSedeList().add(sede);
                ciudadNew = em.merge(ciudadNew);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                SedePK id = sede.getSedePK();
                if (findSede(id) == null) {
                    throw new NonexistentEntityException("The sede with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(SedePK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Sede sede;
            try {
                sede = em.getReference(Sede.class, id);
                sede.getSedePK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sede with id " + id + " no longer exists.", enfe);
            }
            Facultad facultad = sede.getFacultad();
            if (facultad != null) {
                facultad.getSedeList().remove(sede);
                facultad = em.merge(facultad);
            }
            Ciudad ciudad = sede.getCiudad();
            if (ciudad != null) {
                ciudad.getSedeList().remove(sede);
                ciudad = em.merge(ciudad);
            }
            em.remove(sede);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sede> findSedeEntities() {
        return findSedeEntities(true, -1, -1);
    }

    public List<Sede> findSedeEntities(int maxResults, int firstResult) {
        return findSedeEntities(false, maxResults, firstResult);
    }

    private List<Sede> findSedeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sede.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Sede findSede(SedePK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sede.class, id);
        } finally {
            em.close();
        }
    }

    public int getSedeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sede> rt = cq.from(Sede.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
