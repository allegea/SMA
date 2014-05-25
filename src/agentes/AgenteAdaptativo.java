/*
 * AgenteTecnico.java
 */

package agentes;

import inferencia.*;

import informacion.TablaRdto;
import java.util.*;


public class AgenteAdaptativo extends Generico
{
    
        public int cortePeriodo;
        public int[][] cantidadAccionesEstrategia;
        public float[][] saldoAccionesEstrategia;
        public float[][] saldoEfectivoEstrategia;
        public float[][] rendimientoActualEstrategia;
        public float[][] rendimientoAcumuladoEstrategia;
        public int[] estrategiaActual;
        public int cantidadEstrategias;

    private float[] RSIAnterior, promedioCortoAnterior, promedioLargoAnterior, MACDAnterior, promedioSimpleAnterior, promSimplePrecioAnterior,
            MACDLineaSenalAnterior, ROCAnterior, momentoAnterior, promTripleCortoAnterior, promTripleMedioAnterior, promTripleLargoAnterior;
    private int indicador = -1; //0:PMD, 1:PMS, 2:PMT, 3:RSI, 4:VHF, 5:MACD, 6:ROC, 7:Momento
    private int nCorta, nLarga, nRSI, nVHF, nMedia, nMomento, nROC;
    private int posPMD, posRSI, posVHF, posPMS, posPMT, posMACD, posROC, posMomento;
    private boolean posibleCompraPMT, posibleVentaPMT;
    private ArrayList<ArrayList<Float>> historiaMACD;

   // PrintWriter log;
    
