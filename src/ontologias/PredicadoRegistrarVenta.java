package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PredicadoRegistrarVenta
* @author ontology bean generator
* @version 2009/10/6, 20:18:14
*/
public class PredicadoRegistrarVenta implements Predicate {

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
