package ontologias;


import jade.content.*;

/**
* Protege name: ConceptoCalce
* @author ontology bean generator
* @version 2009/10/6, 20:18:13
*/
public class ConceptoCalce implements Concept {

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
* Protege name: IDCalce
   */
   private int idCalce;
   public void setIDCalce(int value) { 
    this.idCalce=value;
   }
   public int getIDCalce() {
     return this.idCalce;
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
* Protege name: precioSugerido
   */
   private float precioSugerido;
   public void setPrecioSugerido(float value) { 
    this.precioSugerido=value;
   }
   public float getPrecioSugerido() {
     return this.precioSugerido;
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
