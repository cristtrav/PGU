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
import com.pgu.entidades.DetalleTasa;
import com.pgu.entidades.Tasa;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class TasaJpaController implements Serializable {

    public TasaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Tasa tasa) throws RollbackFailureException, Exception {
        if (tasa.getDetalleTasaList() == null) {
            tasa.setDetalleTasaList(new ArrayList<DetalleTasa>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<DetalleTasa> attachedDetalleTasaList = new ArrayList<DetalleTasa>();
            for (DetalleTasa detalleTasaListDetalleTasaToAttach : tasa.getDetalleTasaList()) {
                detalleTasaListDetalleTasaToAttach = em.getReference(detalleTasaListDetalleTasaToAttach.getClass(), detalleTasaListDetalleTasaToAttach.getIdDetalleTasa());
                attachedDetalleTasaList.add(detalleTasaListDetalleTasaToAttach);
            }
            tasa.setDetalleTasaList(attachedDetalleTasaList);
            em.persist(tasa);
            for (DetalleTasa detalleTasaListDetalleTasa : tasa.getDetalleTasaList()) {
                Tasa oldIdTasaOfDetalleTasaListDetalleTasa = detalleTasaListDetalleTasa.getIdTasa();
                detalleTasaListDetalleTasa.setIdTasa(tasa);
                detalleTasaListDetalleTasa = em.merge(detalleTasaListDetalleTasa);
                if (oldIdTasaOfDetalleTasaListDetalleTasa != null) {
                    oldIdTasaOfDetalleTasaListDetalleTasa.getDetalleTasaList().remove(detalleTasaListDetalleTasa);
                    oldIdTasaOfDetalleTasaListDetalleTasa = em.merge(oldIdTasaOfDetalleTasaListDetalleTasa);
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

    public void edit(Tasa tasa) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Tasa persistentTasa = em.find(Tasa.class, tasa.getIdTasa());
            List<DetalleTasa> detalleTasaListOld = persistentTasa.getDetalleTasaList();
            List<DetalleTasa> detalleTasaListNew = tasa.getDetalleTasaList();
            List<String> illegalOrphanMessages = null;
            for (DetalleTasa detalleTasaListOldDetalleTasa : detalleTasaListOld) {
                if (!detalleTasaListNew.contains(detalleTasaListOldDetalleTasa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleTasa " + detalleTasaListOldDetalleTasa + " since its idTasa field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<DetalleTasa> attachedDetalleTasaListNew = new ArrayList<DetalleTasa>();
            for (DetalleTasa detalleTasaListNewDetalleTasaToAttach : detalleTasaListNew) {
                detalleTasaListNewDetalleTasaToAttach = em.getReference(detalleTasaListNewDetalleTasaToAttach.getClass(), detalleTasaListNewDetalleTasaToAttach.getIdDetalleTasa());
                attachedDetalleTasaListNew.add(detalleTasaListNewDetalleTasaToAttach);
            }
            detalleTasaListNew = attachedDetalleTasaListNew;
            tasa.setDetalleTasaList(detalleTasaListNew);
            tasa = em.merge(tasa);
            for (DetalleTasa detalleTasaListNewDetalleTasa : detalleTasaListNew) {
                if (!detalleTasaListOld.contains(detalleTasaListNewDetalleTasa)) {
                    Tasa oldIdTasaOfDetalleTasaListNewDetalleTasa = detalleTasaListNewDetalleTasa.getIdTasa();
                    detalleTasaListNewDetalleTasa.setIdTasa(tasa);
                    detalleTasaListNewDetalleTasa = em.merge(detalleTasaListNewDetalleTasa);
                    if (oldIdTasaOfDetalleTasaListNewDetalleTasa != null && !oldIdTasaOfDetalleTasaListNewDetalleTasa.equals(tasa)) {
                        oldIdTasaOfDetalleTasaListNewDetalleTasa.getDetalleTasaList().remove(detalleTasaListNewDetalleTasa);
                        oldIdTasaOfDetalleTasaListNewDetalleTasa = em.merge(oldIdTasaOfDetalleTasaListNewDetalleTasa);
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
                Integer id = tasa.getIdTasa();
                if (findTasa(id) == null) {
                    throw new NonexistentEntityException("The tasa with id " + id + " no longer exists.");
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
            Tasa tasa;
            try {
                tasa = em.getReference(Tasa.class, id);
                tasa.getIdTasa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The tasa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DetalleTasa> detalleTasaListOrphanCheck = tasa.getDetalleTasaList();
            for (DetalleTasa detalleTasaListOrphanCheckDetalleTasa : detalleTasaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Tasa (" + tasa + ") cannot be destroyed since the DetalleTasa " + detalleTasaListOrphanCheckDetalleTasa + " in its detalleTasaList field has a non-nullable idTasa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(tasa);
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

    public List<Tasa> findTasaEntities() {
        return findTasaEntities(true, -1, -1);
    }

    public List<Tasa> findTasaEntities(int maxResults, int firstResult) {
        return findTasaEntities(false, maxResults, firstResult);
    }

    private List<Tasa> findTasaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Tasa.class));
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

    public Tasa findTasa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Tasa.class, id);
        } finally {
            em.close();
        }
    }

    public int getTasaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Tasa> rt = cq.from(Tasa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
