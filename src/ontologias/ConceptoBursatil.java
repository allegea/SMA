package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: ConceptoBursatil
* @author ontology bean generator
* @version 2009/10/6, 20:18:13
*/
public class ConceptoBursatil implements Concept {

   /**
* Protege name: saldo
   */
   private float saldo;
   public void setSaldo(float value) { 
    this.saldo=value;
   }
   public float getSaldo() {
     return this.saldo;
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
* Protege name: tipo
   */
   private int tipo;
   public void setTipo(int value) { 
    this.tipo=value;
   }
   public int getTipo() {
     return this.tipo;
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
