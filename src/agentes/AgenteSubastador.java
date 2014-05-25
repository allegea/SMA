/*
 * AgenteSubastador.java
 *
 * Created on 24 de noviembre de 2008, 08:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package agentes;

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
import java.util.Date;
import java.util.logging.*;
import jade.wrapper.StaleProxyException;


public class AgenteSubastador extends Agent 
{
    
    private Codec codec = new SLCodec(); //Se crea un lenguaje para la ontologia
    private Ontology ontologia = DoblePuntaOntology.getInstance();//se crea la ontologia
    private AID[] participantes;     //Vector que contiene a los participantes cuando se registran
    private AID agenteMercado;
    
    private int[] registrosDinamicos = new int[100];
    private int[] cantidadCotizacion;
    private float[] precioCotizacion;
    private int Repeticion;
    private int contadorParticipantes = 0;      // Contador del vector de participantes
    private int contCom = 0;    // Contador del vector de precios de compra
    private int contVen = 0;    // Contador del vector de precios de venta
    private int contComTmp = 0;
    private int contVenTmp = 0;
    private int contadorNotCompra = 0;
    private int contadorNotVenta = 0;
    private int contadorOfertasCompra = 0;
    private int contadorOfertasVenta = 0;
    private int contadorCalces = 0;
    private int contadorCalcesCM = 0;
    private int cantidadProductos = 0;
    private int nRegDinamicos = 0;
    private long tiempoInicio;      // Indica el tiempo de inicio del resgistro y la subasta
    private float ganancia = 0;
    private boolean registroDinamico = true;
    public boolean cargarSerie = false;
    public static float[] volatilidadActual;
    public static double[] volatilidadHistorico;
    public static int simulacionActual;
    public static int cantidadSimulaciones = 0;
    public static int numAgentes = 0;
    public static int CalcesEnRunTime = 0;
    public static long tiempoSimulacion;
    public static long tiempoRegistros;
    public static FileWriter Origen=null;
    public static PrintWriter archivo=null;
    
    ConexionBD BD;

    private ArrayList<ArrayList<OfertaCallMarket>> ofertasCallSession = new ArrayList<ArrayList<OfertaCallMarket>>();

    private ArrayList<ArrayList<OfertaVenta>> ofertasVenta = new ArrayList<ArrayList<OfertaVenta>>();
    private ArrayList<ArrayList<OfertaVenta>> ofertasVentaTmp = new ArrayList<ArrayList<OfertaVenta>>();
    private ArrayList<ArrayList<OfertaCompra>> ofertasCompra = new ArrayList<ArrayList<OfertaCompra>>();
    private ArrayList<ArrayList<OfertaCompra>> ofertasCompraTmp = new ArrayList<ArrayList<OfertaCompra>>();
    private ArrayList<InfoOfertaCompra> bufferOfertasCompra = new ArrayList<InfoOfertaCompra>();
    private ArrayList<InfoOfertaVenta> bufferOfertasVenta = new ArrayList<InfoOfertaVenta>();

    public  static ArrayList<ArrayList<InfoSubasta>> historicoSubastas = new ArrayList<ArrayList<InfoSubasta>>();
    public  static ArrayList<ArrayList<Integer>> accionesxagente = new ArrayList<ArrayList<Integer>>();
    public  static ArrayList<Float> saldosAgentes = new ArrayList<Float>();
    public  ArrayList<InfoCalce> bufferCalces = new ArrayList<InfoCalce>();
    public  ArrayList<InfoCalce> bufferCalcesCM = new ArrayList<InfoCalce>();
    public  static int[] paquetesMinimos;
    public  static String[] nombreAcciones;
    
    public static FileWriter pararSub=null;
    public static PrintWriter paraSubastador=null;

    jade.wrapper.AgentContainer contenedor;

    //////////////////////////////////////////////////////////////////////

    protected static float PrecioPorEncima[];
    protected static float PrecioPorDebajo[];
    protected static float porcentajeCanal[];
    protected static float PrecioReferencia[];
    protected static float NrodeOperaciones[];
    protected float RuedasXAccion[];
    protected float CantidadesNegociadas[];
    private float sumatoriaOperaciones =0;
    protected static float promedioCantNegociacion;
    
    private boolean EstaEnCallMarket[];
    Random objeto;


   // private ArrayList<ArrayList<Integer>> caducidadOfertasCompra = new ArrayList<ArrayList<Integer>>();
    //private ArrayList<ArrayList<Integer>> caducidadOfertasVenta = new ArrayList<ArrayList<Integer>>();
    private int permanenciaOfertas = 2;
    public static float alphaPrecioOfertas = 0.015f;
    
    
    private  ConceptoProducto[] conceptosProductos;
    private boolean[] ConceptosLlenos;
    private long tiempoInicial=0;
    private long tiempoFinal;//Tiempo en segundos que durara un dia bursatil;
    private long tiempoCallMarketIntra;
    static long tiempoCallMarketFinal;
    private long tiempoAleatorioCallIntra;
    static long tiempoAleatorioCallFinal;
    static long tiempoCallMarket;
    static long tiempoCallMarketAlea;

    public static Date horaInicial;
    private float UVR = 0;
    private boolean SesionFinal = false;
    public static long[] cantidadesATransar;
    private boolean graficarLibro = false;
    private boolean simulacionCorriendo = true;

    Libro2 onSimulation;

    RecibirOfertas ROBehaviour;
    

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
    Behaviour flusher;
    //***********************************************************************************************
    @SuppressWarnings({"static-access", "CallToThreadDumpStack"})
    @Override
    protected void setup()
    {

        /*try {
            // Create a file:
            System.setOut(new PrintStream(new FileOutputStream("OutputSimulation.txt")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
        }*/
            
            objeto = new java.util.Random();

            //SE GENERA EL ARCHIVO PARA LA GRAFICA DE CALCERUNTIME/////////
        try {
            CalcesEnRunTime = 0;
            Origen = new FileWriter("CalceRunTime.txt");
            archivo = new PrintWriter(Origen);
            archivo.print(CalcesEnRunTime);
            archivo.close();
        } catch (IOException ex) {
            Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
        }



            Object[] args = getArguments();
            if (args != null && args.length > 0){
                cantidadSimulaciones = Integer.parseInt(args[0].toString()); //numero de simulaciones
                numAgentes = Integer.parseInt(args[1].toString()); //numero de agentes
                Repeticion = Integer.parseInt(args[2].toString());

                //System.out.println(getLocalName()+" -> argumentos "+args.length);
                NrodeOperaciones = new float[args.length-3];
                 participantes = new AID[numAgentes];
                for(int i=3;i<args.length;i++)
                {
                    NrodeOperaciones[i-3] = Float.parseFloat(args[i].toString());
                    sumatoriaOperaciones+=NrodeOperaciones[i-3];
                    //System.out.println(getLocalName()+" -> "+(i-3)+" - "+NrodeOperaciones[i-3]);

                }

                    promedioCantNegociacion = sumatoriaOperaciones/(args.length-3);
                    System.out.println(getLocalName()+" -> "+promedioCantNegociacion);

            }



        simulacionActual = 1;
        tiempoSimulacion = 15000; // la simulacion durara 15 segundos
        tiempoRegistros = 10000; // seran 5 segundos de registro
        tiempoFinal=86400*1000; //son 86400 minutos, es decir 24 horas, pero en milisegundos
        tiempoCallMarketIntra = (tiempoSimulacion*150000)/14100000; //150000 son 2.5 minutos en milisegundos, 14100000 son 235 min en mili
        tiempoCallMarketFinal = (tiempoSimulacion*300000)/14100000; //300000 son 5 minutos en milisegundos, 14100000 son 235 min en mili
        tiempoAleatorioCallIntra = (tiempoSimulacion*30000)/14100000; // 30000 son 30 segundos
        tiempoAleatorioCallFinal = (tiempoSimulacion*60000)/14100000; // 60000 son 60 segundos

        tiempoCallMarket = tiempoCallMarketIntra;
        tiempoCallMarketAlea = tiempoAleatorioCallIntra;

        

        System.out.println("tiempoCallMarketIntra - "+tiempoCallMarketIntra);
        System.out.println("tiempoCallMarketFinal - "+tiempoCallMarketFinal);
        System.out.println("tiempoAleatorioCallIntra - "+tiempoAleatorioCallIntra);
        System.out.println("tiempoAleatorioCallFinal - "+tiempoAleatorioCallFinal);
        System.out.println("Tiempo de la simulacion - "+(tiempoSimulacion-(tiempoCallMarketFinal+tiempoAleatorioCallFinal)));
        // Registrar el servicio
        try {
            // Se crea una lista de servicios de agente
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());

            // Se crea una descripcion de servicio
            ServiceDescription sd = new ServiceDescription();
            sd.setName("RealizarCalce");

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
            
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontologia);

        try {
            BD = new ConexionBD("mysql");
            ResultSet r = null;
            r = BD.consulta("SELECT count(*) FROM producto");
            if (r.next())
                cantidadProductos = r.getInt(1);
            BD.cerrarConexion();

            paquetesMinimos = new int[cantidadProductos];
            nombreAcciones = new String[cantidadProductos];

            for (int i=0; i<cantidadProductos; i++) {
                BD = new ConexionBD("mysql");
                r = BD.consulta("SELECT paqueteMinimo, nombre FROM producto WHERE IDProducto="+i);
                if (r.next()) {
                    paquetesMinimos[i] = r.getInt(1);
                    nombreAcciones[i] = r.getString(2);
                }
                BD.cerrarConexion();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        for (int i=0; i<numAgentes; i++) {
            saldosAgentes.add(new Float(-1));
            accionesxagente.add(i, new ArrayList<Integer>());
            for (int j=0; j<cantidadProductos; j++) {
                accionesxagente.get(i).add(new Integer(-1));
            }
        }
        for (int i=0; i<cantidadProductos; i++) {
            historicoSubastas.add(i, new ArrayList<InfoSubasta>());
        }
        // Se carga el historial de cotizaciones
        try {
            InfoSubasta info = new InfoSubasta();
            for (int i=0; i<cantidadProductos; i++) {
                BD = new ConexionBD("mysql");
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
                BD.cerrarConexion();
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
        
        volatilidadActual = new float[cantidadProductos];
        volatilidadHistorico = new double[cantidadProductos];

        /////////////////////////////////////////////////////////////////////
        PrecioPorEncima = new float[cantidadProductos];
        PrecioPorDebajo = new float[cantidadProductos];
        porcentajeCanal = new float[cantidadProductos];
        PrecioReferencia= new float[cantidadProductos];
        EstaEnCallMarket= new boolean[cantidadProductos];
        conceptosProductos= new ConceptoProducto[cantidadProductos];
        ConceptosLlenos = new boolean[cantidadProductos];
        
        RuedasXAccion = new float[cantidadProductos];
        CantidadesNegociadas = new float[cantidadProductos];
        cantidadesATransar = new long[cantidadProductos];

        try {

            for (int i=0; i<cantidadProductos; i++) {
                BD = new ConexionBD("mysql");
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
                System.out.println(getLocalName()+" -> cantidadesATransar "+cantidadesATransar[i]);
                BD.cerrarConexion();
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

        ConceptoProducto producto = new ConceptoProducto();

        //new Libro().setVisible(true);

          HiloLibro HiloGrafica= new HiloLibro(cantidadProductos,tiempoSimulacion, tiempoFinal);//////////////////////////
          if(graficarLibro)
            HiloGrafica.start();


        /////////////////////////////////////////////////////////////////////

        for (int i=0; i<cantidadProductos; i++) {

            ////////////////////////////////////////////////////////////////////
            
           // sumatoriaOperaciones+=NrodeOperaciones[i];
            
            //////////////////////////////////////////////////////////////////////

            ofertasVenta.add(i, new ArrayList<OfertaVenta>());
            ofertasCompra.add(i, new ArrayList<OfertaCompra>());
            ofertasVentaTmp.add(i, new ArrayList<OfertaVenta>());
            ofertasCompraTmp.add(i, new ArrayList<OfertaCompra>());
            ofertasCallSession.add(i, new ArrayList<OfertaCallMarket>());

            //caducidadOfertasCompra.add(i, new ArrayList<Integer>());
            //caducidadOfertasVenta.add(i, new ArrayList<Integer>());

            producto.setIDProducto(i);
            producto.setDescripcion(" ");
            producto.setNombre(nombreAcciones[i]);
            conceptosProductos[i] = producto;
            ConceptosLlenos[i] = false;
            
            int n = AgenteSubastador.historicoSubastas.get(i).size();

            //////////// VOLATILIDAD DEL HISTORICO
            float precios[] = new float[n];
            int k = 0;
            for (int j=n-1; j>=0; j--) {
                if (AgenteSubastador.historicoSubastas.get(i).get(j).getPrecioCierre() > 0)
                    precios[k++] = AgenteSubastador.historicoSubastas.get(i).get(j).getPrecioCierre();
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


            
            
        
        // Se genera el comportamiento de registro
        RegistrarParticipantes RPBehaviour = new RegistrarParticipantes(this);
        contenedor = getContainerController();
        // Hasta que no haya al menos un comprador o un vendedor no inicia
        addBehaviour(RPBehaviour);
                       /*if (flusher==null) {
			flusher = new GCAgent( this, 1000);
			//addBehaviour( flusher);
		}*/
        tiempoInicio = new GregorianCalendar().getTimeInMillis(); // Se guarda el tiempo de inicio
        HiloContinuar EstadoSubastador = new HiloContinuar();
        EstadoSubastador.start();
    }
	
    class RegistrarParticipantes extends SimpleBehaviour {
        Agent agenteSubastador;
        boolean finalizado = false;
        IniciarSubasta ISBehaviour;
       
        
        public RegistrarParticipantes(Agent a)
        {
            super(a);
            agenteSubastador = a;
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
                            participantes[contadorParticipantes] = mensajeEntrante.getSender();

                            // Se ingresa el agente en la BD con su ID, el saldo y la fecha
                            BD = new ConexionBD("mysql");
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
                            BD.cerrarConexion();

                            
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
              Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public boolean done()
        {
        // Termina el comportamiento de recibir registros y llama a iniciar subasta
            if (finalizado) {

                for (int i=0; i<cantidadProductos; i++)
                {
                    int n = AgenteSubastador.historicoSubastas.get(i).size();
                    EstaEnCallMarket[i] = false;/////////////////////////////////////////////////////////////////////
                    PrecioReferencia[i] = AgenteSubastador.historicoSubastas.get(i).get(n-1).getPrecioCierre();/////////////////////
                    porcentajeCanal[i] = (float)0.04;////////////////////////////////////////////////////////////
                    CanalNegociacion((float)1,i);///////////////////////////////////////////////////////////////

                    if(graficarLibro)
                    {
                        onSimulation.TablaInformacion(nombreAcciones[i], 0,0, 0,true,i);
                        onSimulation.serie(PrecioPorEncima[i], PrecioPorDebajo[i], PrecioReferencia[i], PrecioReferencia[i], i);
                    }
                }


                ISBehaviour = new IniciarSubasta(agenteSubastador);
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
        Agent agenteSubastador;
        
        boolean finalizado = false;
          
        @SuppressWarnings("element-type-mismatch")
        public IniciarSubasta(Agent a)
        {
            super(a);
            if(simulacionActual> 1)
             System.out.println(getLocalName()+" -> Iniciar Subasta - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            agenteSubastador = a;
            bufferCalces.clear();
            bufferCalcesCM.clear();
            bufferOfertasCompra.clear();
            bufferOfertasVenta.clear();

            tiempoCallMarket = tiempoCallMarketIntra;
            tiempoCallMarketAlea = tiempoAleatorioCallIntra;


            int i, j;
            if(permanenciaOfertas>1)
            {
                for(j=0;j<cantidadProductos;j++)
                {

                    for(i=0;i<ofertasCompra.get(j).size();i++)
                    {
                        int caducidad = ofertasCompra.get(j).get(i).getCaducidadOferta();
                        if(caducidad == permanenciaOfertas)
                        {
                            enviarNotCancelarCompra(ofertasCompra.get(j).get(i),"caduco", ofertasCompra.get(j).get(i).getProducto().getIDProducto());
                            //caducidadOfertasCompra.get(j).remove(i);
                            ofertasCompra.get(j).remove(i);
                            i--;
                            //System.out.println(getLocalName()+" -> Se elimino por caducidad");
                        }
                        else
                            ofertasCompra.get(j).get(i).setCaducidadOferta(caducidad+1);
                            //caducidadOfertasCompra.get(j).set(i,caducidadOfertasCompra.get(j).get(i)+1 );
                    }

                    for(i=0;i<ofertasVenta.get(j).size();i++)
                    {
                        int caducidad = ofertasVenta.get(j).get(i).getCaducidadOferta();
                        if(caducidad==permanenciaOfertas)
                        {
                            enviarNotCancelarVenta(ofertasVenta.get(j).get(i),"caduco", ofertasVenta.get(j).get(i).getProducto().getIDProducto());
                            //caducidadOfertasVenta.get(j).remove(i);
                            ofertasVenta.get(j).remove(i);
                            i--;
                            //System.out.println(getLocalName()+" -> Se elimino por caducidad");
                        }
                        else
                            ofertasVenta.get(j).get(i).setCaducidadOferta(caducidad+1);
                            //caducidadOfertasVenta.get(j).set(i,caducidadOfertasVenta.get(j).get(i)+1 );
                    }
                }

            }
                
            else
            {
                 for(j=0;j<cantidadProductos;j++)
                {
                    ofertasCompra.get(j).clear();
                    ofertasVenta.get(j).clear();

                    //caducidadOfertasCompra.get(j).clear();
                    //caducidadOfertasVenta.get(j).clear();
                 }
            }

            //onSimulation.TablaLibroOfertas(ofertasVentaTmp.get(onSimulation.ProductoActual), ofertasCompraTmp.get(onSimulation.ProductoActual));


            contCom = 0;
            contVen = 0;
            //ROBehaviour = new RecibirOfertas(a);
             if(simulacionActual> 1)
            System.out.println(getLocalName()+" Iniciar Subasta Fin- "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));

            simulacionCorriendo = true;
        }
          
        @SuppressWarnings("CallToThreadDumpStack")
        public void action()
        {
            System.out.println("Periodo: ------------"+simulacionActual+"-----------------");
            System.out.println("-------------------------------------------------");
            if (simulacionActual == 1) {
                BD = new ConexionBD("mysql");
                for (int i=0; i<numAgentes; i++) {
                    if (participantes[i] != null) {
                        for (int j=0; j<cantidadProductos; j++) {
                            
                            //System.out.println(accionesxagente.get(i).get(j));
                            String res = BD.insertarAccionesXAgente(i, j, 0, accionesxagente.get(i).get(j),Repeticion);

                        }
                    }
                }
                BD.cerrarConexion();
            }
            else
            {
                System.out.println("Periodo: ------------"+simulacionActual+"-----------------"+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                if(graficarLibro)
                onSimulation.agregarEtiquetaDia("Day - "+simulacionActual);
            }

            if (nRegDinamicos > 0) {
                BD = new ConexionBD("mysql");
                for (int i=0; i<nRegDinamicos; i++) {
                    int agente = registrosDinamicos[i];
                    if (participantes[agente] != null) {
                        for (int j=0; j<cantidadProductos; j++) {
                            String res;
                            res = BD.insertarAccionesXAgente(agente, j, 0, accionesxagente.get(agente-1).get(j),Repeticion);
                        }
                    }
                }
                BD.cerrarConexion();
            }

            nRegDinamicos = 0;
            try {
                if(simulacionActual!=1)
                System.out.println(getLocalName()+" -> INICIO MENSAJE INICIO DE SUBASTA - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
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
                send(mensajeInicio);
                if(simulacionActual!=1)
                System.out.println(getLocalName()+" -> FIN MENSAJE INICIO DE SUBASTA -"+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                tiempoInicio = new GregorianCalendar().getTimeInMillis(); // Se calcula el tiempo de inicio de subasta
                horaInicial = new Date();

                
            } catch (Exception e) {
                e.printStackTrace();
            }

            HiloEsperarOfertas tiempoOfertas = new HiloEsperarOfertas();
            tiempoOfertas.start();
            ROBehaviour = new RecibirOfertas(agenteSubastador);
            
            addBehaviour(ROBehaviour);


            finalizado = true;
        }

        /*public boolean done()
        {
            return finalizado;
        }*/
    }
     
    class HiloEsperarOfertas extends java.lang.Thread {
        public HiloEsperarOfertas()
        {
            

        }

        @Override
        public void run()
        {
            try {
                SesionFinal = false;
               
                sleep(tiempoSimulacion-(tiempoCallMarketFinal+tiempoAleatorioCallFinal));
                //System.out.println(getLocalName()+" - Termino la sesion");
            } catch(InterruptedException e) {
                //e.printStackTrace();
                System.out.println(getLocalName() + " - El hilo de las ofertas se ha terminado se ha terminado");
            }

            SesionFinal = true;
            //FinDeSubasta();
            
            for(int i=0;i<cantidadProductos;i++)
            {
                if(EstaEnCallMarket[i] == false)
                {
                    tiempoCallMarket = tiempoCallMarketFinal;
                    tiempoCallMarketAlea = tiempoAleatorioCallFinal;

                    System.out.println(getLocalName()+" -> Se entra a Session Call Market para accion "+i+" -- Final del dia -------------- "+(tiempoSimulacion-(tiempoCallMarketFinal+tiempoAleatorioCallFinal)));
                    EstaEnCallMarket[i] = true;
                    //InicioCallMarketSubastador(i);
                    //InicioCallMarketAgentes(i);
                    InicioCallMarket(i);

                    //HiloEjecutarCallMarket ejecutar = new HiloEjecutarCallMarket(i,tiempoCallMarketFinal,tiempoAleatorioCallFinal);

                }
            }

            
          
        }
    }
    
    class RecibirOfertas extends SimpleBehaviour {
        Agent agenteSubastador;
        boolean finalizado = false;

        public RecibirOfertas(Agent a)
        {
            super(a);
            agenteSubastador = a;
        }

        @SuppressWarnings("CallToThreadDumpStack")
        public void action() {
            try {
                // se crea una plantilla para el mensaje entrante, el cual contiene el lenguaje
                // y la ontologia.
                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));

                ACLMessage mensajeOfertas = blockingReceive(mt);

                //ACLMessage mensajeOfertas = myAgent.receive(mt);
                //while(mensajeOfertas!=null)
                {

                    AID emisor = new AID();

                    //FIN DE SUBASTA

                    if (mensajeOfertas != null && mensajeOfertas.getPerformative()==ACLMessage.INFORM &&
                        mensajeOfertas.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE)) {
                        ContentElement oferta = getContentManager().extractContent(mensajeOfertas);
                        if (oferta instanceof PredicadoFinSubasta) {
                            System.out.println(getLocalName()+" -> MENSAJE FIN DE LA SUBASTA A LLEGADO - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                            finalizado = true;
                            //return;

                        }

                        else if(oferta instanceof PredicadoInicioCallMarket)
                            {
                                int idAccion = ((PredicadoInicioCallMarket)oferta).getProducto().getIDProducto();

                                    System.out.println(getLocalName()+" -> LLego mensaje Inicio Call Market para Accion - "+idAccion+ " - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                                    HiloEjecutarCallMarket tiempoOfertas;

                                    if(graficarLibro)
                                    onSimulation.serie(PrecioPorEncima[idAccion], PrecioPorDebajo[idAccion], PrecioReferencia[idAccion], -1, idAccion);

                                    CanalNegociacion(2.5f,idAccion);

                                    //InicioCallMarketAgentes(idAccion);

                                    long tiem = tiempoSimulacion-(tiempoCallMarketFinal+tiempoAleatorioCallFinal);
                                    long intra = tiempoCallMarketIntra + tiempoAleatorioCallIntra;
                                    long tiempoTrans = (((new Date()).getTime()-horaInicial.getTime())+intra);
                                    System.out.println(getLocalName() + " -> Tiempo transcurrido de la simulacion hasta ahora ----- "+tiempoTrans);
                                    if(tiempoTrans>=tiem)
                                    {
                                        SesionFinal = true;
                                        tiempoCallMarket = tiempoCallMarketFinal;
                                        tiempoCallMarketAlea = tiempoAleatorioCallFinal;

                                        tiempoOfertas = new HiloEjecutarCallMarket(idAccion, tiempoCallMarketFinal, tiempoAleatorioCallFinal);
                                    }
                                    else
                                    tiempoOfertas = new HiloEjecutarCallMarket(idAccion, tiempoCallMarketIntra, tiempoAleatorioCallIntra);


                                    tiempoOfertas.start();
                                    

                                    if(graficarLibro)
                                    if(idAccion == onSimulation.ProductoActual)
                                    {
                                        ordenarOfertasCompra(idAccion);//ordena desendentemente
                                        ordenarOfertasVenta(idAccion);//ordena ascendentemente
                                        onSimulation.TablaLibroOfertas(ofertasVentaTmp.get(idAccion), ofertasCompraTmp.get(idAccion));
                                    }
                                    //EstaEnCallMarket[idAccion] = true;


                            }

                            else if(oferta instanceof PredicadoFinCallMarket)
                            {

                                int idAccion = ((PredicadoFinCallMarket)oferta).getProducto().getIDProducto();
                                System.out.println(getLocalName()+" -> LLego mensaje de final de sesion Call Market para accion "+idAccion+" -  "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                                SessionCallMarket(idAccion);

                                //FinCallMarketAgentes(idAccion);

                                //CanalNegociacion(1f,idAccion);

                                if(graficarLibro)
                                onSimulation.serie(PrecioPorEncima[idAccion], PrecioPorDebajo[idAccion], PrecioReferencia[idAccion], -1, idAccion);

                               tiempoCallMarket = tiempoCallMarketIntra;
                               tiempoCallMarketAlea = tiempoAleatorioCallIntra;

                              // addBehabiour(flusher = new GCAgent( this, 1000));
                               /////////Elimina los mensajes que no se han visto, para evitar que se acumulen
                               //flusher = new GCAgent( agenteSubastador, 1000,idAccion);
                               //addBehaviour( flusher);
                              /////////Elimina los mensajes que no se han visto, para evitar que se acumulen

                               EstaEnCallMarket[idAccion] = false;

                               if(SesionFinal == true)
                               {
                                   boolean enviar = true;

                                   //Se verifica que no se haya terminado la simulacion, si ya se termino, y toco esperar a que la sesion
                                   //Call Market terminara, entonces al finalizar esta envia el mensaje de fin de Subasta
                                    for(int i=0;i<cantidadProductos;i++)
                                    {
                                        if(EstaEnCallMarket[i] == true)
                                        {
                                            enviar = false;
                                            break;
                                        }
                                    }
                                    if(enviar == true)
                                    {
                                       //FinDeSubasta();
                                         
                                        FinDeSubastaAgentes();
                                        System.out.println(getLocalName()+" -> MENSAJE FIN DE LA SUBASTA - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                                       finalizado = true;
                                      
                                        //return;
                                    }
                               }
                               if(graficarLibro)
                                mostrarInfoPrecios(idAccion,0);
                                

                            }
                    }

                    //REGISTRO DINAMICO DE AGENTES
                    else if(mensajeOfertas != null && mensajeOfertas.getPerformative()==ACLMessage.SUBSCRIBE &&
                              mensajeOfertas.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE) && registroDinamico) {
                            // Se guarda en registroEntrante el contenido del mensaje
                        ACLMessage mensajeRespuesta = mensajeOfertas.createReply();

                        ContentElement registroEntrante = getContentManager().extractContent(mensajeOfertas);

                        ConceptoRegistro registro = new ConceptoRegistro();
                        PredicadoConfirmarRegistro confirmacionRegistro = new PredicadoConfirmarRegistro();
                        PredicadoRegistrarAgenteBursatil registroAgente;
                        ConceptoBursatil agente;

                        if (registroEntrante instanceof PredicadoRegistrarAgenteBursatil) {
                            Date fecha = new Date();
                            registroAgente = (PredicadoRegistrarAgenteBursatil)registroEntrante;
                            agente = (ConceptoBursatil)registroAgente.getBursatil();
                            numAgentes++;
                            accionesxagente.add(new ArrayList<Integer>());
                            for (int j=0; j<cantidadProductos; j++) {
                                accionesxagente.get(numAgentes-1).add(new Integer(-1));
                            }
                            saldosAgentes.add(new Float(-1));
                                // Se agrega al vector de participantes el AID
                            participantes[contadorParticipantes] = mensajeOfertas.getSender();

                                // Se ingresa el agente en la BD con su ID, el saldo y la fecha
                            BD = new ConexionBD("mysql");
                            String res;
                            res = BD.insertarAgente(contadorParticipantes, agente.getNombre(), agente.getTipo());
                            res = BD.insertarSaldoXAgente(contadorParticipantes, 0, agente.getSaldo(),Repeticion);
                            BD.cerrarConexion();

                            System.out.println(getLocalName()+" -> REGISTRO DINAMICO");
                            System.out.println(getLocalName()+" -> REGISTRO DINAMICO");
                            System.out.println(getLocalName()+" -> REGISTRO DINAMICO");
                            System.out.println(getLocalName()+" -> REGISTRO DINAMICO");
                            System.out.println(getLocalName()+" -> REGISTRO DINAMICO");
                            System.out.println(getLocalName()+" -> REGISTRO DINAMICO");
                            System.out.println(getLocalName()+" -> REGISTRO DINAMICO");

                            registrosDinamicos[nRegDinamicos++] = contadorParticipantes;

                            registro.setIDBursatil(contadorParticipantes++);
                            registro.setFecha(fecha.toString());
                            confirmacionRegistro.setRegistro(registro);

                            mensajeRespuesta.setPerformative(ACLMessage.CONFIRM);
                            mensajeRespuesta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                            mensajeRespuesta.setLanguage(codec.getName());
                            mensajeRespuesta.setOntology(ontologia.getName());
                            getContentManager().fillContent(mensajeRespuesta, confirmacionRegistro);
                            send(mensajeRespuesta); //Se confirma el registro con el ID y la fecha en un PredicadoConfirmarRegistro
                        }
                    }

                    //RECEPCION DE OFERTAS
                    else if (mensajeOfertas != null && mensajeOfertas.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE))
                    {
                        if(mensajeOfertas.getPerformative()==ACLMessage.PROPOSE)
                        {
                            ACLMessage mensajeRespuesta = mensajeOfertas.createReply();
                            ContentElement oferta = getContentManager().extractContent(mensajeOfertas);
                            //OFERTAS DE COMPRA

                            int IDProducto = -1;
                            boolean OfCompra = false;
                            boolean aceptada = true;


                            if (oferta instanceof PredicadoRegistrarCompra) {

                                PredicadoRegistrarCompra predComp = ((PredicadoRegistrarCompra)oferta);
                                emisor = mensajeOfertas.getSender();
                                predComp.getOfertaCompra().setIDOfertaCompra(contadorOfertasCompra++);
                                IDProducto = predComp.getProducto().getIDProducto();
                                float precio = predComp.getOfertaCompra().getPrecioCompra();

                                System.out.println(getLocalName()+" -> Oferta compra de "+emisor.getLocalName()+" -- Cantidad = "+ predComp.getOfertaCompra().getCantidad()+" --- Precio = "+ predComp.getOfertaCompra().getPrecioCompra());

                                if(PrecioPorEncima[IDProducto] >= precio && PrecioPorDebajo[IDProducto] <= precio)
                                {
                                    ofertasCompra.get(IDProducto).add(new OfertaCompra(emisor, predComp.getOfertaCompra(), predComp.getProducto(),1));
                                    //caducidadOfertasCompra.get(IDProducto).add(1);

                                    //if(IDProducto == 0)
                                    //System.out.println(getLocalName()+" -> Oferta compra de "+predComp.getOfertaCompra().getPrecioCompra());

                                    contCom++;
                                    OfCompra = true;

                                    if(ConceptosLlenos[IDProducto] == false)
                                    {
                                        conceptosProductos[IDProducto] =  predComp.getProducto();
                                        ConceptosLlenos[IDProducto] = true;
                                    }

                                    bufferOfertasCompra.add(new InfoOfertaCompra(predComp.getOfertaCompra().getIDOfertaCompra(), predComp.getOfertaCompra().getIDComprador(), IDProducto, predComp.getOfertaCompra().getPrecioCompra(), simulacionActual, predComp.getOfertaCompra().getCantidad()));

                                    PredicadoConfirmarOferta confirmacion = new PredicadoConfirmarOferta();
                                    confirmacion.setConfirmacionOferta("OfertaCompra");
                                    mensajeRespuesta.setPerformative(ACLMessage.CONFIRM);
                                    mensajeRespuesta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                                    mensajeRespuesta.setLanguage(codec.getName());
                                    mensajeRespuesta.setOntology(ontologia.getName());
                                    getContentManager().fillContent(mensajeRespuesta, confirmacion);
                                    send(mensajeRespuesta);
                                }
                                else
                                {
                                    System.out.println(getLocalName()+" -> Oferta compra Rechazada de "+emisor.getLocalName()+" -- Cantidad = "+ predComp.getOfertaCompra().getCantidad()+" --- Precio = "+ predComp.getOfertaCompra().getPrecioCompra());
                                    enviarNotCancelarCompra(new OfertaCompra(emisor, predComp.getOfertaCompra(), predComp.getProducto(),1),"tarde",IDProducto);
                                    aceptada = false;
                                }
                            }

                            //OFERTA DE VENTA
                            else if (oferta instanceof PredicadoRegistrarVenta) {
                                PredicadoRegistrarVenta predVenta = ((PredicadoRegistrarVenta)oferta);
                                emisor = mensajeOfertas.getSender();
                                predVenta.getOfertaVenta().setIDOfertaVenta(contadorOfertasVenta++);

                                IDProducto = predVenta.getProducto().getIDProducto();
                                float precio = predVenta.getOfertaVenta().getPrecioVenta();
                                System.out.println(getLocalName()+" -> Oferta venta de "+emisor.getLocalName()+" -- Cantidad = "+ predVenta.getOfertaVenta().getCantidad()+" --- Precio = "+ predVenta.getOfertaVenta().getPrecioVenta());

                                if(PrecioPorEncima[IDProducto] >= precio && PrecioPorDebajo[IDProducto] <= precio)
                                {
                                    ofertasVenta.get(IDProducto).add(new OfertaVenta(emisor, predVenta.getOfertaVenta(), predVenta.getProducto(),1));
                                    //caducidadOfertasVenta.get(IDProducto).add(1);

                                    contVen++;
                                    OfCompra = false;

                                    //Llenar la informacion para el envio de las notificaciones de las sesiones CallMarket
                                    if(ConceptosLlenos[IDProducto] == false)
                                    {
                                        conceptosProductos[IDProducto] =  predVenta.getProducto();
                                        ConceptosLlenos[IDProducto] = true;
                                    }


                                    bufferOfertasVenta.add(new InfoOfertaVenta(predVenta.getOfertaVenta().getIDOfertaVenta(), predVenta.getOfertaVenta().getIDVendedor(), IDProducto, predVenta.getOfertaVenta().getPrecioVenta(), simulacionActual, predVenta.getOfertaVenta().getCantidad()));

                                    PredicadoConfirmarOferta confirmacion = new PredicadoConfirmarOferta();
                                    confirmacion.setConfirmacionOferta("OfertaVenta");
                                    mensajeRespuesta.setPerformative(ACLMessage.CONFIRM);
                                    mensajeRespuesta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                                    mensajeRespuesta.setLanguage(codec.getName());
                                    mensajeRespuesta.setOntology(ontologia.getName());
                                    getContentManager().fillContent(mensajeRespuesta, confirmacion);
                                    send(mensajeRespuesta);
                                }
                                 else
                                {
                                    System.out.println(getLocalName()+" -> Oferta venta Rechazada de "+emisor.getLocalName()+" -- Cantidad = "+ predVenta.getOfertaVenta().getCantidad()+" --- Precio = "+ predVenta.getOfertaVenta().getPrecioVenta());
                                    enviarNotCancelarVenta(new OfertaVenta(emisor, predVenta.getOfertaVenta(), predVenta.getProducto(),1),"tarde",IDProducto);
                                    aceptada = false;
                                 }

                            }

                            // Aqui es donde se lleva a cabo el calce
                        //if (!SessionContinuous(IDProducto)){
                        //System.out.println("OfCompra "+OfCompra );
                            if(aceptada)
                            {
                                if(simulacionCorriendo)
                                {
                                    if(EstaEnCallMarket[IDProducto] == false){
                                        if (!SessionContinuousMejorada(IDProducto,OfCompra)) {
                                            PredicadoEnEspera noCalzo = new PredicadoEnEspera();
                                            noCalzo.setEnEspera("No Calzo");

                                            ACLMessage mensajeEspera = new ACLMessage(ACLMessage.INFORM);
                                            mensajeEspera.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                                            mensajeEspera.setLanguage(codec.getName());
                                            mensajeEspera.setOntology(ontologia.getName());
                                            mensajeEspera.addReceiver(emisor);
                                            getContentManager().fillContent(mensajeEspera, noCalzo);
                                            send(mensajeEspera);
                                        }
                                    }
                                    else
                                    {
                                            PredicadoEnEspera noCalzo = new PredicadoEnEspera();
                                            noCalzo.setEnEspera("No Calzo");

                                            ACLMessage mensajeEspera = new ACLMessage(ACLMessage.INFORM);
                                            mensajeEspera.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                                            mensajeEspera.setLanguage(codec.getName());
                                            mensajeEspera.setOntology(ontologia.getName());
                                            mensajeEspera.addReceiver(emisor);
                                            getContentManager().fillContent(mensajeEspera, noCalzo);
                                            send(mensajeEspera);

                                        //CSystem.out.println("NUeva oferta en sesion call market!!!!!!!!!!");


                                        if(graficarLibro)
                                        {
                                            mostrarInfoPrecios(IDProducto,1);
                                            if(IDProducto == onSimulation.ProductoActual)
                                            {
                                                ordenarOfertasCompra(IDProducto);//ordena desendentemente
                                                ordenarOfertasVenta(IDProducto);//ordena ascendentemente
                                                onSimulation.TablaLibroOfertas(ofertasVentaTmp.get(IDProducto), ofertasCompraTmp.get(IDProducto));
                                            }
                                        }

                                    }
                                }
                                else
                                {
                                            PredicadoEnEspera noCalzo = new PredicadoEnEspera();
                                            noCalzo.setEnEspera("No Calzo");

                                            ACLMessage mensajeEspera = new ACLMessage(ACLMessage.INFORM);
                                            mensajeEspera.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                                            mensajeEspera.setLanguage(codec.getName());
                                            mensajeEspera.setOntology(ontologia.getName());
                                            mensajeEspera.addReceiver(emisor);
                                            getContentManager().fillContent(mensajeEspera, noCalzo);
                                            send(mensajeEspera);
                                }
                            }
                        }

                    }

                   // mensajeOfertas = myAgent.receive();
                }
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }
        }

        @SuppressWarnings("CallToThreadDumpStack")
        public boolean done()
        {
            if (finalizado) {
                System.out.println(getLocalName()+" -> Entro al metodo de done - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));

                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));

                 ACLMessage mensaje = myAgent.receive(mt);
                while(mensaje!=null)
                {
                    dumpMessage(mensaje);
                     mensaje = myAgent.receive(mt);

                }


               // System.out.println(getLocalName() + " -> Beneficios Seguro -> "+ganancia);
                // Se ingresan las ofertas de compra a la BD
                BD = new ConexionBD("mysql");
                String res;
                for (int i=0; i<bufferOfertasCompra.size(); i++) {
                    res = BD.insertarOfertaCompra(bufferOfertasCompra.get(i).getIDOfertaCompra(), bufferOfertasCompra.get(i).getIDAgente(), bufferOfertasCompra.get(i).getIDProducto(),
                            bufferOfertasCompra.get(i).getPrecioCompra(), bufferOfertasCompra.get(i).getFecha(), bufferOfertasCompra.get(i).getCantidad(),Repeticion);
                }
                //BD.cerrarConexion();
                // Se ingresa las ofertas de venta a la BD
                //BD = new ConexionBD("mysql");
                for (int i=0; i<bufferOfertasVenta.size(); i++) {
                    res = BD.insertarOfertaVenta(bufferOfertasVenta.get(i).getIDOfertaVenta(), bufferOfertasVenta.get(i).getIDAgente(), bufferOfertasVenta.get(i).getIDProducto(),
                            bufferOfertasVenta.get(i).getPrecioVenta(), bufferOfertasVenta.get(i).getFecha(), bufferOfertasVenta.get(i).getCantidad(), Repeticion);
                }
                //BD.cerrarConexion();
                // Se ingresan los calces realizados en el período que terminó a la BD
                //BD = new ConexionBD("mysql");
                for (int i=0; i<bufferCalces.size(); i++) {
                    res = BD.insertarCalce(/*bufferCalces.get(i).getIDCalce(),*/ bufferCalces.get(i).getIDOfertaCompra(), bufferCalces.get(i).getIDOfertaVenta(),
                            bufferCalces.get(i).getIDProducto(), bufferCalces.get(i).getPrecioCompra(), bufferCalces.get(i).getPrecioVenta(),
                            bufferCalces.get(i).getFecha(), bufferCalces.get(i).getCantidad(), Repeticion);
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<bufferCalcesCM.size(); i++) {
                    res = BD.insertarCalceCM(/*bufferCalcesCM.get(i).getIDCalce(),*/ bufferCalcesCM.get(i).getIDOfertaCompra(), bufferCalcesCM.get(i).getIDOfertaVenta(),
                            bufferCalcesCM.get(i).getIDProducto(), bufferCalcesCM.get(i).getPrecioCompra(), bufferCalcesCM.get(i).getCantidad(),
                            bufferCalcesCM.get(i).getFecha(),Repeticion);
                }
                //BD.cerrarConexion();
                
                // Se ingresa la informacion de la subasta en el array para que
                // los agentes puedan ver esta informacion
                InfoSubasta info = new InfoSubasta();
                precioCotizacion = new float[cantidadProductos];
                cantidadCotizacion = new int[cantidadProductos];

                for (int i=0; i<cantidadProductos; i++) {
                    info = new InfoSubasta();
                    try {
                        info.setIDAccion(i);
                        // Precio de inicio de la subasta actual
                        //BD = new ConexionBD("mysql");
                        ResultSet r1 = null;
                        if(simulacionActual == 1)
                        r1 = BD.consulta("SELECT precioCierre FROM cotizacion WHERE fecha = " + (simulacionActual-1) + " AND repeticion = "+0+" AND idproducto = "+i);
                        else
                        r1 = BD.consulta("SELECT precioCierre FROM cotizacion WHERE fecha = " + (simulacionActual-1) + " AND repeticion = "+Repeticion+" AND idproducto = "+i);
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
                        r3 = BD.consulta("SELECT max((precioCompra+precioVenta)/2) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacionActual+" AND repeticion = "+Repeticion);
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
                        r4 = BD.consulta("SELECT min((precioCompra+precioVenta)/2) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacionActual+" AND repeticion = "+Repeticion);
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
                        r5 = BD.consulta("SELECT avg((precioCompra+precioVenta)/2) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacionActual+" AND repeticion = "+Repeticion);
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
                        r6 = BD.consulta("SELECT sum(cantidad) FROM calce WHERE IDProducto="+i+" AND fecha=" + simulacionActual+" AND repeticion = "+Repeticion);
                        if (r6 != null) {
                            if (r6.next())
                                info.setCantidad(r6.getInt(1));
                            else
                                info.setCantidad(0);
                            if (r6.wasNull())
                                info.setCantidad(0);
                        }
                        //BD.cerrarConexion();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    info.setPeriodo(simulacionActual);
                    
                    int n = historicoSubastas.get(i).size();
                    
                 
                            cantidadCotizacion[i] = info.getCantidad();
                            precioCotizacion[i] =PrecioReferencia[i];

                            //BD = new ConexionBD("mysql");
                            res = BD.insertarCotizacion(i, simulacionActual, info.getPrecioInicio(), precioCotizacion[i], info.getPrecioMax(),
                                    info.getPrecioMin(), info.getPrecioPromedio(), cantidadCotizacion[i],Repeticion);
                            //BD.cerrarConexion();
                            info.setPrecioPromedio(info.getPrecioPromedio());
                            info.setCantidad(cantidadCotizacion[i]);
                        
                       
                    
                    
                    historicoSubastas.get(i).add(info);
                }
                
                //Se actualizan las volatilidades de las acciones
                for (int i=0; i<cantidadProductos; i++) {
                    int n = historicoSubastas.get(i).size();
                    volatilidadActual[i] = (float)Math.log(historicoSubastas.get(i).get(n-1).getPrecioCierre()/historicoSubastas.get(i).get(n-2).getPrecioCierre());
                }

                

                //BD = new ConexionBD("mysql");
                for (int i=0; i<numAgentes; i++) {
                    //if (participantes[i+1] != null)
                    {
                        res = BD.insertarSaldoXAgente(i, simulacionActual, saldosAgentes.get(i),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<numAgentes; i++) {
                    //if (participantes[i+1] != null)
                    {
                        for (int j=0; j<cantidadProductos; j++) {
                            res = BD.insertarAccionesXAgente(i, j, simulacionActual, accionesxagente.get(i).get(j),Repeticion);
                        }
                    }
                }
                //BD.cerrarConexion();

                // Se ingresan a la base de datos los valores de cada indicador, por cada agente tecnico
                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesCorto.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesCorto.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosPMD.get(i), j, simulacionActual, "PDMCorto", datosPromediosMovilesCorto.get(i).get(j),Repeticion);
                    }
                }
               // BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesLargo.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesLargo.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosPMD.get(i), j, simulacionActual, "PDMLargo", datosPromediosMovilesLargo.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();
                
                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesSimple.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesSimple.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosPMS.get(i), j, simulacionActual, "PMSimple", datosPromediosMovilesSimple.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesTripleCorto.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesTripleCorto.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosPMT.get(i), j, simulacionActual, "PMTCorto", datosPromediosMovilesTripleCorto.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesTripleMedio.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesTripleMedio.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosPMT.get(i), j, simulacionActual, "PMTMedio", datosPromediosMovilesTripleMedio.get(i).get(j),Repeticion);
                    }
                }
               // BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosPromediosMovilesTripleLargo.size(); i++) {
                    for (int j=0; j<datosPromediosMovilesTripleLargo.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosPMT.get(i), j, simulacionActual, "PMTLargo", datosPromediosMovilesTripleLargo.get(i).get(j),Repeticion);
                    }
                }
               // BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosRSI.size(); i++) {
                    for (int j=0; j<datosRSI.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosRSI.get(i), j, simulacionActual, "RSI", datosRSI.get(i).get(j),Repeticion);
                    }
                }
               // BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosVHF.size(); i++) {
                    for (int j=0; j<datosVHF.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosVHF.get(i), j, simulacionActual, "VHF", datosVHF.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosMACD.size(); i++) {
                    for (int j=0; j<datosMACD.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosMACD.get(i), j, simulacionActual, "MACD", datosMACD.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosMACDLineaSenal.size(); i++) {
                    for (int j=0; j<datosMACDLineaSenal.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosMACD.get(i), j, simulacionActual, "MACDSenal", datosMACDLineaSenal.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosROC.size(); i++) {
                    for (int j=0; j<datosROC.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosROC.get(i), j, simulacionActual, "ROC", datosROC.get(i).get(j),Repeticion);
                    }
                }
                //BD.cerrarConexion();

                //BD = new ConexionBD("mysql");
                for (int i=0; i<datosMomento.size(); i++) {
                    for (int j=0; j<datosMomento.get(i).size(); j++) {
                        BD.insertarAnalisisTecnico(agentesTecnicosMomento.get(i), j, simulacionActual, "Momento", datosMomento.get(i).get(j),Repeticion);
                    }
                }
                BD.cerrarConexion();

                // Si hay algun agente cuyo saldo sea cero y no tenga acciones, se saca de la simulacion
                for (int i=0; i<numAgentes; i++) {
                    int totalAcciones = 0;
                    for (int j=0; j<cantidadProductos; j++) {
                        totalAcciones += accionesxagente.get(i).get(j);
                    }
                    if (saldosAgentes.get(i) == 0 && totalAcciones == 0) {
                        participantes[i] = null;
                    }
                }

                System.out.println(getLocalName()+" -> Va a salir del metodo de done - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));

                if (simulacionActual < cantidadSimulaciones) {
                    simulacionActual++;
                    IniciarSubasta ISBehaviour = new IniciarSubasta(myAgent);
                    addBehaviour(ISBehaviour);

                    //removeBehaviour(this);
                    
                    tiempoInicio = new GregorianCalendar().getTimeInMillis(); // Se guarda el tiempo de inicio
                }
                else
                {
                    try {
                        pararSub = new FileWriter("Continuar.txt");
                    
                    paraSubastador = new PrintWriter(pararSub);
                    paraSubastador.println(0);
                    paraSubastador.close();
                      File file = new File("Continuar.txt");
                      file.delete();
                    
                        /*AgenteSubastador.super.getContainerController().kill();

                    } catch (StaleProxyException ex) {
                        Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
                    */}catch (IOException ex) {
                        Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
            return finalizado;
        }
    }

    private void mostrarInfoPrecios(int IDProducto, int tipo)
    {
            float mayorVenta,menorVenta,mayorCompra,menorCompra;

            mayorVenta = menorVenta = mayorCompra = menorCompra = 0;

            if(ofertasVenta.get(IDProducto).size() > 0 )
            {
                mayorVenta = Collections.max(ofertasVenta.get(IDProducto)).getInfo().getPrecioVenta();
                menorVenta = Collections.min(ofertasVenta.get(IDProducto)).getInfo().getPrecioVenta();
            }
            if(ofertasCompra.get(IDProducto).size() > 0)
            {
                mayorCompra = Collections.max(ofertasCompra.get(IDProducto)).getInfo().getPrecioCompra();
                menorCompra = Collections.min(ofertasCompra.get(IDProducto)).getInfo().getPrecioCompra();
            }

            float mayor = mayorVenta;
            float menor = menorVenta;

            if(mayorCompra> mayor)
                mayor = mayorCompra;

            if(menorCompra< menor)
                menor = menorCompra;

            //System.out.println(mayor+"     "+menor);
            //tipo 1 es para CallMarket, tipo 0 Continuous
            onSimulation.TablaInformacion(nombreAcciones[IDProducto],tipo,mayor,menor,false, IDProducto);
    }

    private void ordenarOfertasVenta(int idAccion)
    {
        /*ofertasVentaTmp = new ArrayList<OfertaVenta>();

        for (int i=0; i<ofertasVenta.size(); i++) {
            if (ofertasVenta.get(i).getProducto().getIDProducto() == idAccion) {
                ofertasVentaTmp.add(ofertasVenta.get(i));
            }
        }
        contVenTmp = ofertasVentaTmp.size();*/

        obtenerOfertasVenta(idAccion);

        if(ofertasVentaTmp.get(idAccion).size() > 0)
        Collections.sort(ofertasVentaTmp.get(idAccion));
    }

    private void ordenarOfertasCompra(int idAccion)
    {
        /*ofertasCompraTmp = new ArrayList<OfertaCompra>();

        for (int i=0; i<ofertasCompra.size(); i++) {
            if (ofertasCompra.get(i).getProducto().getIDProducto() == idAccion) {
                ofertasCompraTmp.add(ofertasCompra.get(i));
            }
        }
        contComTmp = ofertasCompraTmp.size();*/

        obtenerOfertasCompra(idAccion);
        

        if(ofertasCompraTmp.get(idAccion).size()>0)
        {
            Collections.sort(ofertasCompraTmp.get(idAccion));
            Collections.reverse(ofertasCompraTmp.get(idAccion));
        }

         /*for (int i=0; i<ofertasCompra.get(idAccion).size(); i++) {
           System.out.println(ofertasCompraTmp.get(i).getInfo().getPrecioCompra()+"  ----  "+ofertasCompra.get(idAccion).get(i).getInfo().getPrecioCompra());

        }*/

        
    }

  

    //////////////////////////
    /////////////NUEVO///////
    ////////////////////////
    ///////////////////////

   private void CanalNegociacion(float ValorAjuste, int idAccion)
   {
       PrecioPorEncima[idAccion] = PrecioReferencia[idAccion]+PrecioReferencia[idAccion]*porcentajeCanal[idAccion]*ValorAjuste;
       PrecioPorDebajo[idAccion] = PrecioReferencia[idAccion]-PrecioReferencia[idAccion]*porcentajeCanal[idAccion]*ValorAjuste;

       System.out.println("-------------------------------------------------");
       System.out.println("IDAccion "+idAccion+"\nCanal por encima "+PrecioPorEncima[idAccion]);
        System.out.println("Precio Referencia "+PrecioReferencia[idAccion]);
        System.out.println("Canal por debajo "+PrecioPorDebajo[idAccion]);
        System.out.println("-------------------------------------------------");
   }

    ////Obtiene las ofertas de venta de una determinada accion, conservando el orden de presedencia de las mismas
    ///por el tiempo de llegada, adicionalmente guarda el indice de la ultima oferta que ha llegado
    private void obtenerOfertasVenta(int idAccion)
    {
        ofertasVentaTmp.add(idAccion, new ArrayList<OfertaVenta>(ofertasVenta.get(idAccion)));

        /*for (int i=0; i<ofertasVenta.size(); i++) {
            if (ofertasVenta.get(i).getProducto().getIDProducto() == idAccion) {
                ofertasVentaTmp.add(ofertasVenta.get(i));
            }
        }*/

        

       // System.out.println(getLocalName()+ "  -  "+ofertasVenta.get(idAccion).size()+"  --  "+ofertasVentaTmp.size());
        contVenTmp = ofertasVentaTmp.get(idAccion).size();

    }

    ////Obtiene las ofertas de compra de una determinada accion, conservando el orden de presedencia de las mismas
    ///por el tiempo de llegada, adicionalmente guarda el indice de la ultima oferta que ha llegado
    private void obtenerOfertasCompra(int idAccion)
    {
        ofertasCompraTmp.add(idAccion,new ArrayList<OfertaCompra>(ofertasCompra.get(idAccion)));

        /*for (int i=0; i<ofertasCompra.size(); i++) {
             
                ofertasCompraTmp.add(ofertasCompra.get(idAccion).get(i));
            
        }*/

        //ofertasCompraTmp = ofertasCompra.get(idAccion);

        //System.out.println(getLocalName()+ "  -  "+ofertasCompra.get(idAccion).size()+"  --  "+ofertasCompraTmp.size());

        contComTmp = ofertasCompraTmp.get(idAccion).size();

    }

    

    //Se envia la notificacion que se a pasado de una sesion Continuous a CallMarket, para una determinada accion
    @SuppressWarnings("CallToThreadDumpStack")
    private void InicioCallMarketSubastador(int Accion)
    {
            EstaEnCallMarket[Accion] = true;
            System.out.println(getLocalName()+" -> Enviar mensaje Inicio Call Market para accion S- "+Accion+" - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            try {
                PredicadoInicioCallMarket inicioCM = new PredicadoInicioCallMarket();
                ACLMessage mensajeInicioCallMarket= new ACLMessage(ACLMessage.INFORM);
                inicioCM.setProducto(conceptosProductos[Accion]);


                mensajeInicioCallMarket.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeInicioCallMarket.setLanguage(codec.getName());
                mensajeInicioCallMarket.setOntology(ontologia.getName());
                
                mensajeInicioCallMarket.setSender(getAID());
                mensajeInicioCallMarket.addReceiver(getAID());
                getContentManager().fillContent(mensajeInicioCallMarket, inicioCM);


                send(mensajeInicioCallMarket);
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private void InicioCallMarketAgentes(int Accion)
    {
        System.out.println(getLocalName()+" INICIO MENSAJE INICIO CALL MARKET Agentes - "+Accion+" - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            //ConceptoProducto producto;
            PredicadoInicioCallMarket inicioCM;
            ACLMessage mensajeInicioCallMarket;

            try {
                //producto  = new ConceptoProducto();
                inicioCM = new PredicadoInicioCallMarket();
                mensajeInicioCallMarket = new ACLMessage(ACLMessage.INFORM);
                //producto = Accion;

                /*producto.setDescripcion(Accion.getDescripcion());
                producto.setIDProducto(Accion.getIDProducto());
                producto.setNombre(Accion.getNombre());   */

                inicioCM.setProducto(conceptosProductos[Accion]);


                mensajeInicioCallMarket.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeInicioCallMarket.setLanguage(codec.getName());
                mensajeInicioCallMarket.setOntology(ontologia.getName());
                getContentManager().fillContent(mensajeInicioCallMarket, inicioCM);

                for (int i=0; i<contadorParticipantes;i++) {
                    if (participantes[i] != null)
                        mensajeInicioCallMarket.addReceiver(participantes[i]);
                }
                System.out.println(getLocalName()+" INICIO MENSAJE INICIO CALL MARKET Agentes - antes de send "+Accion+" - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                send(mensajeInicioCallMarket);
                System.out.println(getLocalName()+" -> FIN MENSAJE INICIO CALL MARKET Agentes - "+Accion+" - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));

            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }
    }

    //Se envia la notificacion que se a pasado de una sesion Continuous a CallMarket, para una determinada accion
    @SuppressWarnings("CallToThreadDumpStack")
    private void FinCallMarketSubastador(int Accion)
    {
        System.out.println(getLocalName()+" -> MENSAJE FIN CALL MARKET PARA SUBASTADOR PARA ACCION "+Accion+" - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
           
            try {
                PredicadoFinCallMarket finCM = new PredicadoFinCallMarket();
                ACLMessage mensajeFinCallMarket = new ACLMessage(ACLMessage.INFORM);
                finCM.setProducto(conceptosProductos[Accion]);


                mensajeFinCallMarket.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeFinCallMarket.setLanguage(codec.getName());
                mensajeFinCallMarket.setOntology(ontologia.getName());


                 mensajeFinCallMarket.setSender(getAID());
                mensajeFinCallMarket.addReceiver(getAID());
                getContentManager().fillContent(mensajeFinCallMarket, finCM);

                send(mensajeFinCallMarket);
               // System.out.println(getLocalName()+" -> Envie mensaje de Fin Call Market para subastador "+Accion);


            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private void FinCallMarketAgentes(int Accion)
    {
        System.out.println(getLocalName()+" -> INICIO MENSAJE FIN CALL MARKET Para agentes - "+Accion+" - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            //ConceptoProducto producto;
            PredicadoFinCallMarket finCM;
            ACLMessage mensajeFinCallMarket;

            try {
                //producto  = new ConceptoProducto();
                finCM = new PredicadoFinCallMarket();
                mensajeFinCallMarket = new ACLMessage(ACLMessage.INFORM);
                //producto = Accion;

                /*producto.setDescripcion(Accion.getDescripcion());
                producto.setIDProducto(Accion.getIDProducto());
                producto.setNombre(Accion.getNombre());   */

                finCM.setProducto(conceptosProductos[Accion]);


                mensajeFinCallMarket.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeFinCallMarket.setLanguage(codec.getName());
                mensajeFinCallMarket.setOntology(ontologia.getName());
                getContentManager().fillContent(mensajeFinCallMarket, finCM);

                for (int i=0; i<contadorParticipantes;i++) {
                    if (participantes[i] != null)
                        mensajeFinCallMarket.addReceiver(participantes[i]);
                }
                System.out.println(getLocalName()+" -> INICIO MENSAJE FIN CALL MARKET Para agentes - antes de send  "+Accion+" - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                send(mensajeFinCallMarket);

                System.out.println(getLocalName()+" -> FIN MENSAJE FIN CALL MARKET Para agentes - "+Accion+" - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }
    }

    //Envia mensaje de ofertas que son canceladas porque salen del canal de negociación
    @SuppressWarnings("CallToThreadDumpStack")
    private void enviarNotCancelarCompra(OfertaCompra comprador, String concepto, int idproducto)
    {

            ConceptoOfertaCompra cancelacionCompra;

            PredicadoCancelarCompra cancelarCompra;

            ACLMessage mensajeNotificacionCompra;

            try {
                cancelacionCompra  = new ConceptoOfertaCompra();

                cancelarCompra = new PredicadoCancelarCompra();

                mensajeNotificacionCompra = new ACLMessage(ACLMessage.CANCEL);

                cancelacionCompra = comprador.getInfo();

                //System.out.println(getLocalName() + " -> Precio de Compra ==> " + precioBalance);
                /*cancelacionCompra.setPrecioCompra(comprador.getInfo().getPrecioCompra());
                cancelacionCompra.setIDComprador(comprador.getInfo().getIDComprador());/////////////////
                cancelacionCompra.setFecha(fecha.toString());
                cancelacionCompra.setCantidad(comprador.getInfo().getCantidad());
                cancelacionCompra.setIDOfertaCompra(comprador.getInfo().getIDOfertaCompra());*/


                cancelarCompra.setOfertaCompra(cancelacionCompra);
                cancelarCompra.setConcepto(concepto);
                cancelarCompra.setIDProducto(idproducto);

                mensajeNotificacionCompra.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeNotificacionCompra.setLanguage(codec.getName());
                mensajeNotificacionCompra.setOntology(ontologia.getName());
                AID senderOC = comprador.getAID(); //////////////////
                mensajeNotificacionCompra.addReceiver(senderOC);
                getContentManager().fillContent(mensajeNotificacionCompra, cancelarCompra);
                send(mensajeNotificacionCompra);


            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }



    }

    //Envia mensaje de ofertas que son canceladas porque salen del canal de negociación o porque caducan
    @SuppressWarnings("CallToThreadDumpStack")
     private void enviarNotCancelarVenta(OfertaVenta vendedor, String concepto, int idproducto)
    {

            ConceptoOfertaVenta cancelacionVenta;

            PredicadoCancelarVenta cancelarVenta;

            ACLMessage mensajeNotificacioVenta;


            try {
                cancelacionVenta  = new ConceptoOfertaVenta();

                cancelarVenta = new PredicadoCancelarVenta();

                mensajeNotificacioVenta = new ACLMessage(ACLMessage.CANCEL);

                cancelacionVenta = vendedor.getInfo();

                //System.out.println(getLocalName() + " -> Precio de Compra ==> " + precioBalance);
               /* cancelacionVenta.setPrecioVenta(vendedor.getInfo().getPrecioVenta());
                cancelacionVenta.setIDVendedor(vendedor.getInfo().getIDVendedor());/////////////////
                cancelacionVenta.setFecha(fecha.toString());
                cancelacionVenta.setCantidad(vendedor.getInfo().getCantidad());
                cancelacionVenta.setIDOfertaVenta(vendedor.getInfo().getIDOfertaVenta());*/

                cancelarVenta.setConcepto(concepto);
                cancelarVenta.setOfertaVenta(cancelacionVenta);
                cancelarVenta.setIDProducto(idproducto);


                mensajeNotificacioVenta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeNotificacioVenta.setLanguage(codec.getName());
                mensajeNotificacioVenta.setOntology(ontologia.getName());
                AID senderOC = vendedor.getAID(); //////////////////
                mensajeNotificacioVenta.addReceiver(senderOC);
                getContentManager().fillContent(mensajeNotificacioVenta, cancelarVenta);
                send(mensajeNotificacioVenta);


            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }



    }

    //Recibe la información necesaria para notificar sobre un calce
    @SuppressWarnings("CallToThreadDumpStack")
    private void enviarNotificacionesCalce(OfertaCompra comprador, OfertaVenta vendedor, float precio, int cantidad, int idAccion, boolean CallMarket)
    {

            ConceptoNotificacionCompra notificacionCompra;
            ConceptoNotificacionVenta notificacionVenta;
            PredicadoNotificarCompra notificarCompra;
            PredicadoNotificarVenta notificarVenta;
            ACLMessage mensajeNotificacionCompra;
            ACLMessage mensajeNotificacionVenta;


            Date fecha = new Date();
            
            try {
                notificacionCompra  = new ConceptoNotificacionCompra();
                notificacionVenta = new ConceptoNotificacionVenta();
                notificarCompra = new PredicadoNotificarCompra();
                notificarVenta = new PredicadoNotificarVenta();
                mensajeNotificacionCompra = new ACLMessage(ACLMessage.INFORM);
                mensajeNotificacionVenta = new ACLMessage(ACLMessage.INFORM);

                //System.out.println(getLocalName() + " -> Precio de Compra ==> " + precioBalance);
                notificacionCompra.setPrecio(precio);
                notificacionCompra.setIDComprador(comprador.getInfo().getIDComprador());/////////////////
                notificacionCompra.setIDNotificacionCompra(contadorNotCompra++);
                notificacionCompra.setFecha(fecha.toString());
                notificacionCompra.setCantidad(cantidad);
                notificacionCompra.setIDProducto(idAccion);

                notificarCompra.setNotificacionCompra(notificacionCompra);

                mensajeNotificacionCompra.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeNotificacionCompra.setLanguage(codec.getName());
                mensajeNotificacionCompra.setOntology(ontologia.getName());
                AID senderOC = comprador.getAID(); //////////////////
                mensajeNotificacionCompra.addReceiver(senderOC);
                getContentManager().fillContent(mensajeNotificacionCompra, notificarCompra);
                send(mensajeNotificacionCompra);

                //System.out.println(getLocalName() + " -> Precio de Venta ==> " + precioBalance);
                notificacionVenta.setPrecio(precio);
                notificacionVenta.setIDVendedor(vendedor.getInfo().getIDVendedor());//////////////////
                notificacionVenta.setIDNotificacionVenta(contadorNotVenta++);
                notificacionVenta.setFecha(fecha.toString());
                notificacionVenta.setCantidad(cantidad);
                notificacionVenta.setIDProducto(idAccion);

                notificarVenta.setNotificacionVenta(notificacionVenta);

                mensajeNotificacionVenta.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeNotificacionVenta.setLanguage(codec.getName());
                mensajeNotificacionVenta.setOntology(ontologia.getName());
                AID senderOV = vendedor.getAID();
                mensajeNotificacionVenta.addReceiver(senderOV);
                getContentManager().fillContent(mensajeNotificacionVenta, notificarVenta);
                send(mensajeNotificacionVenta);
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }

            if(CallMarket == true)
                bufferCalcesCM.add(new InfoCalce(contadorCalcesCM++, comprador.getInfo().getIDOfertaCompra(), vendedor.getInfo().getIDOfertaVenta(), idAccion, precio, precio, simulacionActual, cantidad));
            else
            {
                bufferCalces.add(new InfoCalce(contadorCalces++, comprador.getInfo().getIDOfertaCompra(), vendedor.getInfo().getIDOfertaVenta(), idAccion, precio, precio, simulacionActual, cantidad));
                try {
                    Origen = new FileWriter("CalceRunTime.txt");
                    archivo = new PrintWriter(Origen);
                    CalcesEnRunTime++;
                    archivo.println(CalcesEnRunTime);
                    archivo.close();
                } catch (IOException ex) {
                    Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
                

    }

    @SuppressWarnings("CallToThreadDumpStack")
    private void FinDeSubasta()
    {
         try {
                PredicadoFinSubasta finalizoSubasta = new PredicadoFinSubasta();
                finalizoSubasta.setFinSubasta("Fin de la Subasta");

                ACLMessage mensaje = new ACLMessage(ACLMessage.INFORM);
                mensaje.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensaje.setLanguage(codec.getName());
                mensaje.setOntology(ontologia.getName());

                mensaje.setSender(getAID());
                mensaje.addReceiver(getAID());
                getContentManager().fillContent(mensaje, finalizoSubasta);

                send(mensaje);
                System.out.println(getLocalName()+" -> MENSAJE FIN DE LA SUBASTA a sido enviado - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
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
                    System.out.println(getLocalName()+"-> INICIO MENSAJE FIN DE SUBASTA agentes - antes de send "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                    send(mensajeFin);
                    System.out.println(getLocalName()+"-> FIN MENSAJE FIN DE SUBASTA agentes- "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                } catch (jade.content.lang.Codec.CodecException ce) {
                    ce.printStackTrace();
                } catch (jade.content.onto.OntologyException oe) {
                    oe.printStackTrace();
                }
    }


    //Segun la oferta que desate la función, se procede a verificar si es posible realizar un calce, de ser asi,
    //llama a la funcion para envar las notificaciones y adicionalmente eliminar las calzadas
    @SuppressWarnings("element-type-mismatch")
    private boolean SessionContinuousMejorada(int idAccion, boolean compra)
    {
        int i, posicion ;
        int cantidad;
    	boolean calceExitoso = false;

        if(graficarLibro)
        mostrarInfoPrecios(idAccion,0);

        obtenerOfertasCompra(idAccion);
        //obtenerOfertasVenta(idAccion);


        //System.out.println(getLocalName()+" LLego oferta");

        if(compra)
        {  //System.out.println( ofertasCompra.get(idAccion).size()+" --------- "+ofertasCompraTmp.get(idAccion).size()+"------Antes de calzar--------C "+idAccion);

            posicion = ofertasCompra.get(idAccion).size()-1;
            float precioCompra = ofertasCompra.get(idAccion).get(posicion).getInfo().getPrecioCompra();
            float precioVenta;
            for(i=0; i< ofertasVenta.get(idAccion).size(); i++ )
            {
                /*cantidad = ofertasCompra.get(idAccion).get(posicion).getInfo().getCantidad();
                if(cantidad == 0)
                    break;*/

                precioVenta = ofertasVenta.get(idAccion).get(i).getInfo().getPrecioVenta();

                //System.out.println()

                if(precioCompra >= precioVenta)
                {
                     if(precioVenta == PrecioPorEncima[idAccion] || precioVenta == PrecioPorDebajo[idAccion])
                    {

                        System.out.println(getLocalName()+" -> Se entra a Session Call Market por posible calce en el canal Accion "+idAccion+"!!!!!!!!");
                        EstaEnCallMarket[idAccion] = true;
                        //InicioCallMarketSubastador(idAccion);
                        //InicioCallMarketAgentes(idAccion);
                        InicioCallMarket(idAccion);

                        return false;
                    }
                    else
                    {
                        int cantidadCompra =  ofertasCompra.get(idAccion).get(posicion).getInfo().getCantidad();
                        int cantidadVenta = ofertasVenta.get(idAccion).get(i).getInfo().getCantidad();
                        cantidad = Math.min(cantidadCompra,cantidadVenta );
                        ofertasCompra.get(idAccion).get(posicion).getInfo().setCantidad(cantidadCompra - cantidad);
                        ofertasVenta.get(idAccion).get(i).getInfo().setCantidad(cantidadVenta - cantidad);

                        enviarNotificacionesCalce(ofertasCompra.get(idAccion).get(posicion),ofertasVenta.get(idAccion).get(i),precioVenta, cantidad, idAccion,false);

                        //System.out.println(getLocalName()+" -> Calce a un precio de  ---  "+precioVenta);

                        calceExitoso = true;

                        if(graficarLibro)
                        onSimulation.serie(PrecioPorEncima[idAccion], PrecioPorDebajo[idAccion], PrecioReferencia[idAccion], precioVenta, idAccion);
                        //System.out.println(precioVenta+"  ----  "+idAccion+"  ----  "+Libro.ProductoActual);

                        if(ofertasCompra.get(idAccion).get(posicion).getInfo().getCantidad()==0)
                        {
                            //System.out.println(getLocalName()+" --- Elimino en calce CA  -C-  "+ofertasCompra.get(idAccion).size());
                            ofertasCompra.get(idAccion).remove(posicion);
                            //System.out.println(getLocalName()+" --- Elimino en calce CD  -C-  "+ofertasCompra.get(idAccion).size());
                            break;
                        }
                        if(ofertasVenta.get(idAccion).get(i).getInfo().getCantidad()==0)
                        {
                            //System.out.println(getLocalName()+" --- Elimino en calce VA -C-  "+ofertasVenta.get(idAccion).size());
                            
                            ofertasVenta.get(idAccion).remove(i);
                            //System.out.println(getLocalName()+" --- Elimino en calce VD  -C-  "+ofertasVenta.get(idAccion).size());
                            i--;
                        }

                    }


                }
                //System.out.println( ofertasCompra.get(idAccion).size()+" --------- "+ofertasCompraTmp.get(idAccion).size()+"------Calzando--------C "+idAccion);

            }

            /*if(calceExitoso)
            {
                if(i == ofertasVentaTmp.get(idAccion).size())
                {
                    //System.out.println(getLocalName()+" - ofertasVentaTmp.get(idAccion) - "+ i);
                eliminarCalzadosContinuous(i-1,compra, idAccion);
                }
                else
                {
                    //System.out.println(getLocalName()+" - "+ i + " - ofertasVentaTmp - "+ofertasVentaTmp.get(idAccion).size());
                eliminarCalzadosContinuous(i,compra, idAccion);
                }
            }*/

        }
        else
        {
            //System.out.println( ofertasVenta.get(idAccion).size()+" --------- "+ofertasVentaTmp.get(idAccion).size()+"------Antes de calzar--------V "+idAccion);
            posicion = ofertasVenta.get(idAccion).size()-1;
            float precioVenta = ofertasVenta.get(idAccion).get(posicion).getInfo().getPrecioVenta();
            float precioCompra;
            for(i=0; i< ofertasCompra.get(idAccion).size(); i++ )
            {
                cantidad = ofertasVenta.get(idAccion).get(posicion).getInfo().getCantidad();
                if(cantidad == 0)
                    break;

                precioCompra = ofertasCompra.get(idAccion).get(i).getInfo().getPrecioCompra();

                if(precioCompra >= precioVenta)
                {
                    if(precioCompra == PrecioPorEncima[idAccion] || precioCompra == PrecioPorDebajo[idAccion])
                    {
                        System.out.println(getLocalName()+" -> Se entra a Session Call Market por posible calce en el canal Accion "+idAccion+"!!!!!!!!");
                        EstaEnCallMarket[idAccion] = true;
                        //InicioCallMarketSubastador(idAccion);
                        //InicioCallMarketAgentes(idAccion);
                        InicioCallMarket(idAccion);


                        return false;
                    }
                    else
                    {
                        int cantidadCompra =  ofertasCompra.get(idAccion).get(i).getInfo().getCantidad();
                        int cantidadVenta = ofertasVenta.get(idAccion).get(posicion).getInfo().getCantidad();
                        cantidad = Math.min(cantidadCompra, cantidadVenta);
                        ofertasCompra.get(idAccion).get(i).getInfo().setCantidad(cantidadCompra - cantidad);
                        ofertasVenta.get(idAccion).get(posicion).getInfo().setCantidad(cantidadVenta - cantidad);

                        enviarNotificacionesCalce(ofertasCompra.get(idAccion).get(i),ofertasVenta.get(idAccion).get(posicion),precioCompra, cantidad, idAccion,false);
                        calceExitoso = true;

                        //System.out.println(getLocalName()+" -> Calce a un precio de  ---  "+precioCompra);

                        if(graficarLibro)
                        onSimulation.serie(PrecioPorEncima[idAccion], PrecioPorDebajo[idAccion], PrecioReferencia[idAccion], precioCompra, idAccion);
                        //System.out.println(precioCompra+"  ----  "+idAccion+"  ----  "+Libro.ProductoActual);
                        if(ofertasCompra.get(idAccion).get(i).getInfo().getCantidad()==0)
                        {
                            //System.out.println(getLocalName()+" --- Elimino en calce CA  -V-  "+ofertasCompra.get(idAccion).size());
                            ofertasCompra.get(idAccion).remove(i);
                            //System.out.println(getLocalName()+" --- Elimino en calce CD  -V-  "+ofertasCompra.get(idAccion).size());
                            i--;
                        }
                        if(ofertasVenta.get(idAccion).get(posicion).getInfo().getCantidad()==0)
                        {
                            //System.out.println(getLocalName()+" --- Elimino en calce VA  -V-  "+ofertasVenta.get(idAccion).size());
                            ofertasVenta.get(idAccion).remove(posicion);
                            //System.out.println(getLocalName()+" --- Elimino en calce VD  -V-  "+ofertasVenta.get(idAccion).size());
                            break;
                        }
                    }
                }

                //System.out.println( ofertasVenta.get(idAccion).size()+" --------- "+ofertasVentaTmp.get(idAccion).size()+"------Calzando--------V "+idAccion);

            }
            /*if(calceExitoso)
            {
                if(i== ofertasCompraTmp.get(idAccion).size())
                {
                    //System.out.println(getLocalName()+" - ofertasCompraTmp - "+ i);
                    eliminarCalzadosContinuous(i-1,compra, idAccion);
                }
                else
                {
                    //System.out.println(getLocalName()+" - "+ i+" - ofertasCompraTmp - "+ofertasCompraTmp.size());
                    eliminarCalzadosContinuous(i-1,compra, idAccion);
                }
            }*/


        }

        //////////////////////////////
        if(graficarLibro)
        if(idAccion == onSimulation.ProductoActual)
         onSimulation.TablaLibroOfertas(ofertasVenta.get(idAccion), ofertasCompra.get(idAccion));/////

        return calceExitoso;
    }
    

   private boolean SessionCallMarket(int idAccion)
    {

       obtenerOfertasCallMarketMejorado(idAccion);

        //CanalNegociacion((float)2.5,idAccion);

        boolean calceExitoso = false;
        float precio = 0;
        int contMaxAdj, contMinDesv;
        contMaxAdj = contMinDesv = 1;
        int i,j;

        /*System.out.println("Precio    Qc       Qv      Calce     Desbalance");
        for(i=0;i<ofertasCallSession.get(idAccion).size();i++)
        {
            System.out.print(ofertasCallSession.get(idAccion).get(i).getPrecio() + "    "+ofertasCallSession.get(idAccion).get(i).getCantidadC());
            System.out.print("     "+ofertasCallSession.get(idAccion).get(i).getCantidadV() + "    "+ofertasCallSession.get(idAccion).get(i).getCalce());
            System.out.println("     "+ofertasCallSession.get(idAccion).get(i).getDesvalance());
        }*/
        if(ofertasCallSession.get(idAccion).isEmpty())
        {
            System.out.println(getLocalName()+" -> No hay ofertas para negociar en Call Market para la accion "+idAccion+"!!!!!!!!!!!");
            CanalNegociacion((float)1,idAccion);
            return false;
        }

        for(i=1;i<ofertasCallSession.get(idAccion).size();i++)
        {
            if(ofertasCallSession.get(idAccion).get(0).getCalce()==ofertasCallSession.get(idAccion).get(i).getCalce())
                contMaxAdj++;
            else
                break;
        }

        if(ofertasCallSession.get(idAccion).get(0).getCalce()>0)
        {
                System.out.println(getLocalName()+" -> La cantidad a negociar en Call Market para la accion "+idAccion+" es "+ofertasCallSession.get(idAccion).get(0).getCalce());

            if(contMaxAdj==1)
            {
                precio = ofertasCallSession.get(idAccion).get(0).getPrecio();

            }
            else
            {

                //System.out.println("La cantidad a negociar es "+ofertasCallSession.get(0).getCalce());
                OfertaCallMarket ofertasDesvalance[] = new OfertaCallMarket[contMaxAdj];
                OfertaCallMarket ofertasAux = new OfertaCallMarket();

                for(i=0;i<contMaxAdj;i++)
                    ofertasDesvalance[i]=ofertasCallSession.get(idAccion).get(i);

                for(i=0;i<contMaxAdj-1;i++)
                {
                    for(j=i+1;j<contMaxAdj;j++)
                    {
                        if(ofertasDesvalance[i].getDesvalance()>ofertasDesvalance[j].getDesvalance())
                        {
                            ofertasAux = ofertasDesvalance[i];
                            ofertasDesvalance[i] = ofertasDesvalance[j];
                            ofertasDesvalance[j] = ofertasAux;
                        }
                    }
                }

                for(i=1;i<contMaxAdj;i++)
                {
                    if(ofertasDesvalance[0].getDesvalance()==ofertasDesvalance[i].getDesvalance())
                        contMinDesv++;
                    else
                        break;
                }

                if(contMinDesv == 1)
                {
                    precio=ofertasDesvalance[0].getPrecio();
                }
                else
                {
                    if(ofertasDesvalance[0].getCantidadC()>ofertasDesvalance[0].getCantidadV())
                    {
                        precio = ofertasDesvalance[0].getPrecio();
                        for(i=1;i<contMinDesv;i++)
                        {
                            if(precio<ofertasDesvalance[i].getPrecio())
                                precio=ofertasDesvalance[i].getPrecio();
                        }
                    }
                    else
                    {
                        if(ofertasDesvalance[0].getCantidadC()<ofertasDesvalance[0].getCantidadV())
                        {
                            precio = ofertasDesvalance[0].getPrecio();
                            for(i=1;i<contMinDesv;i++)
                            {
                                if(precio>ofertasDesvalance[i].getPrecio())
                                    precio=ofertasDesvalance[i].getPrecio();
                            }
                        }
                        else
                        {
                            for(i=0;i<contMinDesv;i++)
                            precio+=ofertasDesvalance[i].getPrecio();

                            precio/=contMinDesv;
                        }
                    }
                }
            }

                    //////////////////////////////////////////

                           System.out.println(getLocalName()+" -> El precio a negociar en Call Market para accion "+idAccion+" es "+precio);
//System.out.println( ofertasCompra.get(idAccion).size()+" --------- "+ofertasCompraTmp.get(idAccion).size()+"------Antes de ordenar-------- "+idAccion);

                    ordenarOfertasCompra(idAccion);
                    ordenarOfertasVenta(idAccion);


                    Collections.reverse(ofertasVentaTmp.get(idAccion)); //las de venta estarian ordenadas de mayor a menor
                    Collections.reverse(ofertasCompraTmp.get(idAccion));//las de compra estarian ordenadas de menor a mayor,

//System.out.println( ofertasCompra.get(idAccion).size()+" --------- "+ofertasCompraTmp.get(idAccion).size()+"------Despues de ordenar-------- "+idAccion);

                    int indiceVentas, indiceCompras, cantidad, indiceVentasAux;

                    for(indiceVentas=0; indiceVentas< ofertasVentaTmp.get(idAccion).size(); indiceVentas++ )
                        if(ofertasVentaTmp.get(idAccion).get(indiceVentas).getInfo().getPrecioVenta()<=precio)
                            break;

                    for(indiceCompras=0; indiceCompras< ofertasCompraTmp.get(idAccion).size(); indiceCompras++ )
                        if(ofertasCompraTmp.get(idAccion).get(indiceCompras).getInfo().getPrecioCompra()>=precio)
                            break;


                    indiceVentasAux = indiceVentas;

                    
                    for(i=indiceCompras; i< ofertasCompraTmp.get(idAccion).size(); i++ )
                    {
                            for(j=indiceVentas;j< ofertasVentaTmp.get(idAccion).size();j++)
                            {
                                if(ofertasCompraTmp.get(idAccion).get(i).getInfo().getCantidad()>0)
                                {
                                    if(ofertasVentaTmp.get(idAccion).get(j).getInfo().getCantidad()>0)
                                    {
                                        cantidad = Math.min(ofertasCompraTmp.get(idAccion).get(i).getInfo().getCantidad(), ofertasVentaTmp.get(idAccion).get(j).getInfo().getCantidad());
                                        ofertasCompraTmp.get(idAccion).get(i).getInfo().setCantidad(ofertasCompraTmp.get(idAccion).get(i).getInfo().getCantidad() - cantidad);
                                        ofertasVentaTmp.get(idAccion).get(j).getInfo().setCantidad(ofertasVentaTmp.get(idAccion).get(j).getInfo().getCantidad() - cantidad);

                                        enviarNotificacionesCalce(ofertasCompraTmp.get(idAccion).get(i),ofertasVentaTmp.get(idAccion).get(j),precio, cantidad, idAccion,true);

                                        calceExitoso = true;
                                       

                                    }
                                    else
                                        indiceVentas++;
                                }
                                else
                                    break;
                            }
                      
                    }

            if(precio > UVR)
            {
                PrecioReferencia[idAccion] = precio;
                
                //Libro2.serie(PrecioPorEncima[idAccion], PrecioPorDebajo[idAccion], PrecioReferencia[idAccion], -1, idAccion);
            }

            //System.out.println( ofertasCompra.get(idAccion).size()+" --------- "+ofertasCompraTmp.get(idAccion).size()+"------Despues del proceso-------- "+idAccion);

            eliminarCalzadosCallMarket(indiceCompras,indiceVentasAux, idAccion);
           }
        else
            System.out.println(getLocalName() + " -> Cantidad para negociar es 0, no se negociara en Call Market para accion "+idAccion+"!!!!!!!!!!!!!!!!!!!!!!");

        CanalNegociacion((float)1,idAccion);
        eliminarPorFueraDelCanal(idAccion);


        if(graficarLibro)
        if(idAccion == onSimulation.ProductoActual)
         onSimulation.TablaLibroOfertas(ofertasVenta.get(idAccion), ofertasCompra.get(idAccion));/////


           return calceExitoso;
    }

   //Obtiene en un solo array, todas las ofertas pero ordenadas por precio, sin importar que que sean de compra o de venta pues
    //adicionalmente mira las cantidades que pueden comprar o vender
    private void obtenerOfertasCallMarketMejorado(int idAccion)
    {
        ofertasCompraTmp.add(idAccion, new ArrayList<OfertaCompra>());
        ofertasVentaTmp.add(idAccion, new ArrayList<OfertaVenta>());
        OfertaCallMarket ofertaAux;
        ofertasCallSession.set(idAccion, new ArrayList<OfertaCallMarket>());
        Set<Float> preciosO = new HashSet<Float>();

        int i,j, cantidadC, cantidadV;


        for (i=0; i<ofertasCompra.get(idAccion).size(); i++)
        {
              ofertasCompraTmp.get(idAccion).add(ofertasCompra.get(idAccion).get(i));
              preciosO.add(ofertasCompra.get(idAccion).get(i).getInfo().getPrecioCompra());

        }

        for (i=0; i<ofertasVenta.get(idAccion).size(); i++)
        {
                ofertasVentaTmp.get(idAccion).add(ofertasVenta.get(idAccion).get(i));
                preciosO.add(ofertasVenta.get(idAccion).get(i).getInfo().getPrecioVenta());
        }

        i=0;
        float precios[] = new float[preciosO.size()];
        for(float x: preciosO)
            precios[i++] = x;

        for(int k = 0; k < precios.length; k++ )
        {
            cantidadC = 0;
            for(i=0; i<ofertasCompraTmp.get(idAccion).size(); i++)
            {
                if(precios[k] <= ofertasCompraTmp.get(idAccion).get(i).getInfo().getPrecioCompra())
                    cantidadC+= ofertasCompraTmp.get(idAccion).get(i).getInfo().getCantidad();
            }

            cantidadV = 0;
            for(j=0; j<ofertasVentaTmp.get(idAccion).size(); j++)
            {
                if(precios[k] >= ofertasVentaTmp.get(idAccion).get(j).getInfo().getPrecioVenta())
                    cantidadV+= ofertasVentaTmp.get(idAccion).get(j).getInfo().getCantidad();
            }

            ofertaAux = new OfertaCallMarket();

            ofertaAux.setCantidadC(cantidadC);
            ofertaAux.setCantidadV(cantidadV);
            ofertaAux.setCalce( Math.min(cantidadC, cantidadV));
            ofertaAux.setPrecio(precios[k]);
            ofertaAux.setDesvalance(Math.abs(cantidadC-cantidadV));

            ofertasCallSession.get(idAccion).add(ofertaAux);

        }

        Collections.sort(ofertasCallSession.get(idAccion));
        Collections.reverse(ofertasCallSession.get(idAccion));


    }

   
    ///Si se produjo algun calce, se eliminan las ofertas que calzaron y que se adjudicaron totalmente
    //Hace la discriminación si el proceso del calce lo desato una oferta de compra o de venta para optimizar el algoritmo
    @SuppressWarnings("element-type-mismatch")
    private void eliminarCalzadosContinuous(int k, boolean compra, int idAccion)
    {
        int i, j;


        //System.out.println(k+"    " +ofertasCompraTmp.get(idAccion).size() +"   " +compra+"   "+ ofertasCompra.get(idAccion).size()+" ------");

            //System.out.println(k+"    " +ofertasVentaTmp.get(idAccion).size() +"   " +compra+"   "+ofertasVenta.get(idAccion).size()+"-------" );
        for (i=0; i<=k; i++) {
            if(compra==false)
            {
                if (ofertasCompraTmp.get(idAccion).get(i).getInfo().getCantidad() > 0)
                {
                    ofertasCompra.get(idAccion).get(i).getInfo().setCantidad(ofertasCompraTmp.get(idAccion).get(i).getInfo().getCantidad());
                }
                else {
                    //System.out.println(k+"    " +ofertasCompraTmp.get(idAccion).size() +"   " +compra+"   "+ ofertasCompra.get(idAccion).size());
                    ofertasCompra.get(idAccion).remove(i);
                    //caducidadOfertasCompra.get(idAccion).remove(i);
                    ofertasCompraTmp.get(idAccion).remove(i);
                    contCom--;
                    //System.out.println(k+"    " +ofertasCompraTmp.get(idAccion).size() +"   " +compra+"   "+ ofertasCompra.get(idAccion).size());
                    k--;
                    i--;
                }
            }
            else
            {

                if (ofertasVentaTmp.get(idAccion).get(i).getInfo().getCantidad() > 0)
                    ofertasVenta.get(idAccion).get(i).getInfo().setCantidad(ofertasVentaTmp.get(idAccion).get(i).getInfo().getCantidad());
                else {
                    //System.out.println(k+"    " +ofertasVentaTmp.get(idAccion).size() +"   " +compra+"   "+ofertasVenta.get(idAccion).size() );
                    ofertasVenta.get(idAccion).remove(i);
                    //caducidadOfertasVenta.get(idAccion).remove(i);
                    ofertasVentaTmp.get(idAccion).remove(i);
                    //System.out.println(k+"    " +ofertasVentaTmp.get(idAccion).size() +"   " +compra+"   "+ofertasVenta.get(idAccion).size() );
                    contVen--;
                    k--;
                    i--;

                }

            }
        }


        if(compra==false)
        {
            j = ofertasVentaTmp.get(idAccion).size()-1;
//System.out.println(" Venta   " +ofertasVentaTmp.get(idAccion).size() +"   " +j+"   "+ofertasVenta.get(idAccion).size() );

            if(ofertasVentaTmp.get(idAccion).get(j).getInfo().getCantidad() > 0)
                ofertasVenta.get(idAccion).get(j).getInfo().setCantidad(ofertasVentaTmp.get(idAccion).get(j).getInfo().getCantidad());
            else
            {
                ofertasVenta.get(idAccion).remove(j);
                ofertasVentaTmp.get(idAccion).remove(j);
                //caducidadOfertasVenta.get(idAccion).remove(j);
                contVen--;

            }

        }
        else
        {
            j = ofertasCompraTmp.get(idAccion).size()-1;
            //System.out.println(" Compra   " +ofertasCompraTmp.get(idAccion).size() +"   " +j+"   "+ofertasCompra.get(idAccion).size() );
            if(ofertasCompraTmp.get(idAccion).get(j).getInfo().getCantidad() > 0)
                ofertasCompra.get(idAccion).get(j).getInfo().setCantidad(ofertasCompraTmp.get(idAccion).get(j).getInfo().getCantidad());
            else
            {
                ofertasCompra.get(idAccion).remove(j);
                ofertasCompraTmp.get(idAccion).remove(j);
                //caducidadOfertasCompra.get(idAccion).remove(j);
                contCom--;
            }
        }


         //Libro.TablaResultados.setViewportView(generarTabla());
        //Libro.metodo(ofertasVentaTmp.get(idAccion), ofertasCompraTmp.get(idAccion));/////

    }

    @SuppressWarnings("element-type-mismatch")
    private void eliminarCalzadosCallMarket(int indCompra, int indVenta, int Accion)
    {
        int i, posicion;

        //System.out.println( ofertasCompra.get(Accion).size()+" --------- "+ofertasCompraTmp.get(Accion).size()+"-------Compra-------CallMarket "+Accion);
        for (i=indCompra; i<ofertasCompraTmp.get(Accion).size(); i++)
        {
            posicion = ofertasCompra.get(Accion).indexOf(ofertasCompraTmp.get(Accion).get(i));
            //System.out.println("Posicion C " +posicion);
            int cantidad = ofertasCompraTmp.get(Accion).get(i).getInfo().getCantidad();
            if ( cantidad > 0)
                ofertasCompra.get(Accion).get(posicion).getInfo().setCantidad(cantidad);
            else
            {
                //System.out.println( ofertasCompra.get(Accion).size()+" ----C----- "+ofertasCompraTmp.get(Accion).size());
                ofertasCompra.get(Accion).remove(posicion);
                ofertasCompraTmp.get(Accion).remove(i);
                //caducidadOfertasCompra.get(Accion).remove(posicion);
                //System.out.println( ofertasCompra.get(Accion).size()+" ----C------ "+ofertasCompraTmp.get(Accion).size());
                contCom--;
                i--;

            }

        }

        //el problema es que tiene que buscar las ofertas
        //System.out.println( ofertasVenta.get(Accion).size()+" --------- "+ofertasVentaTmp.get(Accion).size()+"-----Venta---------CallMarket "+Accion);
        for (i=indVenta; i<ofertasVentaTmp.get(Accion).size(); i++)
        {
            posicion = ofertasVenta.get(Accion).indexOf(ofertasVentaTmp.get(Accion).get(i));
            //System.out.println("Posicion v " +posicion);
            int cantidad = ofertasVentaTmp.get(Accion).get(i).getInfo().getCantidad();
            if (cantidad > 0)
                ofertasVenta.get(Accion).get(posicion).getInfo().setCantidad(cantidad);
            else
            {
                //System.out.println( ofertasVenta.get(Accion).size()+" --------- "+ofertasVentaTmp.get(Accion).size()+"-----Venta---CallMarket "+Accion);
                ofertasVenta.get(Accion).remove(posicion);
                //caducidadOfertasVenta.get(Accion).remove(posicion);
                ofertasVentaTmp.get(Accion).remove(i);
                //System.out.println( ofertasVenta.get(Accion).size()+" --------- "+ofertasVentaTmp.get(Accion).size()+"-----Venta---CallMarket "+Accion);

                contVen--;
                i--;

            }
        }




       // EstaEnCallMarket[Accion] = false;


    }

    @SuppressWarnings("element-type-mismatch")
    public void eliminarPorFueraDelCanal(int Accion)
    {
        int i;
        for (i=0; i<ofertasVenta.get(Accion).size(); i++)
        {
            if(ofertasVenta.get(Accion).get(i).getInfo().getPrecioVenta() >  PrecioPorEncima[Accion] || ofertasVenta.get(Accion).get(i).getInfo().getPrecioVenta() <  PrecioPorDebajo[Accion]  )
            {
                enviarNotCancelarVenta(ofertasVenta.get(Accion).get(i),"canal",ofertasVenta.get(Accion).get(i).getProducto().getIDProducto() );
                ofertasVenta.get(Accion).remove(i);
                //System.out.println(getLocalName()+"------Se cancelo una de venta---------------");
                //caducidadOfertasVenta.get(Accion).remove(i);
                contVen--;
                i--;
            }
        }


        for (i=0; i<ofertasCompra.get(Accion).size(); i++)
        {
            if(ofertasCompra.get(Accion).get(i).getInfo().getPrecioCompra() >  PrecioPorEncima[Accion] || ofertasCompra.get(Accion).get(i).getInfo().getPrecioCompra() <  PrecioPorDebajo[Accion] )
            {
                enviarNotCancelarCompra(ofertasCompra.get(Accion).get(i),"canal", ofertasCompra.get(Accion).get(i).getProducto().getIDProducto());
                //System.out.println(getLocalName()+"------Se cancelo una de compra---------------");
                ofertasCompra.get(Accion).remove(i);
                //caducidadOfertasCompra.get(Accion).remove(i);
                contCom--;
                i--;
            }
        }
    }
    
    
    ///////////////////////////////
    ////////////////////////////////
    ////////////////////////////////
    ////////////////////////////////




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
            String leer;
            int accion = -1;
            int continuar = 0;
            try {
                System.out.println("Esperando 1 segundo - Hilo para el control del subastador");
                sleep(1000);
                System.out.println("Pasaron 1 segundos - Hilo para el control del subastador");

                while (simulacionActual < cantidadSimulaciones) {
                    sleep(1000);
                    Scanner reader = new Scanner(new File("Continuar.txt"));
                    if (reader.hasNextLine()) {
                        leer = reader.nextLine();

                        if(leer.length() != 0)
                        accion = Integer.parseInt(leer);
                    }

                    if (accion == 1 && continuar ==0) {
                        AgenteSubastador.super.doSuspend();
                        continuar++;
                        System.out.println("Subastador -- Me suspendieron ");
                    }

                    if(accion == 0 && continuar == 1)
                    {
                        AgenteSubastador.super.doActivate();
                        continuar--;
                        System.out.println("Subastador -- Estaba suspendido, voy a continuar ");
                    }

                    if(accion == 2)
                    {
                    	            pararSub = new FileWriter("Continuar.txt");
                                    paraSubastador = new PrintWriter(pararSub);
                                    paraSubastador.println(0);
                                    paraSubastador.close();
                                    File file = new File("Continuar.txt");
                                    file.delete();
                       try {
                            AgenteSubastador.super.getContainerController().kill();
                            
                        } catch (StaleProxyException ex) {
                            Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {

            }
        }
    }

    class HiloEjecutarCallMarket extends java.lang.Thread {
        int Accion;
        long duracionSesion;
        long aleatorioSesion;
        public HiloEjecutarCallMarket(int idAccion, long duracion, long aleatorio)
        {
             //HiloEjecutarCallMarket tiempoOfertas = new HiloEjecutarCallMarket();
             Accion = idAccion;
             duracionSesion = duracion;
             aleatorioSesion = aleatorio;
             //this.setPriority(this.MAX_PRIORITY);
             this.start();
           
        }

        @Override
        @SuppressWarnings("CallToThreadDumpStack")
        public void run()
        {
            try {

                if(graficarLibro)
                mostrarInfoPrecios(Accion,1);
                
                //CanalNegociacion((float)2.5,Accion);
                long tiempo = (long)aleatorio((float)(duracionSesion+aleatorioSesion),(float)(duracionSesion-aleatorioSesion));
                System.out.println(getLocalName()+" -> Entro al Hilo para Call market de accion "+Accion+", duración de la sesion de "+tiempo);
               
                sleep(tiempo);

                

            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            
            
            //FinCallMarketAgentes(Accion);
            //FinCallMarketSubastador(Accion);
            FinCallMarket(Accion);
            



        }
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
        public void run()
        {
            /*try {


                //System.out.println("Entro al Hilo para el libro ");
                sleep(1000);

            } catch(InterruptedException e) {
                e.printStackTrace();
            }*/

            if(graficarLibro)
            {
                onSimulation = new Libro2(cAcciones,tiempo,tiempoFi);
                onSimulation.setVisible(true);
            }


        }
    }

        private float aleatorio(float limiteSuperior, float limiteInferior)
    {
        return (objeto.nextFloat())*(limiteSuperior-limiteInferior)+limiteInferior;
    }

    @Override
        protected void takeDown()
       {
          try { DFService.deregister(this);
          System.out.println(this.getLocalName()+" -> He muerto y ya no estoy registrado");}
          catch (Exception e) {}
       }

    class GCAgent extends TickerBehaviour
	{
		Set seen = new HashSet(),
		    old  = new HashSet();
                int accion = -1;

		GCAgent( Agent a, long dt, int id) { super(a,dt); accion=id;}

		protected void onTick()
		{      System.out.println("====  TickerBehaviour  ====");
			ACLMessage msg = myAgent.receive();

    

			while (msg != null) {
                        if(msg.getSender().getLocalName().equals("subastador"))
                        {
                            dumpMessage( msg );
                            myAgent.putBack(msg);
                            continue;
                        }
				if (! old.contains(msg))
					seen.add( msg);

				else {
                                    /*if(msg.getSender().getLocalName().equals("subastador"))
                                        myAgent.putBack(msg);
                                    else
                                    {*/
                                    System.out.println("============================================");
					System.out.println("==== Flushing message:");
					dumpMessage( msg );
                                        System.out.println("============================================");
                                   // }
				}
				msg = myAgent.receive();
			}
			for( Iterator it = seen.iterator(); it.hasNext(); )
				myAgent.putBack( (ACLMessage) it.next() );

			old.clear();
			Set tmp = old;
			old = seen;
			seen = tmp;
                        System.out.println("============================================");
                      System.out.println("SE HA PARADO - "+accion);
                       System.out.println("============================================");
                        removeBehaviour(this);

		}


	}

    // ---------- Message print-out --------------------------------------

	static long t0 = System.currentTimeMillis();

	public void dumpMessage( ACLMessage msg )
	{
		System.out.print( "t=" + (System.currentTimeMillis()-t0)/1000F + " in "
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


        void InicioCallMarket(int accion)
        {

            InicioCallMarketAgentes(accion);
            System.out.println(getLocalName()+" -> LLego mensaje Inicio Call Market para Accion - "+accion+ " - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            HiloEjecutarCallMarket tiempoOfertas;

            if(graficarLibro)
            onSimulation.serie(PrecioPorEncima[accion], PrecioPorDebajo[accion], PrecioReferencia[accion], -1, accion);

            CanalNegociacion(2.5f,accion);

            //InicioCallMarketAgentes(idAccion);

            long tiem = tiempoSimulacion-(tiempoCallMarketFinal+tiempoAleatorioCallFinal);
            long intra = tiempoCallMarketIntra + tiempoAleatorioCallIntra;
            long tiempoTrans = (((new Date()).getTime()-horaInicial.getTime())+intra);
            System.out.println(getLocalName() + " -> Tiempo transcurrido de la simulacion hasta ahora ----- "+tiempoTrans);
            if(tiempoTrans>=tiem)
            {
                SesionFinal = true;
                tiempoCallMarket = tiempoCallMarketFinal;
                tiempoCallMarketAlea = tiempoAleatorioCallFinal;

                tiempoOfertas = new HiloEjecutarCallMarket(accion, tiempoCallMarketFinal, tiempoAleatorioCallFinal);
            }
            else
            tiempoOfertas = new HiloEjecutarCallMarket(accion, tiempoCallMarketIntra, tiempoAleatorioCallIntra);

            //tiempoOfertas.start();


            if(graficarLibro)
            if(accion == onSimulation.ProductoActual)
            {
                ordenarOfertasCompra(accion);//ordena desendentemente
                ordenarOfertasVenta(accion);//ordena ascendentemente
                onSimulation.TablaLibroOfertas(ofertasVentaTmp.get(accion), ofertasCompraTmp.get(accion));
            }
            //EstaEnCallMarket[idAccion] = true;



        }
       void FinCallMarket(int Accion)
        {

                    FinCallMarketAgentes(Accion);
                    System.out.println(getLocalName()+" -> LLego mensaje de final de sesion Call Market para accion "+Accion+" -  "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                    SessionCallMarket(Accion);

                    //FinCallMarketAgentes(idAccion);

                    //CanalNegociacion(1f,idAccion);

                    if(graficarLibro)
                    onSimulation.serie(PrecioPorEncima[Accion], PrecioPorDebajo[Accion], PrecioReferencia[Accion], -1, Accion);

                   tiempoCallMarket = tiempoCallMarketIntra;
                   tiempoCallMarketAlea = tiempoAleatorioCallIntra;

                  // addBehabiour(flusher = new GCAgent( this, 1000));
                   /////////Elimina los mensajes que no se han visto, para evitar que se acumulen
                   //flusher = new GCAgent( agenteSubastador, 1000,idAccion);
                   //addBehaviour( flusher);
                  /////////Elimina los mensajes que no se han visto, para evitar que se acumulen

                   EstaEnCallMarket[Accion] = false;

                   if(SesionFinal == true)
                   {
                       boolean enviar = true;

                       //Se verifica que no se haya terminado la simulacion, si ya se termino, y toco esperar a que la sesion
                       //Call Market terminara, entonces al finalizar esta envia el mensaje de fin de Subasta
                        for(int i=0;i<cantidadProductos;i++)
                        {
                            if(EstaEnCallMarket[i] == true)
                            {
                                enviar = false;
                                break;
                            }
                        }
                        if(enviar == true)
                        {
                           //FinDeSubasta();

                            FinDeSubastaAgentes();
                            System.out.println(getLocalName()+" -> MENSAJE FIN DE LA SUBASTA - "+(((new Date()).getTime()-horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                           FinDeSubasta();
                           simulacionCorriendo = false;
                           //ROBehaviour.action();
                            //finalizado = true;

                            //return;
                        }
                   }
                   if(graficarLibro)
                    mostrarInfoPrecios(Accion,0);

       }


}


