/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package informacion;

/**
 *
 * @author Sebas
 */
public class InfoOfertaVenta {
    private int IDOfertaVenta;
    private int IDAgente;
    private int IDProducto;
    private float precioVenta;
    private int fecha;
    private int cantidad;

    public InfoOfertaVenta(int IDOV, int IDAgente, int IDProd, float pVenta,
            int fecha, int cant)
    {
        this.IDOfertaVenta = IDOV;
        this.IDAgente = IDAgente;
        this.IDProducto = IDProd;
        this.precioVenta = pVenta;
        this.fecha = fecha;
        this.cantidad = cant;
    }

    public int getIDOfertaVenta()
    {
        return this.IDOfertaVenta;
    }

    public void setIDOfertaVenta(int id)
    {
        this.IDOfertaVenta = id;
    }

    public int getIDAgente()
    {
        return this.IDAgente;
    }

    public void setIDAgente(int id)
    {
        this.IDAgente = id;
    }

    public int getIDProducto()
    {
        return this.IDProducto;
    }

    public void setIDProducto(int id)
    {
        this.IDProducto = id;
    }

    public float getPrecioVenta()
    {
        return this.precioVenta;
    }

    public void setPrecioVenta(float p)
    {
        this.precioVenta = p;
    }

    public int getFecha()
    {
        return this.fecha;
    }

    public void setFecha(int fecha)
    {
        this.fecha = fecha;
    }

    public int getCantidad()
    {
        return this.cantidad;
    }

    public void setCantidad(int c)
    {
        this.cantidad = c;
    }
}
