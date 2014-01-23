/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pgu.entidades;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "detalle_pago")
@NamedQueries({
    @NamedQuery(name = "DetallePago.findAll", query = "SELECT d FROM DetallePago d")})
public class DetallePago implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id_detalle_pago")
    private Integer idDetallePago;
    @Basic(optional = false)
    @Column(name = "entrega")
    private int entrega;
    @Basic(optional = false)
    @Column(name = "cantidad")
    private int cantidad;
    @Basic(optional = false)
    @Column(name = "subtotal")
    private int subtotal;
    @JoinColumn(name = "id_pago_tarifa", referencedColumnName = "id_pago_tarifa")
    @ManyToOne(optional = false)
    private Pago idPagoTarifa;
    @JoinColumn(name = "id_detalle_tasa", referencedColumnName = "id_detalle_tasa")
    @ManyToOne(optional = false)
    private DetalleTasa idDetalleTasa;

    public DetallePago() {
    }

    public DetallePago(Integer idDetallePago) {
        this.idDetallePago = idDetallePago;
    }

    public DetallePago(Integer idDetallePago, int entrega, int cantidad, int subtotal) {
        this.idDetallePago = idDetallePago;
        this.entrega = entrega;
        this.cantidad = cantidad;
        this.subtotal = subtotal;
    }

    public Integer getIdDetallePago() {
        return idDetallePago;
    }

    public void setIdDetallePago(Integer idDetallePago) {
        this.idDetallePago = idDetallePago;
    }

    public int getEntrega() {
        return entrega;
    }

    public void setEntrega(int entrega) {
        this.entrega = entrega;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(int subtotal) {
        this.subtotal = subtotal;
    }

    public Pago getIdPagoTarifa() {
        return idPagoTarifa;
    }

    public void setIdPagoTarifa(Pago idPagoTarifa) {
        this.idPagoTarifa = idPagoTarifa;
    }

    public DetalleTasa getIdDetalleTasa() {
        return idDetalleTasa;
    }

    public void setIdDetalleTasa(DetalleTasa idDetalleTasa) {
        this.idDetalleTasa = idDetalleTasa;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idDetallePago != null ? idDetallePago.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof DetallePago)) {
            return false;
        }
        DetallePago other = (DetallePago) object;
        if ((this.idDetallePago == null && other.idDetallePago != null) || (this.idDetallePago != null && !this.idDetallePago.equals(other.idDetallePago))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.pgu.entidades.DetallePago[ idDetallePago=" + idDetallePago + " ]";
    }
    
}
