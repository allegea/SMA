/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package informacion;

/**
 *
 * @author Andres
 */
public class OfertaCallMarket implements Comparable
{
    private float precio;
    private int cantidadC;
    private int cantidadV;
    private int calce;
    private int desvalance;

    public int getCalce() {
        return calce;
    }

    public void setCalce(int calce) {
        this.calce = calce;
    }

    public int getCantidadC() {
        return cantidadC;
    }

    public void setCantidadC(int cantidadC) {
        this.cantidadC = cantidadC;
    }

    public int getCantidadV() {
        return cantidadV;
    }

    public void setCantidadV(int cantidadV) {
        this.cantidadV = cantidadV;
    }

    public int getDesvalance() {
        return desvalance;
    }

    public void setDesvalance(int desvalance) {
        this.desvalance = desvalance;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public int compareTo(Object oferta2)
    {
        /*int calce1 =this.getCalce();
        Integer calce = Integer.valueOf(calce1);  //Precio de Compra de this
        int calce2 = ((OfertaCallMarket)oferta2).getCalce();
        Integer calceOtro = Integer.valueOf(calce2);  //Precio de Compra de otro agente
        return calce.compareTo(calceOtro);*/
        return this.getCalce() - ((OfertaCallMarket)oferta2).getCalce();

    }
    /*public int compareTo(Object oferta2)
    {
        float precio1 =this.getPrecio();
        Float precioDouble = Float.valueOf(precio1);  //Precio de Compra de this
        float precio2 = ((OfertaCallMarket)oferta2).getPrecio();
        Float precioDoubleOtro = Float.valueOf(precio2);  //Precio de Compra de otro agente
        return precioDouble.compareTo(precioDoubleOtro);
                
    }*/
}
