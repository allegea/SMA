package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PredicadoConfirmarRegistro
* @author ontology bean generator
* @version 2009/10/6, 20:18:14
*/
public class PredicadoConfirmarRegistro implements Predicate {

   /**
* Protege name: registro
   */
   private ConceptoRegistro registro;
   public void setRegistro(ConceptoRegistro value) { 
    this.registro=value;
   }
   public ConceptoRegistro getRegistro() {
     return this.registro;
   }

}
