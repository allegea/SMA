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
public class OfertaCompra implements Comparable
{
    private AID ofertante= null;
    private ConceptoOfertaCompra informacion = null;
    private ConceptoProducto producto = null;
    private int caducidadOferta = 0;

    public int getCaducidadOferta() {
        return caducidadOferta;
    }

    public void setCaducidadOferta(int caducidadOferta) {
        this.caducidadOferta = caducidadOferta;
    }
    
    public OfertaCompra(AID ofertante, ConceptoOfertaCompra informacion, ConceptoProducto producto, int caducidad)
    {
        this.ofertante=ofertante;
        this.informacion=informacion;
        this.producto = producto;
        this.caducidadOferta=caducidad;
        this.caducidadOferta = caducidad;

    }
    
    public AID getAID()
    {
        return this.ofertante;
    }
    
    public ConceptoOfertaCompra getInfo()
    {
        return this.informacion;
    }

    public ConceptoProducto getProducto()
    {
        return this.producto;
    }
    
    public int compareTo(Object oferta2) //Oferta2 es un objeto de tipo OfertaCompra
    {
        /*float precioCompra =this.informacion.getPrecioCompra();
        Float precioDouble = Float.valueOf(precioCompra);  //Precio de Compra de this
        float precioCompraOtro = ((OfertaCompra)oferta2).informacion.getPrecioCompra();
        Float precioDoubleOtro = Float.valueOf(precioCompraOtro);  //Precio de Compra de otro agente
        return precioDouble.compareTo(precioDoubleOtro);*/
        return (int) (this.getInfo().getPrecioCompra() - ((OfertaCompra)oferta2).getInfo().getPrecioCompra());

                
    }
}
