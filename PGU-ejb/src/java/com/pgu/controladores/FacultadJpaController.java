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
import com.pgu.entidades.Sede;
import java.util.ArrayList;
import java.util.List;
import com.pgu.entidades.Carrera;
import com.pgu.entidades.Facultad;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author cristhian
 */
public class FacultadJpaController implements Serializable {

    public FacultadJpaController(UserTransaction utx, EntityManagerFactory emf) {
        this.utx = utx;
        this.emf = emf;
    }
    private UserTransaction utx = null;
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Facultad facultad) throws RollbackFailureException, Exception {
        if (facultad.getSedeList() == null) {
            facultad.setSedeList(new ArrayList<Sede>());
        }
        if (facultad.getCarreraList() == null) {
            facultad.setCarreraList(new ArrayList<Carrera>());
        }
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            List<Sede> attachedSedeList = new ArrayList<Sede>();
            for (Sede sedeListSedeToAttach : facultad.getSedeList()) {
                sedeListSedeToAttach = em.getReference(sedeListSedeToAttach.getClass(), sedeListSedeToAttach.getSedePK());
                attachedSedeList.add(sedeListSedeToAttach);
            }
            facultad.setSedeList(attachedSedeList);
            List<Carrera> attachedCarreraList = new ArrayList<Carrera>();
            for (Carrera carreraListCarreraToAttach : facultad.getCarreraList()) {
                carreraListCarreraToAttach = em.getReference(carreraListCarreraToAttach.getClass(), carreraListCarreraToAttach.getIdCarrera());
                attachedCarreraList.add(carreraListCarreraToAttach);
            }
            facultad.setCarreraList(attachedCarreraList);
            em.persist(facultad);
            for (Sede sedeListSede : facultad.getSedeList()) {
                Facultad oldFacultadOfSedeListSede = sedeListSede.getFacultad();
                sedeListSede.setFacultad(facultad);
                sedeListSede = em.merge(sedeListSede);
                if (oldFacultadOfSedeListSede != null) {
                    oldFacultadOfSedeListSede.getSedeList().remove(sedeListSede);
                    oldFacultadOfSedeListSede = em.merge(oldFacultadOfSedeListSede);
                }
            }
            for (Carrera carreraListCarrera : facultad.getCarreraList()) {
                Facultad oldIdFacultadOfCarreraListCarrera = carreraListCarrera.getIdFacultad();
                carreraListCarrera.setIdFacultad(facultad);
                carreraListCarrera = em.merge(carreraListCarrera);
                if (oldIdFacultadOfCarreraListCarrera != null) {
                    oldIdFacultadOfCarreraListCarrera.getCarreraList().remove(carreraListCarrera);
                    oldIdFacultadOfCarreraListCarrera = em.merge(oldIdFacultadOfCarreraListCarrera);
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

    public void edit(Facultad facultad) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            utx.begin();
            em = getEntityManager();
            Facultad persistentFacultad = em.find(Facultad.class, facultad.getIdFacultad());
            List<Sede> sedeListOld = persistentFacultad.getSedeList();
            List<Sede> sedeListNew = facultad.getSedeList();
            List<Carrera> carreraListOld = persistentFacultad.getCarreraList();
            List<Carrera> carreraListNew = facultad.getCarreraList();
            List<String> illegalOrphanMessages = null;
            for (Sede sedeListOldSede : sedeListOld) {
                if (!sedeListNew.contains(sedeListOldSede)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Sede " + sedeListOldSede + " since its facultad field is not nullable.");
                }
            }
            for (Carrera carreraListOldCarrera : carreraListOld) {
                if (!carreraListNew.contains(carreraListOldCarrera)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Carrera " + carreraListOldCarrera + " since its idFacultad field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Sede> attachedSedeListNew = new ArrayList<Sede>();
            for (Sede sedeListNewSedeToAttach : sedeListNew) {
                sedeListNewSedeToAttach = em.getReference(sedeListNewSedeToAttach.getClass(), sedeListNewSedeToAttach.getSedePK());
                attachedSedeListNew.add(sedeListNewSedeToAttach);
            }
            sedeListNew = attachedSedeListNew;
            facultad.setSedeList(sedeListNew);
            List<Carrera> attachedCarreraListNew = new ArrayList<Carrera>();
            for (Carrera carreraListNewCarreraToAttach : carreraListNew) {
                carreraListNewCarreraToAttach = em.getReference(carreraListNewCarreraToAttach.getClass(), carreraListNewCarreraToAttach.getIdCarrera());
                attachedCarreraListNew.add(carreraListNewCarreraToAttach);
            }
            carreraListNew = attachedCarreraListNew;
            facultad.setCarreraList(carreraListNew);
            facultad = em.merge(facultad);
            for (Sede sedeListNewSede : sedeListNew) {
                if (!sedeListOld.contains(sedeListNewSede)) {
                    Facultad oldFacultadOfSedeListNewSede = sedeListNewSede.getFacultad();
                    sedeListNewSede.setFacultad(facultad);
                    sedeListNewSede = em.merge(sedeListNewSede);
                    if (oldFacultadOfSedeListNewSede != null && !oldFacultadOfSedeListNewSede.equals(facultad)) {
                        oldFacultadOfSedeListNewSede.getSedeList().remove(sedeListNewSede);
                        oldFacultadOfSedeListNewSede = em.merge(oldFacultadOfSedeListNewSede);
                    }
                }
            }
            for (Carrera carreraListNewCarrera : carreraListNew) {
                if (!carreraListOld.contains(carreraListNewCarrera)) {
                    Facultad oldIdFacultadOfCarreraListNewCarrera = carreraListNewCarrera.getIdFacultad();
                    carreraListNewCarrera.setIdFacultad(facultad);
                    carreraListNewCarrera = em.merge(carreraListNewCarrera);
                    if (oldIdFacultadOfCarreraListNewCarrera != null && !oldIdFacultadOfCarreraListNewCarrera.equals(facultad)) {
                        oldIdFacultadOfCarreraListNewCarrera.getCarreraList().remove(carreraListNewCarrera);
                        oldIdFacultadOfCarreraListNewCarrera = em.merge(oldIdFacultadOfCarreraListNewCarrera);
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
                Integer id = facultad.getIdFacultad();
                if (findFacultad(id) == null) {
                    throw new NonexistentEntityException("The facultad with id " + id + " no longer exists.");
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
            Facultad facultad;
            try {
                facultad = em.getReference(Facultad.class, id);
                facultad.getIdFacultad();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The facultad with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Sede> sedeListOrphanCheck = facultad.getSedeList();
            for (Sede sedeListOrphanCheckSede : sedeListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facultad (" + facultad + ") cannot be destroyed since the Sede " + sedeListOrphanCheckSede + " in its sedeList field has a non-nullable facultad field.");
            }
            List<Carrera> carreraListOrphanCheck = facultad.getCarreraList();
            for (Carrera carreraListOrphanCheckCarrera : carreraListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Facultad (" + facultad + ") cannot be destroyed since the Carrera " + carreraListOrphanCheckCarrera + " in its carreraList field has a non-nullable idFacultad field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(facultad);
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

    public List<Facultad> findFacultadEntities() {
        return findFacultadEntities(true, -1, -1);
    }

    public List<Facultad> findFacultadEntities(int maxResults, int firstResult) {
        return findFacultadEntities(false, maxResults, firstResult);
    }

    private List<Facultad> findFacultadEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Facultad.class));
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

    public Facultad findFacultad(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Facultad.class, id);
        } finally {
            em.close();
        }
    }

    public int getFacultadCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Facultad> rt = cq.from(Facultad.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
