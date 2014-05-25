/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package inferencia;

import java.util.ArrayList;
import agentes.*;

/**
 *
 * @author Sebas
 */
public class IndicadoresTecnicos {

    /*
     * Calcula el promedio de los precios de cierre de los ultimos n periodos
     */
    public float calcularPromedioMovilPonderado(int idProducto, int nInicial, int nFinal)
    {
        float promedio;
        float suma;
        int cont = 0;

        suma = 0;

        for (int j=1; j<=nFinal-nInicial; j++) {
            suma += AgenteAdministrador.historicoSubastas.get(idProducto).get(nInicial+j).getPrecioCierre()*j;
            cont+=j;
        }
        promedio = suma/cont;

        return promedio;
    }

    /*public int senalPromediosMovilesDobles(int idProducto, int nCorta, int nLarga)
    {
        float promedioCortoActual, promedioLargoActual, promedioCortoAnterior, promedioLargoAnterior;
        int senal = 0;

        int n = AgenteSubastador.historicoSubastas.get(idProducto).size();

        promedioCortoActual = calcularPromedioMovilPonderado(idProducto, n-1-nCorta, n-1);
        promedioCortoAnterior = calcularPromedioMovilPonderado(idProducto, n-2-nCorta, n-2);

        promedioLargoActual = calcularPromedioMovilPonderado(idProducto, n-1-nLarga, n-1);
        promedioLargoAnterior = calcularPromedioMovilPonderado(idProducto, n-2-nLarga, n-2);

        // Si el promedio de corto plazo corta al promedio de mediano plazo de abajo para arriba compra
        // sino vende
        if (promedioCortoAnterior < promedioLargoAnterior && promedioCortoActual > promedioLargoActual) {
            //System.out.println(promedioCortoAnterior+" < "+promedioLargoAnterior+" && "+promedioCortoActual+" > "+promedioLargoActual+" ==> Compra");
            senal = 1; // Senal de compra
        } else if (promedioCortoAnterior > promedioLargoAnterior && promedioCortoActual < promedioLargoActual) {
            //System.out.println(promedioCortoAnterior+" > "+promedioLargoAnterior+" && "+promedioCortoActual+" < "+promedioLargoActual+" ==> Venta");
            senal = -1; // Senal de venta
        }
        return senal;
    }*/

