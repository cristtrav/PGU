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
import com.pgu.entidades.Tasa;
import com.pgu.entidades.Carrera;
import com.pgu.entidades.DetallePago;
import com.pgu.entidades.DetalleTasa;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class DetalleTasaJpaController implements Serializable {

    public DetalleTasaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetalleTasa detalleTasa) throws RollbackFailureException, Exception {
        if (detalleTasa.getDetallePagoList() == null) {
            detalleTasa.setDetallePagoList(new ArrayList<DetallePago>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Tasa idTasa = detalleTasa.getIdTasa();
            if (idTasa != null) {
                idTasa = em.getReference(idTasa.getClass(), idTasa.getIdTasa());
                detalleTasa.setIdTasa(idTasa);
            }
            Carrera idCarrera = detalleTasa.getIdCarrera();
            if (idCarrera != null) {
                idCarrera = em.getReference(idCarrera.getClass(), idCarrera.getIdCarrera());
                detalleTasa.setIdCarrera(idCarrera);
            }
            List<DetallePago> attachedDetallePagoList = new ArrayList<DetallePago>();
            for (DetallePago detallePagoListDetallePagoToAttach : detalleTasa.getDetallePagoList()) {
                detallePagoListDetallePagoToAttach = em.getReference(detallePagoListDetallePagoToAttach.getClass(), detallePagoListDetallePagoToAttach.getIdDetallePago());
                attachedDetallePagoList.add(detallePagoListDetallePagoToAttach);
            }
            detalleTasa.setDetallePagoList(attachedDetallePagoList);
            em.persist(detalleTasa);
            if (idTasa != null) {
                idTasa.getDetalleTasaList().add(detalleTasa);
                idTasa = em.merge(idTasa);
            }
            if (idCarrera != null) {
                idCarrera.getDetalleTasaList().add(detalleTasa);
                idCarrera = em.merge(idCarrera);
            }
            for (DetallePago detallePagoListDetallePago : detalleTasa.getDetallePagoList()) {
                DetalleTasa oldIdDetalleTasaOfDetallePagoListDetallePago = detallePagoListDetallePago.getIdDetalleTasa();
                detallePagoListDetallePago.setIdDetalleTasa(detalleTasa);
                detallePagoListDetallePago = em.merge(detallePagoListDetallePago);
                if (oldIdDetalleTasaOfDetallePagoListDetallePago != null) {
                    oldIdDetalleTasaOfDetallePagoListDetallePago.getDetallePagoList().remove(detallePagoListDetallePago);
                    oldIdDetalleTasaOfDetallePagoListDetallePago = em.merge(oldIdDetalleTasaOfDetallePagoListDetallePago);
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

    public void edit(DetalleTasa detalleTasa) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            DetalleTasa persistentDetalleTasa = em.find(DetalleTasa.class, detalleTasa.getIdDetalleTasa());
            Tasa idTasaOld = persistentDetalleTasa.getIdTasa();
            Tasa idTasaNew = detalleTasa.getIdTasa();
            Carrera idCarreraOld = persistentDetalleTasa.getIdCarrera();
            Carrera idCarreraNew = detalleTasa.getIdCarrera();
            List<DetallePago> detallePagoListOld = persistentDetalleTasa.getDetallePagoList();
            List<DetallePago> detallePagoListNew = detalleTasa.getDetallePagoList();
            List<String> illegalOrphanMessages = null;
            for (DetallePago detallePagoListOldDetallePago : detallePagoListOld) {
                if (!detallePagoListNew.contains(detallePagoListOldDetallePago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetallePago " + detallePagoListOldDetallePago + " since its idDetalleTasa field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idTasaNew != null) {
                idTasaNew = em.getReference(idTasaNew.getClass(), idTasaNew.getIdTasa());
                detalleTasa.setIdTasa(idTasaNew);
            }
            if (idCarreraNew != null) {
                idCarreraNew = em.getReference(idCarreraNew.getClass(), idCarreraNew.getIdCarrera());
                detalleTasa.setIdCarrera(idCarreraNew);
            }
            List<DetallePago> attachedDetallePagoListNew = new ArrayList<DetallePago>();
            for (DetallePago detallePagoListNewDetallePagoToAttach : detallePagoListNew) {
                detallePagoListNewDetallePagoToAttach = em.getReference(detallePagoListNewDetallePagoToAttach.getClass(), detallePagoListNewDetallePagoToAttach.getIdDetallePago());
                attachedDetallePagoListNew.add(detallePagoListNewDetallePagoToAttach);
            }
            detallePagoListNew = attachedDetallePagoListNew;
            detalleTasa.setDetallePagoList(detallePagoListNew);
            detalleTasa = em.merge(detalleTasa);
            if (idTasaOld != null && !idTasaOld.equals(idTasaNew)) {
                idTasaOld.getDetalleTasaList().remove(detalleTasa);
                idTasaOld = em.merge(idTasaOld);
            }
            if (idTasaNew != null && !idTasaNew.equals(idTasaOld)) {
                idTasaNew.getDetalleTasaList().add(detalleTasa);
                idTasaNew = em.merge(idTasaNew);
            }
            if (idCarreraOld != null && !idCarreraOld.equals(idCarreraNew)) {
                idCarreraOld.getDetalleTasaList().remove(detalleTasa);
                idCarreraOld = em.merge(idCarreraOld);
            }
            if (idCarreraNew != null && !idCarreraNew.equals(idCarreraOld)) {
                idCarreraNew.getDetalleTasaList().add(detalleTasa);
                idCarreraNew = em.merge(idCarreraNew);
            }
            for (DetallePago detallePagoListNewDetallePago : detallePagoListNew) {
                if (!detallePagoListOld.contains(detallePagoListNewDetallePago)) {
                    DetalleTasa oldIdDetalleTasaOfDetallePagoListNewDetallePago = detallePagoListNewDetallePago.getIdDetalleTasa();
                    detallePagoListNewDetallePago.setIdDetalleTasa(detalleTasa);
                    detallePagoListNewDetallePago = em.merge(detallePagoListNewDetallePago);
                    if (oldIdDetalleTasaOfDetallePagoListNewDetallePago != null && !oldIdDetalleTasaOfDetallePagoListNewDetallePago.equals(detalleTasa)) {
                        oldIdDetalleTasaOfDetallePagoListNewDetallePago.getDetallePagoList().remove(detallePagoListNewDetallePago);
                        oldIdDetalleTasaOfDetallePagoListNewDetallePago = em.merge(oldIdDetalleTasaOfDetallePagoListNewDetallePago);
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
                Integer id = detalleTasa.getIdDetalleTasa();
                if (findDetalleTasa(id) == null) {
                    throw new NonexistentEntityException("The detalleTasa with id " + id + " no longer exists.");
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
            DetalleTasa detalleTasa;
            try {
                detalleTasa = em.getReference(DetalleTasa.class, id);
                detalleTasa.getIdDetalleTasa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detalleTasa with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DetallePago> detallePagoListOrphanCheck = detalleTasa.getDetallePagoList();
            for (DetallePago detallePagoListOrphanCheckDetallePago : detallePagoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This DetalleTasa (" + detalleTasa + ") cannot be destroyed since the DetallePago " + detallePagoListOrphanCheckDetallePago + " in its detallePagoList field has a non-nullable idDetalleTasa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Tasa idTasa = detalleTasa.getIdTasa();
            if (idTasa != null) {
                idTasa.getDetalleTasaList().remove(detalleTasa);
                idTasa = em.merge(idTasa);
            }
            Carrera idCarrera = detalleTasa.getIdCarrera();
            if (idCarrera != null) {
                idCarrera.getDetalleTasaList().remove(detalleTasa);
                idCarrera = em.merge(idCarrera);
            }
            em.remove(detalleTasa);
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

    public List<DetalleTasa> findDetalleTasaEntities() {
        return findDetalleTasaEntities(true, -1, -1);
    }

    public List<DetalleTasa> findDetalleTasaEntities(int maxResults, int firstResult) {
        return findDetalleTasaEntities(false, maxResults, firstResult);
    }

    private List<DetalleTasa> findDetalleTasaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetalleTasa.class));
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

    public DetalleTasa findDetalleTasa(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetalleTasa.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetalleTasaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetalleTasa> rt = cq.from(DetalleTasa.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
