/*
 * AgenteTecnico.java
 */

package agentes;

import inferencia.*;

import java.util.*;


public class AgenteTecnico extends Generico
{

    private float[] RSIAnterior, promedioCortoAnterior, promedioLargoAnterior, MACDAnterior, promedioSimpleAnterior, promSimplePrecioAnterior,
            MACDLineaSenalAnterior, ROCAnterior, momentoAnterior, promTripleCortoAnterior, promTripleMedioAnterior, promTripleLargoAnterior;
    private int indicador; //0:PMD, 1:PMS, 2:PMT, 3:RSI, 4:VHF, 5:MACD, 6:ROC, 7:Momento
    private int nCorta, nLarga, nRSI, nVHF, nMedia, nMomento, nROC;
    private int posPMD, posRSI, posVHF, posPMS, posPMT, posMACD, posROC, posMomento;
    private boolean posibleCompraPMT, posibleVentaPMT;
    private ArrayList<ArrayList<Float>> historiaMACD;

   // PrintWriter log;

//********************************************************************************************************************************************************
//FUNCION DE INICIALIZACION DEL AGENTE
//********************************************************************************************************************************************************
    protected void setup()
    {
        objeto = new java.util.Random();
        
        Object[] args = getArguments();
        numAcciones = args.length-2;
        cantidadXAccion = new int[numAcciones];
        probabilidadTransarAccion = new float[numAcciones];
        cantidadOfertada = new int[numAcciones];
        saldoOfertado  = new float[numAcciones];
        cantidadesaOfertar = new int[numAcciones];

        OfertaEnviada = new boolean[numAcciones];
        OfertaEliminada = new boolean[numAcciones];
        /**********************************************************************/
        // CAMBIAR ESTO!!!!!!!!!!!!!!!!!!!!
        /**********************************************************************/
       // for (int i = 0; i < numAcciones; ++i)
         //   cantidadesaOfertar[i] = 2;

        if (args != null && args.length > 0) {
            saldo = Float.parseFloat(args[0].toString());
            indicador = Integer.parseInt(args[1].toString());

            for (int i = 2; i < args.length; ++i)
                cantidadXAccion[i-2] = Integer.parseInt(args[i].toString());
        }

        nombre = 	this.getLocalName();
        tipoAgente = 1; //Corresponde al ID de los tecnico

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
        }

        posibleVentaPMT = false;
        posibleCompraPMT = false;

