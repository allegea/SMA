/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package informacion;

import jade.core.AID;
import ontologias.*;

/**
 *
 * @author Andres
 */
public class OfertaVenta implements Comparable
{
    private AID ofertante= null;
    private ConceptoOfertaVenta informacion = null;
    private ConceptoProducto producto = null;
    private int caducidadOferta = 0;


    public int getCaducidadOferta() {
        return caducidadOferta;
    }

    public void setCaducidadOferta(int caducidadOferta) {
        this.caducidadOferta = caducidadOferta;
    }
    
    public OfertaVenta(AID ofertante, ConceptoOfertaVenta informacion, ConceptoProducto producto, int caducidad)
    {
        this.ofertante=ofertante;
        this.informacion=informacion;
        this.producto = producto;
        this.caducidadOferta = caducidad;
       
    }
    
    public AID getAID()
    {
        return this.ofertante;
    }
    
    public ConceptoOfertaVenta getInfo()
    {
        return this.informacion;
    }

    public ConceptoProducto getProducto()
    {
        return this.producto;
    }
    
    public int compareTo(Object oferta2) //Oferta2 es un objeto de tipo OfertaVenta
    {
        /*float precioVenta =this.informacion.getPrecioVenta();
        Float precioDouble = Float.valueOf(precioVenta);  //Precio de venta de this
        float precioVentaOtro = ((OfertaVenta)oferta2).informacion.getPrecioVenta();
        Float precioDoubleOtro = Float.valueOf(precioVentaOtro);  //Precio de venta de otro agente
        return precioDouble.compareTo(precioDoubleOtro);*/
        return (int) (this.getInfo().getPrecioVenta() - ((OfertaVenta)oferta2).getInfo().getPrecioVenta());
                
    }
}