    public double[] probabilidadComprar; //Esto es por el aleatorio
    public double[] probabilidadVender; //Esto es por el aleatorio
    float probabilidad = 0; ///Esto es por el fundamental
    
     
    
    
    ///Esto es por el tecnico
    public  ArrayList<ArrayList<Float>> datosPromediosMovilesCorto = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosPromediosMovilesLargo = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosRSI = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosVHF = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosPromediosMovilesSimple = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosPromediosMovilesTripleCorto = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosPromediosMovilesTripleMedio = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosPromediosMovilesTripleLargo = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosMACD = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosMACDLineaSenal = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosROC = new ArrayList<ArrayList<Float>>();
    public  ArrayList<ArrayList<Float>> datosMomento = new ArrayList<ArrayList<Float>>();
    
//********************************************************************************************************************************************************
//FUNCION DE INICIALIZACION DEL AGENTE
//********************************************************************************************************************************************************
    protected void setup()
    {       

         
        
        objeto = new java.util.Random();
        
        Object[] args = getArguments();
        numAcciones = args.length-4;
        cantidadXAccion = new int[numAcciones];
        precioPromedioAccion = new float[numAcciones];
        probabilidadTransarAccion = new float[numAcciones];
        cantidadOfertada = new int[numAcciones];
        saldoOfertado  = new float[numAcciones];
        cantidadesaOfertar = new int[numAcciones];
        
        probabilidadComprar = new double[numAcciones];
        probabilidadVender = new double[numAcciones];

        OfertaEnviada = new boolean[numAcciones];
        OfertaEliminada = new boolean[numAcciones];

        
                 cortePeriodo=2;
        
        cantidadEstrategias = 10;
        
        //int pl=(int)(Math.ceil(AgenteAdministrador.cantidadSimulaciones/cortePeriodo));
        //System.err.println( AgenteAdministrador.cantidadSimulaciones+"***********************************************TAMANO VECTORES:"+pl);
        cantidadAccionesEstrategia=new int[cantidadEstrategias][numAcciones];
        saldoAccionesEstrategia=new float[cantidadEstrategias][numAcciones];
        saldoEfectivoEstrategia=new float[cantidadEstrategias][numAcciones];
        rendimientoActualEstrategia=new float[cantidadEstrategias][numAcciones];
        rendimientoAcumuladoEstrategia=new float[cantidadEstrategias][numAcciones];
        estrategiaActual = new int[numAcciones];
        
        
        Random rn=new Random();
        //rn.setSeed(new Date().getTime());
        


        float probabilidadCom = 0;
        float probabilidadVen = 0;

        if (args != null && args.length > 0) {
            saldo = Float.parseFloat(args[0].toString());
            
            ////CAROLINA!!!!!!!!!!!!!!!!!!!!!!!!!!!hay que tener cuidado entonces de que a los agentes si les llegue todos estos parametros
            probabilidadCom = Float.parseFloat(args[1].toString());
            probabilidadVen = Float.parseFloat(args[2].toString());
            //indicador = Integer.parseInt(args[3].toString());
            probabilidad = Float.parseFloat(args[3].toString());
             
             

            for (int i = 4; i < args.length; ++i)
                cantidadXAccion[i-4] = Integer.parseInt(args[i].toString());
        }
        
       

        nombre = 	this.getLocalName();
        //System.out.println(getLocalName() + " -> probabilidadCom "+probabilidadCom);
       // System.out.println(getLocalName() + " -> probabilidadVen "+probabilidadVen);
       // System.out.println(getLocalName() + " -> indicador "+indicador);
       // System.out.println(getLocalName() + " -> probabilidad "+probabilidad);
        
        tipoAgente = 5; //Corresponde al ID de los adaptativos

        nCorta = (int)aleatorio(14, 8);
        nLarga = (int)aleatorio(nCorta+14, nCorta+6);
        nMedia = (nCorta + nLarga) / 2;
        nRSI = (int)aleatorio(20, 14);
        nVHF = (int)aleatorio(31, 25);
        nROC = (int)aleatorio(15, 9);
        nMomento = (int)aleatorio(21, 14);

        promedioCortoAnterior = new float[numAcciones];
        promedioLargoAnterior = new float[numAcciones];
        promedioSimpleAnterior = new float[numAcciones];
        promSimplePrecioAnterior = new float[numAcciones];
        RSIAnterior = new float[numAcciones];
        MACDAnterior = new float[numAcciones];
        MACDLineaSenalAnterior = new float[numAcciones];
        ROCAnterior = new float[numAcciones];
        momentoAnterior = new float[numAcciones];
        promTripleCortoAnterior = new float[numAcciones];
        promTripleMedioAnterior = new float[numAcciones];
        promTripleLargoAnterior = new float[numAcciones];
        historiaMACD = new ArrayList<ArrayList<Float>>();

        
        
        for (int i = 0; i < numAcciones; ++i) {
            historiaMACD.add(new ArrayList<Float>());
            
            OfertaEnviada[i] = false;
            OfertaEliminada[i] = false;
            
            probabilidadComprar[i] = probabilidadCom;
            probabilidadVender[i] = probabilidadVen;
            
            do {
                estrategiaActual[i] = rn.nextInt(cantidadEstrategias);
            } while (estrategiaActual[i] == 5); //Las estrategias se cuentan como si la primera fuera la aleatoria, la segunda la fundamental, y de la tercera en adelante
            //la del tecnico, entonces como el indicador de VFH es el 4, por eso mientras el aleatorio de igual a 5, vuelve y calcula

            //System.err.println(getLocalName()+" -> Para la accion "+i+" estrategia inicial es - "+estrategiaActual[i]);
        }

        posibleVentaPMT = false;
        posibleCompraPMT = false;

        registrarServidioAgente();

        RealizarRegistro RRBehaviour = new RealizarRegistro(this);
        addBehaviour(RRBehaviour); // Se llama al comportamiento de registrarse
    }

	

    ///////////////////////////////////////////
    ///////////////////////////////////////////
    ///////////////////////////////////////////
    ///////////////////////////////////////////

