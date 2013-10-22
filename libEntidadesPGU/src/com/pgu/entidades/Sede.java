/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.entidades;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author cristhian
 */
@Entity
@Table(name = "sede")
@NamedQueries({
    @NamedQuery(name = "Sede.findAll", query = "SELECT s FROM Sede s")})
public class Sede implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected SedePK sedePK;
    @Column(name = "descripcion")
    private String descripcion;
    @JoinColumn(name = "id_facultad", referencedColumnName = "id_facultad", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Facultad facultad;
    @JoinColumn(name = "id_ciudad", referencedColumnName = "id_ciudad", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Ciudad ciudad;

    public Sede() {
    }

    public Sede(SedePK sedePK) {
        this.sedePK = sedePK;
    }

    public Sede(int idCiudad, int idFacultad) {
        this.sedePK = new SedePK(idCiudad, idFacultad);
    }

    public SedePK getSedePK() {
        return sedePK;
    }

    public void setSedePK(SedePK sedePK) {
        this.sedePK = sedePK;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Facultad getFacultad() {
        return facultad;
    }

    public void setFacultad(Facultad facultad) {
        this.facultad = facultad;
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (sedePK != null ? sedePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Sede)) {
            return false;
        }
        Sede other = (Sede) object;
        if ((this.sedePK == null && other.sedePK != null) || (this.sedePK != null && !this.sedePK.equals(other.sedePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pgu.entidades.Sede[ sedePK=" + sedePK + " ]";
    }
    
}
