/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.controladores;

import com.pgu.controladores.exceptions.IllegalOrphanException;
import com.pgu.controladores.exceptions.NonexistentEntityException;
import com.pgu.controladores.exceptions.RollbackFailureException;
import com.pgu.entidades.Carrera;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.pgu.entidades.Facultad;
import com.pgu.entidades.Materia;
import java.util.ArrayList;
import java.util.List;
import com.pgu.entidades.DetalleTasa;
import com.pgu.entidades.Matricula;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class CarreraJpaController implements Serializable {

    public CarreraJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Carrera carrera) throws RollbackFailureException, Exception {
        if (carrera.getMateriaList() == null) {
            carrera.setMateriaList(new ArrayList<Materia>());
        }
        if (carrera.getDetalleTasaList() == null) {
            carrera.setDetalleTasaList(new ArrayList<DetalleTasa>());
        }
        if (carrera.getMatriculaList() == null) {
            carrera.setMatriculaList(new ArrayList<Matricula>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facultad idFacultad = carrera.getIdFacultad();
            if (idFacultad != null) {
                idFacultad = em.getReference(idFacultad.getClass(), idFacultad.getIdFacultad());
                carrera.setIdFacultad(idFacultad);
            }
            List<Materia> attachedMateriaList = new ArrayList<Materia>();
            for (Materia materiaListMateriaToAttach : carrera.getMateriaList()) {
                materiaListMateriaToAttach = em.getReference(materiaListMateriaToAttach.getClass(), materiaListMateriaToAttach.getIdMateria());
                attachedMateriaList.add(materiaListMateriaToAttach);
            }
            carrera.setMateriaList(attachedMateriaList);
            List<DetalleTasa> attachedDetalleTasaList = new ArrayList<DetalleTasa>();
            for (DetalleTasa detalleTasaListDetalleTasaToAttach : carrera.getDetalleTasaList()) {
                detalleTasaListDetalleTasaToAttach = em.getReference(detalleTasaListDetalleTasaToAttach.getClass(), detalleTasaListDetalleTasaToAttach.getIdDetalleTasa());
                attachedDetalleTasaList.add(detalleTasaListDetalleTasaToAttach);
            }
            carrera.setDetalleTasaList(attachedDetalleTasaList);
            List<Matricula> attachedMatriculaList = new ArrayList<Matricula>();
            for (Matricula matriculaListMatriculaToAttach : carrera.getMatriculaList()) {
                matriculaListMatriculaToAttach = em.getReference(matriculaListMatriculaToAttach.getClass(), matriculaListMatriculaToAttach.getIdMatricula());
                attachedMatriculaList.add(matriculaListMatriculaToAttach);
            }
            carrera.setMatriculaList(attachedMatriculaList);
            em.persist(carrera);
            if (idFacultad != null) {
                idFacultad.getCarreraList().add(carrera);
                idFacultad = em.merge(idFacultad);
            }
            for (Materia materiaListMateria : carrera.getMateriaList()) {
                Carrera oldIdCarreraOfMateriaListMateria = materiaListMateria.getIdCarrera();
                materiaListMateria.setIdCarrera(carrera);
                materiaListMateria = em.merge(materiaListMateria);
                if (oldIdCarreraOfMateriaListMateria != null) {
                    oldIdCarreraOfMateriaListMateria.getMateriaList().remove(materiaListMateria);
                    oldIdCarreraOfMateriaListMateria = em.merge(oldIdCarreraOfMateriaListMateria);
                }
            }
            for (DetalleTasa detalleTasaListDetalleTasa : carrera.getDetalleTasaList()) {
                Carrera oldIdCarreraOfDetalleTasaListDetalleTasa = detalleTasaListDetalleTasa.getIdCarrera();
                detalleTasaListDetalleTasa.setIdCarrera(carrera);
                detalleTasaListDetalleTasa = em.merge(detalleTasaListDetalleTasa);
                if (oldIdCarreraOfDetalleTasaListDetalleTasa != null) {
                    oldIdCarreraOfDetalleTasaListDetalleTasa.getDetalleTasaList().remove(detalleTasaListDetalleTasa);
                    oldIdCarreraOfDetalleTasaListDetalleTasa = em.merge(oldIdCarreraOfDetalleTasaListDetalleTasa);
                }
            }
            for (Matricula matriculaListMatricula : carrera.getMatriculaList()) {
                Carrera oldIdCarreraOfMatriculaListMatricula = matriculaListMatricula.getIdCarrera();
                matriculaListMatricula.setIdCarrera(carrera);
                matriculaListMatricula = em.merge(matriculaListMatricula);
                if (oldIdCarreraOfMatriculaListMatricula != null) {
                    oldIdCarreraOfMatriculaListMatricula.getMatriculaList().remove(matriculaListMatricula);
                    oldIdCarreraOfMatriculaListMatricula = em.merge(oldIdCarreraOfMatriculaListMatricula);
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

    public void edit(Carrera carrera) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Carrera persistentCarrera = em.find(Carrera.class, carrera.getIdCarrera());
            Facultad idFacultadOld = persistentCarrera.getIdFacultad();
            Facultad idFacultadNew = carrera.getIdFacultad();
            List<Materia> materiaListOld = persistentCarrera.getMateriaList();
            List<Materia> materiaListNew = carrera.getMateriaList();
            List<DetalleTasa> detalleTasaListOld = persistentCarrera.getDetalleTasaList();
            List<DetalleTasa> detalleTasaListNew = carrera.getDetalleTasaList();
            List<Matricula> matriculaListOld = persistentCarrera.getMatriculaList();
            List<Matricula> matriculaListNew = carrera.getMatriculaList();
            List<String> illegalOrphanMessages = null;
            for (Materia materiaListOldMateria : materiaListOld) {
                if (!materiaListNew.contains(materiaListOldMateria)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Materia " + materiaListOldMateria + " since its idCarrera field is not nullable.");
                }
            }
            for (DetalleTasa detalleTasaListOldDetalleTasa : detalleTasaListOld) {
                if (!detalleTasaListNew.contains(detalleTasaListOldDetalleTasa)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain DetalleTasa " + detalleTasaListOldDetalleTasa + " since its idCarrera field is not nullable.");
                }
            }
            for (Matricula matriculaListOldMatricula : matriculaListOld) {
                if (!matriculaListNew.contains(matriculaListOldMatricula)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Matricula " + matriculaListOldMatricula + " since its idCarrera field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (idFacultadNew != null) {
                idFacultadNew = em.getReference(idFacultadNew.getClass(), idFacultadNew.getIdFacultad());
                carrera.setIdFacultad(idFacultadNew);
            }
            List<Materia> attachedMateriaListNew = new ArrayList<Materia>();
            for (Materia materiaListNewMateriaToAttach : materiaListNew) {
                materiaListNewMateriaToAttach = em.getReference(materiaListNewMateriaToAttach.getClass(), materiaListNewMateriaToAttach.getIdMateria());
                attachedMateriaListNew.add(materiaListNewMateriaToAttach);
            }
            materiaListNew = attachedMateriaListNew;
            carrera.setMateriaList(materiaListNew);
            List<DetalleTasa> attachedDetalleTasaListNew = new ArrayList<DetalleTasa>();
            for (DetalleTasa detalleTasaListNewDetalleTasaToAttach : detalleTasaListNew) {
                detalleTasaListNewDetalleTasaToAttach = em.getReference(detalleTasaListNewDetalleTasaToAttach.getClass(), detalleTasaListNewDetalleTasaToAttach.getIdDetalleTasa());
                attachedDetalleTasaListNew.add(detalleTasaListNewDetalleTasaToAttach);
            }
            detalleTasaListNew = attachedDetalleTasaListNew;
            carrera.setDetalleTasaList(detalleTasaListNew);
            List<Matricula> attachedMatriculaListNew = new ArrayList<Matricula>();
            for (Matricula matriculaListNewMatriculaToAttach : matriculaListNew) {
                matriculaListNewMatriculaToAttach = em.getReference(matriculaListNewMatriculaToAttach.getClass(), matriculaListNewMatriculaToAttach.getIdMatricula());
                attachedMatriculaListNew.add(matriculaListNewMatriculaToAttach);
            }
            matriculaListNew = attachedMatriculaListNew;
            carrera.setMatriculaList(matriculaListNew);
            carrera = em.merge(carrera);
            if (idFacultadOld != null && !idFacultadOld.equals(idFacultadNew)) {
                idFacultadOld.getCarreraList().remove(carrera);
                idFacultadOld = em.merge(idFacultadOld);
            }
            if (idFacultadNew != null && !idFacultadNew.equals(idFacultadOld)) {
                idFacultadNew.getCarreraList().add(carrera);
                idFacultadNew = em.merge(idFacultadNew);
            }
            for (Materia materiaListNewMateria : materiaListNew) {
                if (!materiaListOld.contains(materiaListNewMateria)) {
                    Carrera oldIdCarreraOfMateriaListNewMateria = materiaListNewMateria.getIdCarrera();
                    materiaListNewMateria.setIdCarrera(carrera);
                    materiaListNewMateria = em.merge(materiaListNewMateria);
                    if (oldIdCarreraOfMateriaListNewMateria != null && !oldIdCarreraOfMateriaListNewMateria.equals(carrera)) {
                        oldIdCarreraOfMateriaListNewMateria.getMateriaList().remove(materiaListNewMateria);
                        oldIdCarreraOfMateriaListNewMateria = em.merge(oldIdCarreraOfMateriaListNewMateria);
                    }
                }
            }
            for (DetalleTasa detalleTasaListNewDetalleTasa : detalleTasaListNew) {
                if (!detalleTasaListOld.contains(detalleTasaListNewDetalleTasa)) {
                    Carrera oldIdCarreraOfDetalleTasaListNewDetalleTasa = detalleTasaListNewDetalleTasa.getIdCarrera();
                    detalleTasaListNewDetalleTasa.setIdCarrera(carrera);
                    detalleTasaListNewDetalleTasa = em.merge(detalleTasaListNewDetalleTasa);
                    if (oldIdCarreraOfDetalleTasaListNewDetalleTasa != null && !oldIdCarreraOfDetalleTasaListNewDetalleTasa.equals(carrera)) {
                        oldIdCarreraOfDetalleTasaListNewDetalleTasa.getDetalleTasaList().remove(detalleTasaListNewDetalleTasa);
                        oldIdCarreraOfDetalleTasaListNewDetalleTasa = em.merge(oldIdCarreraOfDetalleTasaListNewDetalleTasa);
                    }
                }
            }
            for (Matricula matriculaListNewMatricula : matriculaListNew) {
                if (!matriculaListOld.contains(matriculaListNewMatricula)) {
                    Carrera oldIdCarreraOfMatriculaListNewMatricula = matriculaListNewMatricula.getIdCarrera();
                    matriculaListNewMatricula.setIdCarrera(carrera);
                    matriculaListNewMatricula = em.merge(matriculaListNewMatricula);
                    if (oldIdCarreraOfMatriculaListNewMatricula != null && !oldIdCarreraOfMatriculaListNewMatricula.equals(carrera)) {
                        oldIdCarreraOfMatriculaListNewMatricula.getMatriculaList().remove(matriculaListNewMatricula);
                        oldIdCarreraOfMatriculaListNewMatricula = em.merge(oldIdCarreraOfMatriculaListNewMatricula);
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
                Integer id = carrera.getIdCarrera();
                if (findCarrera(id) == null) {
                    throw new NonexistentEntityException("The carrera with id " + id + " no longer exists.");
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
            Carrera carrera;
            try {
                carrera = em.getReference(Carrera.class, id);
                carrera.getIdCarrera();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The carrera with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Materia> materiaListOrphanCheck = carrera.getMateriaList();
            for (Materia materiaListOrphanCheckMateria : materiaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Carrera (" + carrera + ") cannot be destroyed since the Materia " + materiaListOrphanCheckMateria + " in its materiaList field has a non-nullable idCarrera field.");
            }
            List<DetalleTasa> detalleTasaListOrphanCheck = carrera.getDetalleTasaList();
            for (DetalleTasa detalleTasaListOrphanCheckDetalleTasa : detalleTasaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Carrera (" + carrera + ") cannot be destroyed since the DetalleTasa " + detalleTasaListOrphanCheckDetalleTasa + " in its detalleTasaList field has a non-nullable idCarrera field.");
            }
            List<Matricula> matriculaListOrphanCheck = carrera.getMatriculaList();
            for (Matricula matriculaListOrphanCheckMatricula : matriculaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Carrera (" + carrera + ") cannot be destroyed since the Matricula " + matriculaListOrphanCheckMatricula + " in its matriculaList field has a non-nullable idCarrera field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Facultad idFacultad = carrera.getIdFacultad();
            if (idFacultad != null) {
                idFacultad.getCarreraList().remove(carrera);
                idFacultad = em.merge(idFacultad);
            }
            em.remove(carrera);
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

    public List<Carrera> findCarreraEntities() {
        return findCarreraEntities(true, -1, -1);
    }

    public List<Carrera> findCarreraEntities(int maxResults, int firstResult) {
        return findCarreraEntities(false, maxResults, firstResult);
    }

    private List<Carrera> findCarreraEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Carrera.class));
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

    public Carrera findCarrera(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Carrera.class, id);
        } finally {
            em.close();
        }
    }

    public int getCarreraCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Carrera> rt = cq.from(Carrera.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