  void registroTecnico()
   {
        //AgenteAdministrador.saldosAgentes.set(idAgente, saldo);
       AgenteAdministrador.saldosAgentes[idAgente]= saldo;
        // Inicializa los datos para las graficas de los indicadores
        //if (indicador == 0) {
            datosPromediosMovilesCorto.add(new ArrayList<Float>());
            datosPromediosMovilesLargo.add(new ArrayList<Float>());
            posPMD = datosPromediosMovilesLargo.size() - 1;
            //AgenteAdministrador.agentesTecnicosPMD.add(posPMD, new Integer(idAgente));
        //} else if (indicador == 1) {
            datosPromediosMovilesSimple.add(new ArrayList<Float>());
            posPMS = datosPromediosMovilesSimple.size() - 1;
            //AgenteAdministrador.agentesTecnicosPMS.add(posPMS, new Integer(idAgente));
       // } else if (indicador == 2) {
            datosPromediosMovilesTripleCorto.add(new ArrayList<Float>());
            datosPromediosMovilesTripleMedio.add(new ArrayList<Float>());
            datosPromediosMovilesTripleLargo.add(new ArrayList<Float>());
            posPMT = datosPromediosMovilesTripleLargo.size() - 1;
           // AgenteAdministrador.agentesTecnicosPMT.add(posPMT, new Integer(idAgente));
       // } else if (indicador == 3) {
            datosRSI.add(new ArrayList<Float>());
            posRSI = datosRSI.size() - 1;
           // AgenteAdministrador.agentesTecnicosRSI.add(posRSI, new Integer(idAgente));
        //} else if (indicador == 4) {
            datosVHF.add(new ArrayList<Float>());
            posVHF = datosVHF.size() - 1;
            //AgenteAdministrador.agentesTecnicosVHF.add(posVHF, new Integer(idAgente));
       // } else if (indicador == 5) {
            datosMACD.add(new ArrayList<Float>());
            datosMACDLineaSenal.add(new ArrayList<Float>());
            posMACD = datosMACD.size() - 1;
            //AgenteAdministrador.agentesTecnicosMACD.add(posMACD, new Integer(idAgente));
        //} else if (indicador == 6) {
            datosROC.add(new ArrayList<Float>());
            posROC = datosROC.size() - 1;
           // AgenteAdministrador.agentesTecnicosROC.add(posROC, new Integer(idAgente));
       // }else if (indicador == 7) {
            datosMomento.add(new ArrayList<Float>());
            posMomento = datosMomento.size() - 1;
           // AgenteAdministrador.agentesTecnicosMomento.add(posMomento, new Integer(idAgente));
        //}
        for (int i=0; i<numAcciones; i++) {
           // AgenteAdministrador.accionesxagente.get(idAgente).set(i, cantidadXAccion[i]);
            AgenteAdministrador.accionesxagente[idAgente][i]= cantidadXAccion[i];
           // if (indicador == 0) {
                datosPromediosMovilesCorto.get(posPMD).add(i, new Float(-1));
                datosPromediosMovilesLargo.get(posPMD).add(i, new Float(-1));
            //} else if (indicador == 1) {
                datosPromediosMovilesSimple.get(posPMS).add(i, new Float(-1));
           // } else if (indicador == 2) {
                datosPromediosMovilesTripleCorto.get(posPMT).add(i, new Float(-1));
                datosPromediosMovilesTripleMedio.get(posPMT).add(i, new Float(-1));
                datosPromediosMovilesTripleLargo.get(posPMT).add(i, new Float(-1));
           // } else if (indicador == 3) {
                datosRSI.get(posRSI).add(i, new Float(-1));
           // } else if (indicador == 4) {
                datosVHF.get(posVHF).add(i, new Float(-1));
           // } else if (indicador == 5) {
                datosMACD.get(posMACD).add(i, new Float(-1));
                datosMACDLineaSenal.get(posMACD).add(i, new Float(-1));
           // } else if (indicador == 6) {
                datosROC.get(posROC).add(i, new Float(-1));
           // } else if (indicador == 7) {
                datosMomento.get(posMomento).add(i, new Float(-1));
          // }

        }
   }

