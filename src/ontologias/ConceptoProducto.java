package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ConceptoProducto
* @author ontology bean generator
* @version 2009/10/6, 20:18:13
*/
public class ConceptoProducto implements Concept {

   /**
* Protege name: descripcion
   */
   private String descripcion;
   public void setDescripcion(String value) { 
    this.descripcion=value;
   }
   public String getDescripcion() {
     return this.descripcion;
   }

   /**
* Protege name: nombre
   */
   private String nombre;
   public void setNombre(String value) { 
    this.nombre=value;
   }
   public String getNombre() {
     return this.nombre;
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

}
