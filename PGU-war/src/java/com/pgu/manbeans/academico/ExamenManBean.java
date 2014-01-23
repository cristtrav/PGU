/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pgu.manbeans.academico;

import com.pgu.entidades.Examen;
import com.pgu.sesbeans.ExamenFacade;
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
import javax.faces.model.SelectItem;
import org.primefaces.context.RequestContext;

/**
 *
 * @author serql
 */
@Named(value = "Examenes")
@SessionScoped
public class ExamenManBean implements Serializable {
   
   
 private static final Logger LOG = Logger.getLogger(ExamenManBean.class.getName());
 
  @EJB
    private ExamenFacade examenFacade;
  
   @EJB
    private SesUtils sesUtils;
 private String Calificacion;
 private List<Examen> listaexa;
 private List<Examen> selectexa;
 private Examen temporalexa;
 private Examen SelSimpexa;
 private List<Examen> FiltroExa; 
// private List<Examen> alusel;
 private SelectItem[] Semestreoption;

 
 
    public List<Examen> getFiltroExa() {
        return FiltroExa;
    }

    public void setFiltroExa(List<Examen> FiltroExa) {
        this.FiltroExa = FiltroExa;
    }
 
    public List<Examen> getListaexa() {
        return listaexa;
    }

    public void setListaexa(List<Examen> listaexa) {
        this.listaexa = listaexa;
    }

    public List<Examen> getSelectexa() {
        return selectexa;
    }

    public void setSelectexa(List<Examen> selectexa) {
        this.selectexa = selectexa;
    }

    public Examen getTemporalexa() {
        return temporalexa;
    }

    public void setTemporalexa(Examen temporalexa) {
        this.temporalexa = temporalexa;
    }

    public Examen getSelSimpexa() {
        return SelSimpexa;
    }

    public void setSelSimpexa(Examen SelSimpexa) {
        this.SelSimpexa = SelSimpexa;
    }

 
 
 
 /**
     * Creates a new instance of Examenes
     */
    public ExamenManBean() {
        this.listaexa=new ArrayList<Examen>();
        this.temporalexa=new Examen();
        this.selectexa=new ArrayList<Examen>();
        this.SelSimpexa=new Examen();
        System.out.println("Constructor ExamenManBeans");
       
       
    }
    @PostConstruct
    public void cargarExmen(){
        System.out.println("bandera01");
        System.out.println("CArgando Examenes");
        this.listaexa.clear();
        this.listaexa.addAll(this.examenFacade.findAll());
        System.out.println("bandera001");
    }
    
    public void guardarExamen(){
        try {
            System.out.println("bandera1");
             System.out.println("En la lista existen:"+this.listaexa.size());
             System.out.println("Guardando Examen");
             System.out.println(this.temporalexa.getCalificacion());
             this.examenFacade.create(this.temporalexa);
            System.out.println("bandera2"); 
             FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "####Correcto####", ""));
             this.cargarExmen();
             RequestContext.getCurrentInstance().update("formExamen:tablaExa");
             System.out.println("Existen en lista en este momento:"+this.listaexa.size());
        } catch (Exception e) {
            System.out.println("bandera3");
            System.out.println("Error al crear Examen");
        }

    }
    
    public void limpiarFormAlta(){
        this.temporalexa=new Examen();
    }
    
    public void deletExa(){
    boolean error=false;
    ListIterator<Examen> lit=this.selectexa.listIterator();
    List<Examen> Exadelt=new ArrayList<Examen>();
        while (lit.hasNext()) {            
            Examen temporal=lit.next();
            try {
                this.examenFacade.remove(temporal);
                Exadelt.add(temporal);
            } catch (Exception exe) {
                error=true;
                ExamenManBean.LOG.log(Level.WARNING, "No se pudo eliminar el registro"+temporal, exe);
            }
        }
        ListIterator<Examen> itsel=Exadelt.listIterator();
        while(itsel.hasNext()){
            this.selectexa.remove(itsel.next());
        }
        this.cargarExmen();
        if(error){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al tratar de eliminar los registros", ""));
        }else{
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Seha eliminado correctamente los registros seleccionados", ""));
        }
    }
    
    public void confirDelt(){
        System.out.println("Methodo conelin");
        if (this.selectexa.isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Debes seleccionar por lo menos un registros", ""));
        } else {
            RequestContext.getCurrentInstance().execute("dlgConfirmElimns.show()");
        }
    }
    
    public void modifiExa(){
        try {
            this.examenFacade.edit(this.SelSimpexa);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se modifico correctamente el registro", ""));
        } catch (Exception ex) {
            ExamenManBean.LOG.log(Level.WARNING, "Error al modificar registros Examen", ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar Examen"+ex.getLocalizedMessage(), ""));
        }
    }
    
    public void llamardlgDEtalles(){
        if(this.selectexa!=null){
            if(this.selectexa.size()==1){
                this.SelSimpexa=this.selectexa.get(0);
                RequestContext.getCurrentInstance().update("formModifi");
                RequestContext.getCurrentInstance().execute("dlgViewExamen.show()");
            }else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error al tratar de modificar el registro", ""));
            }
        }
    }
    
    
    public void refrescarTablas(){
        this.sesUtils.refrescarEntidad(this.listaexa);
    }
    
  private SelectItem[] createFilterOptions(String[] data){
      SelectItem[]options = new SelectItem[data.length +1];
      
      options[0]= new SelectItem("","Select");
      for(int i=0; i< data.length; i++){
          options[i+1] = new SelectItem(data[i],data[i]);
      }
      return options;
  }
  
  public SelectItem[] getSemestreoption(){
      return Semestreoption;
  }
    
    @PreDestroy
    private void destroybeans(){
        System.out.println("El beans se ha suicidado");
    }

  
}
