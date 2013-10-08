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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author cristhian
 */
@Entity
@Table(name = "tasa")
@NamedQueries({
    @NamedQuery(name = "Tasa.findAll", query = "SELECT t FROM Tasa t")})
public class Tasa implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_tasa")
    private Integer idTasa;
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idTasa")
    private List<DetalleTasa> detalleTasaList;

    public Tasa() {
    }

    public Tasa(Integer idTasa) {
        this.idTasa = idTasa;
    }

    public Tasa(Integer idTasa, String nombre) {
        this.idTasa = idTasa;
        this.nombre = nombre;
    }

    public Integer getIdTasa() {
        return idTasa;
    }

    public void setIdTasa(Integer idTasa) {
        this.idTasa = idTasa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<DetalleTasa> getDetalleTasaList() {
        return detalleTasaList;
    }

    public void setDetalleTasaList(List<DetalleTasa> detalleTasaList) {
        this.detalleTasaList = detalleTasaList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idTasa != null ? idTasa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tasa)) {
            return false;
        }
        Tasa other = (Tasa) object;
        if ((this.idTasa == null && other.idTasa != null) || (this.idTasa != null && !this.idTasa.equals(other.idTasa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pgu.entidades.Tasa[ idTasa=" + idTasa + " ]";
    }
    
}