        registrarServidioAgente();

//        // Registrar el servicio
//        try {
//            // Se crea una lista de servicios de agente
//            DFAgentDescription dfd = new DFAgentDescription();
//            dfd.setName(getAID());
//
//            // Se crea una descripcion de servicio
//            ServiceDescription sd = new ServiceDescription();
//
//            sd.setName("Tecnico"); // Si es vendedor el servicio es vender acciones
//
//            // Se define el tipo de servicio
//            sd.setType("Agente de la bolsa de valores que desea participar en la subasta");
//
//            // Se define la ontologa del servicio
//            sd.addOntologies("DoblePunta");
//
//            // Se define la ontologa del agente
//            dfd.addOntologies("DoblePunta");
//
//            // Se especifica el lenguaje que deben "hablar" los agentes que acceden al servicio
//            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
//
//            // Se especifica el lenguaje "habla" el agente
//            dfd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
//
//            // Se agrega el servicio a la lista de servicios
//            dfd.addServices(sd);
//
//            DFService.register(this, dfd);
//        } catch (FIPAException fe) {
//            fe.printStackTrace();
//        } catch (java.lang.NumberFormatException num) {
//            num.printStackTrace();
//        }
//
//        getContentManager().registerLanguage(codec);    // Se registra el lenguaje
//        getContentManager().registerOntology(ontologia); // Se registra la ontologia
//        contenedorBursatil = getContainerController();

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
        if (indicador == 0) {
            AgenteAdministrador.datosPromediosMovilesCorto.add(new ArrayList<Float>());
            AgenteAdministrador.datosPromediosMovilesLargo.add(new ArrayList<Float>());
            posPMD = AgenteAdministrador.datosPromediosMovilesLargo.size() - 1;
            AgenteAdministrador.agentesTecnicosPMD.add(posPMD, new Integer(idAgente));
        } else if (indicador == 1) {
            AgenteAdministrador.datosPromediosMovilesSimple.add(new ArrayList<Float>());
            posPMS = AgenteAdministrador.datosPromediosMovilesSimple.size() - 1;
            AgenteAdministrador.agentesTecnicosPMS.add(posPMS, new Integer(idAgente));
        } else if (indicador == 2) {
            AgenteAdministrador.datosPromediosMovilesTripleCorto.add(new ArrayList<Float>());
            AgenteAdministrador.datosPromediosMovilesTripleMedio.add(new ArrayList<Float>());
            AgenteAdministrador.datosPromediosMovilesTripleLargo.add(new ArrayList<Float>());
            posPMT = AgenteAdministrador.datosPromediosMovilesTripleLargo.size() - 1;
            AgenteAdministrador.agentesTecnicosPMT.add(posPMT, new Integer(idAgente));
        } else if (indicador == 3) {
            AgenteAdministrador.datosRSI.add(new ArrayList<Float>());
            posRSI = AgenteAdministrador.datosRSI.size() - 1;
            AgenteAdministrador.agentesTecnicosRSI.add(posRSI, new Integer(idAgente));
        } else if (indicador == 4) {
            AgenteAdministrador.datosVHF.add(new ArrayList<Float>());
            posVHF = AgenteAdministrador.datosVHF.size() - 1;
            AgenteAdministrador.agentesTecnicosVHF.add(posVHF, new Integer(idAgente));
        } else if (indicador == 5) {
            AgenteAdministrador.datosMACD.add(new ArrayList<Float>());
            AgenteAdministrador.datosMACDLineaSenal.add(new ArrayList<Float>());
            posMACD = AgenteAdministrador.datosMACD.size() - 1;
            AgenteAdministrador.agentesTecnicosMACD.add(posMACD, new Integer(idAgente));
        } else if (indicador == 6) {
            AgenteAdministrador.datosROC.add(new ArrayList<Float>());
            posROC = AgenteAdministrador.datosROC.size() - 1;
            AgenteAdministrador.agentesTecnicosROC.add(posROC, new Integer(idAgente));
        }else if (indicador == 7) {
            AgenteAdministrador.datosMomento.add(new ArrayList<Float>());
            posMomento = AgenteAdministrador.datosMomento.size() - 1;
            AgenteAdministrador.agentesTecnicosMomento.add(posMomento, new Integer(idAgente));
        }
        for (int i=0; i<numAcciones; i++) {
           // AgenteAdministrador.accionesxagente.get(idAgente).set(i, cantidadXAccion[i]);
            AgenteAdministrador.accionesxagente[idAgente][i]= cantidadXAccion[i];
            if (indicador == 0) {
                AgenteAdministrador.datosPromediosMovilesCorto.get(posPMD).add(i, new Float(-1));
                AgenteAdministrador.datosPromediosMovilesLargo.get(posPMD).add(i, new Float(-1));
            } else if (indicador == 1) {
                AgenteAdministrador.datosPromediosMovilesSimple.get(posPMS).add(i, new Float(-1));
            } else if (indicador == 2) {
                AgenteAdministrador.datosPromediosMovilesTripleCorto.get(posPMT).add(i, new Float(-1));
                AgenteAdministrador.datosPromediosMovilesTripleMedio.get(posPMT).add(i, new Float(-1));
                AgenteAdministrador.datosPromediosMovilesTripleLargo.get(posPMT).add(i, new Float(-1));
            } else if (indicador == 3) {
                AgenteAdministrador.datosRSI.get(posRSI).add(i, new Float(-1));
            } else if (indicador == 4) {
                AgenteAdministrador.datosVHF.get(posVHF).add(i, new Float(-1));
            } else if (indicador == 5) {
                AgenteAdministrador.datosMACD.get(posMACD).add(i, new Float(-1));
                AgenteAdministrador.datosMACDLineaSenal.get(posMACD).add(i, new Float(-1));
            } else if (indicador == 6) {
                AgenteAdministrador.datosROC.get(posROC).add(i, new Float(-1));
            } else if (indicador == 7) {
                AgenteAdministrador.datosMomento.get(posMomento).add(i, new Float(-1));
            }

        }
   }

   void parametrosIniciarSubasta()
   {
       // Selecciona las acciones que se compran, venden y las que no hacen nada
       
       //subastaActiva = true;
       
       
    for (int i=0; i<numAcciones; i++) {
        cantidadOfertada[i] = 0;
        saldoOfertado[i] = 0;

        int senal = 0;
        if (indicador == 0) { // Promedios Moviles Dobles
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
                senal = 1; // Senal de compra
            } else if (promedioCortoAnterior[i] > promedioLargoAnterior[i] && promedioCortoActual < promedioLargoActual) {
                senal = -1; // Senal de venta
            }

            AgenteAdministrador.datosPromediosMovilesCorto.get(posPMD).set(i, promedioCortoActual);
            AgenteAdministrador.datosPromediosMovilesLargo.get(posPMD).set(i, promedioLargoActual);
            promedioCortoAnterior[i] = promedioCortoActual;
            promedioLargoAnterior[i] = promedioLargoActual;
        } else if (indicador == 1) { // Promedio Movil Simple
            int n = AgenteAdministrador.historicoSubastas.get(i).size();
            float promedioSimpleActual, precioActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                promedioSimpleAnterior[i] = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-2-nMedia, n-2);
                promSimplePrecioAnterior[i] = AgenteAdministrador.historicoSubastas.get(i).get(n-2).getPrecioCierre();
            }
            promedioSimpleActual = new IndicadoresTecnicos().calcularPromedioMovilPonderado(i, n-1-nMedia, n-1);
            precioActual = AgenteAdministrador.historicoSubastas.get(i).get(n-1).getPrecioCierre();

            if (promSimplePrecioAnterior[i] < promedioSimpleAnterior[i] && precioActual > promedioSimpleActual)
                senal = 1;
            else if (promSimplePrecioAnterior[i] > promedioSimpleAnterior[i] && precioActual < promedioSimpleActual)
                senal = -1;

            AgenteAdministrador.datosPromediosMovilesSimple.get(posPMS).set(i, promedioSimpleActual);

            promedioSimpleAnterior[i] = promedioSimpleActual;
            promSimplePrecioAnterior[i] = precioActual;

        } else if (indicador == 2) { // Promedios Moviles Triples
            int n = AgenteAdministrador.historicoSubastas.get(i).size();
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
                senal = 1;
                posibleCompraPMT = false;
            }


            // Si la corta atraviesa desde arriba a la larga, es alerta de senal de venta
            if (promTripleCortoAnterior[i] > promTripleMedioAnterior[i] && promTripleCortoActual < promTripleMedioActual)
                posibleVentaPMT = true;
            // Si la media atraviesa desde arriba a la larga y hay alerta de venta, es senal de venta
            if (promTripleMedioAnterior[i] > promTripleLargoAnterior[i] && promTripleMedioActual < promTripleLargoActual && posibleVentaPMT) {
                senal = -1;
                posibleVentaPMT = false;
            }

            AgenteAdministrador.datosPromediosMovilesTripleCorto.get(posPMT).set(i, promTripleCortoActual);
            AgenteAdministrador.datosPromediosMovilesTripleMedio.get(posPMT).set(i, promTripleMedioActual);
            AgenteAdministrador.datosPromediosMovilesTripleLargo.get(posPMT).set(i, promTripleLargoActual);

            promTripleCortoAnterior[i] = promTripleCortoActual;
            promTripleMedioAnterior[i] = promTripleMedioActual;
            promTripleLargoAnterior[i] = promTripleLargoActual;
        } else if (indicador == 3) { // RSI
            float RSIActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                RSIAnterior[i] = new IndicadoresTecnicos().calcularRSI(i, nRSI, AgenteAdministrador.simulacionActual);
            }
            RSIActual = new IndicadoresTecnicos().calcularRSI(i, nRSI, AgenteAdministrador.simulacionActual);
            // Si el RSI corta de hacia abajo el nivel de 70 es senal de venta
            if (RSIAnterior[i] > 70 && RSIActual < 70) {
                senal = -1; //Venta
            // Si el RSI corta hacia arriba el nivel de 30 es senal de compra
            } else if (RSIAnterior[i] < 30 && RSIActual > 30) {
                senal = 1; //Compra
            }

            AgenteAdministrador.datosRSI.get(posRSI).set(i, RSIActual);
            RSIAnterior[i] = RSIActual;

        } else if (indicador == 4) { // VHF
            // ********ARREGLAR ESTO PORQUE NO SON SENALES DE COMPRA Y VENTA)********
            //probabilidadTransarAccion[i] = new IndicadoresTecnicos().senalVHF(i, nVHF);
            AgenteAdministrador.datosVHF.get(posVHF).set(i, new IndicadoresTecnicos().calcularVHF(i, nVHF));
        } else if (indicador == 5) { // MACD
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
                senal = 1;
            else if (MACDAnterior[i] > MACDLineaSenalAnterior[i] && MACDActual < MACDLineaSenal)
                senal = -1;

            AgenteAdministrador.datosMACD.get(posMACD).set(i, MACDActual);
            AgenteAdministrador.datosMACDLineaSenal.get(posMACD).set(i, MACDLineaSenal);
            MACDAnterior[i] = MACDActual;
            MACDLineaSenalAnterior[i] = MACDLineaSenal;

        } else if (indicador == 6) { // ROC
            float ROCActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                ROCAnterior[i] = new IndicadoresTecnicos().calcularROC(i, nROC);
            }
            ROCActual = new IndicadoresTecnicos().calcularROC(i, nROC);

            if (ROCAnterior[i] < 100 && ROCActual > 100)
                senal = 1;
            else if (ROCAnterior[i] > 100 && ROCActual < 100)
                senal = -1;

            AgenteAdministrador.datosROC.get(posROC).set(i, ROCActual);
            ROCAnterior[i] = ROCActual;

        } else if (indicador == 7) { // Momento
            float momentoActual;

            if (AgenteAdministrador.simulacionActual == 1) {
                momentoAnterior[i] = new IndicadoresTecnicos().calcularMomento(i, nMomento);
            }
            momentoActual = new IndicadoresTecnicos().calcularMomento(i, nMomento);

            if (momentoAnterior[i] < 0 && momentoActual > 0)
                senal = 1;
            else if (momentoAnterior[i] > 0 && momentoActual < 0)
                senal = -1;

            AgenteAdministrador.datosMomento.get(posMomento).set(i, momentoActual);
            momentoAnterior[i] = momentoActual;
        }
        //if (senal != 0) {
            probabilidadTransarAccion[i] = senal;
//                                log.println("La senal para la accion "+i+" es "+((senal == 1)?"comprar":"vender"));
        //}
    }
   }

   void seleccionarAccionesAComprar(){}
   void seleccionarAccionesAVender(){}
}