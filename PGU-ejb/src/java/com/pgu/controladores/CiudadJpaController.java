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
import com.pgu.entidades.Departamento;
import com.pgu.entidades.Sede;
import java.util.ArrayList;
import java.util.List;
import com.pgu.entidades.Alumno;
import com.pgu.entidades.Ciudad;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class CiudadJpaController implements Serializable {

    public CiudadJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Ciudad ciudad) throws RollbackFailureException, Exception {
        if (ciudad.getSedeList() == null) {
            ciudad.setSedeList(new ArrayList<Sede>());
        }
        if (ciudad.getAlumnoList() == null) {
            ciudad.setAlumnoList(new ArrayList<Alumno>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Departamento idDepartamento = ciudad.getIdDepartamento();
            if (idDepartamento != null) {
                idDepartamento = em.getReference(idDepartamento.getClass(), idDepartamento.getIdDepartamento());
                ciudad.setIdDepartamento(idDepartamento);
            }
            List<Sede> attachedSedeList = new ArrayList<Sede>();
            for (Sede sedeListSedeToAttach : ciudad.getSedeList()) {
                sedeListSedeToAttach = em.getReference(sedeListSedeToAttach.getClass(), sedeListSedeToAttach.getSedePK());
                attachedSedeList.add(sedeListSedeToAttach);
            }
            ciudad.setSedeList(attachedSedeList);
            List<Alumno> attachedAlumnoList = new ArrayList<Alumno>();
            for (Alumno alumnoListAlumnoToAttach : ciudad.getAlumnoList()) {
                alumnoListAlumnoToAttach = em.getReference(alumnoListAlumnoToAttach.getClass(), alumnoListAlumnoToAttach.getIdAlumno());
                attachedAlumnoList.add(alumnoListAlumnoToAttach);
            }
            ciudad.setAlumnoList(attachedAlumnoList);
            em.persist(ciudad);
            if (idDepartamento != null) {
                idDepartamento.getCiudadList().add(ciudad);
                idDepartamento = em.merge(idDepartamento);
            }
            for (Sede sedeListSede : ciudad.getSedeList()) {
                Ciudad oldCiudadOfSedeListSede = sedeListSede.getCiudad();
                sedeListSede.setCiudad(ciudad);
                sedeListSede = em.merge(sedeListSede);
                if (oldCiudadOfSedeListSede != null) {
                    oldCiudadOfSedeListSede.getSedeList().remove(sedeListSede);
                    oldCiudadOfSedeListSede = em.merge(oldCiudadOfSedeListSede);
                }
            }
            for (Alumno alumnoListAlumno : ciudad.getAlumnoList()) {
                Ciudad oldIdCiudadOfAlumnoListAlumno = alumnoListAlumno.getIdCiudad();
                alumnoListAlumno.setIdCiudad(ciudad);
                alumnoListAlumno = em.merge(alumnoListAlumno);
                if (oldIdCiudadOfAlumnoListAlumno != null) {
                    oldIdCiudadOfAlumnoListAlumno.getAlumnoList().remove(alumnoListAlumno);
                    oldIdCiudadOfAlumnoListAlumno = em.merge(oldIdCiudadOfAlumnoListAlumno);
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

    public void edit(Ciudad ciudad) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Ciudad persistentCiudad = em.find(Ciudad.class, ciudad.getIdCiudad());
            Departamento idDepartamentoOld = persistentCiudad.getIdDepartamento();
            Departamento idDepartamentoNew = ciudad.getIdDepartamento();
            List<Sede> sedeListOld = persistentCiudad.getSedeList();
            List<Sede> sedeListNew = ciudad.getSedeList();
            List<Alumno> alumnoListOld = persistentCiudad.getAlumnoList();
            List<Alumno> alumnoListNew = ciudad.getAlumnoList();
            List<String> illegalOrphanMessages = null;
            for (Sede sedeListOldSede : sedeListOld) {
                if (!sedeListNew.contains(sedeListOldSede)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sede " + sedeListOldSede + " since its ciudad field is not nullable.");
                }
            }
            for (Alumno alumnoListOldAlumno : alumnoListOld) {
                if (!alumnoListNew.contains(alumnoListOldAlumno)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Alumno " + alumnoListOldAlumno + " since its idCiudad field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idDepartamentoNew != null) {
                idDepartamentoNew = em.getReference(idDepartamentoNew.getClass(), idDepartamentoNew.getIdDepartamento());
                ciudad.setIdDepartamento(idDepartamentoNew);
            }
            List<Sede> attachedSedeListNew = new ArrayList<Sede>();
            for (Sede sedeListNewSedeToAttach : sedeListNew) {
                sedeListNewSedeToAttach = em.getReference(sedeListNewSedeToAttach.getClass(), sedeListNewSedeToAttach.getSedePK());
                attachedSedeListNew.add(sedeListNewSedeToAttach);
            }
            sedeListNew = attachedSedeListNew;
            ciudad.setSedeList(sedeListNew);
            List<Alumno> attachedAlumnoListNew = new ArrayList<Alumno>();
            for (Alumno alumnoListNewAlumnoToAttach : alumnoListNew) {
                alumnoListNewAlumnoToAttach = em.getReference(alumnoListNewAlumnoToAttach.getClass(), alumnoListNewAlumnoToAttach.getIdAlumno());
                attachedAlumnoListNew.add(alumnoListNewAlumnoToAttach);
            }
            alumnoListNew = attachedAlumnoListNew;
            ciudad.setAlumnoList(alumnoListNew);
            ciudad = em.merge(ciudad);
            if (idDepartamentoOld != null && !idDepartamentoOld.equals(idDepartamentoNew)) {
                idDepartamentoOld.getCiudadList().remove(ciudad);
                idDepartamentoOld = em.merge(idDepartamentoOld);
            }
            if (idDepartamentoNew != null && !idDepartamentoNew.equals(idDepartamentoOld)) {
                idDepartamentoNew.getCiudadList().add(ciudad);
                idDepartamentoNew = em.merge(idDepartamentoNew);
            }
            for (Sede sedeListNewSede : sedeListNew) {
                if (!sedeListOld.contains(sedeListNewSede)) {
                    Ciudad oldCiudadOfSedeListNewSede = sedeListNewSede.getCiudad();
                    sedeListNewSede.setCiudad(ciudad);
                    sedeListNewSede = em.merge(sedeListNewSede);
                    if (oldCiudadOfSedeListNewSede != null && !oldCiudadOfSedeListNewSede.equals(ciudad)) {
                        oldCiudadOfSedeListNewSede.getSedeList().remove(sedeListNewSede);
                        oldCiudadOfSedeListNewSede = em.merge(oldCiudadOfSedeListNewSede);
                    }
                }
            }
            for (Alumno alumnoListNewAlumno : alumnoListNew) {
                if (!alumnoListOld.contains(alumnoListNewAlumno)) {
                    Ciudad oldIdCiudadOfAlumnoListNewAlumno = alumnoListNewAlumno.getIdCiudad();
                    alumnoListNewAlumno.setIdCiudad(ciudad);
                    alumnoListNewAlumno = em.merge(alumnoListNewAlumno);
                    if (oldIdCiudadOfAlumnoListNewAlumno != null && !oldIdCiudadOfAlumnoListNewAlumno.equals(ciudad)) {
                        oldIdCiudadOfAlumnoListNewAlumno.getAlumnoList().remove(alumnoListNewAlumno);
                        oldIdCiudadOfAlumnoListNewAlumno = em.merge(oldIdCiudadOfAlumnoListNewAlumno);
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
                Integer id = ciudad.getIdCiudad();
                if (findCiudad(id) == null) {
                    throw new NonexistentEntityException("The ciudad with id " + id + " no longer exists.");
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
            Ciudad ciudad;
            try {
                ciudad = em.getReference(Ciudad.class, id);
                ciudad.getIdCiudad();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The ciudad with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Sede> sedeListOrphanCheck = ciudad.getSedeList();
            for (Sede sedeListOrphanCheckSede : sedeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ciudad (" + ciudad + ") cannot be destroyed since the Sede " + sedeListOrphanCheckSede + " in its sedeList field has a non-nullable ciudad field.");
            }
            List<Alumno> alumnoListOrphanCheck = ciudad.getAlumnoList();
            for (Alumno alumnoListOrphanCheckAlumno : alumnoListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Ciudad (" + ciudad + ") cannot be destroyed since the Alumno " + alumnoListOrphanCheckAlumno + " in its alumnoList field has a non-nullable idCiudad field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Departamento idDepartamento = ciudad.getIdDepartamento();
            if (idDepartamento != null) {
                idDepartamento.getCiudadList().remove(ciudad);
                idDepartamento = em.merge(idDepartamento);
            }
            em.remove(ciudad);
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

    public List<Ciudad> findCiudadEntities() {
        return findCiudadEntities(true, -1, -1);
    }

    public List<Ciudad> findCiudadEntities(int maxResults, int firstResult) {
        return findCiudadEntities(false, maxResults, firstResult);
    }

    private List<Ciudad> findCiudadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Ciudad.class));
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

    public Ciudad findCiudad(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Ciudad.class, id);
        } finally {
            em.close();
        }
    }

    public int getCiudadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Ciudad> rt = cq.from(Ciudad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