   int[][] seleccionarOfertasTecnico()
   {
       // Selecciona las acciones que se compran, venden y las que no hacen nada
       
       //subastaActiva = true;     
    int[][] senal = new int[8][numAcciones];
    for (int i=0; i<numAcciones; i++) {
        /*cantidadOfertada[i] = 0;
        saldoOfertado[i] = 0;*/
        

        
       //if (indicador == 0) { // Promedios Moviles Dobles
              int n = AgenteAdministrador.historicoSubastas.get(i).size();
            float promedioCortoActual, promedioLargoActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                promedioCortoAnterior[i] = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-2-nCorta, n-2);
                promedioLargoAnterior[i] = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-2-nLarga, n-2);
            }

            promedioCortoActual = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nCorta, n-1);
            promedioLargoActual = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nLarga, n-1);

            // Si el promedio de corto plazo corta al promedio de mediano plazo de abajo para arriba compra
            // sino vende
            if (promedioCortoAnterior[i] < promedioLargoAnterior[i] && promedioCortoActual > promedioLargoActual) {
                senal[0][i] = 1; // Senal de compra
            } else if (promedioCortoAnterior[i] > promedioLargoAnterior[i] && promedioCortoActual < promedioLargoActual) {
                senal[0][i] = -1; // Senal de venta
            }
    
          //  datosPromediosMovilesCorto.get(posPMD).set(i, promedioCortoActual);
         //   datosPromediosMovilesLargo.get(posPMD).set(i, promedioLargoActual);
            promedioCortoAnterior[i] = promedioCortoActual;
            promedioLargoAnterior[i] = promedioLargoActual;
      //  } else if (indicador == 1) { // Promedio Movil Simple
           // int n = AgenteAdministrador.historicoSubastas.get(i).size();
            float promedioSimpleActual, precioActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                promedioSimpleAnterior[i] = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-2-nMedia, n-2);
                promSimplePrecioAnterior[i] = AgenteAdministrador.historicoSubastas.get(i).get(n-2).getPrecioCierre();
            }
            promedioSimpleActual = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nMedia, n-1);
            precioActual = AgenteAdministrador.historicoSubastas.get(i).get(n-1).getPrecioCierre();

            if (promSimplePrecioAnterior[i] < promedioSimpleAnterior[i] && precioActual > promedioSimpleActual)
                senal[1][i] = 1;
            else if (promSimplePrecioAnterior[i] > promedioSimpleAnterior[i] && precioActual < promedioSimpleActual)
                senal[1][i] = -1;

