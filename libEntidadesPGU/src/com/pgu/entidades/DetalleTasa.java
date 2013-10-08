/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.entidades;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author cristhian
 */
@Entity
@Table(name = "detalle_tasa")
@NamedQueries({
    @NamedQuery(name = "DetalleTasa.findAll", query = "SELECT d FROM DetalleTasa d")})
public class DetalleTasa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_detalle_tasa")
    private Integer idDetalleTasa;
    @Basic(optional = false)
    @Column(name = "monto")
    private int monto;
    @Basic(optional = false)
    @Column(name = "minimo")
    private int minimo;
    @Basic(optional = false)
    @Column(name = "pagos_parciales")
    private int pagosParciales;
    @JoinColumn(name = "id_tasa", referencedColumnName = "id_tasa")
    @ManyToOne(optional = false)
    private Tasa idTasa;
    @JoinColumn(name = "id_carrera", referencedColumnName = "id_carrera")
    @ManyToOne(optional = false)
    private Carrera idCarrera;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idDetalleTasa")
    private List<DetallePago> detallePagoList;

    public DetalleTasa() {
    }

    public DetalleTasa(Integer idDetalleTasa) {
        this.idDetalleTasa = idDetalleTasa;
    }

    public DetalleTasa(Integer idDetalleTasa, int monto, int minimo, int pagosParciales) {
        this.idDetalleTasa = idDetalleTasa;
        this.monto = monto;
        this.minimo = minimo;
        this.pagosParciales = pagosParciales;
    }

    public Integer getIdDetalleTasa() {
        return idDetalleTasa;
    }

    public void setIdDetalleTasa(Integer idDetalleTasa) {
        this.idDetalleTasa = idDetalleTasa;
    }

    public int getMonto() {
        return monto;
    }

    public void setMonto(int monto) {
        this.monto = monto;
    }

    public int getMinimo() {
        return minimo;
    }

    public void setMinimo(int minimo) {
        this.minimo = minimo;
    }

    public int getPagosParciales() {
        return pagosParciales;
    }

    public void setPagosParciales(int pagosParciales) {
        this.pagosParciales = pagosParciales;
    }

    public Tasa getIdTasa() {
        return idTasa;
    }

    public void setIdTasa(Tasa idTasa) {
        this.idTasa = idTasa;
    }

    public Carrera getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(Carrera idCarrera) {
        this.idCarrera = idCarrera;
    }

    public List<DetallePago> getDetallePagoList() {
        return detallePagoList;
    }

    public void setDetallePagoList(List<DetallePago> detallePagoList) {
        this.detallePagoList = detallePagoList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDetalleTasa != null ? idDetalleTasa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetalleTasa)) {
            return false;
        }
        DetalleTasa other = (DetalleTasa) object;
        if ((this.idDetalleTasa == null && other.idDetalleTasa != null) || (this.idDetalleTasa != null && !this.idDetalleTasa.equals(other.idDetalleTasa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pgu.entidades.DetalleTasa[ idDetalleTasa=" + idDetalleTasa + " ]";
    }
    
}
