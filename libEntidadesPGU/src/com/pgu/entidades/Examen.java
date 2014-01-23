/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.entidades;

import java.io.Serializable;
import javax.persistence.Basic;
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
@Table(name = "examen")
@NamedQueries({
    @NamedQuery(name = "Examen.findAll", query = "SELECT e FROM Examen e")})
public class Examen implements Serializable {
    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected ExamenPK examenPK;
    @Basic(optional = false)
    @Column(name = "calificacion")
    private int calificacion;
    @JoinColumn(name = "id_semestre", referencedColumnName = "id_semestre", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Semestre semestre;
    @JoinColumn(name = "id_materia", referencedColumnName = "id_materia", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Materia materia;
    @JoinColumn(name = "id_docente", referencedColumnName = "id_docente")
    @ManyToOne(optional = false)
    private Docente idDocente;
    @JoinColumn(name = "id_alumno", referencedColumnName = "id_alumno", insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Alumno alumno;

    public Examen() {
    }

    public Examen(ExamenPK examenPK) {
        this.examenPK = examenPK;
    }

    public Examen(ExamenPK examenPK, int calificacion) {
        this.examenPK = examenPK;
        this.calificacion = calificacion;
    }

    public Examen(int idAlumno, int convocatoria, int idMateria, int idSemestre) {
        this.examenPK = new ExamenPK(idAlumno, convocatoria, idMateria, idSemestre);
    }

    public ExamenPK getExamenPK() {
        return examenPK;
    }

    public void setExamenPK(ExamenPK examenPK) {
        this.examenPK = examenPK;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public Semestre getSemestre() {
        return semestre;
    }

    public void setSemestre(Semestre semestre) {
        this.semestre = semestre;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Docente getIdDocente() {
        return idDocente;
    }

    public void setIdDocente(Docente idDocente) {
        this.idDocente = idDocente;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (examenPK != null ? examenPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Examen)) {
            return false;
        }
        Examen other = (Examen) object;
        if ((this.examenPK == null && other.examenPK != null) || (this.examenPK != null && !this.examenPK.equals(other.examenPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pgu.entidades.Examen[ examenPK=" + examenPK + " ]";
    }
    
}
