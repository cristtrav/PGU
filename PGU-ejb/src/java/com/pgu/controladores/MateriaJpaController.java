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
import com.pgu.entidades.Carrera;
import com.pgu.entidades.Matricula;
import java.util.ArrayList;
import java.util.List;
import com.pgu.entidades.Examen;
import com.pgu.entidades.Materia;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class MateriaJpaController implements Serializable {

    public MateriaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Materia materia) throws RollbackFailureException, Exception {
        if (materia.getMatriculaList() == null) {
            materia.setMatriculaList(new ArrayList<Matricula>());
        }
        if (materia.getExamenList() == null) {
            materia.setExamenList(new ArrayList<Examen>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Carrera idCarrera = materia.getIdCarrera();
            if (idCarrera != null) {
                idCarrera = em.getReference(idCarrera.getClass(), idCarrera.getIdCarrera());
                materia.setIdCarrera(idCarrera);
            }
            List<Matricula> attachedMatriculaList = new ArrayList<Matricula>();
            for (Matricula matriculaListMatriculaToAttach : materia.getMatriculaList()) {
                matriculaListMatriculaToAttach = em.getReference(matriculaListMatriculaToAttach.getClass(), matriculaListMatriculaToAttach.getIdMatricula());
                attachedMatriculaList.add(matriculaListMatriculaToAttach);
            }
            materia.setMatriculaList(attachedMatriculaList);
            List<Examen> attachedExamenList = new ArrayList<Examen>();
            for (Examen examenListExamenToAttach : materia.getExamenList()) {
                examenListExamenToAttach = em.getReference(examenListExamenToAttach.getClass(), examenListExamenToAttach.getExamenPK());
                attachedExamenList.add(examenListExamenToAttach);
            }
            materia.setExamenList(attachedExamenList);
            em.persist(materia);
            if (idCarrera != null) {
                idCarrera.getMateriaList().add(materia);
                idCarrera = em.merge(idCarrera);
            }
            for (Matricula matriculaListMatricula : materia.getMatriculaList()) {
                matriculaListMatricula.getMateriaList().add(materia);
                matriculaListMatricula = em.merge(matriculaListMatricula);
            }
            for (Examen examenListExamen : materia.getExamenList()) {
                Materia oldMateriaOfExamenListExamen = examenListExamen.getMateria();
                examenListExamen.setMateria(materia);
                examenListExamen = em.merge(examenListExamen);
                if (oldMateriaOfExamenListExamen != null) {
                    oldMateriaOfExamenListExamen.getExamenList().remove(examenListExamen);
                    oldMateriaOfExamenListExamen = em.merge(oldMateriaOfExamenListExamen);
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

    public void edit(Materia materia) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Materia persistentMateria = em.find(Materia.class, materia.getIdMateria());
            Carrera idCarreraOld = persistentMateria.getIdCarrera();
            Carrera idCarreraNew = materia.getIdCarrera();
            List<Matricula> matriculaListOld = persistentMateria.getMatriculaList();
            List<Matricula> matriculaListNew = materia.getMatriculaList();
            List<Examen> examenListOld = persistentMateria.getExamenList();
            List<Examen> examenListNew = materia.getExamenList();
            List<String> illegalOrphanMessages = null;
            for (Examen examenListOldExamen : examenListOld) {
                if (!examenListNew.contains(examenListOldExamen)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Examen " + examenListOldExamen + " since its materia field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCarreraNew != null) {
                idCarreraNew = em.getReference(idCarreraNew.getClass(), idCarreraNew.getIdCarrera());
                materia.setIdCarrera(idCarreraNew);
            }
            List<Matricula> attachedMatriculaListNew = new ArrayList<Matricula>();
            for (Matricula matriculaListNewMatriculaToAttach : matriculaListNew) {
                matriculaListNewMatriculaToAttach = em.getReference(matriculaListNewMatriculaToAttach.getClass(), matriculaListNewMatriculaToAttach.getIdMatricula());
                attachedMatriculaListNew.add(matriculaListNewMatriculaToAttach);
            }
            matriculaListNew = attachedMatriculaListNew;
            materia.setMatriculaList(matriculaListNew);
            List<Examen> attachedExamenListNew = new ArrayList<Examen>();
            for (Examen examenListNewExamenToAttach : examenListNew) {
                examenListNewExamenToAttach = em.getReference(examenListNewExamenToAttach.getClass(), examenListNewExamenToAttach.getExamenPK());
                attachedExamenListNew.add(examenListNewExamenToAttach);
            }
            examenListNew = attachedExamenListNew;
            materia.setExamenList(examenListNew);
            materia = em.merge(materia);
            if (idCarreraOld != null && !idCarreraOld.equals(idCarreraNew)) {
                idCarreraOld.getMateriaList().remove(materia);
                idCarreraOld = em.merge(idCarreraOld);
            }
            if (idCarreraNew != null && !idCarreraNew.equals(idCarreraOld)) {
                idCarreraNew.getMateriaList().add(materia);
                idCarreraNew = em.merge(idCarreraNew);
            }
            for (Matricula matriculaListOldMatricula : matriculaListOld) {
                if (!matriculaListNew.contains(matriculaListOldMatricula)) {
                    matriculaListOldMatricula.getMateriaList().remove(materia);
                    matriculaListOldMatricula = em.merge(matriculaListOldMatricula);
                }
            }
            for (Matricula matriculaListNewMatricula : matriculaListNew) {
                if (!matriculaListOld.contains(matriculaListNewMatricula)) {
                    matriculaListNewMatricula.getMateriaList().add(materia);
                    matriculaListNewMatricula = em.merge(matriculaListNewMatricula);
                }
            }
            for (Examen examenListNewExamen : examenListNew) {
                if (!examenListOld.contains(examenListNewExamen)) {
                    Materia oldMateriaOfExamenListNewExamen = examenListNewExamen.getMateria();
                    examenListNewExamen.setMateria(materia);
                    examenListNewExamen = em.merge(examenListNewExamen);
                    if (oldMateriaOfExamenListNewExamen != null && !oldMateriaOfExamenListNewExamen.equals(materia)) {
                        oldMateriaOfExamenListNewExamen.getExamenList().remove(examenListNewExamen);
                        oldMateriaOfExamenListNewExamen = em.merge(oldMateriaOfExamenListNewExamen);
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
                Integer id = materia.getIdMateria();
                if (findMateria(id) == null) {
                    throw new NonexistentEntityException("The materia with id " + id + " no longer exists.");
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
            Materia materia;
            try {
                materia = em.getReference(Materia.class, id);
                materia.getIdMateria();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The materia with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Examen> examenListOrphanCheck = materia.getExamenList();
            for (Examen examenListOrphanCheckExamen : examenListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Materia (" + materia + ") cannot be destroyed since the Examen " + examenListOrphanCheckExamen + " in its examenList field has a non-nullable materia field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Carrera idCarrera = materia.getIdCarrera();
            if (idCarrera != null) {
                idCarrera.getMateriaList().remove(materia);
                idCarrera = em.merge(idCarrera);
            }
            List<Matricula> matriculaList = materia.getMatriculaList();
            for (Matricula matriculaListMatricula : matriculaList) {
                matriculaListMatricula.getMateriaList().remove(materia);
                matriculaListMatricula = em.merge(matriculaListMatricula);
            }
            em.remove(materia);
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

    public List<Materia> findMateriaEntities() {
        return findMateriaEntities(true, -1, -1);
    }

    public List<Materia> findMateriaEntities(int maxResults, int firstResult) {
        return findMateriaEntities(false, maxResults, firstResult);
    }

    private List<Materia> findMateriaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Materia.class));
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

    public Materia findMateria(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Materia.class, id);
        } finally {
            em.close();
        }
    }

    public int getMateriaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Materia> rt = cq.from(Materia.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
