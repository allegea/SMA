package ontologias;


import jade.content.*;
import jade.util.leap.*;
import jade.core.*;

/**
* Protege name: PredicadoNotificarCompra
* @author ontology bean generator
* @version 2009/10/6, 20:18:14
*/
public class PredicadoNotificarCompra implements Predicate {

   /**
* Protege name: notificacionCompra
   */
   private ConceptoNotificacionCompra notificacionCompra;
   public void setNotificacionCompra(ConceptoNotificacionCompra value) { 
    this.notificacionCompra=value;
   }
   public ConceptoNotificacionCompra getNotificacionCompra() {
     return this.notificacionCompra;
   }

}
