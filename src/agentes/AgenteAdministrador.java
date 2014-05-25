/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package agentes;

/**
 *
 * @author alejo
 */

import ontologias.*;
import basedatos.*;
import informacion.*;

import java.io.*;
import java.sql.*;
import java.util.*;
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.wrapper.AgentController;
import java.util.Date;
import java.util.logging.*;
import jade.wrapper.StaleProxyException;
import java.lang.Integer;

public class AgenteAdministrador extends Agent{

    /////////////JADE
    private Codec codec = new SLCodec(); //Se crea un lenguaje para la ontologia
    private Ontology ontologia = DoblePuntaOntology.getInstance();//se crea la ontologia
    public static AID[] participantes;     //Vector que contiene a los participantes cuando se registran
    public static AID[] subastadores;
    AgentController[] ControllerSubastadores;
    jade.wrapper.AgentContainer contenedor;

    ConexionBD BD;
     ConexionBD BDA;
    Random objeto;
     Random aleatoriosNormal = new Random();

    public static int CalcesEnRunTime = 0;
    public static int estadoActual = -1;
    public static int simulacionActual;
    public static int cantidadSimulaciones = 0;
    public  int numAgentes = 0;
    private int Repeticion;
    private int indiceActualCalce;
    private int indiceActualCalceCM;
    private int indiceActualOC;
    private int indiceActualOV;


    static int contadorOfertasCompra = 0;
    static int contadorOfertasVenta = 0;


    //////////////////tiempos
    private long tiempoFinal;//Tiempo en segundos que durara un dia bursatil;
    static long tiempoCallMarketIntra;
    static long tiempoCallMarketFinal;
    static long tiempoAleatorioCallIntra;
    static long tiempoAleatorioCallFinal;
    static long tiempoCallMarket[];
    static long tiempoCallMarketAlea[];
    public static long tiempoSimulacion;
    public static long tiempoRegistros;

    //////////////ofertas
    public static int permanenciaOfertas = 1;
    public static float alphaPrecioOfertas = 0.015f;

    ///////////////////////precios y cantidades
    protected static float PrecioPorEncima[];
    protected static float PrecioPorDebajo[];
    protected static float porcentajeCanal[];
    //private boolean EstaEnCallMarket[];
    protected static float PrecioReferencia[];
    protected static float PrecioFundamental[];
    
    private boolean participanFundamentales = true;
    
    private float ProbabilidadAccion[];
    
    protected static float NrodeOperaciones[];
    protected float RuedasXAccion[];
    protected float CantidadesNegociadas[];
    private float sumatoriaOperaciones =0;
    protected static float promedioCantNegociacion;
    public static long[] cantidadesATransar;

    ////////////Informacion varia
    private int cantidadProductos = 0;
    public  static int[] paquetesMinimos;
    public  static String[] nombreAcciones;

    public  static ArrayList<ArrayList<InfoSubasta>> historicoSubastas = new ArrayList<ArrayList<InfoSubasta>>();

    /*public  static ArrayList<ArrayList<Integer>> accionesxagente = new ArrayList<ArrayList<Integer>>();
    public  static ArrayList<Float> saldosAgentes = new ArrayList<Float>();*/

    public  static int[][] accionesxagente;
    //public static int[][][] estrategiasXaccionAdaptativos;
    public  static float[] saldosAgentes;
    
    public static ArrayList<HashMap<Integer,Integer>> estrategiasXaccionAdaptativos= new ArrayList<HashMap<Integer,Integer>>();

    ////Informacion otros agentes
    public static float[] volatilidadActual;
    public static double[] volatilidadHistorico;

    ////Informacion que llenan todos los agentes
    public static ArrayList<InfoOfertaCompra> bufferOfertasCompra = new ArrayList<InfoOfertaCompra>();
    public static ArrayList<InfoOfertaVenta> bufferOfertasVenta = new ArrayList<InfoOfertaVenta>();
    public static ArrayList<InfoCalce> bufferCalces = new ArrayList<InfoCalce>();
    public static ArrayList<InfoCalce> bufferCalcesCM = new ArrayList<InfoCalce>();

    //////Grafica del libro
     public static boolean graficarLibro = false;
     public static Libro2 onSimulation;
     
     public static Date horaInicial;
     public static float UVR = (float) 190.8288; //Valor del UVR al 31 de diciembre del 2010

     //////////////////////////
     public static FileWriter pararSub=null;
    public static PrintWriter paraSubastador=null;

    private int contadorParticipantes = 0;      // Contador del vector de participantes
    private int nRegDinamicos = 0;
    private int[] registrosDinamicos = new int[100];

    /*static int contadorCalcesCM = 0;
    static int contadorCalces = 0;*/

    public static int noCompra = 1;
    public static int noVenta = 1;

    public static FileWriter Origen=null;
    public static PrintWriter archivo=null;
    
   /*private PrintWriter[] archivosImpresion;
   private double[] enCirculacion;
   private double[] Transadas;
   private double[] VolumenTransado;
   private double[] VolumenOfertado;
   private int tiempoHistorico = 60;*/

    /********************************************************
     * Para el calculo del volumen transado
     * 
     */
    
    public static int tiempoModificacionCanal = 20;
    public static float[] VolumenOfertadoCompra;
    public static float[] VolumenOfertadoVenta;
    public static float[] VolumenNegociado;
    
    public static ArrayList<HashSet<Integer>> IDOrdenesCompra;
    public static ArrayList<HashSet<Integer>> IDOrdenesVenta;
    public static ArrayList<HashSet<Integer>> IDOrdenesCompraExito;
    public static ArrayList<HashSet<Integer>> IDOrdenesVentaExito;
    
    public float[] FactorOrdenes;
    public float[] FactorVolumen;
    public float[] FactorFrecuencia;
    
    public int[] SizeFOrdenes;
    public int[] SizeFVolumen;
    public int[] SizeFFrecuencia;
    
    

    //***********************************************************************************************
    // Datos para graficar indicadores tecnicos, idagenteXidproducto

    public static ArrayList<ArrayList<Float>> datosPromediosMovilesCorto = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosPromediosMovilesLargo = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosRSI = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosVHF = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosPromediosMovilesSimple = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosPromediosMovilesTripleCorto = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosPromediosMovilesTripleMedio = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosPromediosMovilesTripleLargo = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosMACD = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosMACDLineaSenal = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosROC = new ArrayList<ArrayList<Float>>();
    public static ArrayList<ArrayList<Float>> datosMomento = new ArrayList<ArrayList<Float>>();

    public static ArrayList<Integer> agentesTecnicosPMD = new ArrayList<Integer>();
    public static ArrayList<Integer> agentesTecnicosRSI = new ArrayList<Integer>();
    public static ArrayList<Integer> agentesTecnicosVHF = new ArrayList<Integer>();
    public static ArrayList<Integer> agentesTecnicosPMS = new ArrayList<Integer>();
    public static ArrayList<Integer> agentesTecnicosPMT = new ArrayList<Integer>();
    public static ArrayList<Integer> agentesTecnicosMACD = new ArrayList<Integer>();
    public static ArrayList<Integer> agentesTecnicosROC = new ArrayList<Integer>();
    public static ArrayList<Integer> agentesTecnicosMomento = new ArrayList<Integer>();
    //***********************************************************************************************

    @SuppressWarnings("CallToThreadDumpStack")
    protected void setup()
    {
        /*try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AgenteAdministrador.class.getName()).log(Level.SEVERE, null, ex);
        }*/

            datosPromediosMovilesCorto.clear();
            datosPromediosMovilesLargo.clear();
            datosRSI.clear();
            datosVHF.clear();
            datosPromediosMovilesSimple.clear();
            datosPromediosMovilesTripleCorto.clear();
            datosPromediosMovilesTripleMedio.clear();
            datosPromediosMovilesTripleLargo.clear();
            datosMACD.clear();
            datosMACDLineaSenal.clear();
            datosROC.clear();
            datosMomento.clear();
            agentesTecnicosPMD.clear();
            agentesTecnicosRSI.clear();
            agentesTecnicosVHF.clear();
            agentesTecnicosPMS.clear();
            agentesTecnicosPMT.clear();
            agentesTecnicosMACD.clear();
            agentesTecnicosROC.clear();
            agentesTecnicosMomento.clear();

                    //System.err.println(getLocalName()+" -> tiempoModificacionCanal - "+tiempoModificacionCanal);
                    
            objeto = new java.util.Random();

            //SE GENERA EL ARCHIVO PARA LA GRAFICA DE CALCERUNTIME/////////
        /*try {
            CalcesEnRunTime = 0;
            Origen = new FileWriter("CalceRunTime.txt");
            archivo = new PrintWriter(Origen);
            archivo.print(CalcesEnRunTime);
            archivo.close();
        } catch (IOException ex) {
            Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
        }*/

            Object[] args = getArguments();
            
        try {
            BD = new ConexionBD("mysql");
            BDA= new ConexionBD("mysql");
            ResultSet r = null;
            r = BD.consulta("SELECT count(*) FROM producto");
            if (r.next())
                cantidadProductos = r.getInt(1);
            //BD.cerrarConexion();


            if (args != null && args.length > 0){
                cantidadSimulaciones = Integer.parseInt(args[0].toString()); //numero de simulaciones
                numAgentes = Integer.parseInt(args[1].toString()); //numero de agentes
                Repeticion = Integer.parseInt(args[2].toString());

                //System.err.println("Tiempo para cambio del canal - "+(tiempoModificacionCanal+((Repeticion-1)*10)));
                //
                //NrodeOperaciones = new float[args.length-3];
                NrodeOperaciones = new float[cantidadProductos];
                ProbabilidadAccion = new float[cantidadProductos];
                 participantes = new AID[numAgentes];
                 
                for(int i=3;i<3+cantidadProductos;i++)
                {
                    NrodeOperaciones[i-3] = Float.parseFloat(args[i].toString());
                    sumatoriaOperaciones+=NrodeOperaciones[i-3];
                    //System.out.println(getLocalName()+" -> "+(i-3)+" - "+NrodeOperaciones[i-3]);

                }
                
                if(args.length - cantidadProductos - 3 > 0)
                for(int i=3+cantidadProductos;i<2*cantidadProductos+3;i++)
                {
                    ProbabilidadAccion[i-3-cantidadProductos] = Float.parseFloat(args[i].toString());
                   
                    System.out.println(getLocalName()+" ->Probabilidad de accion "+(i-3-cantidadProductos)+" - "+ProbabilidadAccion[i-3-cantidadProductos]);

                }
                
                if(args.length - 2*cantidadProductos - 3 > 0)
                    participanFundamentales = true;
                
                
                
                System.out.println(getLocalName()+" -> argumentos "+args.length);
                System.out.println(getLocalName()+" -> participanFundamentales "+participanFundamentales);

                    promedioCantNegociacion = sumatoriaOperaciones/(args.length-3);
                    System.out.println(getLocalName()+" -> promedioCantNegociacion "+promedioCantNegociacion);

            }



        simulacionActual = 1;
        tiempoSimulacion = 15000; // la simulacion durara 15 segundos
        tiempoRegistros = 10000; // seran 5 segundos de registro
        tiempoFinal=86400*1000; //son 86400 minutos, es decir 24 horas, pero en milisegundos
        tiempoCallMarketIntra = (tiempoSimulacion*150000)/14100000; //150000 son 2.5 minutos en milisegundos, 14100000 son 235 min en mili
        tiempoCallMarketFinal = (tiempoSimulacion*300000)/14100000; //300000 son 5 minutos en milisegundos, 14100000 son 235 min en mili
        tiempoAleatorioCallIntra = (tiempoSimulacion*30000)/14100000; // 30000 son 30 segundos
        tiempoAleatorioCallFinal = (tiempoSimulacion*60000)/14100000; // 60000 son 60 segundos

        



         System.out.println(getLocalName()+" -> tiempoCallMarketIntra - "+tiempoCallMarketIntra);
         System.out.println(getLocalName()+" -> tiempoCallMarketFinal - "+tiempoCallMarketFinal);
         System.out.println(getLocalName()+" -> tiempoAleatorioCallIntra - "+tiempoAleatorioCallIntra);
         System.out.println(getLocalName()+" -> tiempoAleatorioCallFinal - "+tiempoAleatorioCallFinal);
         System.out.println(getLocalName()+" -> Tiempo de la simulacion - "+(tiempoSimulacion-(tiempoCallMarketFinal+tiempoAleatorioCallFinal)));
        // Registrar el servicio
     
            // Se crea una lista de servicios de agente
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());

            // Se crea una descripcion de servicio
            ServiceDescription sd = new ServiceDescription();
            sd.setName("Administrador");

            // Se define el tipo de servicio
            sd.setType("Agente que realiza calce entre las ofertas de compra y de venta");

            // Se define la ontologia del servicio
            sd.addOntologies("DoblePunta");

            // Se define la ontologia del agente
            dfd.addOntologies("DoblePunta");

            // Se especifica el lenguaje que deben "hablar" los agentes que acceder al servicio
            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);

