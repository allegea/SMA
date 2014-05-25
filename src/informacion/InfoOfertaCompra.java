/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package informacion;

/**
 *
 * @author Sebas
 */
public class InfoOfertaCompra {
    private int IDOfertaCompra;
    private int IDAgente;
    private int IDProducto;
    private float precioCompra;
    private int fecha;
    private int cantidad;

    public InfoOfertaCompra(int IDOC, int IDAgente, int IDProd, float pCompra,
            int fecha, int cant)
    {
        this.IDOfertaCompra = IDOC;
        this.IDAgente = IDAgente;
        this.IDProducto = IDProd;
        this.precioCompra = pCompra;
        this.fecha = fecha;
        this.cantidad = cant;
    }

    public int getIDOfertaCompra()
    {
        return this.IDOfertaCompra;
    }

    public void setIDOfertaCompra(int id)
    {
        this.IDOfertaCompra = id;
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

    public float getPrecioCompra()
    {
        return this.precioCompra;
    }

    public void setPrecioCompra(float p)
    {
        this.precioCompra = p;
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
