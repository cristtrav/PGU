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
import com.pgu.entidades.Usuario;
import com.pgu.entidades.Alumno;
import com.pgu.entidades.DetallePago;
import com.pgu.entidades.Pago;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class PagoJpaController implements Serializable {

    public PagoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pago pago) throws RollbackFailureException, Exception {
        if (pago.getDetallePagoList() == null) {
            pago.setDetallePagoList(new ArrayList<DetallePago>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario idUsuario = pago.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                pago.setIdUsuario(idUsuario);
            }
            Alumno idAlumno = pago.getIdAlumno();
            if (idAlumno != null) {
                idAlumno = em.getReference(idAlumno.getClass(), idAlumno.getIdAlumno());
                pago.setIdAlumno(idAlumno);
            }
            List<DetallePago> attachedDetallePagoList = new ArrayList<DetallePago>();
            for (DetallePago detallePagoListDetallePagoToAttach : pago.getDetallePagoList()) {
                detallePagoListDetallePagoToAttach = em.getReference(detallePagoListDetallePagoToAttach.getClass(), detallePagoListDetallePagoToAttach.getIdDetallePago());
                attachedDetallePagoList.add(detallePagoListDetallePagoToAttach);
            }
            pago.setDetallePagoList(attachedDetallePagoList);
            em.persist(pago);
            if (idUsuario != null) {
                idUsuario.getPagoList().add(pago);
                idUsuario = em.merge(idUsuario);
            }
            if (idAlumno != null) {
                idAlumno.getPagoList().add(pago);
                idAlumno = em.merge(idAlumno);
            }
            for (DetallePago detallePagoListDetallePago : pago.getDetallePagoList()) {
                Pago oldIdPagoTarifaOfDetallePagoListDetallePago = detallePagoListDetallePago.getIdPagoTarifa();
                detallePagoListDetallePago.setIdPagoTarifa(pago);
                detallePagoListDetallePago = em.merge(detallePagoListDetallePago);
                if (oldIdPagoTarifaOfDetallePagoListDetallePago != null) {
                    oldIdPagoTarifaOfDetallePagoListDetallePago.getDetallePagoList().remove(detallePagoListDetallePago);
                    oldIdPagoTarifaOfDetallePagoListDetallePago = em.merge(oldIdPagoTarifaOfDetallePagoListDetallePago);
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

    public void edit(Pago pago) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Pago persistentPago = em.find(Pago.class, pago.getIdPagoTarifa());
            Usuario idUsuarioOld = persistentPago.getIdUsuario();
            Usuario idUsuarioNew = pago.getIdUsuario();
            Alumno idAlumnoOld = persistentPago.getIdAlumno();
            Alumno idAlumnoNew = pago.getIdAlumno();
            List<DetallePago> detallePagoListOld = persistentPago.getDetallePagoList();
            List<DetallePago> detallePagoListNew = pago.getDetallePagoList();
            List<String> illegalOrphanMessages = null;
            for (DetallePago detallePagoListOldDetallePago : detallePagoListOld) {
                if (!detallePagoListNew.contains(detallePagoListOldDetallePago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetallePago " + detallePagoListOldDetallePago + " since its idPagoTarifa field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                pago.setIdUsuario(idUsuarioNew);
            }
            if (idAlumnoNew != null) {
                idAlumnoNew = em.getReference(idAlumnoNew.getClass(), idAlumnoNew.getIdAlumno());
                pago.setIdAlumno(idAlumnoNew);
            }
            List<DetallePago> attachedDetallePagoListNew = new ArrayList<DetallePago>();
            for (DetallePago detallePagoListNewDetallePagoToAttach : detallePagoListNew) {
                detallePagoListNewDetallePagoToAttach = em.getReference(detallePagoListNewDetallePagoToAttach.getClass(), detallePagoListNewDetallePagoToAttach.getIdDetallePago());
                attachedDetallePagoListNew.add(detallePagoListNewDetallePagoToAttach);
            }
            detallePagoListNew = attachedDetallePagoListNew;
            pago.setDetallePagoList(detallePagoListNew);
            pago = em.merge(pago);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getPagoList().remove(pago);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getPagoList().add(pago);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            if (idAlumnoOld != null && !idAlumnoOld.equals(idAlumnoNew)) {
                idAlumnoOld.getPagoList().remove(pago);
                idAlumnoOld = em.merge(idAlumnoOld);
            }
            if (idAlumnoNew != null && !idAlumnoNew.equals(idAlumnoOld)) {
                idAlumnoNew.getPagoList().add(pago);
                idAlumnoNew = em.merge(idAlumnoNew);
            }
            for (DetallePago detallePagoListNewDetallePago : detallePagoListNew) {
                if (!detallePagoListOld.contains(detallePagoListNewDetallePago)) {
                    Pago oldIdPagoTarifaOfDetallePagoListNewDetallePago = detallePagoListNewDetallePago.getIdPagoTarifa();
                    detallePagoListNewDetallePago.setIdPagoTarifa(pago);
                    detallePagoListNewDetallePago = em.merge(detallePagoListNewDetallePago);
                    if (oldIdPagoTarifaOfDetallePagoListNewDetallePago != null && !oldIdPagoTarifaOfDetallePagoListNewDetallePago.equals(pago)) {
                        oldIdPagoTarifaOfDetallePagoListNewDetallePago.getDetallePagoList().remove(detallePagoListNewDetallePago);
                        oldIdPagoTarifaOfDetallePagoListNewDetallePago = em.merge(oldIdPagoTarifaOfDetallePagoListNewDetallePago);
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
                Integer id = pago.getIdPagoTarifa();
                if (findPago(id) == null) {
                    throw new NonexistentEntityException("The pago with id " + id + " no longer exists.");
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
            Pago pago;
            try {
                pago = em.getReference(Pago.class, id);
                pago.getIdPagoTarifa();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pago with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<DetallePago> detallePagoListOrphanCheck = pago.getDetallePagoList();
            for (DetallePago detallePagoListOrphanCheckDetallePago : detallePagoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pago (" + pago + ") cannot be destroyed since the DetallePago " + detallePagoListOrphanCheckDetallePago + " in its detallePagoList field has a non-nullable idPagoTarifa field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario idUsuario = pago.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getPagoList().remove(pago);
                idUsuario = em.merge(idUsuario);
            }
            Alumno idAlumno = pago.getIdAlumno();
            if (idAlumno != null) {
                idAlumno.getPagoList().remove(pago);
                idAlumno = em.merge(idAlumno);
            }
            em.remove(pago);
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

    public List<Pago> findPagoEntities() {
        return findPagoEntities(true, -1, -1);
    }

    public List<Pago> findPagoEntities(int maxResults, int firstResult) {
        return findPagoEntities(false, maxResults, firstResult);
    }

    private List<Pago> findPagoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pago.class));
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

    public Pago findPago(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pago.class, id);
        } finally {
            em.close();
        }
    }

    public int getPagoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pago> rt = cq.from(Pago.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
