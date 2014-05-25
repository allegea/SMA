package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ConceptoOfertaVenta
* @author ontology bean generator
* @version 2009/10/6, 20:18:13
*/
public class ConceptoOfertaVenta implements Concept {



    private int periodo;
    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
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
* Protege name: precioVenta
   */
   private float precioVenta;
   public void setPrecioVenta(float value) { 
    this.precioVenta=value;
   }
   public float getPrecioVenta() {
     return this.precioVenta;
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
* Protege name: IDOfertaVenta
   */
   private int idOfertaVenta;
   public void setIDOfertaVenta(int value) { 
    this.idOfertaVenta=value;
   }
   public int getIDOfertaVenta() {
     return this.idOfertaVenta;
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
