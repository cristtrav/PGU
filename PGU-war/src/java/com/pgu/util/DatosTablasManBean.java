/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.util;

import com.pgu.entidades.Alumno;
import com.pgu.entidades.Carrera;
import com.pgu.entidades.Ciudad;
import com.pgu.entidades.Docente;
import com.pgu.entidades.Facultad;
import com.pgu.entidades.Materia;
import com.pgu.entidades.Semestre;
import com.pgu.sesbeans.AlumnoFacade;
import com.pgu.sesbeans.CarreraFacade;
import com.pgu.sesbeans.CiudadFacade;
import com.pgu.sesbeans.DocenteFacade;
import com.pgu.sesbeans.FacultadFacade;
import com.pgu.sesbeans.MateriaFacade;
import com.pgu.sesbeans.SemestreFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;

/**
 *
 * @author cristhian
 */
@Named(value = "datosTablasManBean")
@RequestScoped
public class DatosTablasManBean {
    @EJB
    private DocenteFacade docenteFacade;
    @EJB
    private SemestreFacade semestreFacade;
    @EJB
    private MateriaFacade materiaFacade;
    @EJB
    private AlumnoFacade alumnoFacade;
    @EJB
    private CarreraFacade carreraFacade;
    @EJB
    private FacultadFacade facultadFacade;
    @EJB
    private CiudadFacade ciudadFacade;
    
    
    

    /**
     * Creates a new instance of DatosTablasManBean
     */
    public DatosTablasManBean() {
    }
    
    public List<Ciudad> getListaCiudades(){
        return this.ciudadFacade.findAll();
    }

    public List<Facultad> getFacultadFacade() {
        return this.facultadFacade.findAll();
    }
    public List<Carrera> getCarreras(){
        return this.carreraFacade.findAll();
    }

    public List<Alumno> getAlumnoFacade() {
        return this.alumnoFacade.findAll();
    }

    public List<Materia> getMateriaFacade() {
        return this.materiaFacade.findAll();
    }

    public List<Semestre> getSemestreFacade() {
        return this.semestreFacade.findAll();
    }

    public List<Docente> getDocenteFacade() {
        return this.docenteFacade.findAll();
    }
    
}
