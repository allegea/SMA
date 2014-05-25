package ontologias;


import jade.content.*;

/**
* Protege name: PredicadoCancelarVenta
* @author ontology bean generator
* @version 2009/10/6, 20:18:14
*/
public class PredicadoCancelarVenta implements Predicate {

   /**
* Protege name: ofertaVenta
   */

    private int idProducto;

   public void setIDProducto(int value) {
    this.idProducto=value;
   }
   public int getIDProducto() {
     return this.idProducto;
   }

    private String concepto;

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    
   private ConceptoOfertaVenta ofertaVenta;
   public void setOfertaVenta(ConceptoOfertaVenta value) { 
    this.ofertaVenta=value;
   }
   public ConceptoOfertaVenta getOfertaVenta() {
     return this.ofertaVenta;
   }

}
