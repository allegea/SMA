package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PredicadoNotificarVenta
* @author ontology bean generator
* @version 2009/10/6, 20:18:14
*/
public class PredicadoNotificarVenta implements Predicate {

   /**
* Protege name: notificacionVenta
   */
   private ConceptoNotificacionVenta notificacionVenta;
   public void setNotificacionVenta(ConceptoNotificacionVenta value) { 
    this.notificacionVenta=value;
   }
   public ConceptoNotificacionVenta getNotificacionVenta() {
     return this.notificacionVenta;
   }

}
