/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.controladores;

import com.pgu.controladores.exceptions.IllegalOrphanException;
import com.pgu.controladores.exceptions.NonexistentEntityException;
import com.pgu.controladores.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.pgu.entidades.Ciudad;
import com.pgu.entidades.Departamento;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class DepartamentoJpaController implements Serializable {

    public DepartamentoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Departamento departamento) throws RollbackFailureException, Exception {
        if (departamento.getCiudadList() == null) {
            departamento.setCiudadList(new ArrayList<Ciudad>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Ciudad> attachedCiudadList = new ArrayList<Ciudad>();
            for (Ciudad ciudadListCiudadToAttach : departamento.getCiudadList()) {
                ciudadListCiudadToAttach = em.getReference(ciudadListCiudadToAttach.getClass(), ciudadListCiudadToAttach.getIdCiudad());
                attachedCiudadList.add(ciudadListCiudadToAttach);
            }
            departamento.setCiudadList(attachedCiudadList);
            em.persist(departamento);
            for (Ciudad ciudadListCiudad : departamento.getCiudadList()) {
                Departamento oldIdDepartamentoOfCiudadListCiudad = ciudadListCiudad.getIdDepartamento();
                ciudadListCiudad.setIdDepartamento(departamento);
                ciudadListCiudad = em.merge(ciudadListCiudad);
                if (oldIdDepartamentoOfCiudadListCiudad != null) {
                    oldIdDepartamentoOfCiudadListCiudad.getCiudadList().remove(ciudadListCiudad);
                    oldIdDepartamentoOfCiudadListCiudad = em.merge(oldIdDepartamentoOfCiudadListCiudad);
                }
            }
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

    public void edit(Departamento departamento) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Departamento persistentDepartamento = em.find(Departamento.class, departamento.getIdDepartamento());
            List<Ciudad> ciudadListOld = persistentDepartamento.getCiudadList();
            List<Ciudad> ciudadListNew = departamento.getCiudadList();
            List<String> illegalOrphanMessages = null;
            for (Ciudad ciudadListOldCiudad : ciudadListOld) {
                if (!ciudadListNew.contains(ciudadListOldCiudad)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Ciudad " + ciudadListOldCiudad + " since its idDepartamento field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Ciudad> attachedCiudadListNew = new ArrayList<Ciudad>();
            for (Ciudad ciudadListNewCiudadToAttach : ciudadListNew) {
                ciudadListNewCiudadToAttach = em.getReference(ciudadListNewCiudadToAttach.getClass(), ciudadListNewCiudadToAttach.getIdCiudad());
                attachedCiudadListNew.add(ciudadListNewCiudadToAttach);
            }
            ciudadListNew = attachedCiudadListNew;
            departamento.setCiudadList(ciudadListNew);
            departamento = em.merge(departamento);
            for (Ciudad ciudadListNewCiudad : ciudadListNew) {
                if (!ciudadListOld.contains(ciudadListNewCiudad)) {
                    Departamento oldIdDepartamentoOfCiudadListNewCiudad = ciudadListNewCiudad.getIdDepartamento();
                    ciudadListNewCiudad.setIdDepartamento(departamento);
                    ciudadListNewCiudad = em.merge(ciudadListNewCiudad);
                    if (oldIdDepartamentoOfCiudadListNewCiudad != null && !oldIdDepartamentoOfCiudadListNewCiudad.equals(departamento)) {
                        oldIdDepartamentoOfCiudadListNewCiudad.getCiudadList().remove(ciudadListNewCiudad);
                        oldIdDepartamentoOfCiudadListNewCiudad = em.merge(oldIdDepartamentoOfCiudadListNewCiudad);
                    }
                }
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
                Integer id = departamento.getIdDepartamento();
                if (findDepartamento(id) == null) {
                    throw new NonexistentEntityException("The departamento with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Departamento departamento;
            try {
                departamento = em.getReference(Departamento.class, id);
                departamento.getIdDepartamento();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The departamento with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Ciudad> ciudadListOrphanCheck = departamento.getCiudadList();
            for (Ciudad ciudadListOrphanCheckCiudad : ciudadListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Departamento (" + departamento + ") cannot be destroyed since the Ciudad " + ciudadListOrphanCheckCiudad + " in its ciudadList field has a non-nullable idDepartamento field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(departamento);
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

    public List<Departamento> findDepartamentoEntities() {
        return findDepartamentoEntities(true, -1, -1);
    }

    public List<Departamento> findDepartamentoEntities(int maxResults, int firstResult) {
        return findDepartamentoEntities(false, maxResults, firstResult);
    }

    private List<Departamento> findDepartamentoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Departamento.class));
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

    public Departamento findDepartamento(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Departamento.class, id);
        } finally {
            em.close();
        }
    }

    public int getDepartamentoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Departamento> rt = cq.from(Departamento.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
