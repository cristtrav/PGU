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
import com.pgu.entidades.Rol;
import java.util.ArrayList;
import java.util.List;
import com.pgu.entidades.Pago;
import com.pgu.entidades.Matricula;
import com.pgu.entidades.Usuario;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) throws RollbackFailureException, Exception {
        if (usuario.getRolList() == null) {
            usuario.setRolList(new ArrayList<Rol>());
        }
        if (usuario.getPagoList() == null) {
            usuario.setPagoList(new ArrayList<Pago>());
        }
        if (usuario.getMatriculaList() == null) {
            usuario.setMatriculaList(new ArrayList<Matricula>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Rol> attachedRolList = new ArrayList<Rol>();
            for (Rol rolListRolToAttach : usuario.getRolList()) {
                rolListRolToAttach = em.getReference(rolListRolToAttach.getClass(), rolListRolToAttach.getIdRol());
                attachedRolList.add(rolListRolToAttach);
            }
            usuario.setRolList(attachedRolList);
            List<Pago> attachedPagoList = new ArrayList<Pago>();
            for (Pago pagoListPagoToAttach : usuario.getPagoList()) {
                pagoListPagoToAttach = em.getReference(pagoListPagoToAttach.getClass(), pagoListPagoToAttach.getIdPagoTarifa());
                attachedPagoList.add(pagoListPagoToAttach);
            }
            usuario.setPagoList(attachedPagoList);
            List<Matricula> attachedMatriculaList = new ArrayList<Matricula>();
            for (Matricula matriculaListMatriculaToAttach : usuario.getMatriculaList()) {
                matriculaListMatriculaToAttach = em.getReference(matriculaListMatriculaToAttach.getClass(), matriculaListMatriculaToAttach.getIdMatricula());
                attachedMatriculaList.add(matriculaListMatriculaToAttach);
            }
            usuario.setMatriculaList(attachedMatriculaList);
            em.persist(usuario);
            for (Rol rolListRol : usuario.getRolList()) {
                rolListRol.getUsuarioList().add(usuario);
                rolListRol = em.merge(rolListRol);
            }
            for (Pago pagoListPago : usuario.getPagoList()) {
                Usuario oldIdUsuarioOfPagoListPago = pagoListPago.getIdUsuario();
                pagoListPago.setIdUsuario(usuario);
                pagoListPago = em.merge(pagoListPago);
                if (oldIdUsuarioOfPagoListPago != null) {
                    oldIdUsuarioOfPagoListPago.getPagoList().remove(pagoListPago);
                    oldIdUsuarioOfPagoListPago = em.merge(oldIdUsuarioOfPagoListPago);
                }
            }
            for (Matricula matriculaListMatricula : usuario.getMatriculaList()) {
                Usuario oldIdUsuarioOfMatriculaListMatricula = matriculaListMatricula.getIdUsuario();
                matriculaListMatricula.setIdUsuario(usuario);
                matriculaListMatricula = em.merge(matriculaListMatricula);
                if (oldIdUsuarioOfMatriculaListMatricula != null) {
                    oldIdUsuarioOfMatriculaListMatricula.getMatriculaList().remove(matriculaListMatricula);
                    oldIdUsuarioOfMatriculaListMatricula = em.merge(oldIdUsuarioOfMatriculaListMatricula);
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

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getIdUsuario());
            List<Rol> rolListOld = persistentUsuario.getRolList();
            List<Rol> rolListNew = usuario.getRolList();
            List<Pago> pagoListOld = persistentUsuario.getPagoList();
            List<Pago> pagoListNew = usuario.getPagoList();
            List<Matricula> matriculaListOld = persistentUsuario.getMatriculaList();
            List<Matricula> matriculaListNew = usuario.getMatriculaList();
            List<String> illegalOrphanMessages = null;
            for (Pago pagoListOldPago : pagoListOld) {
                if (!pagoListNew.contains(pagoListOldPago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pago " + pagoListOldPago + " since its idUsuario field is not nullable.");
                }
            }
            for (Matricula matriculaListOldMatricula : matriculaListOld) {
                if (!matriculaListNew.contains(matriculaListOldMatricula)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Matricula " + matriculaListOldMatricula + " since its idUsuario field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Rol> attachedRolListNew = new ArrayList<Rol>();
            for (Rol rolListNewRolToAttach : rolListNew) {
                rolListNewRolToAttach = em.getReference(rolListNewRolToAttach.getClass(), rolListNewRolToAttach.getIdRol());
                attachedRolListNew.add(rolListNewRolToAttach);
            }
            rolListNew = attachedRolListNew;
            usuario.setRolList(rolListNew);
            List<Pago> attachedPagoListNew = new ArrayList<Pago>();
            for (Pago pagoListNewPagoToAttach : pagoListNew) {
                pagoListNewPagoToAttach = em.getReference(pagoListNewPagoToAttach.getClass(), pagoListNewPagoToAttach.getIdPagoTarifa());
                attachedPagoListNew.add(pagoListNewPagoToAttach);
            }
            pagoListNew = attachedPagoListNew;
            usuario.setPagoList(pagoListNew);
            List<Matricula> attachedMatriculaListNew = new ArrayList<Matricula>();
            for (Matricula matriculaListNewMatriculaToAttach : matriculaListNew) {
                matriculaListNewMatriculaToAttach = em.getReference(matriculaListNewMatriculaToAttach.getClass(), matriculaListNewMatriculaToAttach.getIdMatricula());
                attachedMatriculaListNew.add(matriculaListNewMatriculaToAttach);
            }
            matriculaListNew = attachedMatriculaListNew;
            usuario.setMatriculaList(matriculaListNew);
            usuario = em.merge(usuario);
            for (Rol rolListOldRol : rolListOld) {
                if (!rolListNew.contains(rolListOldRol)) {
                    rolListOldRol.getUsuarioList().remove(usuario);
                    rolListOldRol = em.merge(rolListOldRol);
                }
            }
            for (Rol rolListNewRol : rolListNew) {
                if (!rolListOld.contains(rolListNewRol)) {
                    rolListNewRol.getUsuarioList().add(usuario);
                    rolListNewRol = em.merge(rolListNewRol);
                }
            }
            for (Pago pagoListNewPago : pagoListNew) {
                if (!pagoListOld.contains(pagoListNewPago)) {
                    Usuario oldIdUsuarioOfPagoListNewPago = pagoListNewPago.getIdUsuario();
                    pagoListNewPago.setIdUsuario(usuario);
                    pagoListNewPago = em.merge(pagoListNewPago);
                    if (oldIdUsuarioOfPagoListNewPago != null && !oldIdUsuarioOfPagoListNewPago.equals(usuario)) {
                        oldIdUsuarioOfPagoListNewPago.getPagoList().remove(pagoListNewPago);
                        oldIdUsuarioOfPagoListNewPago = em.merge(oldIdUsuarioOfPagoListNewPago);
                    }
                }
            }
            for (Matricula matriculaListNewMatricula : matriculaListNew) {
                if (!matriculaListOld.contains(matriculaListNewMatricula)) {
                    Usuario oldIdUsuarioOfMatriculaListNewMatricula = matriculaListNewMatricula.getIdUsuario();
                    matriculaListNewMatricula.setIdUsuario(usuario);
                    matriculaListNewMatricula = em.merge(matriculaListNewMatricula);
                    if (oldIdUsuarioOfMatriculaListNewMatricula != null && !oldIdUsuarioOfMatriculaListNewMatricula.equals(usuario)) {
                        oldIdUsuarioOfMatriculaListNewMatricula.getMatriculaList().remove(matriculaListNewMatricula);
                        oldIdUsuarioOfMatriculaListNewMatricula = em.merge(oldIdUsuarioOfMatriculaListNewMatricula);
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
                Integer id = usuario.getIdUsuario();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getIdUsuario();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Pago> pagoListOrphanCheck = usuario.getPagoList();
            for (Pago pagoListOrphanCheckPago : pagoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Pago " + pagoListOrphanCheckPago + " in its pagoList field has a non-nullable idUsuario field.");
            }
            List<Matricula> matriculaListOrphanCheck = usuario.getMatriculaList();
            for (Matricula matriculaListOrphanCheckMatricula : matriculaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Matricula " + matriculaListOrphanCheckMatricula + " in its matriculaList field has a non-nullable idUsuario field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Rol> rolList = usuario.getRolList();
            for (Rol rolListRol : rolList) {
                rolListRol.getUsuarioList().remove(usuario);
                rolListRol = em.merge(rolListRol);
            }
            em.remove(usuario);
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

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
