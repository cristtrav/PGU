/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author cristhian
 */
@Embeddable
public class SedePK implements Serializable {
    @Basic(optional = false)
    @Column(name = "id_ciudad")
    private int idCiudad;
    @Basic(optional = false)
    @Column(name = "id_facultad")
    private int idFacultad;

    public SedePK() {
    }

    public SedePK(int idCiudad, int idFacultad) {
        this.idCiudad = idCiudad;
        this.idFacultad = idFacultad;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public int getIdFacultad() {
        return idFacultad;
    }

    public void setIdFacultad(int idFacultad) {
        this.idFacultad = idFacultad;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idCiudad;
        hash += (int) idFacultad;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SedePK)) {
            return false;
        }
        SedePK other = (SedePK) object;
        if (this.idCiudad != other.idCiudad) {
            return false;
        }
        if (this.idFacultad != other.idFacultad) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pgu.entidades.SedePK[ idCiudad=" + idCiudad + ", idFacultad=" + idFacultad + " ]";
    }
    
}
