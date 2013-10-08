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
import com.pgu.entidades.Matricula;
import java.util.ArrayList;
import java.util.List;
import com.pgu.entidades.Examen;
import com.pgu.entidades.Semestre;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class SemestreJpaController implements Serializable {

    public SemestreJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Semestre semestre) throws RollbackFailureException, Exception {
        if (semestre.getMatriculaList() == null) {
            semestre.setMatriculaList(new ArrayList<Matricula>());
        }
        if (semestre.getExamenList() == null) {
            semestre.setExamenList(new ArrayList<Examen>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Matricula> attachedMatriculaList = new ArrayList<Matricula>();
            for (Matricula matriculaListMatriculaToAttach : semestre.getMatriculaList()) {
                matriculaListMatriculaToAttach = em.getReference(matriculaListMatriculaToAttach.getClass(), matriculaListMatriculaToAttach.getIdMatricula());
                attachedMatriculaList.add(matriculaListMatriculaToAttach);
            }
            semestre.setMatriculaList(attachedMatriculaList);
            List<Examen> attachedExamenList = new ArrayList<Examen>();
            for (Examen examenListExamenToAttach : semestre.getExamenList()) {
                examenListExamenToAttach = em.getReference(examenListExamenToAttach.getClass(), examenListExamenToAttach.getExamenPK());
                attachedExamenList.add(examenListExamenToAttach);
            }
            semestre.setExamenList(attachedExamenList);
            em.persist(semestre);
            for (Matricula matriculaListMatricula : semestre.getMatriculaList()) {
                Semestre oldIdSemestreOfMatriculaListMatricula = matriculaListMatricula.getIdSemestre();
                matriculaListMatricula.setIdSemestre(semestre);
                matriculaListMatricula = em.merge(matriculaListMatricula);
                if (oldIdSemestreOfMatriculaListMatricula != null) {
                    oldIdSemestreOfMatriculaListMatricula.getMatriculaList().remove(matriculaListMatricula);
                    oldIdSemestreOfMatriculaListMatricula = em.merge(oldIdSemestreOfMatriculaListMatricula);
                }
            }
            for (Examen examenListExamen : semestre.getExamenList()) {
                Semestre oldSemestreOfExamenListExamen = examenListExamen.getSemestre();
                examenListExamen.setSemestre(semestre);
                examenListExamen = em.merge(examenListExamen);
                if (oldSemestreOfExamenListExamen != null) {
                    oldSemestreOfExamenListExamen.getExamenList().remove(examenListExamen);
                    oldSemestreOfExamenListExamen = em.merge(oldSemestreOfExamenListExamen);
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

    public void edit(Semestre semestre) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Semestre persistentSemestre = em.find(Semestre.class, semestre.getIdSemestre());
            List<Matricula> matriculaListOld = persistentSemestre.getMatriculaList();
            List<Matricula> matriculaListNew = semestre.getMatriculaList();
            List<Examen> examenListOld = persistentSemestre.getExamenList();
            List<Examen> examenListNew = semestre.getExamenList();
            List<String> illegalOrphanMessages = null;
            for (Matricula matriculaListOldMatricula : matriculaListOld) {
                if (!matriculaListNew.contains(matriculaListOldMatricula)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Matricula " + matriculaListOldMatricula + " since its idSemestre field is not nullable.");
                }
            }
            for (Examen examenListOldExamen : examenListOld) {
                if (!examenListNew.contains(examenListOldExamen)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Examen " + examenListOldExamen + " since its semestre field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Matricula> attachedMatriculaListNew = new ArrayList<Matricula>();
            for (Matricula matriculaListNewMatriculaToAttach : matriculaListNew) {
                matriculaListNewMatriculaToAttach = em.getReference(matriculaListNewMatriculaToAttach.getClass(), matriculaListNewMatriculaToAttach.getIdMatricula());
                attachedMatriculaListNew.add(matriculaListNewMatriculaToAttach);
            }
            matriculaListNew = attachedMatriculaListNew;
            semestre.setMatriculaList(matriculaListNew);
            List<Examen> attachedExamenListNew = new ArrayList<Examen>();
            for (Examen examenListNewExamenToAttach : examenListNew) {
                examenListNewExamenToAttach = em.getReference(examenListNewExamenToAttach.getClass(), examenListNewExamenToAttach.getExamenPK());
                attachedExamenListNew.add(examenListNewExamenToAttach);
            }
            examenListNew = attachedExamenListNew;
            semestre.setExamenList(examenListNew);
            semestre = em.merge(semestre);
            for (Matricula matriculaListNewMatricula : matriculaListNew) {
                if (!matriculaListOld.contains(matriculaListNewMatricula)) {
                    Semestre oldIdSemestreOfMatriculaListNewMatricula = matriculaListNewMatricula.getIdSemestre();
                    matriculaListNewMatricula.setIdSemestre(semestre);
                    matriculaListNewMatricula = em.merge(matriculaListNewMatricula);
                    if (oldIdSemestreOfMatriculaListNewMatricula != null && !oldIdSemestreOfMatriculaListNewMatricula.equals(semestre)) {
                        oldIdSemestreOfMatriculaListNewMatricula.getMatriculaList().remove(matriculaListNewMatricula);
                        oldIdSemestreOfMatriculaListNewMatricula = em.merge(oldIdSemestreOfMatriculaListNewMatricula);
                    }
                }
            }
            for (Examen examenListNewExamen : examenListNew) {
                if (!examenListOld.contains(examenListNewExamen)) {
                    Semestre oldSemestreOfExamenListNewExamen = examenListNewExamen.getSemestre();
                    examenListNewExamen.setSemestre(semestre);
                    examenListNewExamen = em.merge(examenListNewExamen);
                    if (oldSemestreOfExamenListNewExamen != null && !oldSemestreOfExamenListNewExamen.equals(semestre)) {
                        oldSemestreOfExamenListNewExamen.getExamenList().remove(examenListNewExamen);
                        oldSemestreOfExamenListNewExamen = em.merge(oldSemestreOfExamenListNewExamen);
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
                Integer id = semestre.getIdSemestre();
                if (findSemestre(id) == null) {
                    throw new NonexistentEntityException("The semestre with id " + id + " no longer exists.");
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
            Semestre semestre;
            try {
                semestre = em.getReference(Semestre.class, id);
                semestre.getIdSemestre();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The semestre with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Matricula> matriculaListOrphanCheck = semestre.getMatriculaList();
            for (Matricula matriculaListOrphanCheckMatricula : matriculaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Semestre (" + semestre + ") cannot be destroyed since the Matricula " + matriculaListOrphanCheckMatricula + " in its matriculaList field has a non-nullable idSemestre field.");
            }
            List<Examen> examenListOrphanCheck = semestre.getExamenList();
            for (Examen examenListOrphanCheckExamen : examenListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Semestre (" + semestre + ") cannot be destroyed since the Examen " + examenListOrphanCheckExamen + " in its examenList field has a non-nullable semestre field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(semestre);
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

    public List<Semestre> findSemestreEntities() {
        return findSemestreEntities(true, -1, -1);
    }

    public List<Semestre> findSemestreEntities(int maxResults, int firstResult) {
        return findSemestreEntities(false, maxResults, firstResult);
    }

    private List<Semestre> findSemestreEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Semestre.class));
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

    public Semestre findSemestre(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Semestre.class, id);
        } finally {
            em.close();
        }
    }

    public int getSemestreCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Semestre> rt = cq.from(Semestre.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
