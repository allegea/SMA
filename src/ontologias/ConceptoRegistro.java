package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ConceptoRegistro
* @author ontology bean generator
* @version 2009/10/6, 20:18:13
*/
public class ConceptoRegistro implements Concept {

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
* Protege name: IDBursatil
   */
   private int idBursatil;
   public void setIDBursatil(int value) { 
    this.idBursatil=value;
   }
   public int getIDBursatil() {
     return this.idBursatil;
   }

}
