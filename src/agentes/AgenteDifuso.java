/*
 * AgenteDifuso.java
 */

package agentes;

import inferencia.*;




public class AgenteDifuso extends Generico
{

    private float[] rentabilidadActual;
    private float[] rentabilidadEsperada;
    private double[] rentabilidadPromedioHist;
    private int tiempoEsperaRentabilidad;
    private int N;
    private double propensionRiesgo = 0;
        public double[] probabilidadComprar;
    public double[] probabilidadVender;

    
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
        precioPromedioAccion = new float[numAcciones];
        rentabilidadActual = new float[numAcciones];
        rentabilidadPromedioHist = new double[numAcciones];
        rentabilidadEsperada = new float[numAcciones];
        probabilidadComprar = new double[numAcciones];
        probabilidadVender = new double[numAcciones];
        tiempoEsperadoPorAccion = new int[numAcciones];
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
        
        for (int i=0; i<numAcciones; i++)
        {
            tiempoEsperadoPorAccion[i] = 1;
            OfertaEnviada[i] = false;
            OfertaEliminada[i] = false;
        }
        
        if (args != null && args.length > 0) {
            saldo = Float.parseFloat(args[0].toString()); //saldo del agente
            propensionRiesgo = Double.parseDouble(args[1].toString());
            for (int i=2; i<args.length; i++)
                cantidadXAccion[i-2] = Integer.parseInt(args[i].toString()); //saldo del agente
        }

        nombre = 	this.getLocalName();
        tipoAgente = 0; //Corresponde al ID de los difusos

        N = (int)aleatorio(14, 8);

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
//            sd.setName("Difuso"); // Si es vendedor el servicio es vender acciones
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

    void seleccionarAccionesAComprar()
    {
        int i, j, k, n;
        double rentabilidad, volatilidad, sumatoria, promedio;
        float aleatorio;
        float[] precios;

        /* Por cada producto se quiere sacar la probabilidad de que una accion
         * sea escogida para ser ofertada por el agente difuso
         */
        for (i=0; i<numAcciones; i++) {
            n = AgenteAdministrador.historicoSubastas.get(i).size();
            // Tamano total del historico de cotizaciones para el producto i
            n = Math.min(N, n);
            ////System.out.println("N: "+n);
            precios = new float[n];
            k = 0;
            for (j=0; j<AgenteAdministrador.historicoSubastas.get(i).size(); j++) {
                if (AgenteAdministrador.historicoSubastas.get(i).get(j).getPrecioCierre() > 0)
                    precios[k++] = AgenteAdministrador.historicoSubastas.get(i).get(j).getPrecioCierre();
                if (k >= n)
                    break;
            }
            // Rendimiento del producto i: Pf/Pi - 1
            rentabilidad = precios[n-1]/precios[0] - 1; 
            sumatoria = 0;
            // Sumatoria del precio promedio en todas los periodos anteriores de la accion i
            for (j=0; j<n; j++) {
                sumatoria += precios[j];
            }
            // Promedio del precio promedio en todas las fechas de la accion i
            promedio = sumatoria/n;

            sumatoria = 0;
            for (j=0; j<n; j++) {
                sumatoria += Math.abs((precios[j] - promedio)/promedio);
            }
            // volatilidad: la tasa de variacion del precio promedio de la accion i con respecto a su media
            volatilidad = sumatoria/n;
            
            if (saldo > AgenteAdministrador.paquetesMinimos[i]*precios[n-1]*1.05) {
                probabilidadComprar[i] = new InferenciaCompraCortoPlazo().probabilidadEscogencia(rentabilidad, volatilidad, propensionRiesgo);
                // *************************************************************
                // *************************************************************
                // ESTO ES TEMPORAL
                // *************************************************************
                // *************************************************************
                probabilidadComprar[i] -= probabilidadComprar[i]*0.6;
            } else
                probabilidadComprar[i] = 0;
            
            // Se calcula un aleatorio entre 0 y 1, si el aleatorio es menor que la probabilidad de
            // escoger la accion i, se marca para comprar, sino no, no se hace nada
            aleatorio = aleatorio(1, 0);
            if (aleatorio < probabilidadComprar[i])
                probabilidadTransarAccion[i] = 1;
        }
    }
    
