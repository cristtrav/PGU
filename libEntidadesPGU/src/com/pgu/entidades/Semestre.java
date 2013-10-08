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
@Table(name = "semestre")
@NamedQueries({
    @NamedQuery(name = "Semestre.findAll", query = "SELECT s FROM Semestre s")})
public class Semestre implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_semestre")
    private Integer idSemestre;
    @Column(name = "descripcion")
    private String descripcion;
    @Basic(optional = false)
    @Column(name = "anio_lectivo")
    private int anioLectivo;
    @Basic(optional = false)
    @Column(name = "nro_semestre")
    private int nroSemestre;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idSemestre")
    private List<Matricula> matriculaList;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "semestre")
    private List<Examen> examenList;

    public Semestre() {
    }

    public Semestre(Integer idSemestre) {
        this.idSemestre = idSemestre;
    }

    public Semestre(Integer idSemestre, int anioLectivo, int nroSemestre) {
        this.idSemestre = idSemestre;
        this.anioLectivo = anioLectivo;
        this.nroSemestre = nroSemestre;
    }

    public Integer getIdSemestre() {
        return idSemestre;
    }

    public void setIdSemestre(Integer idSemestre) {
        this.idSemestre = idSemestre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(int anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public int getNroSemestre() {
        return nroSemestre;
    }

    public void setNroSemestre(int nroSemestre) {
        this.nroSemestre = nroSemestre;
    }

    public List<Matricula> getMatriculaList() {
        return matriculaList;
    }

    public void setMatriculaList(List<Matricula> matriculaList) {
        this.matriculaList = matriculaList;
    }

    public List<Examen> getExamenList() {
        return examenList;
    }

    public void setExamenList(List<Examen> examenList) {
        this.examenList = examenList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSemestre != null ? idSemestre.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Semestre)) {
            return false;
        }
        Semestre other = (Semestre) object;
        if ((this.idSemestre == null && other.idSemestre != null) || (this.idSemestre != null && !this.idSemestre.equals(other.idSemestre))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pgu.entidades.Semestre[ idSemestre=" + idSemestre + " ]";
    }
    
}
