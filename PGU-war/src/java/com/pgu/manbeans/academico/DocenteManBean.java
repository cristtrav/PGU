/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.manbeans.academico;

import com.pgu.entidades.Docente;
import com.pgu.sesbeans.DocenteFacade;
import com.pgu.sesbeans.SesUtils;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;

/**
 *
 * @author cristhian
 */
@Named(value = "docenteManBean")
@SessionScoped
public class DocenteManBean implements Serializable {

    private List<Docente> selDocente; //Lista para guardar los elementos seleccionados de la tabla

    public List<Docente> getSelDocente() {
        return selDocente;
    }

    public void setSelDocente(List<Docente> selDocente) {
        this.selDocente = selDocente;
    }


    private Docente selSimpDocente; //Objeto docente para almacenar la seleccion de la tabla

    public Docente getSelSimpDocente() {
        return selSimpDocente;
    }

    public void setSelSimpDocente(Docente selSimpDocente) {
        this.selSimpDocente = selSimpDocente;
    }

    
    @EJB
    private SesUtils sesUtils;

    //Objeto docente axiliar para la carga de datos del formulario y posterior alta en la base de datos
    private Docente tmpDocente;

    public Docente getTmpDocente() {
        return tmpDocente;
    }

    public void setTmpDocente(Docente tmpDocente) {
        this.tmpDocente = tmpDocente;
    }

    // se llama al EJB que interactua con la base de datos
    @EJB
    private DocenteFacade docenteFacade;
    
    //Objeto List que contiene los registros que se mostrarán en la tabla principal
    private List<Docente> lstDocentes;

    public List<Docente> getLstDocentes() {
        return lstDocentes;
    }

    public void setLstDocentes(List<Docente> lstDocentes) {
        this.lstDocentes = lstDocentes;
    }

    /**
     * Creates a new instance of ProfesorManBean
     */
    public DocenteManBean() {
        this.lstDocentes=new ArrayList<Docente>();
        this.tmpDocente=new Docente();
    }
    
    //Este método se ejecuta luego del constructor
    @PostConstruct()
    private void postConstruct(){
        this.cargarDocentes();
    }
    
    public void cargarDocentes(){
        this.lstDocentes.clear();
        this.lstDocentes.addAll(this.docenteFacade.findAll());
    }
    
    public void guardarDocente(){
        this.docenteFacade.create(tmpDocente);
        this.lstDocentes.add(tmpDocente);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Docente Agregado",""));
        this.tmpDocente=new Docente();
    }
    
    public void recargarDocente(){
            this.sesUtils.refrescarEntidad(lstDocentes);
    }
    
    public void mostrarDetalleDoc(){
        if(this.selDocente.size()==1){
            this.selSimpDocente=this.selDocente.get(0);
            RequestContext.getCurrentInstance().update("formDetalleDocente");
            RequestContext.getCurrentInstance().execute("dlgDetalleDocente.show()");
        }
    }
    
    public void modificarDocente(){
        this.docenteFacade.edit(this.selSimpDocente);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Modificado correctamente",""));
    }
    
    public void nuevoDocente(){
        this.tmpDocente=new Docente();
    }
    
}
