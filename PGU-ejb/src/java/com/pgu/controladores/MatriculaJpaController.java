/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.controladores;

import com.pgu.controladores.exceptions.NonexistentEntityException;
import com.pgu.controladores.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.pgu.entidades.Usuario;
import com.pgu.entidades.Semestre;
import com.pgu.entidades.Carrera;
import com.pgu.entidades.Alumno;
import com.pgu.entidades.Materia;
import com.pgu.entidades.Matricula;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class MatriculaJpaController implements Serializable {

    public MatriculaJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Matricula matricula) throws RollbackFailureException, Exception {
        if (matricula.getMateriaList() == null) {
            matricula.setMateriaList(new ArrayList<Materia>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Usuario idUsuario = matricula.getIdUsuario();
            if (idUsuario != null) {
                idUsuario = em.getReference(idUsuario.getClass(), idUsuario.getIdUsuario());
                matricula.setIdUsuario(idUsuario);
            }
            Semestre idSemestre = matricula.getIdSemestre();
            if (idSemestre != null) {
                idSemestre = em.getReference(idSemestre.getClass(), idSemestre.getIdSemestre());
                matricula.setIdSemestre(idSemestre);
            }
            Carrera idCarrera = matricula.getIdCarrera();
            if (idCarrera != null) {
                idCarrera = em.getReference(idCarrera.getClass(), idCarrera.getIdCarrera());
                matricula.setIdCarrera(idCarrera);
            }
            Alumno idAlumno = matricula.getIdAlumno();
            if (idAlumno != null) {
                idAlumno = em.getReference(idAlumno.getClass(), idAlumno.getIdAlumno());
                matricula.setIdAlumno(idAlumno);
            }
            List<Materia> attachedMateriaList = new ArrayList<Materia>();
            for (Materia materiaListMateriaToAttach : matricula.getMateriaList()) {
                materiaListMateriaToAttach = em.getReference(materiaListMateriaToAttach.getClass(), materiaListMateriaToAttach.getIdMateria());
                attachedMateriaList.add(materiaListMateriaToAttach);
            }
            matricula.setMateriaList(attachedMateriaList);
            em.persist(matricula);
            if (idUsuario != null) {
                idUsuario.getMatriculaList().add(matricula);
                idUsuario = em.merge(idUsuario);
            }
            if (idSemestre != null) {
                idSemestre.getMatriculaList().add(matricula);
                idSemestre = em.merge(idSemestre);
            }
            if (idCarrera != null) {
                idCarrera.getMatriculaList().add(matricula);
                idCarrera = em.merge(idCarrera);
            }
            if (idAlumno != null) {
                idAlumno.getMatriculaList().add(matricula);
                idAlumno = em.merge(idAlumno);
            }
            for (Materia materiaListMateria : matricula.getMateriaList()) {
                materiaListMateria.getMatriculaList().add(matricula);
                materiaListMateria = em.merge(materiaListMateria);
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

    public void edit(Matricula matricula) throws NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Matricula persistentMatricula = em.find(Matricula.class, matricula.getIdMatricula());
            Usuario idUsuarioOld = persistentMatricula.getIdUsuario();
            Usuario idUsuarioNew = matricula.getIdUsuario();
            Semestre idSemestreOld = persistentMatricula.getIdSemestre();
            Semestre idSemestreNew = matricula.getIdSemestre();
            Carrera idCarreraOld = persistentMatricula.getIdCarrera();
            Carrera idCarreraNew = matricula.getIdCarrera();
            Alumno idAlumnoOld = persistentMatricula.getIdAlumno();
            Alumno idAlumnoNew = matricula.getIdAlumno();
            List<Materia> materiaListOld = persistentMatricula.getMateriaList();
            List<Materia> materiaListNew = matricula.getMateriaList();
            if (idUsuarioNew != null) {
                idUsuarioNew = em.getReference(idUsuarioNew.getClass(), idUsuarioNew.getIdUsuario());
                matricula.setIdUsuario(idUsuarioNew);
            }
            if (idSemestreNew != null) {
                idSemestreNew = em.getReference(idSemestreNew.getClass(), idSemestreNew.getIdSemestre());
                matricula.setIdSemestre(idSemestreNew);
            }
            if (idCarreraNew != null) {
                idCarreraNew = em.getReference(idCarreraNew.getClass(), idCarreraNew.getIdCarrera());
                matricula.setIdCarrera(idCarreraNew);
            }
            if (idAlumnoNew != null) {
                idAlumnoNew = em.getReference(idAlumnoNew.getClass(), idAlumnoNew.getIdAlumno());
                matricula.setIdAlumno(idAlumnoNew);
            }
            List<Materia> attachedMateriaListNew = new ArrayList<Materia>();
            for (Materia materiaListNewMateriaToAttach : materiaListNew) {
                materiaListNewMateriaToAttach = em.getReference(materiaListNewMateriaToAttach.getClass(), materiaListNewMateriaToAttach.getIdMateria());
                attachedMateriaListNew.add(materiaListNewMateriaToAttach);
            }
            materiaListNew = attachedMateriaListNew;
            matricula.setMateriaList(materiaListNew);
            matricula = em.merge(matricula);
            if (idUsuarioOld != null && !idUsuarioOld.equals(idUsuarioNew)) {
                idUsuarioOld.getMatriculaList().remove(matricula);
                idUsuarioOld = em.merge(idUsuarioOld);
            }
            if (idUsuarioNew != null && !idUsuarioNew.equals(idUsuarioOld)) {
                idUsuarioNew.getMatriculaList().add(matricula);
                idUsuarioNew = em.merge(idUsuarioNew);
            }
            if (idSemestreOld != null && !idSemestreOld.equals(idSemestreNew)) {
                idSemestreOld.getMatriculaList().remove(matricula);
                idSemestreOld = em.merge(idSemestreOld);
            }
            if (idSemestreNew != null && !idSemestreNew.equals(idSemestreOld)) {
                idSemestreNew.getMatriculaList().add(matricula);
                idSemestreNew = em.merge(idSemestreNew);
            }
            if (idCarreraOld != null && !idCarreraOld.equals(idCarreraNew)) {
                idCarreraOld.getMatriculaList().remove(matricula);
                idCarreraOld = em.merge(idCarreraOld);
            }
            if (idCarreraNew != null && !idCarreraNew.equals(idCarreraOld)) {
                idCarreraNew.getMatriculaList().add(matricula);
                idCarreraNew = em.merge(idCarreraNew);
            }
            if (idAlumnoOld != null && !idAlumnoOld.equals(idAlumnoNew)) {
                idAlumnoOld.getMatriculaList().remove(matricula);
                idAlumnoOld = em.merge(idAlumnoOld);
            }
            if (idAlumnoNew != null && !idAlumnoNew.equals(idAlumnoOld)) {
                idAlumnoNew.getMatriculaList().add(matricula);
                idAlumnoNew = em.merge(idAlumnoNew);
            }
            for (Materia materiaListOldMateria : materiaListOld) {
                if (!materiaListNew.contains(materiaListOldMateria)) {
                    materiaListOldMateria.getMatriculaList().remove(matricula);
                    materiaListOldMateria = em.merge(materiaListOldMateria);
                }
            }
            for (Materia materiaListNewMateria : materiaListNew) {
                if (!materiaListOld.contains(materiaListNewMateria)) {
                    materiaListNewMateria.getMatriculaList().add(matricula);
                    materiaListNewMateria = em.merge(materiaListNewMateria);
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
                Integer id = matricula.getIdMatricula();
                if (findMatricula(id) == null) {
                    throw new NonexistentEntityException("The matricula with id " + id + " no longer exists.");
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
            Matricula matricula;
            try {
                matricula = em.getReference(Matricula.class, id);
                matricula.getIdMatricula();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The matricula with id " + id + " no longer exists.", enfe);
            }
            Usuario idUsuario = matricula.getIdUsuario();
            if (idUsuario != null) {
                idUsuario.getMatriculaList().remove(matricula);
                idUsuario = em.merge(idUsuario);
            }
            Semestre idSemestre = matricula.getIdSemestre();
            if (idSemestre != null) {
                idSemestre.getMatriculaList().remove(matricula);
                idSemestre = em.merge(idSemestre);
            }
            Carrera idCarrera = matricula.getIdCarrera();
            if (idCarrera != null) {
                idCarrera.getMatriculaList().remove(matricula);
                idCarrera = em.merge(idCarrera);
            }
            Alumno idAlumno = matricula.getIdAlumno();
            if (idAlumno != null) {
                idAlumno.getMatriculaList().remove(matricula);
                idAlumno = em.merge(idAlumno);
            }
            List<Materia> materiaList = matricula.getMateriaList();
            for (Materia materiaListMateria : materiaList) {
                materiaListMateria.getMatriculaList().remove(matricula);
                materiaListMateria = em.merge(materiaListMateria);
            }
            em.remove(matricula);
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

    public List<Matricula> findMatriculaEntities() {
        return findMatriculaEntities(true, -1, -1);
    }

    public List<Matricula> findMatriculaEntities(int maxResults, int firstResult) {
        return findMatriculaEntities(false, maxResults, firstResult);
    }

    private List<Matricula> findMatriculaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Matricula.class));
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

    public Matricula findMatricula(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Matricula.class, id);
        } finally {
            em.close();
        }
    }

    public int getMatriculaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Matricula> rt = cq.from(Matricula.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
