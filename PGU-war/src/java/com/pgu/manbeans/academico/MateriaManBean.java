/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pgu.manbeans.academico;

import com.pgu.entidades.Materia;
import com.pgu.sesbeans.MateriaFacade;
import com.pgu.sesbeans.SesUtils;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import java.util.logging.Logger;
import javax.annotation.PreDestroy;

/**
 *
 * @author serql
 */
@Named(value = "MateriaManBean")
@SessionScoped
public class MateriaManBean implements Serializable {
    private static final Logger LOG = Logger.getLogger(MateriaManBean.class.getName());
    @EJB
    private SesUtils sesUtils;
    
    @EJB
    private MateriaFacade materiaFacade;
    
    
    private List<Materia> listaMateria;
    private List<Materia> selectMateria;
    private Materia temporalMateria;
    private Materia selSimpMateria;

    
    
    public List<Materia> getListaMateria() {
        return listaMateria;
    }

    public void setListaMateria(List<Materia> listaMateria) {
        this.listaMateria = listaMateria;
    }

    
    
    public List<Materia> getSelectMateria() {
        return selectMateria;
    }

    public void setSelectMateria(List<Materia> selectMateria) {
        this.selectMateria = selectMateria;
    }

    
    
    public Materia getTemporalMateria() {
        return temporalMateria;
    }

    public void setTemporalMateria(Materia temporalMateria) {
        this.temporalMateria = temporalMateria;
    }

    
    
    public Materia getSelSimpMateria() {
        return selSimpMateria;
    }

    public void setSelSimpMateria(Materia selSimpMateria) {
        this.selSimpMateria = selSimpMateria;
    }
    

    
    
    
    public MateriaManBean() {
    this.listaMateria= new ArrayList<Materia>();
    this.selectMateria=new ArrayList<Materia>();
    this.temporalMateria=new Materia();
    System.out.println("Constructor Materiabeans");
    }
    
    @PostConstruct
    public void cargarMaterias(){
        System.out.println("###Cargando Matria#### :# ");
        this.listaMateria.clear();
        this.listaMateria.addAll(this.materiaFacade.findAll());
    }
    
    
    public void guardadMaterias(){
        try {
            System.out.println(this.listaMateria.size()+"existen en la Lista");
            System.out.println("Cargando Materias");
            System.out.println(this.temporalMateria.getNombre());
            this.materiaFacade.create(this.temporalMateria);
            
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Correcto", ""));
            this.cargarMaterias();
            RequestContext.getCurrentInstance().update("forMateria:tablaMateria");
            System.out.println(this.listaMateria.size()+"existen actualmente en la lista");
        } catch (Exception exguardar) {
            System.out.println("Error al cargar Materias xD:"+exguardar);
        }
        
    }

    
    public void limpiarformAlta(){
        this.temporalMateria=new Materia();    
  }
    
    
       
    public void eliminarMateria(){
        System.out.println("a");
        boolean error=false;
        ListIterator<Materia> listM=this.selectMateria.listIterator(); 
        System.out.println("aa");
        List<Materia> Elims= new ArrayList<Materia>();
        System.out.println("ab");
        while(listM.hasNext()){
            Materia temporal=listM.next();
            try {
                this.materiaFacade.remove(temporal);
                Elims.add(temporal);
            } catch (Exception e) {
                error=true;
                MateriaManBean.LOG.log(Level.WARNING, "No se pudo eliminar el registro... que WAY!!!"+temporal ,e);
            }
        }
        ListIterator<Materia>itsel=Elims.listIterator();
        while(itsel.hasNext()){
            this.selectMateria.remove(itsel.next());
        }
        this.cargarMaterias();
        if(error){
            System.out.println("abc");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "QUE WAY :D::D:: ERROR:D::D::",""));
        }else{
            System.out.println("abcd");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "QUEEE WAY ###exitooo###",""));
        }
    }  
    
    
    
    public void confirmarelims(){
        System.out.println("####Confirmando#####Eliminacion###########Methodo###########");
        if (this.selectMateria.isEmpty()) {
            System.out.println("abcde");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "#######ERROR##AL##ELIMINAR##REGISTROS######", ""));
        }else{
            System.out.println("abcdef");
        RequestContext.getCurrentInstance().execute("dlgConfirmElim.show()");
        }
    }
    
    
    
    public void modifimateria(){
        try {
            this.materiaFacade.edit(this.selSimpMateria);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"##EL REGISTRO MATERIAS SE MODIFICO CORRECTAMENTE###",""));
        } catch (Exception modifi) {
            MateriaManBean.LOG.log(Level.SEVERE, "##error al editar el registro:"+modifi.getLocalizedMessage(), "");
        }
    }
    
    
    
    
    public void llamardlgDetalle(){
        if (this.selectMateria!=null) {
            if (this.selectMateria.size()==1) {
                this.selSimpMateria=this.selectMateria.get(0);
                RequestContext.getCurrentInstance().update("forModifiMateria");
                RequestContext.getCurrentInstance().update("dlgViewMateria");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "##Deves Seleccionar solo un registro para editar##", ""));
            }
        } 
    }
    
    
    
    public void refrescartabla(){
        this.sesUtils.refrescarEntidad(this.listaMateria);
    }
    
    
    
    @PreDestroy
    private void predestroy(){
        System.out.println("El beans se autodestruyo");
    }
}


