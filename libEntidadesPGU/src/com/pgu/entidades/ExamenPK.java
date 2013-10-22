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
public class ExamenPK implements Serializable {
    @Basic(optional = false)
    @Column(name = "id_alumno")
    private int idAlumno;
    @Basic(optional = false)
    @Column(name = "convocatoria")
    private int convocatoria;
    @Basic(optional = false)
    @Column(name = "id_materia")
    private int idMateria;
    @Basic(optional = false)
    @Column(name = "id_semestre")
    private int idSemestre;

    public ExamenPK() {
    }

    public ExamenPK(int idAlumno, int convocatoria, int idMateria, int idSemestre) {
        this.idAlumno = idAlumno;
        this.convocatoria = convocatoria;
        this.idMateria = idMateria;
        this.idSemestre = idSemestre;
    }

    public int getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    public int getConvocatoria() {
        return convocatoria;
    }

    public void setConvocatoria(int convocatoria) {
        this.convocatoria = convocatoria;
    }

    public int getIdMateria() {
        return idMateria;
    }

    public void setIdMateria(int idMateria) {
        this.idMateria = idMateria;
    }

    public int getIdSemestre() {
        return idSemestre;
    }

    public void setIdSemestre(int idSemestre) {
        this.idSemestre = idSemestre;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) idAlumno;
        hash += (int) convocatoria;
        hash += (int) idMateria;
        hash += (int) idSemestre;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExamenPK)) {
            return false;
        }
        ExamenPK other = (ExamenPK) object;
        if (this.idAlumno != other.idAlumno) {
            return false;
        }
        if (this.convocatoria != other.convocatoria) {
            return false;
        }
        if (this.idMateria != other.idMateria) {
            return false;
        }
        if (this.idSemestre != other.idSemestre) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pgu.entidades.ExamenPK[ idAlumno=" + idAlumno + ", convocatoria=" + convocatoria + ", idMateria=" + idMateria + ", idSemestre=" + idSemestre + " ]";
    }
    
}
