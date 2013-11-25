/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.manbeans.academico;

import com.pgu.entidades.Alumno;
import com.pgu.sesbeans.AlumnoFacade;
import com.pgu.sesbeans.SesUtils;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author cristhian
 */
@Named(value = "alumnoManBean")
@SessionScoped
public class AlumnoManBean implements Serializable {
    
    private static final Logger LOG = Logger.getLogger(AlumnoManBean.class.getName());
    
    @EJB
    private AlumnoFacade alumnoFacade;
    
    @EJB
    private SesUtils sesUtils;
    
    private List<Alumno> lstAlumnos;
    private Map<String, Alumno> cambiosPendientes=new HashMap();//Hashmap utilizado para cargar las entidades con cambios pendientes
    private List<Alumno> selAlumnos;
    private Alumno tmpAlu;
    private Alumno selSimpAlu;
    
    /**
     * Get the value of tmpAlu
     *
     * @return the value of tmpAlu
     */
    public Alumno getTmpAlu() {
        return tmpAlu;
    }
    /**
     * Get the value of lstAlumnos
     *
     * @return the value of lstAlumnos
     */
    public List<Alumno> getLstAlumnos() {
        return lstAlumnos;
    }
    
    /**
     * Get the value of selAlumnos
     *
     * @return the value of selAlumnos
     */
    public List<Alumno> getSelAlumnos() {
        return selAlumnos;
    }
    
    public Alumno getSelSimpAlu() {
        return selSimpAlu;
    }

    /**
     * Set the value of lstAlumnos
     *
     * @param lstAlumnos new value of lstAlumnos
     */
    public void setLstAlumnos(List<Alumno> lstAlumnos) {
        this.lstAlumnos = lstAlumnos;
    }
    /**
     * Set the value of selAlumnos
     *
     * @param selAlumnos new value of selAlumnos
     */
    public void setSelAlumnos(List<Alumno> selAlumnos) {
        this.selAlumnos = selAlumnos;
    }
    /**
     * Set the value of tmpAlu
     *
     * @param tmpAlu new value of tmpAlu
     */
    public void setTmpAlu(Alumno tmpAlu) {
        this.tmpAlu = tmpAlu;
    }
    
    public void setSelSimpAlu(Alumno selSimpAlu) {
        this.selSimpAlu = selSimpAlu;
    }

    
    /**
     * Creates a new instance of AlumnoManBean
     */
    public AlumnoManBean() {
        this.lstAlumnos=new ArrayList<Alumno>();
        this.tmpAlu=new Alumno();
        this.selAlumnos=new ArrayList<Alumno>();
       System.out.println("Constructor alumnobean");
    }
    
    @PostConstruct
    public void cargarAlumnos(){
        System.out.println("Cargando alumnos");
        this.lstAlumnos.clear();
        this.lstAlumnos.addAll(this.alumnoFacade.findAll());
    }
    
    public void guardarAlumno(){
        try{
            System.out.println("en lst hay: "+this.lstAlumnos.size());
            System.out.println("Guardando alumno");
            System.out.println(this.tmpAlu.getNombres());
            this.alumnoFacade.create(this.tmpAlu);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Correcto",""));
            this.cargarAlumnos();
            RequestContext.getCurrentInstance().update("formAlumnos:tablaAlumnos");
            System.out.println("en lst AHORA hay: "+this.lstAlumnos.size());
        }catch(Exception ex){
            System.out.println("Error al crear alumno: "+ex);
        }
    }
    
    public void limpiarFormAlta(){
        this.tmpAlu=new Alumno();
    }
    
    public void eliminarAlumnos(){
            boolean error=false;
            ListIterator<Alumno> lit=this.selAlumnos.listIterator();
            List<Alumno> alumElims=new ArrayList<Alumno>();
            while(lit.hasNext()){
                Alumno tmpa=lit.next();
                try{
                    this.alumnoFacade.remove(tmpa);
                    alumElims.add(tmpa);
                }catch(Exception ex){
                    error=true;
                    AlumnoManBean.LOG.log(Level.WARNING, "No se pudo eliminar el alumno: "+tmpa, ex);
                }
            }
            ListIterator<Alumno> itsel=alumElims.listIterator();
            while(itsel.hasNext()){
                this.selAlumnos.remove(itsel.next());
            }
            this.cargarAlumnos();
            if(error){
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"No pudieron eliminarse todos los elementos",""));
            }else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Se eliminaron correctamente los elementos",""));
            }
            //RequestContext.getCurrentInstance().update("formAlumnos");
    }
    
    public void confElim(){
        System.out.println("conelim method");
        if(this.selAlumnos.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN,"Debe seleccionar al menos un elemento",""));
            
        }else{
            RequestContext.getCurrentInstance().execute("dlgConfirmElim.show()");
        }
    }
    
    public void modifAlumno(){
        try{
            this.alumnoFacade.edit(this.selSimpAlu);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Se modificó correctamente",""));
        }catch(Exception exc){
            AlumnoManBean.LOG.log(Level.SEVERE, "Error al editar alumno", exc);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error al modificar alumno: "+exc.getLocalizedMessage(),""));
        }
        
        //System.out.println("El objeto es: "+e.getObject().getClass().getName());
    }
    
    public void llamarDlgDetalles(){
        if(this.selAlumnos!=null){
            if(this.selAlumnos.size()==1){
                this.selSimpAlu=this.selAlumnos.get(0);
                RequestContext.getCurrentInstance().update("formModifAlu");
                RequestContext.getCurrentInstance().execute("dlgViewAlumno.show()");
            }else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"Seleccione solo un elemento para editar",""));
            }
        }
    }
    
    public void refrescarTabla(){
        this.sesUtils.refrescarEntidad(this.lstAlumnos);
    }
    
    @PreDestroy
    private void preDestroy(){
        System.out.println("el bean va a morir");
    }
    
    
}
