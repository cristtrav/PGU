/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.controladores;

import com.pgu.controladores.exceptions.IllegalOrphanException;
import com.pgu.controladores.exceptions.NonexistentEntityException;
import com.pgu.controladores.exceptions.RollbackFailureException;
import com.pgu.entidades.Alumno;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.pgu.entidades.Ciudad;
import com.pgu.entidades.Pago;
import java.util.ArrayList;
import java.util.List;
import com.pgu.entidades.Matricula;
import com.pgu.entidades.Examen;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class AlumnoJpaController implements Serializable {

    public AlumnoJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Alumno alumno) throws RollbackFailureException, Exception {
        if (alumno.getPagoList() == null) {
            alumno.setPagoList(new ArrayList<Pago>());
        }
        if (alumno.getMatriculaList() == null) {
            alumno.setMatriculaList(new ArrayList<Matricula>());
        }
        if (alumno.getExamenList() == null) {
            alumno.setExamenList(new ArrayList<Examen>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ciudad idCiudad = alumno.getIdCiudad();
            if (idCiudad != null) {
                idCiudad = em.getReference(idCiudad.getClass(), idCiudad.getIdCiudad());
                alumno.setIdCiudad(idCiudad);
            }
            List<Pago> attachedPagoList = new ArrayList<Pago>();
            for (Pago pagoListPagoToAttach : alumno.getPagoList()) {
                pagoListPagoToAttach = em.getReference(pagoListPagoToAttach.getClass(), pagoListPagoToAttach.getIdPagoTarifa());
                attachedPagoList.add(pagoListPagoToAttach);
            }
            alumno.setPagoList(attachedPagoList);
            List<Matricula> attachedMatriculaList = new ArrayList<Matricula>();
            for (Matricula matriculaListMatriculaToAttach : alumno.getMatriculaList()) {
                matriculaListMatriculaToAttach = em.getReference(matriculaListMatriculaToAttach.getClass(), matriculaListMatriculaToAttach.getIdMatricula());
                attachedMatriculaList.add(matriculaListMatriculaToAttach);
            }
            alumno.setMatriculaList(attachedMatriculaList);
            List<Examen> attachedExamenList = new ArrayList<Examen>();
            for (Examen examenListExamenToAttach : alumno.getExamenList()) {
                examenListExamenToAttach = em.getReference(examenListExamenToAttach.getClass(), examenListExamenToAttach.getExamenPK());
                attachedExamenList.add(examenListExamenToAttach);
            }
            alumno.setExamenList(attachedExamenList);
            em.persist(alumno);
            if (idCiudad != null) {
                idCiudad.getAlumnoList().add(alumno);
                idCiudad = em.merge(idCiudad);
            }
            for (Pago pagoListPago : alumno.getPagoList()) {
                Alumno oldIdAlumnoOfPagoListPago = pagoListPago.getIdAlumno();
                pagoListPago.setIdAlumno(alumno);
                pagoListPago = em.merge(pagoListPago);
                if (oldIdAlumnoOfPagoListPago != null) {
                    oldIdAlumnoOfPagoListPago.getPagoList().remove(pagoListPago);
                    oldIdAlumnoOfPagoListPago = em.merge(oldIdAlumnoOfPagoListPago);
                }
            }
            for (Matricula matriculaListMatricula : alumno.getMatriculaList()) {
                Alumno oldIdAlumnoOfMatriculaListMatricula = matriculaListMatricula.getIdAlumno();
                matriculaListMatricula.setIdAlumno(alumno);
                matriculaListMatricula = em.merge(matriculaListMatricula);
                if (oldIdAlumnoOfMatriculaListMatricula != null) {
                    oldIdAlumnoOfMatriculaListMatricula.getMatriculaList().remove(matriculaListMatricula);
                    oldIdAlumnoOfMatriculaListMatricula = em.merge(oldIdAlumnoOfMatriculaListMatricula);
                }
            }
            for (Examen examenListExamen : alumno.getExamenList()) {
                Alumno oldAlumnoOfExamenListExamen = examenListExamen.getAlumno();
                examenListExamen.setAlumno(alumno);
                examenListExamen = em.merge(examenListExamen);
                if (oldAlumnoOfExamenListExamen != null) {
                    oldAlumnoOfExamenListExamen.getExamenList().remove(examenListExamen);
                    oldAlumnoOfExamenListExamen = em.merge(oldAlumnoOfExamenListExamen);
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

    public void edit(Alumno alumno) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Alumno persistentAlumno = em.find(Alumno.class, alumno.getIdAlumno());
            Ciudad idCiudadOld = persistentAlumno.getIdCiudad();
            Ciudad idCiudadNew = alumno.getIdCiudad();
            List<Pago> pagoListOld = persistentAlumno.getPagoList();
            List<Pago> pagoListNew = alumno.getPagoList();
            List<Matricula> matriculaListOld = persistentAlumno.getMatriculaList();
            List<Matricula> matriculaListNew = alumno.getMatriculaList();
            List<Examen> examenListOld = persistentAlumno.getExamenList();
            List<Examen> examenListNew = alumno.getExamenList();
            List<String> illegalOrphanMessages = null;
            for (Pago pagoListOldPago : pagoListOld) {
                if (!pagoListNew.contains(pagoListOldPago)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pago " + pagoListOldPago + " since its idAlumno field is not nullable.");
                }
            }
            for (Matricula matriculaListOldMatricula : matriculaListOld) {
                if (!matriculaListNew.contains(matriculaListOldMatricula)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Matricula " + matriculaListOldMatricula + " since its idAlumno field is not nullable.");
                }
            }
            for (Examen examenListOldExamen : examenListOld) {
                if (!examenListNew.contains(examenListOldExamen)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Examen " + examenListOldExamen + " since its alumno field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idCiudadNew != null) {
                idCiudadNew = em.getReference(idCiudadNew.getClass(), idCiudadNew.getIdCiudad());
                alumno.setIdCiudad(idCiudadNew);
            }
            List<Pago> attachedPagoListNew = new ArrayList<Pago>();
            for (Pago pagoListNewPagoToAttach : pagoListNew) {
                pagoListNewPagoToAttach = em.getReference(pagoListNewPagoToAttach.getClass(), pagoListNewPagoToAttach.getIdPagoTarifa());
                attachedPagoListNew.add(pagoListNewPagoToAttach);
            }
            pagoListNew = attachedPagoListNew;
            alumno.setPagoList(pagoListNew);
            List<Matricula> attachedMatriculaListNew = new ArrayList<Matricula>();
            for (Matricula matriculaListNewMatriculaToAttach : matriculaListNew) {
                matriculaListNewMatriculaToAttach = em.getReference(matriculaListNewMatriculaToAttach.getClass(), matriculaListNewMatriculaToAttach.getIdMatricula());
                attachedMatriculaListNew.add(matriculaListNewMatriculaToAttach);
            }
            matriculaListNew = attachedMatriculaListNew;
            alumno.setMatriculaList(matriculaListNew);
            List<Examen> attachedExamenListNew = new ArrayList<Examen>();
            for (Examen examenListNewExamenToAttach : examenListNew) {
                examenListNewExamenToAttach = em.getReference(examenListNewExamenToAttach.getClass(), examenListNewExamenToAttach.getExamenPK());
                attachedExamenListNew.add(examenListNewExamenToAttach);
            }
            examenListNew = attachedExamenListNew;
            alumno.setExamenList(examenListNew);
            alumno = em.merge(alumno);
            if (idCiudadOld != null && !idCiudadOld.equals(idCiudadNew)) {
                idCiudadOld.getAlumnoList().remove(alumno);
                idCiudadOld = em.merge(idCiudadOld);
            }
            if (idCiudadNew != null && !idCiudadNew.equals(idCiudadOld)) {
                idCiudadNew.getAlumnoList().add(alumno);
                idCiudadNew = em.merge(idCiudadNew);
            }
            for (Pago pagoListNewPago : pagoListNew) {
                if (!pagoListOld.contains(pagoListNewPago)) {
                    Alumno oldIdAlumnoOfPagoListNewPago = pagoListNewPago.getIdAlumno();
                    pagoListNewPago.setIdAlumno(alumno);
                    pagoListNewPago = em.merge(pagoListNewPago);
                    if (oldIdAlumnoOfPagoListNewPago != null && !oldIdAlumnoOfPagoListNewPago.equals(alumno)) {
                        oldIdAlumnoOfPagoListNewPago.getPagoList().remove(pagoListNewPago);
                        oldIdAlumnoOfPagoListNewPago = em.merge(oldIdAlumnoOfPagoListNewPago);
                    }
                }
            }
            for (Matricula matriculaListNewMatricula : matriculaListNew) {
                if (!matriculaListOld.contains(matriculaListNewMatricula)) {
                    Alumno oldIdAlumnoOfMatriculaListNewMatricula = matriculaListNewMatricula.getIdAlumno();
                    matriculaListNewMatricula.setIdAlumno(alumno);
                    matriculaListNewMatricula = em.merge(matriculaListNewMatricula);
                    if (oldIdAlumnoOfMatriculaListNewMatricula != null && !oldIdAlumnoOfMatriculaListNewMatricula.equals(alumno)) {
                        oldIdAlumnoOfMatriculaListNewMatricula.getMatriculaList().remove(matriculaListNewMatricula);
                        oldIdAlumnoOfMatriculaListNewMatricula = em.merge(oldIdAlumnoOfMatriculaListNewMatricula);
                    }
                }
            }
            for (Examen examenListNewExamen : examenListNew) {
                if (!examenListOld.contains(examenListNewExamen)) {
                    Alumno oldAlumnoOfExamenListNewExamen = examenListNewExamen.getAlumno();
                    examenListNewExamen.setAlumno(alumno);
                    examenListNewExamen = em.merge(examenListNewExamen);
                    if (oldAlumnoOfExamenListNewExamen != null && !oldAlumnoOfExamenListNewExamen.equals(alumno)) {
                        oldAlumnoOfExamenListNewExamen.getExamenList().remove(examenListNewExamen);
                        oldAlumnoOfExamenListNewExamen = em.merge(oldAlumnoOfExamenListNewExamen);
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
                Integer id = alumno.getIdAlumno();
                if (findAlumno(id) == null) {
                    throw new NonexistentEntityException("The alumno with id " + id + " no longer exists.");
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
            Alumno alumno;
            try {
                alumno = em.getReference(Alumno.class, id);
                alumno.getIdAlumno();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The alumno with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Pago> pagoListOrphanCheck = alumno.getPagoList();
            for (Pago pagoListOrphanCheckPago : pagoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Alumno (" + alumno + ") cannot be destroyed since the Pago " + pagoListOrphanCheckPago + " in its pagoList field has a non-nullable idAlumno field.");
            }
            List<Matricula> matriculaListOrphanCheck = alumno.getMatriculaList();
            for (Matricula matriculaListOrphanCheckMatricula : matriculaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Alumno (" + alumno + ") cannot be destroyed since the Matricula " + matriculaListOrphanCheckMatricula + " in its matriculaList field has a non-nullable idAlumno field.");
            }
            List<Examen> examenListOrphanCheck = alumno.getExamenList();
            for (Examen examenListOrphanCheckExamen : examenListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Alumno (" + alumno + ") cannot be destroyed since the Examen " + examenListOrphanCheckExamen + " in its examenList field has a non-nullable alumno field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Ciudad idCiudad = alumno.getIdCiudad();
            if (idCiudad != null) {
                idCiudad.getAlumnoList().remove(alumno);
                idCiudad = em.merge(idCiudad);
            }
            em.remove(alumno);
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

    public List<Alumno> findAlumnoEntities() {
        return findAlumnoEntities(true, -1, -1);
    }

    public List<Alumno> findAlumnoEntities(int maxResults, int firstResult) {
        return findAlumnoEntities(false, maxResults, firstResult);
    }

    private List<Alumno> findAlumnoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Alumno.class));
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

    public Alumno findAlumno(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Alumno.class, id);
        } finally {
            em.close();
        }
    }

    public int getAlumnoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Alumno> rt = cq.from(Alumno.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
