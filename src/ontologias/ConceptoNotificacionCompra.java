package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ConceptoNotificacionCompra
* @author ontology bean generator
* @version 2009/10/6, 20:18:13
*/
public class ConceptoNotificacionCompra implements Concept {

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
* Protege name: IDComprador
   */
   private int idComprador;
   public void setIDComprador(int value) { 
    this.idComprador=value;
   }
   public int getIDComprador() {
     return this.idComprador;
   }

   /**
* Protege name: IDNotificacionCompra
   */
   private int idNotificacionCompra;
   public void setIDNotificacionCompra(int value) { 
    this.idNotificacionCompra=value;
   }
   public int getIDNotificacionCompra() {
     return this.idNotificacionCompra;
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
