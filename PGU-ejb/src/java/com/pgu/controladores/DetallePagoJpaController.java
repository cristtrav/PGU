/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.controladores;

import com.pgu.controladores.exceptions.NonexistentEntityException;
import com.pgu.controladores.exceptions.RollbackFailureException;
import com.pgu.entidades.DetallePago;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.pgu.entidades.Pago;
import com.pgu.entidades.DetalleTasa;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class DetallePagoJpaController implements Serializable {

    public DetallePagoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(DetallePago detallePago) throws RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pago idPagoTarifa = detallePago.getIdPagoTarifa();
            if (idPagoTarifa != null) {
                idPagoTarifa = em.getReference(idPagoTarifa.getClass(), idPagoTarifa.getIdPagoTarifa());
                detallePago.setIdPagoTarifa(idPagoTarifa);
            }
            DetalleTasa idDetalleTasa = detallePago.getIdDetalleTasa();
            if (idDetalleTasa != null) {
                idDetalleTasa = em.getReference(idDetalleTasa.getClass(), idDetalleTasa.getIdDetalleTasa());
                detallePago.setIdDetalleTasa(idDetalleTasa);
            }
            em.persist(detallePago);
            if (idPagoTarifa != null) {
                idPagoTarifa.getDetallePagoList().add(detallePago);
                idPagoTarifa = em.merge(idPagoTarifa);
            }
            if (idDetalleTasa != null) {
                idDetalleTasa.getDetallePagoList().add(detallePago);
                idDetalleTasa = em.merge(idDetalleTasa);
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

    public void edit(DetallePago detallePago) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            DetallePago persistentDetallePago = em.find(DetallePago.class, detallePago.getIdDetallePago());
            Pago idPagoTarifaOld = persistentDetallePago.getIdPagoTarifa();
            Pago idPagoTarifaNew = detallePago.getIdPagoTarifa();
            DetalleTasa idDetalleTasaOld = persistentDetallePago.getIdDetalleTasa();
            DetalleTasa idDetalleTasaNew = detallePago.getIdDetalleTasa();
            if (idPagoTarifaNew != null) {
                idPagoTarifaNew = em.getReference(idPagoTarifaNew.getClass(), idPagoTarifaNew.getIdPagoTarifa());
                detallePago.setIdPagoTarifa(idPagoTarifaNew);
            }
            if (idDetalleTasaNew != null) {
                idDetalleTasaNew = em.getReference(idDetalleTasaNew.getClass(), idDetalleTasaNew.getIdDetalleTasa());
                detallePago.setIdDetalleTasa(idDetalleTasaNew);
            }
            detallePago = em.merge(detallePago);
            if (idPagoTarifaOld != null && !idPagoTarifaOld.equals(idPagoTarifaNew)) {
                idPagoTarifaOld.getDetallePagoList().remove(detallePago);
                idPagoTarifaOld = em.merge(idPagoTarifaOld);
            }
            if (idPagoTarifaNew != null && !idPagoTarifaNew.equals(idPagoTarifaOld)) {
                idPagoTarifaNew.getDetallePagoList().add(detallePago);
                idPagoTarifaNew = em.merge(idPagoTarifaNew);
            }
            if (idDetalleTasaOld != null && !idDetalleTasaOld.equals(idDetalleTasaNew)) {
                idDetalleTasaOld.getDetallePagoList().remove(detallePago);
                idDetalleTasaOld = em.merge(idDetalleTasaOld);
            }
            if (idDetalleTasaNew != null && !idDetalleTasaNew.equals(idDetalleTasaOld)) {
                idDetalleTasaNew.getDetallePagoList().add(detallePago);
                idDetalleTasaNew = em.merge(idDetalleTasaNew);
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
                Integer id = detallePago.getIdDetallePago();
                if (findDetallePago(id) == null) {
                    throw new NonexistentEntityException("The detallePago with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            DetallePago detallePago;
            try {
                detallePago = em.getReference(DetallePago.class, id);
                detallePago.getIdDetallePago();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The detallePago with id " + id + " no longer exists.", enfe);
            }
            Pago idPagoTarifa = detallePago.getIdPagoTarifa();
            if (idPagoTarifa != null) {
                idPagoTarifa.getDetallePagoList().remove(detallePago);
                idPagoTarifa = em.merge(idPagoTarifa);
            }
            DetalleTasa idDetalleTasa = detallePago.getIdDetalleTasa();
            if (idDetalleTasa != null) {
                idDetalleTasa.getDetallePagoList().remove(detallePago);
                idDetalleTasa = em.merge(idDetalleTasa);
            }
            em.remove(detallePago);
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

    public List<DetallePago> findDetallePagoEntities() {
        return findDetallePagoEntities(true, -1, -1);
    }

    public List<DetallePago> findDetallePagoEntities(int maxResults, int firstResult) {
        return findDetallePagoEntities(false, maxResults, firstResult);
    }

    private List<DetallePago> findDetallePagoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(DetallePago.class));
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

    public DetallePago findDetallePago(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(DetallePago.class, id);
        } finally {
            em.close();
        }
    }

    public int getDetallePagoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<DetallePago> rt = cq.from(DetallePago.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
