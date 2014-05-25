package ontologias;


import jade.content.*;

/**
* Protege name: PredicadoCancelarCompra
* @author ontology bean generator
* @version 2009/10/6, 20:18:13
*/
public class PredicadoCancelarCompra implements Predicate {

   /**
* Protege name: ofertaCompra
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

   private ConceptoOfertaCompra ofertaCompra;
   public void setOfertaCompra(ConceptoOfertaCompra value) { 
    this.ofertaCompra=value;
   }
   public ConceptoOfertaCompra getOfertaCompra() {
     return this.ofertaCompra;
   }

}
