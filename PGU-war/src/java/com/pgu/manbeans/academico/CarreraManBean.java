/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pgu.manbeans.academico;

import com.pgu.entidades.Carrera;
import com.pgu.sesbeans.CarreraFacade;
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

/**
 *
 * @author serql
 */
@Named(value = "CarreraManBean")
@SessionScoped
public class CarreraManBean implements Serializable {
    private static final Logger LOG = Logger.getLogger(CarreraManBean.class.getName());
    @EJB
    private CarreraFacade carreraFacade;
    @EJB
    private SesUtils sesUtils;
    private List<Carrera> lstcarreras ;
    private List<Carrera> selectcarreras;
    private Map<String, Carrera> cambiospendientes= new HashMap();
    private Carrera tmpcarrera;
    private Carrera selSimpcarrera;

    public List<Carrera> getLstcarreras() {
        return lstcarreras;
    }

    public void setLstcarreras(List<Carrera> lstcarreras) {
        this.lstcarreras = lstcarreras;
    }

    public List<Carrera> getSelectcarreras() {
        return selectcarreras;
    }

    public void setSelectcarreras(List<Carrera> selectcarreras) {
        this.selectcarreras = selectcarreras;
    }

    public Carrera getTmpcarrera() {
        return tmpcarrera;
    }

    public void setTmpcarrera(Carrera tmpcarrera) {
        this.tmpcarrera = tmpcarrera;
    }

    public Carrera getSelSimpcarrera() {
        return selSimpcarrera;
    }

    public void setSelSimpcarrera(Carrera selSimpcarrera) {
        this.selSimpcarrera = selSimpcarrera;
    }

    
    /**
     * Creates a new instance of CarreraManBean
     */
    public CarreraManBean() {
    this.lstcarreras=new ArrayList<Carrera>();
    this.tmpcarrera=new Carrera();
    this.selectcarreras=new ArrayList();
    System.out.println("Constructor Carrera ");
    }
    
    
    @PostConstruct
    public void cargaCarrera(){
        System.out.println("Cargando Carreras");
        this.lstcarreras.clear();
        this.lstcarreras.addAll(this.carreraFacade.findAll());
    }
    
    
    public void guardarCarrera(){
        System.out.println("bandera1");
        try {
            System.out.println("En la lista existen:"+this.lstcarreras.size());
            System.out.println("Guardando Carrera");
            System.out.println(this.tmpcarrera.getDescripcion());
            this.carreraFacade.create(this.tmpcarrera);
            
           FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Correcto",""));
           this.cargaCarrera();
           RequestContext.getCurrentInstance().update("formCarrera:tablaCarrera");
           System.out.println(this.lstcarreras.size()+"existen en la lista Carrera");
        } catch (Exception e) {
            System.out.println("Hubo un error al guardar el registro");
        }
    }
    
    
    public void limpiarformAlta(){
        this.tmpcarrera=new Carrera();
    }
    
    
    public void eliminarCarrera(){
        boolean error=false;
        ListIterator<Carrera> lit = this.selectcarreras.listIterator();
        System.out.println("dksqdjkas"+lit);
        List<Carrera> carreraElims =new ArrayList<Carrera>();
        System.out.println("dlkawjdlkwlwe"+carreraElims);
        while(lit.hasNext()){
            Carrera tempo=lit.next();
            try {
                System.out.println("wldkhq"+tempo);
                this.carreraFacade.remove(tempo);
                carreraElims.add(tempo);
            } catch (Exception exc) {
                error=true;
                CarreraManBean.LOG.log(Level.WARNING, "No se pudo Eliminar los Registros "+tempo, exc);
            }
        }
        ListIterator<Carrera> itsel = carreraElims.listIterator();
        while(itsel.hasNext()){
            this.selectcarreras.remove(itsel.next());
        }
        this.cargaCarrera();
        if(error){
            System.out.println("error"+error);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No pudieron eliminarse todos los Registros ", " "));
        }
        else
        {
            System.out.println("maldito error"+lit);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se eliminaron Correctamente los registros ", ""));
        }
    }
    
    
    public void confElim(){
        System.out.println("saf");
        if(this.selectcarreras.isEmpty()){
            FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "Deves seleccionar al menos registro y/o componente ", ""));
        }else{
           System.out.println("sa1");
            RequestContext.getCurrentInstance().execute("dlgConfirmElim.show()");
        }
    }
    

    public void modiCarr(){
        
        try {
            this.carreraFacade.edit(selSimpcarrera);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, ":::Se modifico exitosamente el registro:::", ""));
        } catch (Exception exca) {
            CarreraManBean.LOG.log(Level.SEVERE, "Error al trtar de modificar el registro");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "ERROR:: al tratar de modificar el registro"+exca.getLocalizedMessage(),""));
        }
    }
    
    
    public void llamardlgDetalle(){
        if(this.selectcarreras!=null){
            if(this.selectcarreras.size()==1){
              this.selSimpcarrera=this.selectcarreras.get(0);
              RequestContext.getCurrentInstance().update("formModifiCarrera");
              RequestContext.getCurrentInstance().execute("dlgviewCarrera");
              
            }
            else{
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Debes seleccionar por lo menos un registros para eliminar la Carrera", ""));
            }
        }
    }
    
    public void refrescartabla(){
        this.sesUtils.refrescarEntidad(this.lstcarreras);
    }
    
    @PreDestroy
    public void Predestroy(){
        System.out.println("El bean se autodestruira juazjuazjuaz");
    }
    
}


