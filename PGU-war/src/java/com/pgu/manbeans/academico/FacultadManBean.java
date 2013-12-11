/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pgu.manbeans.academico;

import com.pgu.entidades.Facultad;
import com.pgu.sesbeans.FacultadFacade;
import com.pgu.sesbeans.SesUtils;
import static com.sun.xml.ws.security.addressing.impl.policy.Constants.logger;
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
@Named(value = "FacultadManBean")
@SessionScoped
public class FacultadManBean implements Serializable {
    private static final Logger LOG = Logger.getLogger(FacultadManBean.class.getName());
    @EJB
    private SesUtils sesUtils;
    @EJB
    private FacultadFacade facultadFacade;
    private List<Facultad> listaFacultad;
    private List<Facultad> selectFacu;
    private Map<String, Facultad> cambiospendientes= new HashMap();
    private Facultad temporalFacultad;
    private Facultad selSimpFacu;

    public Facultad getTemporalFacultad() {
        return temporalFacultad;
    }

    public List<Facultad> getSelectFacu() {
        return selectFacu;
    }

    public Facultad getSelSimpFacu() {
        return selSimpFacu;
    }
    
    public List<Facultad> getListaFacultad() {
        return listaFacultad;
    }
    
    public void setTemporalFacultad(Facultad temporalFacultad) {
        this.temporalFacultad = temporalFacultad;
    }


    public void setSelectFacu(List<Facultad> selectFacu) {
        this.selectFacu = selectFacu;
    }

    
    public void setSelSimpFacu(Facultad selSimpFacu) {
        this.selSimpFacu = selSimpFacu;
    }

    
    public void setListaFacultad(List<Facultad> listaFacultad) {
        this.listaFacultad = listaFacultad;
    }
    
    /**
     * Creates a new instance of FacultadManBean
     */
    public FacultadManBean() {
    this.listaFacultad=new ArrayList<Facultad>();
    this.temporalFacultad= new Facultad();
    this.selectFacu=new ArrayList<Facultad>();
    System.out.println("Constructor Facultad");
    }
    
    
    @PostConstruct
    public void cargarFacultad()
    {
        System.out.println("Cargando Facultad");
        this.listaFacultad.clear();
        this.listaFacultad.addAll(this.facultadFacade.findAll());
    }
    
    
    public void guardarFAcultad(){
        System.out.println("Hasta aqui todo bien");
     try
        {
        System.out.println("en Lista hay:"+this.listaFacultad.size());
        System.out.println("Guardando Facultad");
        System.out.println(this.temporalFacultad.getDescripcion());
        this.facultadFacade.create(this.temporalFacultad);
      
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Correcto",""));
        this.cargarFacultad();
        RequestContext.getCurrentInstance().update("formFacultad:tablaFacultad");
        System.out.println("en lista ahora hay:"+this.listaFacultad.size());
        }
            catch(Exception ex)
            {
            System.out.println("Error al crear Facultad"+ex);
            }
      }
    
    
    public void limpiarformAlta()
        {
            System.out.println("LimpiarFormAlta ");
        this.temporalFacultad=new Facultad();

        }
    
    
    public void eliminarfacu()
        {
            System.out.println("quiero eliminar ");
        boolean error = false;
        ListIterator<Facultad> lit= this.selectFacu.listIterator();
        List<Facultad> facultadElims=new ArrayList<Facultad>();
        while(lit.hasNext())
            {
                System.out.println("eliminar primer while ");
               Facultad tmp=lit.next();
             try 
                {
                    System.out.println("eliminar try ");
                   this.facultadFacade.remove(tmp);
                   facultadElims.add(tmp);
                 } 
                   catch (Exception e)
                                       {
                                           System.out.println("eliminar catch e ");
                                        error = true;
                                        FacultadManBean.LOG.log(Level.WARNING, "No se puede Eliminar la Facultad"+tmp, e);
                                        }
               }
            ListIterator<Facultad> itsel = facultadElims.listIterator();
            while(itsel.hasNext())
                                  {
                                      System.out.println("eliminar while ");
                                   this.selectFacu.remove(itsel.next());
                                  }
            this.cargarFacultad();
            if(error)
                    {
                        System.out.println("eliminar if ");
                     FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No pudieron Eliminarse todos los elementos",""));
                     }
                     else
                         {
                             System.out.println("Eliminar else ");
                          FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se eliminaron todos los elementos sastifactoriamente", ""));
                          }
            }
        
    
        public void confElim(){
            if(this.selectFacu.isEmpty()){
                System.out.println("confelim if ");
                FacesContext.getCurrentInstance().addMessage("", new FacesMessage(FacesMessage.SEVERITY_WARN, "Deves seleccionar al menos un elemento", ""));
                  }else{
                RequestContext.getCurrentInstance().execute("dlgConfirmElim.show()");
                       }
                              }
        
        
        public void modifFacu()
                              {
                                  System.out.println("modifFacu ");
                               try 
                                  {
                                      System.out.println("modifFacu en try ");
                                   this.facultadFacade.edit(selSimpFacu);
                                   FacesContext.getCurrentInstance().addMessage(null, new FacesMessage (FacesMessage.SEVERITY_INFO,"Se modifico correctamente", ""));
                                    } 
                                    catch(Exception exc) 
                                                        {
                                                            System.out.println("modifFacu en catch ");
                                                         FacultadManBean.LOG.log(Level.SEVERE, "Error al tratar de midificar Facultad", exc);
                                                         FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error al modificar Facultad"+exc.getLocalizedMessage() ,""));
                                                         }
                                }

        
        public void llamardlgdetalles()
                                       {
                                           System.out.println("llamardlgdetalles");
                                        if(this.selectFacu!=null)
                                                                {
                                                                 if(this.selectFacu.size()==1)
                                                                                              {
                                                                                                  System.out.println("llamardlgdetalles en if");
                                                                                               this.selSimpFacu=this.selectFacu.get(0);
                                                                                               RequestContext.getCurrentInstance().update("formModifiFacu");
                                                                                               RequestContext.getCurrentInstance().execute("dlgviewFacu");
                                                                                               }
                                                                                                else
                                                                                                    {
                                                                                                        System.out.println("llamardlgdetalles en else");
                                                                                                     FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Debes seleccionar solo un elemento para editar", ""));
                                                                                                    }                                   
                                                                }
                                        }

        
        public void refrescarTabla()
                                    {
                                        System.out.println("refrescar tabla");
                                    this.sesUtils.refrescarEntidad(this.listaFacultad);
                                    }
        
        
      @PreDestroy
      private void PreDestroy()
                               {
                                System.out.println("El Bean Morira");
                               }
}

