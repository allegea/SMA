package ontologias;


import jade.content.*;

/**
* Protege name: PredicadoEnCallMarket
* @author ontology bean generator
* @version 2009/10/6, 20:18:14
*/
public class PredicadoFinCallMarket implements Predicate {

   /**
* Protege name: finRegistro
   */
   /*private String enCallMarket;

    public String getEnCallMarket() {
        return enCallMarket;
    }

    public void setEnCallMarket(String enCallMarket) {
        this.enCallMarket = enCallMarket;
    }*/

    private String momento;

    public String getMomento() {
        return momento;
    }

    public void setMomento(String momento) {
        this.momento = momento;
    }
 
   private ConceptoProducto producto;
   public void setProducto(ConceptoProducto value) {
    this.producto=value;
   }
   public ConceptoProducto getProducto() {
     return this.producto;
   }
}