            // Se especifica el lenguaje del agente
            dfd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);

            // Se agrega el servicio a la lista de servicios
            dfd.addServices(sd);

            DFService.register(this, dfd);

        

        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);



            paquetesMinimos = new int[cantidadProductos];
            nombreAcciones = new String[cantidadProductos];
            
            /////////////////Para la prueba del canal
            /*archivosImpresion = new PrintWriter[cantidadProductos];
            
            Transadas       = new double[cantidadProductos];
            VolumenTransado = new double[cantidadProductos];
            VolumenOfertado = new double[cantidadProductos];
            
            enCirculacion = new double[cantidadProductos];
            enCirculacion[0] =  262036432;
            enCirculacion[1] =  567914624;
            enCirculacion[2] = 1107677894;*/
            
            
            
            for (int i=0; i<cantidadProductos; i++) {
                //BD = new ConexionBD("mysql");
                r = BD.consulta("SELECT paqueteMinimo, nombre FROM producto WHERE IDProducto="+i);
                if (r.next()) {
                    paquetesMinimos[i] = r.getInt(1);
                    nombreAcciones[i] = r.getString(2);
                    
                     /////////////////Para la prueba del canal
                   /* archivosImpresion[i] =  new PrintWriter(new FileWriter(nombreAcciones[i]+".txt",true));
                    
                    if(Repeticion==1)
                    {
                        //archivosImpresion[i].flush();
                        archivosImpresion[i].println("Periodo\t# acciones\t# operaciones");
                    }*/
                    //archivosImpresion[i].close();
                    
                    
                }
                //BD.cerrarConexion();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }catch (FIPAException fe) {
            fe.printStackTrace();
        }/*catch (IOException ex) {
                System.err.println(getLocalName()+" -> Archivo no encontrado .txt");
            }*/
        
        

        saldosAgentes = new float[numAgentes];
        accionesxagente = new int[numAgentes][cantidadProductos];
        
        VolumenOfertadoCompra = new float[cantidadProductos];
        VolumenOfertadoVenta = new float[cantidadProductos];
        VolumenNegociado = new float[cantidadProductos];
        
        IDOrdenesCompra = new ArrayList<HashSet<Integer>>();
        IDOrdenesVenta = new ArrayList<HashSet<Integer>>();
        
        IDOrdenesCompraExito = new ArrayList<HashSet<Integer>>();
        IDOrdenesVentaExito = new ArrayList<HashSet<Integer>>();
        
        
        FactorOrdenes = new float[cantidadProductos];
        FactorVolumen = new float[cantidadProductos];
        FactorFrecuencia = new float[cantidadProductos];

        SizeFOrdenes = new int[cantidadProductos];
        SizeFVolumen = new int[cantidadProductos];
        SizeFFrecuencia = new int[cantidadProductos];

        /*for (int i=0; i<numAgentes; i++) {
            saldosAgentes.add(new Float(-1));
            accionesxagente.add(i, new ArrayList<Integer>());
            for (int j=0; j<cantidadProductos; j++) {
                accionesxagente.get(i).add(new Integer(-1));
            }
        }*/
        
        for (int i=0; i<cantidadProductos; i++) {
            historicoSubastas.add(i, new ArrayList<InfoSubasta>());
            
            IDOrdenesCompra.add(i, new HashSet<Integer>());
            IDOrdenesVenta.add(i, new HashSet<Integer>());
            IDOrdenesCompraExito.add(i, new HashSet<Integer>());
            IDOrdenesVentaExito.add(i, new HashSet<Integer>());
            
            estrategiasXaccionAdaptativos.add(i, new HashMap<Integer,Integer>());
        }
        // Se carga el historial de cotizaciones
        try {
            InfoSubasta info = new InfoSubasta();
            for (int i=0; i<cantidadProductos; i++) {
                //BD = new ConexionBD("mysql");
                ResultSet r = null;
                r = BD.consulta("SELECT * FROM cotizacion WHERE IDProducto="+i+" AND fecha<1 AND repeticion = 0 ORDER BY fecha");
                if (r != null) {
                    while (r.next()) {
                        info = new InfoSubasta();
                        info.setIDAccion(r.getInt(1));
                        info.setPeriodo(r.getInt(2));
                        info.setPrecioInicio(r.getFloat(3));
                        info.setPrecioCierre(r.getFloat(4));
                        info.setPrecioMax(r.getFloat(5));
                        info.setPrecioMin(r.getFloat(6));
                        info.setPrecioPromedio(r.getFloat(7));
                        info.setCantidad(r.getInt(8));

                        historicoSubastas.get(i).add(info);
                    }
                }
                //BD.cerrarConexion();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        /*try {

            // Se construye la descripcion usada como plantilla para la busqueda
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription templateSD = new ServiceDescription();

            templateSD.setName("Mercado");
            template.addServices(templateSD);

            // Solo se desea recibir un resultado a lo sumo
            SearchConstraints SC = new SearchConstraints();
            SC.setMaxResults(new Long(1));

            DFAgentDescription[] resultados = DFService.search(this, template, SC);
            if (resultados.length == 1) {

                DFAgentDescription dfd = resultados[0];
                agenteMercado = dfd.getName();

                participantes[contadorParticipantes++] = agenteMercado;

            }
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }*/
            tiempoCallMarket = new long[cantidadProductos];
            tiempoCallMarketAlea = new long[cantidadProductos];

            Arrays.fill(tiempoCallMarket, tiempoCallMarketIntra);
            Arrays.fill(tiempoCallMarketAlea, tiempoAleatorioCallIntra);

        volatilidadActual = new float[cantidadProductos];
        volatilidadHistorico = new double[cantidadProductos];

        /////////////////////////////////////////////////////////////////////
        PrecioPorEncima = new float[cantidadProductos];
        PrecioPorDebajo = new float[cantidadProductos];
        porcentajeCanal = new float[cantidadProductos];
        PrecioReferencia= new float[cantidadProductos];
        PrecioFundamental= new float[cantidadProductos];
        
        //////////////////////////////////////////////////////////////
        
        
        
        
        //EstaEnCallMarket= new boolean[cantidadProductos];
        /*conceptosProductos= new ConceptoProducto[cantidadProductos];
        ConceptosLlenos = new boolean[cantidadProductos];*/

        RuedasXAccion = new float[cantidadProductos];
        CantidadesNegociadas = new float[cantidadProductos];
        cantidadesATransar = new long[cantidadProductos];

        try {

            for (int i=0; i<cantidadProductos; i++) {
               // BD = new ConexionBD("mysql");
                ResultSet r = null;
                int sumaMax=0;
                r = BD.consulta("SELECT cantidad/"+NrodeOperaciones[i]+" FROM cotizacion WHERE IDProducto="+i+" AND repeticion = 0 ORDER BY fecha desc");
                if (r != null) {
                    while (r.next() && sumaMax++<62)
                    {
                        CantidadesNegociadas[i] += r.getFloat(1);
                        //System.out.println(r.getFloat(1));
                    }

                }
                cantidadesATransar[i] =(long)(CantidadesNegociadas[i]);
                System.out.println(getLocalName()+" -> cantidadesATransar - "+i+" - "+cantidadesATransar[i]);
                //BD.cerrarConexion();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }



        /////ESTO ES SOLO PARA PRUEBAS, ES EL VOLUMEN PROMEDIO DE LA ACCION DE CHOCOLATES
        /*if(cantidadProductos !=0 )
        {   RuedasXAccion[0] = 20;
            NrodeOperaciones[0]=726;
            CantidadesNegociadas[0]= 1640338;

        }*/


        //sumatoriaOperaciones = 0;

        //ConceptoProducto producto = new ConceptoProducto();

        //new Libro().setVisible(true);

          HiloLibro HiloGrafica= new HiloLibro(cantidadProductos,tiempoSimulacion, tiempoFinal);//////////////////////////
          if(graficarLibro)
            HiloGrafica.start();


        /////////////////////////////////////////////////////////////////////

        for (int i=0; i<cantidadProductos; i++) {

            ////////////////////////////////////////////////////////////////////

           // sumatoriaOperaciones+=NrodeOperaciones[i];

            //////////////////////////////////////////////////////////////////////

            /*ofertasVenta.add(i, new ArrayList<OfertaVenta>());
            ofertasCompra.add(i, new ArrayList<OfertaCompra>());
            ofertasVentaTmp.add(i, new ArrayList<OfertaVenta>());
            ofertasCompraTmp.add(i, new ArrayList<OfertaCompra>());
            ofertasCallSession.add(i, new ArrayList<OfertaCallMarket>());*/

            //caducidadOfertasCompra.add(i, new ArrayList<Integer>());
            //caducidadOfertasVenta.add(i, new ArrayList<Integer>());

            /*producto.setIDProducto(i);
            producto.setDescripcion(" ");
            producto.setNombre(nombreAcciones[i]);
            conceptosProductos[i] = producto;
            ConceptosLlenos[i] = false;*/

            int n = historicoSubastas.get(i).size();

            //////////// VOLATILIDAD DEL HISTORICO
            float precios[] = new float[n];
            int k = 0;
            for (int j=n-1; j>=0; j--) {
                if (historicoSubastas.get(i).get(j).getPrecioCierre() > 0)
                    precios[k++] = historicoSubastas.get(i).get(j).getPrecioCierre();
                if (k >= n)
                    break;
            }

            double sumatoria = 0;
            // Sumatoria del precio promedio en todas los periodos anteriores de la accion i
            for (int j=0; j<n; j++) {
                sumatoria += precios[j];
            }
            // Promedio del precio promedio en todas las fechas de la accion i
            double promedio = sumatoria/n;

            sumatoria = 0;
            for (int j=0; j<n; j++) {
                sumatoria += Math.abs((precios[j] - promedio)/promedio);
            }
            // volatilidad: la tasa de variacion del precio promedio de la accion i con respecto a su media
            volatilidadHistorico[i] = sumatoria/n;
        }


          contenedor = getContainerController();

        subastadores = new AID[cantidadProductos];
        ControllerSubastadores= new AgentController[cantidadProductos];


        ////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////
                ////////////////////////////////////////////////////////////////
                ////aqui crear los agentes por cada accion/////////////////////
                ///////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////
                ///////////////////////////////////////////////////////////////
                
                 

                 try {
                for(int i=0;i<cantidadProductos;i++)
                {

                        args = new Object[2];
                        args[0] = numAgentes;
                        args[1] = i;
                        //System.out.println("subastador" + i+" - "+args[1]);
                        ControllerSubastadores[i] = contenedor.createNewAgent("subastador" + i, "agentes.AgenteSubastadorAccion", args);
                        ControllerSubastadores[i].start();
                        //((AgenteSubastadorAccion)ControllerSubastadores[i]).
                        //subastadores[i] = ControllerSubastadores[i];


                }
                    } catch (StaleProxyException ex) {
                        Logger.getLogger(AgenteAdministrador.class.getName()).log(Level.SEVERE, null, ex);
                    }



        // Se genera el comportamiento de registro
        RegistrarParticipantes RPBehaviour = new RegistrarParticipantes(this);
        contenedor = getContainerController();

        // Hasta que no haya al menos un comprador o un vendedor no inicia
        addBehaviour(RPBehaviour);
                       /*if (flusher==null) {
			flusher = new GCAgent( this, 1000);
			//addBehaviour( flusher);
		}*/
       // tiempoInicio = new GregorianCalendar().getTimeInMillis(); // Se guarda el tiempo de inicio
        HiloContinuar EstadoAdministrador = new HiloContinuar();
        EstadoAdministrador.start();
    }

    ///////////////////////////
    //////////////////////////
    /////////////////////////
    ////////////////////////

    class RegistrarParticipantes extends SimpleBehaviour {
        Agent agenteAdministrador;
        boolean finalizado = false;
        IniciarSubasta ISBehaviour;
        int subastadoresRegistrados = 0;


        public RegistrarParticipantes(Agent a)
        {
            super(a);
            agenteAdministrador = a;
            //ISBehaviour = new IniciarSubasta(a);
            HiloRegistros hiloTiempo = new HiloRegistros();
            hiloTiempo.start();
        }

        @SuppressWarnings("CallToThreadDumpStack")
        public void action()
        {
            PredicadoRegistrarAgenteBursatil registroAgente;
            ConceptoBursatil agente;

            try {
                //Espera a que llegue un mensaje de registro
                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));

                ACLMessage  mensajeEntrante = blockingReceive(mt);

                if(mensajeEntrante != null && mensajeEntrante.getPerformative() == ACLMessage.INFORM &&
                   mensajeEntrante.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE)) {
                    ContentElement registroFinaliza = getContentManager().extractContent(mensajeEntrante);
                    if (registroFinaliza instanceof PredicadoFinRegistro) {
                        finalizado = true;

                    }
                } else {
                    ACLMessage mensajeRespuesta = mensajeEntrante.createReply();

                    if(mensajeEntrante != null && mensajeEntrante.getPerformative()==ACLMessage.SUBSCRIBE &&
                       mensajeEntrante.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE) && !finalizado) {
                        // Se guarda en registroEntrante el contenido del mensaje
                        ContentElement registroEntrante = getContentManager().extractContent(mensajeEntrante);
                        ConceptoRegistro registro = new ConceptoRegistro();
                        PredicadoConfirmarRegistro confirmacionRegistro = new PredicadoConfirmarRegistro();

                        if (registroEntrante instanceof PredicadoRegistrarAgenteBursatil) {
                            Date fecha = new Date();
                            registroAgente = (PredicadoRegistrarAgenteBursatil)registroEntrante;
                            agente = (ConceptoBursatil)registroAgente.getBursatil();
                            // Se agrega al vector de participantes el AID
                            if(agente.getTipo()==-1)
                            {
                                subastadores[agente.getIDBursatil()] = mensajeEntrante.getSender();
                                registro.setIDBursatil(agente.getIDBursatil());
                                //System.out.println(getLocalName()+" -> mensajeEntrante.getSender() "+agente.getIDBursatil()+" - "+agente.getNombre());
                                subastadoresRegistrados++;
                            }
                            else
                            {
                                participantes[contadorParticipantes] = mensajeEntrante.getSender();

                                // Se ingresa el agente en la BD con su ID, el saldo y la fecha
                                //BD = new ConexionBD("mysql");
                                String res;
                                if(Repeticion == 1)
                                {
                                res = BD.insertarAgente(contadorParticipantes, agente.getNombre(), agente.getTipo());
                                res = BD.insertarSaldoXAgente(contadorParticipantes, 0, agente.getSaldo(),Repeticion);
                                //System.out.println(getLocalName()+" - "+agente.getNombre()+" - "+contadorParticipantes);
                                registro.setIDBursatil(contadorParticipantes++);
                                }
                                else
                                {
                                    ResultSet id = BD.consulta("Select idagente from bursatil where nombre = \""+agente.getNombre()+"\"");
                                    int identificador = -1;
                                         if (id.next())
                                            identificador = id.getInt(1);

                                    res = BD.insertarSaldoXAgente(identificador, 0, agente.getSaldo(),Repeticion);
                                    //System.out.println(getLocalName()+" -> "+agente.getNombre()+" - "+identificador);
                                    registro.setIDBursatil(identificador);

                                    contadorParticipantes++;
                                }
                                //BD.cerrarConexion();

                            }
                            registro.setFecha(fecha.toString());
                            confirmacionRegistro.setRegistro(registro);

                            mensajeRespuesta.setPerformative(ACLMessage.CONFIRM);
                            mensajeRespuesta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                            mensajeRespuesta.setLanguage(codec.getName());
                            mensajeRespuesta.setOntology(ontologia.getName());
                            getContentManager().fillContent(mensajeRespuesta, confirmacionRegistro);
                            send(mensajeRespuesta); //Se confirma el registro con el ID y la fecha en un PredicadoConfirmarRegistro
                        }

                    } else {
                    // Se manda un DISCONFIRM si el mensaje era erroneo
                        mensajeRespuesta.setPerformative(ACLMessage.DISCONFIRM);
                        mensajeRespuesta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                        send(mensajeRespuesta);
                    }
                }
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            } catch (SQLException ex) {
              Logger.getLogger(AgenteSubastadorAccion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public boolean done()
        {
        // Termina el comportamiento de recibir registros y llama a iniciar subasta
            if (finalizado) {

                for (int i=0; i<cantidadProductos; i++)
                {
                    int n = historicoSubastas.get(i).size();
                    //EstaEnCallMarket[i] = false;/////////////////////////////////////////////////////////////////////
                    PrecioReferencia[i] = historicoSubastas.get(i).get(n-1).getPrecioCierre();/////////////////////
                    //porcentajeCanal[i] = (float)0.025;////////////////////////////////////////////////////////////
                     porcentajeCanal[i] = (float)0.04;////////////////////////////////////////////////////////////
                      //porcentajeCanal[i] = (float)0.055;////////////////////////////////////////////////////////////
                      //porcentajeCanal[i] = (float)0.065;////////////////////////////////////////////////////////////
                       //porcentajeCanal[i] = (float)0.075;////////////////////////////////////////////////////////////
                    CanalNegociacion((float)1,i);///////////////////////////////////////////////////////////////
                    System.out.println(getLocalName()+" -> El porcentaje del canal es "+porcentajeCanal[i]+" -*************");
                    PrecioFundamental[i] = PrecioReferencia[i];
                   
                    //ProbabilidadAccion[i] = (float) ((objeto.nextFloat())*(0.25-0.05)+0.05);
                   // System.out.println(getLocalName() + " -> ProbabilidadAccion -"+i+"- "+ProbabilidadAccion[i]);
                    
                    if(graficarLibro)
                    {
                        onSimulation.TablaInformacion(nombreAcciones[i], 0,0, 0,true,i);
                        onSimulation.serie(PrecioPorEncima[i], PrecioPorDebajo[i], PrecioReferencia[i], PrecioReferencia[i], i);
                    }
                }



                //for(int i=0;i<subastadoresRegistrados;i++)
                //System.out.println(getLocalName()+" -> " + i +" - "+(ControllerSubastadores[i]).accionAManejar);
                System.out.println(getLocalName()+" -> Se registraron "+subastadoresRegistrados+" Subastadores");
                System.out.println(getLocalName()+" -> Se registraron "+contadorParticipantes+" Inversionistas");
                



                ISBehaviour = new IniciarSubasta(agenteAdministrador);
                addBehaviour(ISBehaviour);
                /////////Elimina los mensajes que no se han visto, para evitar que se acumulen
                //addBehaviour( flusher);
                /////////Elimina los mensajes que no se han visto, para evitar que se acumulen
            }
            return finalizado;
        }

        class HiloRegistros extends java.lang.Thread {
            //@SuppressWarnings("CallToThreadStartDuringObjectConstruction")
            public HiloRegistros()
            {
                //this.start();
            }
            @Override
            @SuppressWarnings("CallToThreadDumpStack")
            public void run()
            {
                try {
                    sleep(tiempoRegistros);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                }

                        try {
                        PredicadoFinRegistro finalizo = new PredicadoFinRegistro();
                        finalizo.setFinRegistro("finRegistros");

                        ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
                        mensaje.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                        mensaje.setLanguage(codec.getName());
                        mensaje.setOntology(ontologia.getName());
                        mensaje.setSender(getAID());
                        mensaje.addReceiver(getAID());
                        getContentManager().fillContent(mensaje, finalizo);
                        send(mensaje);
                    } catch (jade.content.lang.Codec.CodecException ce) {
                        ce.printStackTrace();
                    } catch (jade.content.onto.OntologyException oe) {
                        oe.printStackTrace();
                    }



            }
        }


    }


    class IniciarSubasta extends OneShotBehaviour {
        Agent agenteAdministrador;
        TerminarSubasta terminarBehaviour;

        boolean finalizado = false;

        @SuppressWarnings("element-type-mismatch")
        public IniciarSubasta(Agent a)
        {
            super(a);
            agenteAdministrador = a;
            //simulacionCorriendo = true;
        }

        @SuppressWarnings("CallToThreadDumpStack")
        public void action()
        {

            //if(simulacionActual> 1)
             //System.out.println(getLocalName()+" -> Iniciar Subasta - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));

            /*System.out.println(getLocalName()+" -> bufferCalces -> "+bufferCalces.size());
            System.out.println(getLocalName()+" -> bufferCalcesCM -> "+bufferCalcesCM.size());
            System.out.println(getLocalName()+" -> bufferOfertasCompra -> "+bufferOfertasCompra.size());
            System.out.println(getLocalName()+" -> bufferOfertasVenta -> "+bufferOfertasVenta.size());*/
            bufferCalces.clear();
            bufferCalcesCM.clear();
            bufferOfertasCompra.clear();
            bufferOfertasVenta.clear();

            Arrays.fill(tiempoCallMarket, tiempoCallMarketIntra);
            Arrays.fill(tiempoCallMarketAlea, tiempoAleatorioCallIntra);



             //if(simulacionActual> 1)
            //System.out.println(getLocalName()+" Iniciar Subasta Fin- "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));


            System.out.println("Periodo: ------------"+simulacionActual+"-----------------"+Repeticion);
            System.out.println("-------------------------------------------------");
            if (simulacionActual == 1) {
                //BD = new ConexionBD("mysql");
                for (int i=0; i<numAgentes; i++) {
                    if (participantes[i] != null) {
                        for (int j=0; j<cantidadProductos; j++) {

                            //System.out.println(accionesxagente.get(i).get(j));
                            //String res = BD.insertarAccionesXAgente(i, j, 0, accionesxagente.get(i).get(j),Repeticion);
                            String res = BD.insertarAccionesXAgente(i, j, 0, accionesxagente[i][j],Repeticion);

                        }
                    }
                }
                //BD.cerrarConexion();
            }
            else
            {
                System.out.println("Periodo: ------------"+simulacionActual+"-----------------"+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                if(graficarLibro)
                onSimulation.agregarEtiquetaDia("Day - "+simulacionActual);
                
                if(participanFundamentales)
                {
                /////////////////////////////////////////////////////
                //////////////CALCULAR EL PRECIO FUNDAMENTAL/////////
                /////////////////////////////////////////////////////
                 for (int j=0; j<cantidadProductos; j++) {
                     
                     //el valor del factor, es una aleatorio con una distribución normal con mu = 0 y sigma^2 igual a 0.1
                     double factor = aleatoriosNormal.nextGaussian()*0.1;
                     double precioAlpha = PrecioFundamental[j]*Math.exp(factor);
                     float aleatorio = objeto.nextFloat();
                     //System.out.println("******************* "+aleatorio+" - "+ProbabilidadAccion[j]);
                     if(aleatorio<ProbabilidadAccion[j])
                     {
                         PrecioFundamental[j] = (float) precioAlpha;
                         
                     }
                     //System.out.println(getLocalName()+" -> PrecioFundamental "+j+" - "+PrecioFundamental[j]);
                    // if(precioAlpha)
                     //PrecioFundamental[j] = 
                 }
                }
                
                
            }

            if (nRegDinamicos > 0) {
                //BD = new ConexionBD("mysql");
                for (int i=0; i<nRegDinamicos; i++) {
                    int agente = registrosDinamicos[i];
                    if (participantes[agente] != null) {
                        for (int j=0; j<cantidadProductos; j++) {
                            String res;
                            //res = BD.insertarAccionesXAgente(agente, j, 0, accionesxagente.get(agente-1).get(j),Repeticion);
                            res = BD.insertarAccionesXAgente(agente, j, 0, accionesxagente[agente-1][j],Repeticion);
                        }
                    }
                }
                //BD.cerrarConexion();
            }

            nRegDinamicos = 0;
            try {
                //System.out.println(getLocalName()+" -> INICIO MENSAJE INICIO DE SUBASTA");
                PredicadoInicioSubasta iniciaSubasta = new PredicadoInicioSubasta();
                iniciaSubasta.setInicioSubasta("Inicio de subasta");
                ACLMessage mensajeInicio = new ACLMessage(ACLMessage.INFORM);
                mensajeInicio.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeInicio.setOntology(ontologia.getName());
                mensajeInicio.setLanguage(codec.getName());
                getContentManager().fillContent(mensajeInicio, iniciaSubasta);

                

                for (int i=0; i<contadorParticipantes;i++) {
                    if (participantes[i] != null)
                        mensajeInicio.addReceiver(participantes[i]);
                }

                //System.out.println(getLocalName()+" -> subastadores.length "+subastadores.length);
                for (int i=0; i<subastadores.length;i++) {
                        mensajeInicio.addReceiver(subastadores[i]);
                }


                send(mensajeInicio);
                System.out.println(getLocalName()+" -> FIN DE ENVIAR MENSAJE INICIO DE SUBASTA");
                //tiempoInicio = new GregorianCalendar().getTimeInMillis(); // Se calcula el tiempo de inicio de subasta
                horaInicial = new Date();


            } catch (Exception e) {
                e.printStackTrace();
            }

            terminarBehaviour = new TerminarSubasta(agenteAdministrador);
            addBehaviour(terminarBehaviour);

            //HiloIngresarOfertasyCalces ingresar = new HiloIngresarOfertasyCalces();
            //ingresar.start();

            indiceActualCalce = 0;
            indiceActualCalceCM = 0;
            indiceActualOC = 0;
            indiceActualOV = 0;

            //BD = new ConexionBD("mysql");
           // finalizado = true;

            System.gc();
        }

        /*public boolean done()
        {
            return finalizado;
        }*/
    }

    class TerminarSubasta extends SimpleBehaviour {
        Agent agenteAdministrador;

        boolean finalizado = false;
        boolean[] finCallMarket = new boolean[subastadores.length];

        @SuppressWarnings("element-type-mismatch")
        public TerminarSubasta(Agent a)
        {
            super(a);
            agenteAdministrador = a;

            Arrays.fill(finCallMarket, false);
            
        }

        @SuppressWarnings("CallToThreadDumpStack")
        public void action()
        {
            
            try{

                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));
                ACLMessage  mensajeGeneral = blockingReceive(mt);

               // mostrarMensaje(mensajeGeneral,"dsadsa");
                ContentElement mensajeEntrante = getContentManager().extractContent(mensajeGeneral);

                if (mensajeGeneral != null && mensajeGeneral.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE))
                {
                    if(  mensajeGeneral.getPerformative() == ACLMessage.INFORM )
                    {
                        if (mensajeEntrante instanceof PredicadoFinCallMarket)
                        {
                            //mostrarMensaje(mensajeGeneral,getLocalName());

                            int idproducto = ((PredicadoFinCallMarket)mensajeEntrante).getProducto().getIDProducto();

                            //System.out.println(getLocalName()+" --- Se ha Finalizado Call Market --- "+nombre+" ----");
                            //System.out.println(getLocalName()+" -> Llego mensaje de fin CM  - "+idproducto);
                            if(!finCallMarket[idproducto])
                                finCallMarket[idproducto]=true;
                            

                            int contador = 0;
                            for(int i=0;i<finCallMarket.length;i++)
                                if(finCallMarket[i])contador++;

                            //System.out.println(getLocalName()+" -> Contador - "+contador);

                            if(contador==finCallMarket.length)
                            {
                                finalizado = true;
                                //FinDeSubastaAgentes();
                                System.out.println(getLocalName()+" -> Va a entrar al metodo done - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));

                            }


                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }

        public boolean done()
        {
            if(finalizado)
            {

                //System.out.println(getLocalName()+" -> Entro al metodo de done - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));

                /*MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));

                 ACLMessage mensaje = myAgent.receive(mt);
                while(mensaje!=null)
                {
                    mostrarMensaje(mensaje,getLocalName());
                     mensaje = myAgent.receive(mt);

                }*/

                int simulacion = simulacionActual;

                if (simulacionActual < cantidadSimulaciones) {

                    simulacionActual++;

                    IniciarSubasta ISBehaviou = new IniciarSubasta(agenteAdministrador);
                    addBehaviour(ISBehaviou);

                    //System.gc();

                    //removeBehaviour(this);

                    //tiempoInicio = new GregorianCalendar().getTimeInMillis(); // Se guarda el tiempo de inicio
                }


                System.out.println(getLocalName() + " -> Va a salir del metodo de done - " + (((new Date()).getTime() - horaInicial.getTime()) + tiempoCallMarketIntra + tiempoAleatorioCallIntra));

                 String Scanal = "INSERT INTO cambiocanal VALUES";
                
                    for(int i = 0;i<cantidadProductos;i++)
                    {
                        
                        float volumen = 0;
                        
                        if(VolumenOfertadoCompra[i]!=0 || VolumenOfertadoVenta[i]!=0)
                        {    volumen = VolumenNegociado[i]/(VolumenOfertadoCompra[i]+VolumenOfertadoVenta[i]);
                             FactorVolumen[i] += volumen;
                             SizeFVolumen[i]++;
                             System.out.println("******Volumen****** de "+i+"\t"+volumen);
                        }
                        
                        //float exitoC = (IDOrdenesCompraExito.get(i).size()/(float)IDOrdenesCompra.get(i).size()); 
                        //float exitoV = (IDOrdenesVentaExito.get(i).size()/(float)IDOrdenesVenta.get(i).size()); 

                        if(!IDOrdenesCompra.get(i).isEmpty() || !IDOrdenesVenta.get(i).isEmpty())
                        {
                            float exitoTotal = (IDOrdenesCompraExito.get(i).size()+IDOrdenesVentaExito.get(i).size())/(float)(IDOrdenesCompra.get(i).size()+IDOrdenesVenta.get(i).size());
                             FactorOrdenes[i] += exitoTotal;
                             SizeFOrdenes[i]++;
                             System.out.println("******ExitoTotal****** de "+i+"\t"+exitoTotal);
                        }
                        
                        if(VolumenNegociado[i]!=0)
                        {SizeFFrecuencia[i]++; 
                         System.out.println("******FrecuenciaActual****** de "+i+"\t"+SizeFFrecuencia[i]);
                        }

                        //System.err.println("************** "+(tiempoModificacionCanal+((Repeticion-1)*10)));
                        if(simulacion%(tiempoModificacionCanal)==0)
                        {
                            float Dvol = (FactorVolumen[i]/SizeFVolumen[i]);
                            float Dord = (FactorOrdenes[i]/SizeFOrdenes[i]);
                            float Dfre = SizeFFrecuencia[i]/(float)(tiempoModificacionCanal);
                            float resultadoCanal = Dvol*Dord*Dfre;
                             double suma = (Math.round(8*resultadoCanal))/2;
                            //porcentajeCanal[i]=(2.5f+suma)/100;
                             porcentajeCanal[i]=(2.5f+(float)suma)/100;
                             
                             if(simulacion==(tiempoModificacionCanal) && i ==0)
                            System.err.println("*************************\nPeriodoActual\tAccion\tVolumen\tFrecuencia\tOrdenes\tResultado\tNuevo Canal");
                             
                            System.err.println(simulacion+"\t"+i+"\t"+(Dvol)+"\t"+(Dfre)+"\t"+(Dord)+"\t"+resultadoCanal+"\t"+( porcentajeCanal[i])); 
                            //System.out.println("******Resultado de "+i+"\t"+(FactorVolumen[i]/SizeFVolumen[i])*(FactorOrdenes[i]/SizeFOrdenes[i])*(tiempoModificacionCanal/SizeFFrecuencia[i]));
                            FactorVolumen[i] = 0;
                            SizeFVolumen[i] = 0;
                            FactorOrdenes[i] = 0;
                            SizeFOrdenes[i] = 0;
                            SizeFFrecuencia[i] = 0;
                            
                            
                           

                            Scanal += "( '" + i + "','" + simulacion + "','" + Dvol + "','" + Dfre + "','" + Dord + "','" + resultadoCanal + "','" + porcentajeCanal[i] + "','" + Repeticion +"'),";


                            
                           
                           // System.err.println("******Nuevo Canal "+i+"\t"+porcentajeCanal[i]);
                            
                        }
                    }
                    
                    if(simulacion%(tiempoModificacionCanal)==0){
                        
                        if (Scanal.indexOf("'") != -1) {
                                Scanal = Scanal.substring(0, Scanal.length() - 1);

                                //System.out.println(getLocalName()+" -> INSERT INTO ofertacompra VALUES");

                                BD.ejecutar(Scanal, "PruebaCanal");
                              //  System.err.println(getLocalName()+" "+Scanal);

                            }
                    }
                
                 
                
                
                if (simulacion == cantidadSimulaciones) {
                    BD.cerrarConexion();
                    
                }


                //System.gc();

                HiloIngresarOfertasyCalces ingresar = new HiloIngresarOfertasyCalces(bufferOfertasCompra, bufferOfertasVenta, bufferCalces, bufferCalcesCM, accionesxagente, saldosAgentes, estrategiasXaccionAdaptativos, simulacion);
                ingresar.parametrosdatos(datosPromediosMovilesCorto, datosPromediosMovilesLargo,
                        datosRSI, datosVHF, datosPromediosMovilesSimple,
                        datosPromediosMovilesTripleCorto, datosPromediosMovilesTripleMedio,
                        datosPromediosMovilesTripleLargo, datosMACD, datosMACDLineaSenal,
                        datosROC, datosMomento);
                ingresar.parametrosagentes(agentesTecnicosPMD, agentesTecnicosRSI, agentesTecnicosVHF,
                        agentesTecnicosPMS, agentesTecnicosPMT, agentesTecnicosMACD,
                        agentesTecnicosROC, agentesTecnicosMomento);
                
                 
                 
                ingresar.parametrosprecios(PrecioReferencia, PrecioFundamental);
                ingresar.start();
                //BD.cerrarConexion();
                /*if (simulacionActual < cantidadSimulaciones) {

                    simulacionActual++;
                    
                    IniciarSubasta ISBehaviou = new IniciarSubasta(agenteAdministrador);
                    addBehaviour(ISBehaviou);

                    System.gc();

                    //removeBehaviour(this);

                    //tiempoInicio = new GregorianCalendar().getTimeInMillis(); // Se guarda el tiempo de inicio
                }
                else   BD.cerrarConexion();*/
                /*else
                {
                    try {
                        pararSub = new FileWriter("Continuar.txt");

                    paraSubastador = new PrintWriter(pararSub);
                    paraSubastador.println(0);
                    paraSubastador.close();
                      File file = new File("Continuar.txt");
                      file.delete();

                        AgenteSubastador.super.getContainerController().kill();

                    } catch (StaleProxyException ex) {
                        Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
                    }catch (IOException ex) {
                        Logger.getLogger(AgenteSubastadorAccion.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }*/

            }
            return finalizado;
        }
    }


        @SuppressWarnings("CallToThreadDumpStack")
    private void FinDeSubastaAgentes()
    {
    try {
                    // Se envia un mensaje de fin de subasta a todos los participantes
                    System.out.println(getLocalName()+"-> INICIO MENSAJE FIN DE SUBASTA agentes- "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                    PredicadoFinSubasta finalizoSubasta = new PredicadoFinSubasta();
                    finalizoSubasta.setFinSubasta("Fin de la Subasta");
                    ACLMessage mensajeFin = new ACLMessage(ACLMessage.INFORM);
                    mensajeFin.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                    mensajeFin.setLanguage(codec.getName());
                    mensajeFin.setOntology(ontologia.getName());

                    getContentManager().fillContent(mensajeFin, finalizoSubasta);

                    for (int i=0; i<contadorParticipantes;i++) {
                        if (participantes[i] != null)
                            mensajeFin.addReceiver(participantes[i]);
                    }
                    send(mensajeFin);
                    System.out.println(getLocalName()+"-> FIN MENSAJE FIN DE SUBASTA agentes- "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                } catch (jade.content.lang.Codec.CodecException ce) {
                    ce.printStackTrace();
                } catch (jade.content.onto.OntologyException oe) {
                    oe.printStackTrace();
                }
    }

        @Override
        protected void takeDown()
       {



          try {
              for (int i = 0; i < ControllerSubastadores.length; i++)
                ControllerSubastadores[i].kill();

                DFService.deregister(this);
                System.out.println(this.getLocalName() + " -> He muerto y ya no estoy registrado");
            } catch (Exception e) { }


       }



    ////////////////////////////////
    public static void CanalNegociacion(float ValorAjuste, int idAccion)
   {
       PrecioPorEncima[idAccion] = PrecioReferencia[idAccion]+PrecioReferencia[idAccion]*porcentajeCanal[idAccion]*ValorAjuste;
       PrecioPorDebajo[idAccion] = PrecioReferencia[idAccion]-PrecioReferencia[idAccion]*porcentajeCanal[idAccion]*ValorAjuste;

       System.out.println("-------------------------------------------------");
       System.out.println("IDAccion "+idAccion+"\nCanal por encima "+PrecioPorEncima[idAccion]);
        System.out.println("Precio Referencia "+PrecioReferencia[idAccion]);
        System.out.println("Canal por debajo "+PrecioPorDebajo[idAccion]);
        System.out.println("-------------------------------------------------");
   }

class HiloLibro extends java.lang.Thread {
        int cAcciones;
        long tiempo;
        long tiempoFi;
        public HiloLibro(int caAccion, long tiempoSim, long tiempoFin)
        {
             //HiloEjecutarCallMarket tiempoOfertas = new HiloEjecutarCallMarket();
            cAcciones = caAccion;
            tiempo = tiempoSim;
            tiempoFi = tiempoFin;


        }

        @Override
        @SuppressWarnings("CallToThreadDumpStack")
        public void run()
        {
            try {


                //System.out.println("Entro al Hilo para el libro ");
                sleep(1000);

            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            if(graficarLibro)
            {
                onSimulation = new Libro2(cAcciones,tiempo,tiempoFi);
                onSimulation.setVisible(true);
            }


        }
    }

    class HiloContinuar extends java.lang.Thread
    {
        public HiloContinuar()
        {
           // this.start();

        }

        @Override
        @SuppressWarnings({"SleepWhileHoldingLock", "CallToThreadDumpStack"})
        public void run()
        {
            //String leer;
            int accion = -1;
            int continuar = 0;
            try {
                System.out.println("Esperando 1 segundo - Hilo para el control del administrador");
                sleep(1000);
                System.out.println("Pasaron 1 segundos - Hilo para el control del administrador");

                while (simulacionActual < cantidadSimulaciones) {
                    sleep(1000);
                    /*Scanner reader = new Scanner(new File("Continuar.txt"));
                    if (reader.hasNextLine()) {
                        leer = reader.nextLine();

                        if(leer.length() != 0)
                        accion = Integer.parseInt(leer);
                    }*/
                    accion = estadoActual;

                    if (accion == 1 && continuar ==0) {
                        AgenteAdministrador.super.doSuspend();
                        for(int i=0;i<ControllerSubastadores.length;i++)
                            ControllerSubastadores[i].suspend();
                        continuar++;
                        System.out.println("Administrador -- Me suspendieron ");
                    }

                    if(accion == 0 && continuar == 1)
                    {
                        AgenteAdministrador.super.doActivate();
                        for(int i=0;i<ControllerSubastadores.length;i++)
                            ControllerSubastadores[i].activate();
                        continuar--;
                        System.out.println("Administrador -- Estaba suspendido, voy a continuar ");
                    }

                    if(accion == 2)
                    {
                    	            /*pararSub = new FileWriter("Continuar.txt");
                                    paraSubastador = new PrintWriter(pararSub);
                                    paraSubastador.println(0);
                                    paraSubastador.close();
                                    File file = new File("Continuar.txt");
                                    file.delete();*/
                       try {
                            AgenteAdministrador.super.getContainerController().kill();

                        } catch (StaleProxyException ex) {
                            Logger.getLogger(AgenteAdministrador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            } catch (StaleProxyException ex) {
                Logger.getLogger(AgenteAdministrador.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }/* catch (IOException ex) {

            }*/
        }
    }

   


    class HiloIngresarOfertasyCalces extends java.lang.Thread
    {
        int simulacion = -1;
        Date horaInicial = new Date();
       
        ArrayList<InfoOfertaCompra> bufferOfertasCompra;
        ArrayList<InfoOfertaVenta> bufferOfertasVenta;
        ArrayList<InfoCalce> bufferCalces;
        ArrayList<InfoCalce> bufferCalcesCM;

        int[][] accionesxagente;
        float[] saldosAgentes;
        
        float[] preciosRefere;
        float[] preciosFundam;

         ArrayList<ArrayList<Float>> datosPromediosMovilesCorto;
         ArrayList<ArrayList<Float>> datosPromediosMovilesLargo;
         ArrayList<ArrayList<Float>> datosRSI;
         ArrayList<ArrayList<Float>> datosVHF;
         ArrayList<ArrayList<Float>> datosPromediosMovilesSimple;
         ArrayList<ArrayList<Float>> datosPromediosMovilesTripleCorto;
         ArrayList<ArrayList<Float>> datosPromediosMovilesTripleMedio;
         ArrayList<ArrayList<Float>> datosPromediosMovilesTripleLargo;
         ArrayList<ArrayList<Float>> datosMACD;
         ArrayList<ArrayList<Float>> datosMACDLineaSenal;
         ArrayList<ArrayList<Float>> datosROC;
         ArrayList<ArrayList<Float>> datosMomento;

         ArrayList<Integer> agentesTecnicosPMD = new ArrayList<Integer>();
         ArrayList<Integer> agentesTecnicosRSI = new ArrayList<Integer>();
         ArrayList<Integer> agentesTecnicosVHF = new ArrayList<Integer>();
         ArrayList<Integer> agentesTecnicosPMS = new ArrayList<Integer>();
         ArrayList<Integer> agentesTecnicosPMT = new ArrayList<Integer>();
         ArrayList<Integer> agentesTecnicosMACD = new ArrayList<Integer>();
         ArrayList<Integer> agentesTecnicosROC = new ArrayList<Integer>();
         ArrayList<Integer> agentesTecnicosMomento = new ArrayList<Integer>();

        ArrayList<HashMap<Integer,Integer>> estrategiasAdaptativos;
         
         
        /*void nuevoCanal(int sim)
        {
            for(int i=0;i<cantidadProductos;i++)
            {
                int tamHis = historicoSubastas.get(i).size();
                double transa = historicoSubastas.get(i).get(tamHis-1).getCantidad();
                Transadas[i] += transa;
                VolumenTransado[i] += historicoSubastas.get(i).get(tamHis-1).getPrecioPromedio()*transa;
                //System.out.println(getLocalName()+" - Tamanno historico "+historicoSubastas.get(i).size()+" simActual "+sim);
                //System.out.println(getLocalName()+" - Transados - "+historicoSubastas.get(i).get(tamHis-1).getCantidad());
                //System.out.println(getLocalName()+" - Volumen - "+historicoSubastas.get(i).get(tamHis-1).getPrecioPromedio()*transa);
                
                if(sim%tiempoHistorico==0)
                {
                    
                    VolumenOfertado[i] = 0;
                    ResultSet volumenes = null;
                      try {                 
                        volumenes = BDA.consulta("SELECT sum(precioVenta*cantidad) FROM ofertaventa where repeticion = "+Repeticion+" and idproducto = "+i+" and fecha >= "+(sim+1-tiempoHistorico)+" and fecha <= "+sim);
                        //System.out.println("SELECT sum(precioVenta*cantidad) FROM ofertaventa where repeticion = "+Repeticion+" and idproducto = "+i+" and fecha >= "+(sim+1-tiempoHistorico)+" and fecha <= "+sim);
                        if (volumenes.next()) {
                        
                            VolumenOfertado[i]+=volumenes.getDouble(1);
                        } 

                        
                        volumenes = BDA.consulta("SELECT sum(precioCompra*cantidad) FROM ofertacompra where repeticion = "+Repeticion+" and idproducto = "+i+" and fecha >= "+(sim+1-tiempoHistorico)+" and fecha <= "+sim);
                       // System.out.println("SELECT sum(precioCompra*cantidad) FROM ofertacompra where repeticion = "+Repeticion+" and idproducto = "+i+" and fecha >= "+(sim+1-tiempoHistorico)+" and fecha <= "+sim);
                        if (volumenes.next()) {
                        
                            VolumenOfertado[i]+=volumenes.getDouble(1);
                        } 
                        

                      }catch (SQLException ex) {
                            Logger.getLogger(AgenteAdministrador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        //BD.cerrarConexion();
                
                      double acc = (Transadas[i]/enCirculacion[i])*100;
                      double vol = ((2*VolumenTransado[i])/VolumenOfertado[i])*100;
                      
                     System.out.println(getLocalName()+" PRUEBA DEL CANAL - "+sim+"\t"+acc+"\t"+vol+"\t"+acc*vol); 
                      archivosImpresion[i].println(sim+"\t"+acc+"\t"+vol+"\t"+acc*vol);
                       archivosImpresion[i].flush();
     
                   
                    
                     Transadas[i]=0;
                     VolumenTransado[i]=0;
                }
                
                
                
                if(sim==cantidadSimulaciones)
                    archivosImpresion[i].close();
            }
            
        }*/

        public HiloIngresarOfertasyCalces(ArrayList<InfoOfertaCompra> bufferOfertasC, ArrayList<InfoOfertaVenta> bufferOfertasV, 
                                            ArrayList<InfoCalce> bufferC, ArrayList<InfoCalce> bufferCCM,int[][] accionesxagente,
                                            float[] saldosAgentes, ArrayList<HashMap<Integer,Integer>> estrate, int sim)
        {
           // this.start();
            bufferOfertasCompra = new ArrayList<InfoOfertaCompra>(bufferOfertasC);
            bufferOfertasVenta = new ArrayList<InfoOfertaVenta>(bufferOfertasV);
            bufferCalces = new ArrayList<InfoCalce>(bufferC);
            bufferCalcesCM =  new ArrayList<InfoCalce>(bufferCCM);
            this.accionesxagente = accionesxagente.clone();
            this.saldosAgentes = saldosAgentes.clone();
            this.estrategiasAdaptativos = new ArrayList<HashMap<Integer,Integer>>(estrate);
            simulacion = sim;
             //System.out.println(getLocalName()+" Se ha creado el hilo - "+this.getId());

        }

        public void parametrosdatos(ArrayList<ArrayList<Float>> datosPromediosMovilesCorto,ArrayList<ArrayList<Float>> datosPromediosMovilesLargo,
                                    ArrayList<ArrayList<Float>> datosRSI, ArrayList<ArrayList<Float>> datosVHF, ArrayList<ArrayList<Float>> datosPromediosMovilesSimple,
                                    ArrayList<ArrayList<Float>> datosPromediosMovilesTripleCorto, ArrayList<ArrayList<Float>> datosPromediosMovilesTripleMedio,
                                    ArrayList<ArrayList<Float>> datosPromediosMovilesTripleLargo, ArrayList<ArrayList<Float>> datosMACD, ArrayList<ArrayList<Float>> datosMACDLineaSenal,
                                    ArrayList<ArrayList<Float>> datosROC, ArrayList<ArrayList<Float>> datosMomento)
        {
             this.datosPromediosMovilesCorto = new ArrayList<ArrayList<Float>>(datosPromediosMovilesCorto);
             this.datosPromediosMovilesLargo = new ArrayList<ArrayList<Float>>(datosPromediosMovilesLargo);
             this.datosRSI = new ArrayList<ArrayList<Float>>(datosRSI);
             this.datosVHF = new ArrayList<ArrayList<Float>>(datosVHF);
             this.datosPromediosMovilesSimple = new ArrayList<ArrayList<Float>>(datosPromediosMovilesSimple);
             this.datosPromediosMovilesTripleCorto = new ArrayList<ArrayList<Float>>(datosPromediosMovilesTripleCorto);
             this.datosPromediosMovilesTripleMedio = new ArrayList<ArrayList<Float>>(datosPromediosMovilesTripleMedio);
             this.datosPromediosMovilesTripleLargo = new ArrayList<ArrayList<Float>>(datosPromediosMovilesTripleLargo);
             this.datosMACD = new ArrayList<ArrayList<Float>>(datosMACD);
             this.datosMACDLineaSenal = new ArrayList<ArrayList<Float>>(datosMACDLineaSenal);
             this.datosROC = new ArrayList<ArrayList<Float>>(datosROC);
             this.datosMomento = new ArrayList<ArrayList<Float>>(datosMomento);
            

        }

        public void parametrosagentes(ArrayList<Integer> agentesTecnicosPMD, ArrayList<Integer> agentesTecnicosRSI, ArrayList<Integer> agentesTecnicosVHF,
                                    ArrayList<Integer> agentesTecnicosPMS,ArrayList<Integer> agentesTecnicosPMT, ArrayList<Integer> agentesTecnicosMACD,
                                    ArrayList<Integer> agentesTecnicosROC,ArrayList<Integer> agentesTecnicosMomento)
        {
             this.agentesTecnicosPMD = new ArrayList<Integer>(agentesTecnicosPMD);
             this.agentesTecnicosRSI = new ArrayList<Integer>(agentesTecnicosRSI);
             this.agentesTecnicosVHF = new ArrayList<Integer>(agentesTecnicosVHF);
             this.agentesTecnicosPMS = new ArrayList<Integer>(agentesTecnicosPMS);
             this.agentesTecnicosPMT = new ArrayList<Integer>(agentesTecnicosPMT);
             this.agentesTecnicosMACD = new ArrayList<Integer>(agentesTecnicosMACD);
             this.agentesTecnicosROC = new ArrayList<Integer>(agentesTecnicosROC);
             this.agentesTecnicosMomento = new ArrayList<Integer>(agentesTecnicosMomento);
        }
        
        public void parametrosprecios(float[] x, float[] y)
        {
            preciosRefere = new float[x.length];
            for(int i=0;i<x.length;i++)
                preciosRefere[i] = x[i];
            
            if(participanFundamentales)
            {
            preciosFundam = new float[y.length];
            for(int i=0;i<y.length;i++)
                preciosFundam[i] = y[i];
            }
           // System.out.println(getLocalName()+ "-> PrecioReferencia "+x[0]+" -  "+simulacionActual);
            
        }

        @Override
        protected void finalize() throws Throwable
        {
              //do finalization here
              super.finalize(); //not necessary if extending Object.
              //System.out.println(getLocalName()+ " -> Destructor del hilo -  "+this.getId());
        }

        @Override
        @SuppressWarnings({"SleepWhileHoldingLock", "CallToThreadDumpStack"})
        public void run()
        {
            
             // System.out.println(getLocalName() + " -> Beneficios Seguro -> "+ganancia);
                // Se ingresan las ofertas de compra a la BD
                 //System.out.println(getLocalName()+" -> Va ingresar ofertas - "+((new Date()).getTime()-horaInicial.getTime()));
                //BD = new ConexionBD("mysql");
                String res;
                String consulta;

                //BDA = new ConexionBD("mysql");
                consulta = "INSERT INTO ofertacompra VALUES";
                for (int indiceActualOC=0; indiceActualOC<bufferOfertasCompra.size(); indiceActualOC++) {
                    //res = BD.insertarOfertaCompra(bufferOfertasCompra.get(indiceActualOC).getIDOfertaCompra(), bufferOfertasCompra.get(indiceActualOC).getIDAgente(), bufferOfertasCompra.get(indiceActualOC).getIDProducto(),
                      //      bufferOfertasCompra.get(indiceActualOC).getPrecioCompra(), bufferOfertasCompra.get(indiceActualOC).getFecha(), bufferOfertasCompra.get(indiceActualOC).getCantidad(),Repeticion);
                    consulta+="( '"+bufferOfertasCompra.get(indiceActualOC).getIDOfertaCompra()+"','"+bufferOfertasCompra.get(indiceActualOC).getIDAgente()+"','"+bufferOfertasCompra.get(indiceActualOC).getIDProducto()+"','"+bufferOfertasCompra.get(indiceActualOC).getPrecioCompra()+"','"+bufferOfertasCompra.get(indiceActualOC).getFecha()+"','"+bufferOfertasCompra.get(indiceActualOC).getCantidad()+"','"+Repeticion+"'),";
                }

                //System.out.println(getLocalName()+" -> Ya creo la consulta - "+((new Date()).getTime()-horaInicial.getTime()));



		if(consulta.indexOf("'")!=-1)
                {
                consulta = consulta.substring(0, consulta.length()-1);


                //System.out.println(getLocalName()+" -> INSERT INTO ofertacompra VALUES");

                BDA.ejecutar(consulta, "Prueba");

                }
                //System.out.println(getLocalName()+" -> Termino de ingresar ofertascompra - "+((new Date()).getTime()-horaInicial.getTime()));


                consulta = "INSERT INTO ofertaventa VALUES";
                //BD.cerrarConexion();
                // Se ingresa las ofertas de venta a la BD
                //BD = new ConexionBD("mysql");
                for (int indiceActualOV=0; indiceActualOV<bufferOfertasVenta.size(); indiceActualOV++) {
                    //res = BD.insertarOfertaVenta(bufferOfertasVenta.get(indiceActualOV).getIDOfertaVenta(), bufferOfertasVenta.get(indiceActualOV).getIDAgente(), bufferOfertasVenta.get(indiceActualOV).getIDProducto(),
                      //      bufferOfertasVenta.get(indiceActualOV).getPrecioVenta(), bufferOfertasVenta.get(indiceActualOV).getFecha(), bufferOfertasVenta.get(indiceActualOV).getCantidad(), Repeticion);
                    consulta+="( '"+bufferOfertasVenta.get(indiceActualOV).getIDOfertaVenta()+"','"+bufferOfertasVenta.get(indiceActualOV).getIDAgente()+"','"+bufferOfertasVenta.get(indiceActualOV).getIDProducto()+"','"+bufferOfertasVenta.get(indiceActualOV).getPrecioVenta()+"','"+bufferOfertasVenta.get(indiceActualOV).getFecha()+"','"+bufferOfertasVenta.get(indiceActualOV).getCantidad()+"','"+Repeticion+"'),";
                }

               // System.out.println(getLocalName()+" -> Ya creo la consulta - "+((new Date()).getTime()-horaInicial.getTime()));



		if(consulta.indexOf("'")!=-1)
                {
                consulta = consulta.substring(0, consulta.length()-1);


               // System.out.println(getLocalName()+" -> INSERT INTO ofertaventa VALUES");

                BDA.ejecutar(consulta, "Prueba");

                }
                //System.out.println(getLocalName()+" -> Termino de ingresar ofertasventa - "+((new Date()).getTime()-horaInicial.getTime()));


                consulta = "INSERT INTO calce (IDOfertaCompra, IDOfertaVenta, IDProducto, precioCompra, precioVenta, fecha, cantidad, repeticion) VALUES";

                //BD.cerrarConexion();
                // Se ingresan los calces realizados en el período que terminó a la BD
                //BD = new ConexionBD("mysql");
                for (int indiceActualCalce=0; indiceActualCalce<bufferCalces.size(); indiceActualCalce++) {
                   // res = BD.insertarCalce(/*bufferCalces.get(i).getIDCalce(),*/ bufferCalces.get(indiceActualCalce).getIDOfertaCompra(), bufferCalces.get(indiceActualCalce).getIDOfertaVenta(),
                     //       bufferCalces.get(indiceActualCalce).getIDProducto(), bufferCalces.get(indiceActualCalce).getPrecioCompra(), bufferCalces.get(indiceActualCalce).getPrecioVenta(),
                       //     bufferCalces.get(indiceActualCalce).getFecha(), bufferCalces.get(indiceActualCalce).getCantidad(), Repeticion);

                    consulta+="( '"+bufferCalces.get(indiceActualCalce).getIDOfertaCompra()+"','"+bufferCalces.get(indiceActualCalce).getIDOfertaVenta()+"','"+bufferCalces.get(indiceActualCalce).getIDProducto()+"','"+bufferCalces.get(indiceActualCalce).getPrecioCompra()+"','"+bufferCalces.get(indiceActualCalce).getPrecioVenta()+"','"+bufferCalces.get(indiceActualCalce).getFecha()+"','"+bufferCalces.get(indiceActualCalce).getCantidad()+"','"+Repeticion+"'),";

                }

                //System.out.println(getLocalName()+" -> Ya creo la consulta - "+((new Date()).getTime()-horaInicial.getTime()));



		if(consulta.indexOf("'")!=-1)
                {
                consulta = consulta.substring(0, consulta.length()-1);


                //System.out.println(getLocalName()+" -> INSERT INTO calce (IDOfertaCompra, IDOfertaVenta, IDProducto, precioCompra, precioVenta, fecha, cantidad, repeticion) VALUES");

                BDA.ejecutar(consulta, "Prueba");

                }
                //System.out.println(getLocalName()+" -> Termino de ingresar Calces - "+((new Date()).getTime()-horaInicial.getTime()));


                consulta = "INSERT INTO calcecm (IDOfertaCompra, IDOfertaVenta, IDProducto, precioCalce, cantidad, fecha, repeticion) VALUES";
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int indiceActualCalceCM=0 ; indiceActualCalceCM<bufferCalcesCM.size(); indiceActualCalceCM++) {
                    //res = BD.insertarCalceCM(/*bufferCalcesCM.get(i).getIDCalce(),*/ bufferCalcesCM.get(indiceActualCalceCM).getIDOfertaCompra(), bufferCalcesCM.get(indiceActualCalceCM).getIDOfertaVenta(),
                           // bufferCalcesCM.get(indiceActualCalceCM).getIDProducto(), bufferCalcesCM.get(indiceActualCalceCM).getPrecioCompra(), bufferCalcesCM.get(indiceActualCalceCM).getCantidad(),
                           // bufferCalcesCM.get(indiceActualCalceCM).getFecha(),Repeticion);
                    consulta+="( '"+bufferCalcesCM.get(indiceActualCalceCM).getIDOfertaCompra()+"','"+bufferCalcesCM.get(indiceActualCalceCM).getIDOfertaVenta()+"','"+bufferCalcesCM.get(indiceActualCalceCM).getIDProducto()+"','"+bufferCalcesCM.get(indiceActualCalceCM).getPrecioCompra()+"','"+bufferCalcesCM.get(indiceActualCalceCM).getCantidad()+"','"+bufferCalcesCM.get(indiceActualCalceCM).getFecha()+"','"+Repeticion+"'),";
                }
                //BD.cerrarConexion();

                //System.out.println(getLocalName()+" -> Ya creo la consulta - "+((new Date()).getTime()-horaInicial.getTime()));



		if(consulta.indexOf("'")!=-1)
                {
                consulta = consulta.substring(0, consulta.length()-1);


               // System.out.println(getLocalName()+" -> INSERT INTO calcecm (IDOfertaCompra, IDOfertaVenta, IDProducto, precioCalce, cantidad, fecha, repeticion) VALUES");

                BDA.ejecutar(consulta, "Prueba");

                }
               // System.out.println(getLocalName()+" -> Termino de ingresar CalcesCM - "+((new Date()).getTime()-horaInicial.getTime()));

                // Se ingresa la informacion de la subasta en el array para que
                // los agentes puedan ver esta informacion
                InfoSubasta info = new InfoSubasta();
                float[] precioCotizacion = new float[cantidadProductos];
                int[] cantidadCotizacion = new int[cantidadProductos];

                consulta = "INSERT INTO cotizacion VALUES";

                for (int i=0; i<cantidadProductos; i++) {
                    info = new InfoSubasta();
                    try {
                        info.setIDAccion(i);
                        // Precio de inicio de la subasta actual
                        //BD = new ConexionBD("mysql");
                        ResultSet r1 = null;
                        if(simulacion == 1)
                        r1 = BDA.consulta("SELECT precioCierre FROM cotizacion WHERE fecha = " + (simulacion-1) + " AND repeticion = "+0+" AND idproducto = "+i);
                        else
                        r1 = BDA.consulta("SELECT precioCierre FROM cotizacion WHERE fecha = " + (simulacion-1) + " AND repeticion = "+Repeticion+" AND idproducto = "+i);
                        if (r1 != null) {
                            if (r1.next())
                                info.setPrecioInicio(r1.getFloat(1));
                            else
                                info.setPrecioInicio(-1);
                            if (r1.wasNull())
                                info.setPrecioInicio(-1);
                        }
                        /*ResultSet r1 = null;
                        r1 = BD.consulta("SELECT ((precioCompra+precioVenta)/2) FROM calce WHERE IDCalce=" +
                                            "(SELECT min(IDCalce) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacionActual + " AND repeticion = "+Repeticion+")");
                        if (r1 != null) {
                            if (r1.next())
                                info.setPrecioInicio(r1.getFloat(1));
                            else
                                info.setPrecioInicio(-1);
                            if (r1.wasNull())
                                info.setPrecioInicio(-1);
                        }*/
                        // Precio de cierre de la subasta actual
                        /*ResultSet r2 = null;
                        r2 = BD.consulta("SELECT ((precioCompra+precioVenta)/2) FROM calce WHERE IDCalce=" +
                                            "(SELECT max(IDCalce) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacionActual + " AND repeticion = "+Repeticion+")");
                        if (r2 != null) {
                            if (r2.next())
                                info.setPrecioCierre(r2.getFloat(1));
                            else
                                info.setPrecioCierre(-1);
                            if (r2.wasNull())
                                info.setPrecioCierre(-1);
                        }*/
                        // Precio maximo de la subasta actual
                        ResultSet r3 = null;
                        r3 = BDA.consulta("SELECT max(precioCompra) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacion+" AND repeticion = "+Repeticion);
                        if (r3 != null) {
                            if (r3.next())
                                info.setPrecioMax(r3.getFloat(1));
                            else
                                info.setPrecioMax(0);
                            if (r3.wasNull())
                                info.setPrecioMax(0);
                        }
                        // Precio minimo de la subasta actual
                        ResultSet r4 = null;
                        r4 = BDA.consulta("SELECT min(precioCompra) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacion+" AND repeticion = "+Repeticion);
                        if (r4 != null) {
                            if (r4.next())
                                info.setPrecioMin(r4.getFloat(1));
                            else
                                info.setPrecioMin(0);
                            if (r4.wasNull())
                                info.setPrecioMin(0);
                        }
                        // Precio promedio de la subasta actual
                        ResultSet r5 = null;
                        r5 = BDA.consulta("SELECT avg(precioCompra) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacion+" AND repeticion = "+Repeticion);
                        if (r5 != null) {
                            if (r5.next())
                                info.setPrecioPromedio(r5.getFloat(1));
                            else
                                info.setPrecioPromedio(0);
                            if (r5.wasNull())
                                info.setPrecioPromedio(0);
                        }
                        // Volumen de la subasta actual
                        ResultSet r6 = null;
                        int cantiTransada = 0;
                        r6 = BDA.consulta("SELECT sum(cantidad) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacion+" AND repeticion = "+Repeticion);
                        if (r6 != null) {
                            if (r6.next())
                                cantiTransada+=r6.getInt(1);
                                //info.setCantidad(r6.getInt(1));
                            else
                                info.setCantidad(0);
                            if (r6.wasNull())
                                info.setCantidad(0);
                        }
                        
                        r6 = BDA.consulta("SELECT sum(cantidad) FROM calcecm WHERE IDProducto="+i+" AND fecha=" + simulacion+" AND repeticion = "+Repeticion);
                        if (r6 != null) {
                            if (r6.next())
                                cantiTransada+=r6.getInt(1);
                                //info.setCantidad(r6.getInt(1));
                            else
                                info.setCantidad(0);
                            if (r6.wasNull())
                                info.setCantidad(0);
                        }
                        
                        info.setCantidad(cantiTransada);
                        
                        //BD.cerrarConexion();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    info.setPeriodo(simulacion);

                    int n = historicoSubastas.get(i).size();


                            cantidadCotizacion[i] = info.getCantidad();
                           //precioCotizacion[i] = PrecioReferencia[i];
                            precioCotizacion[i] = preciosRefere[i];

                            //BD = new ConexionBD("mysql");
                            //res = BD.insertarCotizacion(i, simulacionActual, info.getPrecioInicio(), precioCotizacion[i], info.getPrecioMax(),
                              //      info.getPrecioMin(), info.getPrecioPromedio(), cantidadCotizacion[i],Repeticion);
                            consulta+="( '"+i+"','"+simulacion+"','"+info.getPrecioInicio()+"','"+precioCotizacion[i]+"','"+info.getPrecioMax()+"','"+info.getPrecioMin()+"','"+info.getPrecioPromedio()+"','"+cantidadCotizacion[i]+"','"+Repeticion+"'),";
                            
                            info.setPrecioCierre(PrecioReferencia[i]);
                            //info.setPrecioCierre(preciosRefere[i]);
                            
                            info.setPrecioPromedio(info.getPrecioPromedio());
                            info.setCantidad(cantidadCotizacion[i]);

                            historicoSubastas.get(i).add(info);
                            
                            
                            
                }
                
                //////////////////////////////
                //nuevoCanal(simulacion);
                //////////////////////////////

                //System.out.println(getLocalName()+" -> Ya creo la consulta - "+((new Date()).getTime()-horaInicial.getTime()));
                if(consulta.indexOf("'")!=-1)
                {
                consulta = consulta.substring(0, consulta.length()-1);


                //System.out.println(getLocalName()+" -> INSERT INTO cotizacion VALUES");

                BDA.ejecutar(consulta, "Prueba");
                    
                }
                
                
                
                if(participanFundamentales)
                {
                    ///////////////////////////////////////////////////////////////////////////////
                    //////////Insertar el valor fundamental/////////////////////////////
                    consulta = "INSERT INTO preciofundamental VALUES";
                    for (int i=0; i<cantidadProductos; i++) {
                        consulta+="( '"+simulacion+"','"+i+"','"+preciosFundam[i]+"','"+Repeticion+"'),";
                    }

                    if(consulta.indexOf("'")!=-1)
                    {
                    consulta = consulta.substring(0, consulta.length()-1);
                    BDA.ejecutar(consulta, "Prueba");
                    //System.out.println(consulta);    
                    }
                }
                ///////////////////////////////////////////////////////////////////////////////
                //////////////////////////////////////////////////////////////////////////////

                //System.out.println(getLocalName()+" -> Termino de ingresar cotizacion - "+((new Date()).getTime()-horaInicial.getTime()));

                //Se actualizan las volatilidades de las acciones
                for (int i=0; i<cantidadProductos; i++) {
                    int n = historicoSubastas.get(i).size();
                    volatilidadActual[i] = (float)Math.log(historicoSubastas.get(i).get(n-1).getPrecioCierre()/historicoSubastas.get(i).get(n-2).getPrecioCierre());
                }

               consulta = "INSERT INTO saldoxagente VALUES";

                //BD = new ConexionBD("mysql");
                for (int i=0; i<numAgentes; i++) {
                    //if (participantes[i+1] != null)
                    {
                        //res = BD.insertarSaldoXAgente(i, simulacionActual, saldosAgentes.get(i),Repeticion);
                        consulta+="( '"+i+"','"+simulacion+"','"+saldosAgentes[i]+"','"+Repeticion+"'),";
                    }
                }

              // System.out.println(getLocalName()+" -> Ya creo la consulta - "+((new Date()).getTime()-horaInicial.getTime()));
                if(consulta.indexOf("'")!=-1)
                {
                consulta = consulta.substring(0, consulta.length()-1);


               // System.out.println(getLocalName()+" -> INSERT INTO saldoxagente VALUES");

                BDA.ejecutar(consulta, "Prueba");

                }
                
                //////////////PARA LOS ADAPTATIVOS
                 consulta = "INSERT INTO estrategiadaptativo VALUES";

                //BD = new ConexionBD("mysql");
                for (int i=0; i<cantidadProductos; i++) {
                    //if (participantes[i+1] != null)
                    {
                        Set<Integer> claves = estrategiasAdaptativos.get(i).keySet();
                        for(int j:claves)
                        //res = BD.insertarSaldoXAgente(i, simulacionActual, saldosAgentes.get(i),Repeticion);
                        consulta+="( '"+j+"','"+i+"','"+simulacion+"','"+estrategiasAdaptativos.get(i).get(j) +"','"+Repeticion+"'),";
                    }
                }

              // System.out.println(getLocalName()+" -> Ya creo la consulta - "+((new Date()).getTime()-horaInicial.getTime()));
                if(consulta.indexOf("'")!=-1)
                {
                consulta = consulta.substring(0, consulta.length()-1);


               //System.err.println(getLocalName()+" -> "+consulta);

                BDA.ejecutar(consulta, "Prueba");

                }
                
                        
                //////////////PARA LOS ADAPTATIVOS

                //BD.cerrarConexion();
               // System.out.println(getLocalName()+" -> Termino de ingresar saldos - "+((new Date()).getTime()-horaInicial.getTime()));
                //BD = new ConexionBD("mysql");

                consulta = "INSERT INTO accionesxagente VALUES";


                for (int i=0; i<numAgentes; i++) {
                    //if (participantes[i+1] != null)
                    {
                        for (int j=0; j<cantidadProductos; j++) {
                            //res = BD.insertarAccionesXAgente(i, j, simulacionActual, accionesxagente.get(i).get(j),Repeticion);
                            consulta+="( '"+i+"','"+j+"','"+simulacion+"','"+accionesxagente[i][j]+"','"+Repeticion+"'),";
                        }
                    }
                }

               // System.out.println(getLocalName()+" -> Ya creo la consulta - "+((new Date()).getTime()-horaInicial.getTime()));
                if(consulta.indexOf("'")!=-1)
                {
                consulta = consulta.substring(0, consulta.length()-1);

                /*try {
            CalcesEnRunTime = 0;
                    Origen = new FileWriter("CalceRunTime.txt");
                    archivo = new PrintWriter(Origen);
                    archivo.print(consulta);
                    archivo.close();
                } catch (IOException ex) {
                    Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
                }*/




               // System.out.println(getLocalName()+" -> INSERT INTO accionesxagente VALUES");

                BDA.ejecutar(consulta, "Prueba");

                }
               // System.out.println(getLocalName()+" -> Termino de ingresar cantidad de acciones - "+((new Date()).getTime()-horaInicial.getTime()));
                //BD.cerrarConexion();

                // Se ingresan a la base de datos los valores de cada indicador, por cada agente tecnico
                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesCorto.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesCorto.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosPMD.get(i), j, simulacion, "PDMCorto", datosPromediosMovilesCorto.get(i).get(j),Repeticion);
                    }
                }
               // BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesLargo.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesLargo.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosPMD.get(i), j, simulacion, "PDMLargo", datosPromediosMovilesLargo.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesSimple.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesSimple.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosPMS.get(i), j, simulacion, "PMSimple", datosPromediosMovilesSimple.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesTripleCorto.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesTripleCorto.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosPMT.get(i), j, simulacion, "PMTCorto", datosPromediosMovilesTripleCorto.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesTripleMedio.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesTripleMedio.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosPMT.get(i), j, simulacion, "PMTMedio", datosPromediosMovilesTripleMedio.get(i).get(j),Repeticion);
                    }
                }
               // BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesTripleLargo.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesTripleLargo.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosPMT.get(i), j, simulacion, "PMTLargo", datosPromediosMovilesTripleLargo.get(i).get(j),Repeticion);
                    }
                }
               // BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosRSI.size(); i++) {
                    for (int j=0; j<datosRSI.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosRSI.get(i), j, simulacion, "RSI", datosRSI.get(i).get(j),Repeticion);
                    }
                }
               // BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosVHF.size(); i++) {
                    for (int j=0; j<datosVHF.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosVHF.get(i), j, simulacion, "VHF", datosVHF.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosMACD.size(); i++) {
                    for (int j=0; j<datosMACD.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosMACD.get(i), j, simulacion, "MACD", datosMACD.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosMACDLineaSenal.size(); i++) {
                    for (int j=0; j<datosMACDLineaSenal.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosMACD.get(i), j, simulacion, "MACDSenal", datosMACDLineaSenal.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosROC.size(); i++) {
                    for (int j=0; j<datosROC.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosROC.get(i), j, simulacion, "ROC", datosROC.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosMomento.size(); i++) {
                    for (int j=0; j<datosMomento.get(i).size(); j++) {
                        BDA.insertarAnalisisTecnico(agentesTecnicosMomento.get(i), j, simulacion, "Momento", datosMomento.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                // Si hay algun agente cuyo saldo sea cero y no tenga acciones, se saca de la simulacion
                /*for (int i=0; i<numAgentes; i++) {
                    int totalAcciones = 0;
                    for (int j=0; j<cantidadProductos; j++) {
                        totalAcciones += accionesxagente.get(i).get(j);
                    }
                    if (saldosAgentes.get(i) == 0 && totalAcciones == 0) {
                        participantes[i] = null;
                    }
                }*/

                System.out.println(getLocalName()+" -> Va a salir de ingresar datos a la BD para periodo - "+simulacion+" - "+((new Date()).getTime()-horaInicial.getTime()));

                if(simulacion==cantidadSimulaciones)
                {
                    BDA.cerrarConexion();
                }


           
        }
        
    }



    public void mostrarMensaje( ACLMessage msg , String name)
	{
		System.out.print( name + " ->  t=" + (System.currentTimeMillis())/1000F + " in "
		         + getLocalName() + ": "
					+ ACLMessage.getPerformative(msg.getPerformative() ));
		System.out.print( "  from: " +
					(msg.getSender()==null ? "null" : msg.getSender().getLocalName())
					+  " --> to: ");
	 	for ( Iterator it = msg.getAllReceiver(); it.hasNext();)
	 		System.out.print( ((AID) it.next()).getLocalName() + ", ");
		System.out.println( "  cid: " + msg.getConversationId());
		System.out.println( "  content: " +  msg.getContent());
	}


        public static synchronized int contadorOfertasC()
        {
            return contadorOfertasCompra++;
        }

        public static synchronized int contadorOfertasV()
        {
            return contadorOfertasVenta++;
        }

        public static synchronized int contadorNCompra()
        {
            return noCompra++;
        }

        public static synchronized int contadorNVenta()
        {
            return noVenta++;
        }

}
