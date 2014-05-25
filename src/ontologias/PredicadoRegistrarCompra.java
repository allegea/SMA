package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PredicadoRegistrarCompra
* @author ontology bean generator
* @version 2009/10/6, 20:18:14
*/
public class PredicadoRegistrarCompra implements Predicate {

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

   /**
* Protege name: producto
   */
   private ConceptoProducto producto;
   public void setProducto(ConceptoProducto value) { 
    this.producto=value;
   }
   public ConceptoProducto getProducto() {
     return this.producto;
   }

}
