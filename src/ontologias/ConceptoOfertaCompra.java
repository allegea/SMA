package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ConceptoOfertaCompra
* @author ontology bean generator
* @version 2009/10/6, 20:18:13
*/
public class ConceptoOfertaCompra implements Concept {



    private int periodo;
    public int getPeriodo() {
        return periodo;
    }

    public void setPeriodo(int periodo) {
        this.periodo = periodo;
    }
    
    
   /**
* Protege name: IDOfertaCompra
   */
   private int idOfertaCompra;
   public void setIDOfertaCompra(int value) { 
    this.idOfertaCompra=value;
   }
   public int getIDOfertaCompra() {
     return this.idOfertaCompra;
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
* Protege name: precioCompra
   */
   private float precioCompra;
   public void setPrecioCompra(float value) { 
    this.precioCompra=value;
   }
   public float getPrecioCompra() {
     return this.precioCompra;
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
