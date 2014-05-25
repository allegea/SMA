package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PredicadoModificarCompra
* @author ontology bean generator
* @version 2009/10/6, 20:18:14
*/
public class PredicadoModificarCompra implements Predicate {

   /**
* Protege name: ofertaCompra
   */
   private ConceptoOfertaCompra ofertaCompra;
   public void setOfertaCompra(ConceptoOfertaCompra value) { 
    this.ofertaCompra=value;
   }
   public ConceptoOfertaCompra getOfertaCompra() {
     return this.ofertaCompra;
   }

}
