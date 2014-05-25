/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package informacion;

/**
 *
 * @author Carolina
 */
public class TablaRdto {
    
    int x[];float saldo[];float acciones[];float rendimiento[];

    public TablaRdto(int[] x, float[] saldo, float[] acciones, float[] rendimiento) {
        this.x = x;
        this.saldo = saldo;
        this.acciones = acciones;
        this.rendimiento = rendimiento;
    }

    public float[] getAcciones() {
        return acciones;
    }

    public void setAcciones(float[] acciones) {
        this.acciones = acciones;
    }

    public float[] getRendimiento() {
        return rendimiento;
    }

    public void setRendimiento(float[] rendimiento) {
        this.rendimiento = rendimiento;
    }

    public float[] getSaldo() {
        return saldo;
    }

    public void setSaldo(float[] saldo) {
        this.saldo = saldo;
    }

    public int[] getX() {
        return x;
    }

    public void setX(int[] x) {
        this.x = x;
    }

    public TablaRdto() {
    }
                
    
    
}