    /*
     * Indice de Fuerza Relativa
     * Se calculan las alzas y bajas de los precios de cierre de cada dia.
     * El primer dia se calcula el promedio simple de los ultimos N dias, y a
     * partir de ahi se calcula el promedio exponencial con un alpha = 1/N, tanto
     * para las alzas como para las bajas.
     * RS = PromedioExpAlzas/PromedioExpBajas
     * RSI = 100 - 100/(1 + RS)
     */
    public float calcularRSI(int idProducto, int N, int periodo)
    {
        float rsi, rs, promedioAlza=0, promedioBaja=0, promedioAlzaAnt=0, promedioBajaAnt=0, alpha, alza, baja, cierreHoy, cierreAyer;
        int n;

        n = AgenteAdministrador.historicoSubastas.get(idProducto).size();
        N = Math.min(N, n-1);
        alpha = 1/N;
        for (int j=0; j<periodo; j++) {
            // El primer periodo se calcula el promedio aritmetico de alzas y bajas
            if (j == 0) {
                float sumaAlza = 0, sumaBaja = 0, cierreTmp, cierreAntTmp;
                for (int i=1; i<=N; i++) {
                    cierreTmp = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-i).getPrecioCierre();
                    cierreAntTmp = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-i-1).getPrecioCierre();
                    if (cierreTmp - cierreAntTmp > 0)
                        sumaAlza += cierreTmp - cierreAntTmp;
                    else if (cierreTmp - cierreAntTmp < 0)
                        sumaBaja += -(cierreTmp - cierreAntTmp);
                }
                promedioAlza = sumaAlza/N;
                promedioBaja = sumaBaja/N;
            } else {
                cierreHoy = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-periodo+j).getPrecioCierre();
                cierreAyer = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-1-periodo+j).getPrecioCierre();

                if (cierreHoy - cierreAyer > 0) {
                    alza = cierreHoy - cierreAyer;
                    baja = 0;
                } else if (cierreHoy - cierreAyer < 0) {
                    alza = 0;
                    baja = -(cierreHoy - cierreAyer);
                } else {
                    alza = 0;
                    baja = 0;
                }
                promedioAlza = alpha*alza + (1-alpha)*promedioAlzaAnt;
                promedioBaja = alpha*baja + (1-alpha)*promedioBajaAnt;
            }
            promedioAlzaAnt = promedioAlza;
            promedioBajaAnt = promedioBaja;
        }

        if (promedioBaja != 0) {
            rs = promedioAlza/promedioBaja;
            rsi = 100 - 100/(1 + rs);
        } else
            rsi = 100;

        return rsi;
    }

    /*public int senalRSI(int idProducto, int N, int periodo)
    {
        int senal = 0;
        float RSIAnterior, RSIActual;

        RSIAnterior = calcularRSI(idProducto, N, periodo-1);
        RSIActual = calcularRSI(idProducto, N, periodo);
        // Si el RSI corta de hacia abajo el nivel de 70 es senal de venta
        if (RSIAnterior > 70 && RSIActual < 70) {
            senal = -1; //Venta
            //System.out.println(RSIAnterior+" > 70 && "+RSIActual+" < 70 ==> Venta");
        // Si el RSI corta hacia arriba el nivel de 30 es senal de compra
        } else if (RSIAnterior < 30 && RSIActual > 30) {
            senal = 1; //Compra
            //System.out.println(RSIAnterior+" < 30 && "+RSIActual+" > 30 ==> Compra");
        }
        return senal;
    }*/

    /*
     * Sirve para identificar periodos de tendencia o trading y no cuando comprar o cuando vender.
     *
     * Se calcula como:
     * VHF = abs(cierreMax - cierreMin)/SUMA(1..n)[abs(cierre(i) - cierre(i-1))]
     */
    public float calcularVHF(int idProducto, int N)
    {
        float vhf, cierreMax=0, cierreMin=0, cierreHoy, cierreAyer, suma;
        int n = AgenteAdministrador.historicoSubastas.get(idProducto).size();

        N = Math.min(N, n-1);
        for (int i=1; i<=N; i++) {
            float precio = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-i).getPrecioCierre();
            if (precio > cierreMax)
                cierreMax = precio;
            if (precio < cierreMin)
                cierreMin = precio;
        }

        suma = 0;
        for (int i=1; i<=N; i++) {
            cierreHoy = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-i).getPrecioCierre();
            cierreAyer = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-i-1).getPrecioCierre();
            suma += Math.abs(cierreHoy - cierreAyer);
        }

        vhf = Math.abs(cierreMax - cierreMin)/suma;
        
        return vhf;
    }

    /*
     * Se calcula como PMEhoy = PMEayer + alpha * (precio - EMAayer)
     * donde alpha = 2 / (N + 1), y el primer PME es el precio del primer periodo.
     * Se aplica a TODOS los datos del historico.
     */
    public float calcularPromedioExponencial(int idProducto, int N) {
        float alpha = (float)(2.0 / (N + 1.0));
        int n = AgenteAdministrador.historicoSubastas.get(idProducto).size();
        float precio;
        float promedioAyer = AgenteAdministrador.historicoSubastas.get(idProducto).get(0).getPrecioCierre();
        float promedioHoy = 0;

        for (int i = 1; i < n; i++) {
            precio = AgenteAdministrador.historicoSubastas.get(idProducto).get(i).getPrecioCierre();
            promedioHoy = promedioAyer + alpha * (precio - promedioAyer);
            promedioAyer = promedioHoy;
        }
        
        return promedioHoy;
    }

    public float calcularMACD(int idProducto) {
        float macd;
        float pme12 = calcularPromedioExponencial(idProducto, 12);
        float pme26 = calcularPromedioExponencial(idProducto, 26);

        macd = 100 * (pme12 - pme26) / pme26;

        return macd;
    }

    public float calcularMACDSenal(ArrayList<ArrayList<Float>> historia, int idProducto) {
        float pmeMACD9;

        float alpha = (float)(2.0 / (9.0 + 1.0));
        int n = historia.get(idProducto).size();
        float valor;
        float promedioAyer = historia.get(idProducto).get(0);
        float promedioHoy = 0;

        if (n == 1) return promedioAyer;

        for (int i = 1; i < n; i++) {
            valor = historia.get(idProducto).get(i);
            promedioHoy = promedioAyer + alpha * (valor - promedioAyer);
            promedioAyer = promedioHoy;
        }

        pmeMACD9 = promedioHoy;
        return pmeMACD9;
    }

    public float calcularROC(int idProducto, int N) {
        float roc;
        int n = AgenteAdministrador.historicoSubastas.get(idProducto).size();
        float precio = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-1).getPrecioCierre();
        float precioN = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-1-N).getPrecioCierre();

        roc = 100 * (precio / precioN);

        return roc;
    }

    public float calcularMomento(int idProducto, int N) {
        float momento;
        int n = AgenteAdministrador.historicoSubastas.get(idProducto).size();
        float precio = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-1).getPrecioCierre();
        float precioN = AgenteAdministrador.historicoSubastas.get(idProducto).get(n-1-N).getPrecioCierre();

        momento = precio - precioN;

        return momento;
    }
}
