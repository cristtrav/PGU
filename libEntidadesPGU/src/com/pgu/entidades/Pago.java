/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.entidades;

import java.io.Serializable;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author cristhian
 */
@Entity
@Table(name = "pago")
@NamedQueries({
    @NamedQuery(name = "Pago.findAll", query = "SELECT p FROM Pago p")})
public class Pago implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_pago_tarifa")
    private Integer idPagoTarifa;
    @Basic(optional = false)
    @Column(name = "total")
    private int total;
    @Basic(optional = false)
    @Column(name = "fecha")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    @Basic(optional = false)
    @Column(name = "nro_recibo")
    private int nroRecibo;
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario")
    @ManyToOne(optional = false)
    private Usuario idUsuario;
    @JoinColumn(name = "id_alumno", referencedColumnName = "id_alumno")
    @ManyToOne(optional = false)
    private Alumno idAlumno;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idPagoTarifa")
    private List<DetallePago> detallePagoList;

    public Pago() {
    }

    public Pago(Integer idPagoTarifa) {
        this.idPagoTarifa = idPagoTarifa;
    }

    public Pago(Integer idPagoTarifa, int total, Date fecha, int nroRecibo) {
        this.idPagoTarifa = idPagoTarifa;
        this.total = total;
        this.fecha = fecha;
        this.nroRecibo = nroRecibo;
    }

    public Integer getIdPagoTarifa() {
        return idPagoTarifa;
    }

    public void setIdPagoTarifa(Integer idPagoTarifa) {
        this.idPagoTarifa = idPagoTarifa;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getNroRecibo() {
        return nroRecibo;
    }

    public void setNroRecibo(int nroRecibo) {
        this.nroRecibo = nroRecibo;
    }

    public Usuario getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Usuario idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Alumno getIdAlumno() {
        return idAlumno;
    }

    public void setIdAlumno(Alumno idAlumno) {
        this.idAlumno = idAlumno;
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
        hash += (idPagoTarifa != null ? idPagoTarifa.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Pago)) {
            return false;
        }
        Pago other = (Pago) object;
        if ((this.idPagoTarifa == null && other.idPagoTarifa != null) || (this.idPagoTarifa != null && !this.idPagoTarifa.equals(other.idPagoTarifa))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pgu.entidades.Pago[ idPagoTarifa=" + idPagoTarifa + " ]";
    }
    
}