    void seleccionarAccionesAVender()
    {
        double rentabilidadVenta = 0;
        double volatilidadVenta = 0;

        for (int i=0; i<numAcciones; i++) {
            if (cantidadXAccion[i] > 0) {
                tiempoEsperadoPorAccion[i]++;
                rentabilidadVenta = rentabilidadPromedioHist[i]/(rentabilidadEsperada[i]*tiempoEsperadoPorAccion[i]/tiempoEsperaRentabilidad);
                volatilidadVenta = AgenteAdministrador.volatilidadActual[i]/AgenteAdministrador.volatilidadHistorico[i];
                probabilidadVender[i] = new InferenciaVentaCortoPlazo().probabilidadEscogencia(rentabilidadVenta, volatilidadVenta , propensionRiesgo);
                // *************************************************************
                // *************************************************************
                // ESTO ES TEMPORAL
                // *************************************************************
                // *************************************************************
                probabilidadVender[i] -= probabilidadVender[i]*0.6;
            } else {
                probabilidadVender[i] = 0;
                tiempoEsperadoPorAccion[i] = 1;
            }
                
            float aleatorio = aleatorio(1, 0);
            if (aleatorio < probabilidadVender[i]) {
                if ((probabilidadComprar[i] < probabilidadVender[i] && probabilidadTransarAccion[i] <= 1 && probabilidadTransarAccion[i] >= 0))
                    probabilidadTransarAccion[i] = -1;
            }
        }
    }

    void parametrosIniciarSubasta()
    {
        //subastaActiva = true;
        if (AgenteAdministrador.simulacionActual == 1) {
            for (int i=0; i<numAcciones; i++) {
                // PARAMETROS PARA EL PRECIO: rentPromedio, DesvEstandar
                int n = AgenteAdministrador.historicoSubastas.get(i).size();
                double[] preciosHistorico = new double[n];

                int j = 0;
                for (int k=0; k<n; k++) {
                    float p = AgenteAdministrador.historicoSubastas.get(i).get(k).getPrecioCierre();
                    if (p > 0)
                        preciosHistorico[j++] = p; // j es el numero de precios del historico mayores que cero
                }

                double[] rentabilidades = new double[j-1]; // el tamano de las rentabilidades es uno menos que los precios
                double sum = 0;
                for (int k=0; k<j-1; k++) {
                    rentabilidades[k] = Math.log(preciosHistorico[k+1]/preciosHistorico[k]);
                    sum += rentabilidades[k];
                }
                rentabilidadPromedioHist[i] = sum/(j-1); // Rentabilidad promedio = SUM(r[]/(j-1))
            }

            for (int i=0; i<numAcciones; i++) {
                int n = AgenteAdministrador.historicoSubastas.get(i).size();
                if (cantidadXAccion[i] != 0)
                    precioPromedioAccion[i] = AgenteAdministrador.historicoSubastas.get(i).get(n-1).getPrecioCierre();
                else
                    precioPromedioAccion[i] = 0;
                /////////// RENTABILIDAD ESPERADA HISTORICO
                tiempoEsperaRentabilidad = (int)aleatorio(30, 10);
                if (rentabilidadPromedioHist[i] > 0)
                    rentabilidadEsperada[i] = (float)rentabilidadPromedioHist[i]*tiempoEsperaRentabilidad;
                else
                    rentabilidadEsperada[i] = (float)Math.max(0, rentabilidadPromedioHist[i] + AgenteAdministrador.volatilidadHistorico[i])*aleatorio(6, 2);
            }
        }
        for (int i=0; i<numAcciones; i++) {
            cantidadOfertada[i] = 0;
            saldoOfertado[i] = 0;

            int n = AgenteAdministrador.historicoSubastas.get(i).size();
            rentabilidadActual[i] = AgenteAdministrador.historicoSubastas.get(i).get(n-1).getPrecioCierre()/precioPromedioAccion[i] - 1;
        }
        subastaActiva = true;
        seleccionarAccionesAComprar();
        seleccionarAccionesAVender();
    }

        void registroTecnico(){}
}