/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.pgu.manbeans.academico;

import com.pgu.entidades.Semestre;
import com.pgu.sesbeans.SemestreFacade;
import com.pgu.sesbeans.SesUtils;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
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
@Named(value = "SemestreManbean")
@SessionScoped
public class SemestreManbean implements Serializable {
    private static final Logger  LOG = Logger.getLogger(SemestreManbean.class.getName());
    @EJB
    private SesUtils sesUtils;
    
    
    @EJB
    private SemestreFacade semestreFacade;

    private List<Semestre> listasemestres;
    private List<Semestre>selectsemestres;
    private Semestre temporalSemestres;
    private Semestre selSimpSemestres;

    public List<Semestre> getListasemestres() {
        return listasemestres;
    }

    public void setListasemestres(List<Semestre> listasemestres) {
        this.listasemestres = listasemestres;
    }

    public List<Semestre> getSelectsemestres() {
        return selectsemestres;
    }

    public void setSelectsemestres(List<Semestre> selectsemestres) {
        this.selectsemestres = selectsemestres;
    }

  

    public Semestre getTemporalSemestres() {
        return temporalSemestres;
    }

    public void setTemporalSemestres(Semestre temporalSemestres) {
        this.temporalSemestres = temporalSemestres;
    }

    public Semestre getSelSimpSemestres() {
        return selSimpSemestres;
    }

    public void setSelSimpSemestres(Semestre selSimpSemestres) {
        this.selSimpSemestres = selSimpSemestres;
    }
    
    
    
    
    /**
     * Creates a new instance of SemestreManbean
     */
    
    public SemestreManbean() {
    this.listasemestres=new ArrayList<Semestre>();
    this.temporalSemestres=new Semestre();
    this.selectsemestres=new ArrayList<Semestre>();
    System.out.println("#####CONSTRUCTOR SEMESTREBEAN#######");
    }
    
    @PostConstruct
    public void cargarSemestre(){
        System.out.println("#####Cragando Semetres#######");
        this.listasemestres.clear();
        this.listasemestres.addAll(this.semestreFacade.findAll());
    }
    
    
    public void guardarSemestres(){
        try {
            System.out.println("###EXISTEN:::"+this.listasemestres.size());
            System.out.println("#####Gusrdando Registros########");
            System.out.println(this.temporalSemestres.getDescripcion());
            this.semestreFacade.create(this.temporalSemestres);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "######CORRECTO######", ""));
            this.cargarSemestre();
            RequestContext.getCurrentInstance().update("formSemestre:tablaSemestre");
            System.out.println("###en la lista existen:::"+this.listasemestres.size());
        } catch (Exception e) {
            System.out.println("error al cargar registros"+e);
        }
    }
    
    public void limpiarformalta(){
        this.temporalSemestres=new Semestre();
    }
    
    
    public void eliminarSemestre(){
        System.out.println("abcdefghijkl");
        boolean error=false;
        ListIterator<Semestre> lits=this.selectsemestres.listIterator();
        System.out.println("abcdefghijklmn");
        List<Semestre> semElims=new ArrayList<Semestre>();
        while (lits.hasNext()) {
            System.out.println("askldjlaskhdklas");
            Semestre tmpor=lits.next();
            try {
                System.out.println("nsdhjkashdxjkhasj");
                this.semestreFacade.remove(tmpor);
                semElims.add(tmpor);
            } catch (Exception ex) {
                error=true;
                System.out.println(".mckmkvdjfehwoui");
                SemestreManbean.LOG.log(Level.WARNING, "no se pudo eliminar el registro:"+tmpor, ex);
            }
        }
        ListIterator<Semestre> itsel=semElims.listIterator();
        while (itsel.hasNext()) { 
            System.out.println("xamklasdjlkasjiw");
            this.selectsemestres.remove(itsel.next());
        }
        this.cargarSemestre();
        if(error)
        {
           FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No pudieron eliminarse todos los registros", ""));
        }else{
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se eliminaron correctamente los registros selesccionados", ""));
        }
    }
    
    
    public void confirElims(){
        System.out.println("Metodo confirmar eliminar");
        if (this.selectsemestres.isEmpty()) {
            System.out.println("lckaslk");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Deves seleccionar por lo menos un elemento", ""));
        } else {
            System.out.println("asdhqk");
            RequestContext.getCurrentInstance().execute("dlgConfirmElim.show()");
        }
    }
    
    public void modifiSemestre(){
        try {
            this.semestreFacade.edit(this.selSimpSemestres);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se ha modificado correctamente el registro", ""));
        } catch (Exception exc) {
            SemestreManbean.LOG.log(Level.SEVERE, "ERROR AL EDITAR",exc);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "error al modificar el registro"+exc.getLocalizedMessage(),""));
        }
    }
    
    
    public void dlgllamardetalle(){
        if (this.selectsemestres!=null) {
            if (this.selectsemestres.size()==1) {
                this.selSimpSemestres=this.selectsemestres.get(0);
                RequestContext.getCurrentInstance().update("formModifiSem");
                RequestContext.getCurrentInstance().execute("dlgViewSem.show()");
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Deves seleccionar solo un elemento para editar", ""));
            }
        }
        
        
    }public void refrescartabla(){
        this.sesUtils.refrescarEntidad(this.listasemestres);
    }
    
    
    @PreDestroy
    private void preDestroy(){
        System.out.println("el bean va a morir");
    }
}
