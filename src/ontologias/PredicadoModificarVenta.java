package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PredicadoModificarVenta
* @author ontology bean generator
* @version 2009/10/6, 20:18:14
*/
public class PredicadoModificarVenta implements Predicate {

   /**
* Protege name: ofertaVenta
   */
   private ConceptoOfertaVenta ofertaVenta;
   public void setOfertaVenta(ConceptoOfertaVenta value) { 
    this.ofertaVenta=value;
   }
   public ConceptoOfertaVenta getOfertaVenta() {
     return this.ofertaVenta;
   }

}