//            datosPromediosMovilesSimple.get(posPMS).set(i, promedioSimpleActual);

            promedioSimpleAnterior[i] = promedioSimpleActual;
            promSimplePrecioAnterior[i] = precioActual;
            
            //decisionTecnico.put(""+periodosTecnico, ""+ senal);
                
      //  } else if (indicador == 2) { // Promedios Moviles Triples
          // int n = AgenteAdministrador.historicoSubastas.get(i).size();
            float promTripleCortoActual, promTripleMedioActual, promTripleLargoActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                promTripleCortoAnterior[i] = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nCorta, n-1);
                promTripleMedioAnterior[i] = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nMedia, n-1);
                promTripleLargoAnterior[i] = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nLarga, n-1);
            }
            promTripleCortoActual = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nCorta, n-1);
            promTripleMedioActual = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nMedia, n-1);
            promTripleLargoActual = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nLarga, n-1);

            // Si la corta atraviesa desde abajo a la larga, es alerta de senal de compra
            if (promTripleCortoAnterior[i] < promTripleMedioAnterior[i] && promTripleCortoActual > promTripleMedioActual)
                posibleCompraPMT = true;
            // Si la media atraviesa desde abajo a la larga y hay alerta de compra, es senal de compra
            if (promTripleMedioAnterior[i] < promTripleLargoAnterior[i] && promTripleMedioActual > promTripleLargoActual && posibleCompraPMT) {
                senal[2][i] = 1;
                posibleCompraPMT = false;
            }

            
            // Si la corta atraviesa desde arriba a la larga, es alerta de senal de venta
            if (promTripleCortoAnterior[i] > promTripleMedioAnterior[i] && promTripleCortoActual < promTripleMedioActual)
                posibleVentaPMT = true;
            // Si la media atraviesa desde arriba a la larga y hay alerta de venta, es senal de venta
            if (promTripleMedioAnterior[i] > promTripleLargoAnterior[i] && promTripleMedioActual < promTripleLargoActual && posibleVentaPMT) {
                senal[2][i] = -1;
                posibleVentaPMT = false;
            }
            
           
           
            //datosPromediosMovilesTripleCorto.get(posPMT).set(i, promTripleCortoActual);
            //datosPromediosMovilesTripleMedio.get(posPMT).set(i, promTripleMedioActual);
            //datosPromediosMovilesTripleLargo.get(posPMT).set(i, promTripleLargoActual);

            promTripleCortoAnterior[i] = promTripleCortoActual;
            promTripleMedioAnterior[i] = promTripleMedioActual;
            promTripleLargoAnterior[i] = promTripleLargoActual;
       // } else if (indicador == 3) { // RSI
       
            float RSIActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                RSIAnterior[i] = new IndicadoresTecnicos().calcularRSI(i, nRSI, AgenteAdministrador.simulacionActual);
            }
            RSIActual = new IndicadoresTecnicos().calcularRSI(i, nRSI, AgenteAdministrador.simulacionActual);
            // Si el RSI corta de hacia abajo el nivel de 70 es senal de venta
            if (RSIAnterior[i] > 70 && RSIActual < 70) {
                senal[3][i] = -1; //Venta
            // Si el RSI corta hacia arriba el nivel de 30 es senal de compra
            } else if (RSIAnterior[i] < 30 && RSIActual > 30) {
                senal[3][i] = 1; //Compra
            }

         
            
            datosRSI.get(posRSI).set(i, RSIActual);
            RSIAnterior[i] = RSIActual;

      //  } else if (indicador == 4) { // VHF
            // ********ARREGLAR ESTO PORQUE NO SON SENALES DE COMPRA Y VENTA)********
            //probabilidadTransarAccion[i] = new IndicadoresTecnicos().senalVHF(i, nVHF);
            
            datosVHF.get(posVHF).set(i, new IndicadoresTecnicos().calcularVHF(i, nVHF));
       // } else if (indicador == 5) { // MACD
            
            float MACDActual, MACDLineaSenal;

            if (AgenteAdministrador.simulacionActual == 1) {
                MACDAnterior[i] = new IndicadoresTecnicos().calcularMACD(i);
                historiaMACD.get(i).add(MACDAnterior[i]);
                MACDLineaSenalAnterior[i] = new IndicadoresTecnicos().calcularMACDSenal(historiaMACD, i);
            }
            MACDActual = new IndicadoresTecnicos().calcularMACD(i);
            MACDLineaSenal = new IndicadoresTecnicos().calcularMACDSenal(historiaMACD, i);
            historiaMACD.get(i).add(MACDActual);

            if (MACDAnterior[i] < MACDLineaSenalAnterior[i] && MACDActual > MACDLineaSenal)
                senal[5][i] = 1;
            else if (MACDAnterior[i] > MACDLineaSenalAnterior[i] && MACDActual < MACDLineaSenal)
                senal[5][i] = -1;

           
            
            datosMACD.get(posMACD).set(i, MACDActual);
            datosMACDLineaSenal.get(posMACD).set(i, MACDLineaSenal);
            MACDAnterior[i] = MACDActual;
            MACDLineaSenalAnterior[i] = MACDLineaSenal;

       // } else if (indicador == 6) { // ROC
            
            float ROCActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                ROCAnterior[i] = new IndicadoresTecnicos().calcularROC(i, nROC);
            }
            ROCActual = new IndicadoresTecnicos().calcularROC(i, nROC);

            if (ROCAnterior[i] < 100 && ROCActual > 100)
                senal[6][i] = 1;
            else if (ROCAnterior[i] > 100 && ROCActual < 100)
                senal[6][i] = -1;

                      
            datosROC.get(posROC).set(i, ROCActual);
            ROCAnterior[i] = ROCActual;

       // } else if (indicador == 7) { // Momento
           
            float momentoActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                momentoAnterior[i] = new IndicadoresTecnicos().calcularMomento(i, nMomento);
            }
            momentoActual = new IndicadoresTecnicos().calcularMomento(i, nMomento);

            if (momentoAnterior[i] < 0 && momentoActual > 0)
                senal[7][i] = 1;
            else if (momentoAnterior[i] > 0 && momentoActual < 0)
                senal[7][i] = -1;                   
            datosMomento.get(posMomento).set(i, momentoActual);
            momentoAnterior[i] = momentoActual;
            
            //probabilidadTransarAccion[i] = senal;    
         }
        return senal;
   }
   
    /*int parametrosIniciarSubastaAleatorio()
    {
        //subastaActiva = true;
        //seleccionarAccionesAComprar();
        //seleccionarAccionesAVender();        
       int senal=seleccionarOfertasAleatorio();

        for (int i=0; i<numAcciones; i++) {
            cantidadOfertada[i] = 0;
            saldoOfertado[i] = 0;
        }
       return senal;
    }*/

    private int[] seleccionarOfertasAleatorio()
    {
        float aleatorio;
        int[] senal= new int[numAcciones];
        for (int i=0; i<numAcciones; i++) 
        {

            /*if(CorreccionProbabilidades[i]==false)
            {
                probabilidadComprar[i] = probabilidadComprar[i]*(AgenteAdministrador.NrodeOperaciones[i]/AgenteAdministrador.promedioCantNegociacion);
                probabilidadVender[i] = probabilidadVender[i]*(AgenteAdministrador.NrodeOperaciones[i]/AgenteAdministrador.promedioCantNegociacion);
                CorreccionProbabilidades[i] = true;/
            }*/
            //Al ser el promedio de cant negociadas, entonces puede que cantidad de operaciones de una sea mayor que el promedio, y siempre va a comprar

            float a = AgenteAdministrador.PrecioFundamental[i];
            aleatorio = aleatorio(1f, 0f);
            if ( (0 < aleatorio) && (aleatorio <= probabilidadComprar[i]) && (saldo > AgenteAdministrador.PrecioReferencia[i]*1.015))
                //probabilidadTransarAccion[i] = 1;
                senal[i] = 1;
            else if ( (probabilidadComprar[i] < aleatorio) && (aleatorio <= (probabilidadComprar[i] + probabilidadVender[i]) ) && cantidadXAccion[i] > 0)
                //probabilidadTransarAccion[i] = -1;
                senal[i] = -1;
            else
               // probabilidadTransarAccion[i] = 0;
                senal[i] = 0;

            //senal=(int) probabilidadTransarAccion[i];
        }
        return senal;
    }
    
    /*int parametrosIniciarSubastaFundamental()
    {
        //subastaActiva = true;
        //seleccionarAccionesAComprar();
        //seleccionarAccionesAVender();
        int senal=seleccionarOfertasFundamental();

        
        return senal;
    }*/

    private int[] seleccionarOfertasFundamental()
    {
        float aleatorio;
        float pOfertaAgente = probabilidad;
        int[] senal = new int[numAcciones];
        for (int i=0; i<numAcciones; i++) 
        {

            /*if(CorreccionProbabilidades[i]==false)
            {
                probabilidadComprar[i] = probabilidadComprar[i]*(AgenteAdministrador.NrodeOperaciones[i]/AgenteAdministrador.promedioCantNegociacion);
                probabilidadVender[i] = probabilidadVender[i]*(AgenteAdministrador.NrodeOperaciones[i]/AgenteAdministrador.promedioCantNegociacion);
                CorreccionProbabilidades[i] = true;/
            }*/
            //Al ser el promedio de cant negociadas, entonces puede que cantidad de operaciones de una sea mayor que el promedio, y siempre va a comprar

            float a = AgenteAdministrador.PrecioFundamental[i];
            aleatorio = aleatorio(1f, 0f);
            //pOfertaAgente = aleatorio(0.5f, 0f);
            if ( (a < AgenteAdministrador.PrecioReferencia[i]) && (aleatorio < pOfertaAgente))
                //probabilidadTransarAccion[i] = -1;
                senal[i] = -1;
            else if ( (a > AgenteAdministrador.PrecioReferencia[i]) && (aleatorio > pOfertaAgente))
                //probabilidadTransarAccion[i] = 1;
                senal[i] = 1;
            else
                //probabilidadTransarAccion[i] = 0;
                senal[i] = 0;
		//senal= (int) probabilidadTransarAccion[i];

        }
        return senal;
    }

   void seleccionarAccionesAComprar(){}
   void seleccionarAccionesAVender(){}
   
   void parametrosIniciarSubasta() {

        if(AgenteAdministrador.simulacionActual==1)
          registroTecnico(); 

        
        
        
        int[] senalAleatorio = seleccionarOfertasAleatorio();
        int[] senalFundamental = seleccionarOfertasFundamental();
        int[][] senalTecnico = seleccionarOfertasTecnico();
        
        for (int i=0; i<numAcciones; i++) {
            
            AgenteAdministrador.estrategiasXaccionAdaptativos.get(i).put(idAgente,estrategiaActual[i]);
            
            //System.err.println(idAgente+" - accion = "+i+" - estrategia = "+estrategiaActual[i]);
            
            cantidadOfertada[i] = 0;
            saldoOfertado[i] = 0;
            
            float precioRef=AgenteAdministrador.PrecioReferencia[i];
            
            if(senalAleatorio[i]==-1){
               cantidadAccionesEstrategia[0][i]--; 
               saldoEfectivoEstrategia[0][i]+=precioRef;
  
            }else if(senalAleatorio[i]==1){
                cantidadAccionesEstrategia[0][i]++; 
               saldoEfectivoEstrategia[0][i]-=precioRef;
            }
            
             //System.err.println(getLocalName()+" -> Senal de "+0+" para Accion "+i+" = "+senalAleatorio[i]);
            
            if(senalFundamental[i]==-1){
               cantidadAccionesEstrategia[1][i]--; 
               saldoEfectivoEstrategia[1][i]+=precioRef;
            }else if(senalFundamental[i]==1){
                cantidadAccionesEstrategia[1][i]++; 
               saldoEfectivoEstrategia[1][i]-=precioRef;
            }
            
            //System.err.println(getLocalName()+" -> Senal de "+1+" para Accion "+i+" = "+senalFundamental[i]);
            
            for(int j=0;j<8;j++){
                if(j==4)continue;
                if (senalTecnico[j][i] == -1) {
                    cantidadAccionesEstrategia[2+j][i]--;
                    saldoEfectivoEstrategia[2+j][i] += precioRef;
                } else if (senalTecnico[j][i] == 1) {
                    cantidadAccionesEstrategia[2+j][i]++;
                    saldoEfectivoEstrategia[2+j][i] -= precioRef;
                }
               //  System.err.println(getLocalName()+" -> Senal de "+(2+j)+" para Accion "+i+" = "+senalTecnico[j][i]);
            }
                
            //Calcula la señal respectiva para la estrategia verdadera que aplica en el periodo actual
            if(estrategiaActual[i]==0)
            probabilidadTransarAccion[i] = senalAleatorio[i];
            else if(estrategiaActual[i]==1)
            probabilidadTransarAccion[i] = senalFundamental[i];
            else probabilidadTransarAccion[i] = senalTecnico[estrategiaActual[i]-2][i];
            
            
            
        }
        
        if(AgenteAdministrador.simulacionActual%cortePeriodo == 0 ){
            
            
            for(int i=0;i<numAcciones;i++){
                float precioRef=AgenteAdministrador.PrecioReferencia[i];
                float rentabilidadMax = Float.NEGATIVE_INFINITY;
                int nuevaEstrategia = -1;
            
                
                 
                for(int j=0;j<cantidadEstrategias;j++){
                
                    if(j==5)continue;
                    
                    saldoAccionesEstrategia[j][i]=cantidadAccionesEstrategia[j][i]*precioRef;
                    rendimientoActualEstrategia[j][i]= saldoEfectivoEstrategia[j][i]+saldoAccionesEstrategia[j][i];
                    float rentab =  rendimientoActualEstrategia[j][i]-rendimientoAcumuladoEstrategia[j][i];
                    rendimientoAcumuladoEstrategia[j][i] = rentab;
                    
                    //System.err.println(getLocalName()+" -> cantidadAccionesEstrategia de "+j+" para Accion "+i+" = "+ cantidadAccionesEstrategia[j][i]);
                   // System.err.println(getLocalName()+" -> saldoEfectivoEstrategia de "+j+" para Accion "+i+" = "+ saldoEfectivoEstrategia[j][i]);
                   // System.err.println(getLocalName()+" -> Rentabilidad de "+j+" para Accion "+i+" = "+ rentab);

                    if(rentab>rentabilidadMax){
                        rentabilidadMax = rentab;
                        nuevaEstrategia = j;
                        //System.err.println("NuevaEstrategia de "+i+" es - "+nuevaEstrategia);
                    }
                }
                
                 //System.err.println(getLocalName()+" -> NuevaEstrategia de "+i+" es - "+nuevaEstrategia);
                estrategiaActual[i] = nuevaEstrategia;
            }

      }
    }

  
}