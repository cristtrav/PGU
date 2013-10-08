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
import com.pgu.entidades.Semestre;
import com.pgu.entidades.Materia;
import com.pgu.entidades.Docente;
import com.pgu.entidades.Alumno;
import com.pgu.entidades.Examen;
import com.pgu.entidades.ExamenPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class ExamenJpaController implements Serializable {

    public ExamenJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Examen examen) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (examen.getExamenPK() == null) {
            examen.setExamenPK(new ExamenPK());
        }
        examen.getExamenPK().setIdMateria(examen.getMateria().getIdMateria());
        examen.getExamenPK().setIdAlumno(examen.getAlumno().getIdAlumno());
        examen.getExamenPK().setIdSemestre(examen.getSemestre().getIdSemestre());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Semestre semestre = examen.getSemestre();
            if (semestre != null) {
                semestre = em.getReference(semestre.getClass(), semestre.getIdSemestre());
                examen.setSemestre(semestre);
            }
            Materia materia = examen.getMateria();
            if (materia != null) {
                materia = em.getReference(materia.getClass(), materia.getIdMateria());
                examen.setMateria(materia);
            }
            Docente idDocente = examen.getIdDocente();
            if (idDocente != null) {
                idDocente = em.getReference(idDocente.getClass(), idDocente.getIdDocente());
                examen.setIdDocente(idDocente);
            }
            Alumno alumno = examen.getAlumno();
            if (alumno != null) {
                alumno = em.getReference(alumno.getClass(), alumno.getIdAlumno());
                examen.setAlumno(alumno);
            }
            em.persist(examen);
            if (semestre != null) {
                semestre.getExamenList().add(examen);
                semestre = em.merge(semestre);
            }
            if (materia != null) {
                materia.getExamenList().add(examen);
                materia = em.merge(materia);
            }
            if (idDocente != null) {
                idDocente.getExamenList().add(examen);
                idDocente = em.merge(idDocente);
            }
            if (alumno != null) {
                alumno.getExamenList().add(examen);
                alumno = em.merge(alumno);
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findExamen(examen.getExamenPK()) != null) {
                throw new PreexistingEntityException("Examen " + examen + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Examen examen) throws NonexistentEntityException, RollbackFailureException, Exception {
        examen.getExamenPK().setIdMateria(examen.getMateria().getIdMateria());
        examen.getExamenPK().setIdAlumno(examen.getAlumno().getIdAlumno());
        examen.getExamenPK().setIdSemestre(examen.getSemestre().getIdSemestre());
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Examen persistentExamen = em.find(Examen.class, examen.getExamenPK());
            Semestre semestreOld = persistentExamen.getSemestre();
            Semestre semestreNew = examen.getSemestre();
            Materia materiaOld = persistentExamen.getMateria();
            Materia materiaNew = examen.getMateria();
            Docente idDocenteOld = persistentExamen.getIdDocente();
            Docente idDocenteNew = examen.getIdDocente();
            Alumno alumnoOld = persistentExamen.getAlumno();
            Alumno alumnoNew = examen.getAlumno();
            if (semestreNew != null) {
                semestreNew = em.getReference(semestreNew.getClass(), semestreNew.getIdSemestre());
                examen.setSemestre(semestreNew);
            }
            if (materiaNew != null) {
                materiaNew = em.getReference(materiaNew.getClass(), materiaNew.getIdMateria());
                examen.setMateria(materiaNew);
            }
            if (idDocenteNew != null) {
                idDocenteNew = em.getReference(idDocenteNew.getClass(), idDocenteNew.getIdDocente());
                examen.setIdDocente(idDocenteNew);
            }
            if (alumnoNew != null) {
                alumnoNew = em.getReference(alumnoNew.getClass(), alumnoNew.getIdAlumno());
                examen.setAlumno(alumnoNew);
            }
            examen = em.merge(examen);
            if (semestreOld != null && !semestreOld.equals(semestreNew)) {
                semestreOld.getExamenList().remove(examen);
                semestreOld = em.merge(semestreOld);
            }
            if (semestreNew != null && !semestreNew.equals(semestreOld)) {
                semestreNew.getExamenList().add(examen);
                semestreNew = em.merge(semestreNew);
            }
            if (materiaOld != null && !materiaOld.equals(materiaNew)) {
                materiaOld.getExamenList().remove(examen);
                materiaOld = em.merge(materiaOld);
            }
            if (materiaNew != null && !materiaNew.equals(materiaOld)) {
                materiaNew.getExamenList().add(examen);
                materiaNew = em.merge(materiaNew);
            }
            if (idDocenteOld != null && !idDocenteOld.equals(idDocenteNew)) {
                idDocenteOld.getExamenList().remove(examen);
                idDocenteOld = em.merge(idDocenteOld);
            }
            if (idDocenteNew != null && !idDocenteNew.equals(idDocenteOld)) {
                idDocenteNew.getExamenList().add(examen);
                idDocenteNew = em.merge(idDocenteNew);
            }
            if (alumnoOld != null && !alumnoOld.equals(alumnoNew)) {
                alumnoOld.getExamenList().remove(examen);
                alumnoOld = em.merge(alumnoOld);
            }
            if (alumnoNew != null && !alumnoNew.equals(alumnoOld)) {
                alumnoNew.getExamenList().add(examen);
                alumnoNew = em.merge(alumnoNew);
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
                ExamenPK id = examen.getExamenPK();
                if (findExamen(id) == null) {
                    throw new NonexistentEntityException("The examen with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(ExamenPK id) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Examen examen;
            try {
                examen = em.getReference(Examen.class, id);
                examen.getExamenPK();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The examen with id " + id + " no longer exists.", enfe);
            }
            Semestre semestre = examen.getSemestre();
            if (semestre != null) {
                semestre.getExamenList().remove(examen);
                semestre = em.merge(semestre);
            }
            Materia materia = examen.getMateria();
            if (materia != null) {
                materia.getExamenList().remove(examen);
                materia = em.merge(materia);
            }
            Docente idDocente = examen.getIdDocente();
            if (idDocente != null) {
                idDocente.getExamenList().remove(examen);
                idDocente = em.merge(idDocente);
            }
            Alumno alumno = examen.getAlumno();
            if (alumno != null) {
                alumno.getExamenList().remove(examen);
                alumno = em.merge(alumno);
            }
            em.remove(examen);
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

    public List<Examen> findExamenEntities() {
        return findExamenEntities(true, -1, -1);
    }

    public List<Examen> findExamenEntities(int maxResults, int firstResult) {
        return findExamenEntities(false, maxResults, firstResult);
    }

    private List<Examen> findExamenEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Examen.class));
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

    public Examen findExamen(ExamenPK id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Examen.class, id);
        } finally {
            em.close();
        }
    }

    public int getExamenCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Examen> rt = cq.from(Examen.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
