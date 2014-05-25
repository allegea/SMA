package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ConceptoNotificacionVenta
* @author ontology bean generator
* @version 2009/10/6, 20:18:13
*/
public class ConceptoNotificacionVenta implements Concept {

   /**
* Protege name: precio
   */
   private float precio;
   public void setPrecio(float value) { 
    this.precio=value;
   }
   public float getPrecio() {
     return this.precio;
   }

   /**
* Protege name: fecha
   */
   private String fecha;
   public void setFecha(String value) { 
    this.fecha=value;
   }
   public String getFecha() {
     return this.fecha;
   }

   /**
* Protege name: IDNotificacionVenta
   */
   private int idNotificacionVenta;
   public void setIDNotificacionVenta(int value) { 
    this.idNotificacionVenta=value;
   }
   public int getIDNotificacionVenta() {
     return this.idNotificacionVenta;
   }

   /**
* Protege name: IDProducto
   */
   private int idProducto;
   public void setIDProducto(int value) { 
    this.idProducto=value;
   }
   public int getIDProducto() {
     return this.idProducto;
   }

   /**
* Protege name: IDVendedor
   */
   private int idVendedor;
   public void setIDVendedor(int value) { 
    this.idVendedor=value;
   }
   public int getIDVendedor() {
     return this.idVendedor;
   }

   /**
* Protege name: cantidad
   */
   private int cantidad;
   public void setCantidad(int value) { 
    this.cantidad=value;
   }
   public int getCantidad() {
     return this.cantidad;
   }

}
