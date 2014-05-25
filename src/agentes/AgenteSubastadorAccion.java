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
import com.lowagie.text.xml.xmp.LangAlt;
import informacion.*;

import java.io.*;
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


public class AgenteSubastadorAccion extends Agent 
{
    
    private Codec codec = new SLCodec(); //Se crea un lenguaje para la ontologia
    private Ontology ontologia = DoblePuntaOntology.getInstance();//se crea la ontologia
    private AID agenteAdministrador;
    
    
     ConceptoProducto producto = new ConceptoProducto();


     private int periodoActual = -1;
    

    private int contadorNotCompra = 0;
    private int contadorNotVenta = 0;
    //private int contadorOfertasCompra = 0;
    //private int contadorOfertasVenta = 0;
    
    
    //private int cantidadProductos = 0;

    public boolean cargarSerie = false;

    private int simulacionActual;
 
    private int numAgentes = 0;
   // public static int CalcesEnRunTime = 0;

    public static FileWriter Origen=null;
    public static PrintWriter archivo=null;
    
    ConexionBD BD;

    private ArrayList<OfertaCallMarket> ofertasCallSession = new ArrayList<OfertaCallMarket>();

    private ArrayList<OfertaVenta> ofertasVenta = new ArrayList<OfertaVenta>();
    private ArrayList<OfertaVenta> ofertasVentaTmp = new ArrayList<OfertaVenta>();
    private ArrayList<OfertaCompra> ofertasCompra = new ArrayList<OfertaCompra>();
    private ArrayList<OfertaCompra> ofertasCompraTmp = new ArrayList<OfertaCompra>();


    
    public static FileWriter pararSub=null;
    public static PrintWriter paraSubastador=null;

    jade.wrapper.AgentContainer contenedor;

    //////////////////////////////////////////////////////////////////////

    

    protected static float promedioCantNegociacion;
    
    private boolean EstaEnCallMarket;
    Random objeto;


   // private ArrayList<ArrayList<Integer>> caducidadOfertasCompra = new ArrayList<ArrayList<Integer>>();
    //private ArrayList<ArrayList<Integer>> caducidadOfertasVenta = new ArrayList<ArrayList<Integer>>();

    
    private float PrecioReferenciaOriginal = 0;
    
    private boolean ConceptosLlenos;
    private long tiempoInicial=0;

    private float UVR = 0;
    private boolean SesionFinal = false;
    
    
    private boolean simulacionCorriendo = true;

    private int accionAManejar = -1;
    

    RecibirOfertas ROBehaviour;


    
    private long tiempoSimulacion;
    private long tiempoCallMarketIntra;
    private long tiempoCallMarketFinal;
    private long tiempoAleatorioCallIntra;
    private long tiempoAleatorioCallFinal;

    private int rechazadasC=1;
    private int rechazadasV=1;
    
    
    

    @SuppressWarnings({"static-access", "CallToThreadDumpStack"})
    @Override
    protected void setup()
    {

        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(AgenteAdministrador.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        /*try {
            // Create a file:
            System.setOut(new PrintStream(new FileOutputStream("OutputSimulation.txt")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AgenteSubastador.class.getName()).log(Level.SEVERE, null, ex);
        }*/
            
            objeto = new java.util.Random();

            //SE GENERA EL ARCHIVO PARA LA GRAFICA DE CALCERUNTIME/////////
        /*try {
            CalcesEnRunTime = 0;
            Origen = new FileWriter("CalceRunTime.txt");
            archivo = new PrintWriter(Origen);
            archivo.print(CalcesEnRunTime);
            archivo.close();
        } catch (IOException ex) {
            Logger.getLogger(AgenteSubastadorAccion.class.getName()).log(Level.SEVERE, null, ex);
        }*/



            Object[] args = getArguments();
            if (args != null && args.length > 0){
                
                numAgentes = Integer.parseInt(args[0].toString()); //numero de agentes
                accionAManejar = Integer.parseInt(args[1].toString());


            }

           // System.out.println(getLocalName()+" ->  ESTOY VIVO "+accionAManejar);

            simulacionActual=1;

            tiempoSimulacion = AgenteAdministrador.tiempoSimulacion;
            tiempoCallMarketIntra = AgenteAdministrador.tiempoCallMarketIntra;
            tiempoCallMarketFinal = AgenteAdministrador.tiempoCallMarketFinal;
            tiempoAleatorioCallIntra =AgenteAdministrador.tiempoAleatorioCallIntra;
            tiempoAleatorioCallFinal = AgenteAdministrador.tiempoAleatorioCallFinal;

            UVR = AgenteAdministrador.UVR;
            
            
           


        // Registrar el servicio
        try {
            // Se crea una lista de servicios de agente
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());

            // Se crea una descripcion de servicio
            ServiceDescription sd = new ServiceDescription();
            sd.setName("Subastar"+accionAManejar);

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

     
        
        /////ESTO ES SOLO PARA PRUEBAS, ES EL VOLUMEN PROMEDIO DE LA ACCION DE CHOCOLATES
        /*if(cantidadProductos !=0 )
        {   RuedasXAccion[0] = 20;
            NrodeOperaciones[0]=726;
            CantidadesNegociadas[0]= 1640338;
            
        }*/


        //sumatoriaOperaciones = 0;

       

        //new Libro().setVisible(true);



        /////////////////////////////////////////////////////////////////////

  

            //caducidadOfertasCompra.add(i, new ArrayList<Integer>());
            //caducidadOfertasVenta.add(i, new ArrayList<Integer>());

            producto.setIDProducto(accionAManejar);
            producto.setDescripcion(" ");
            producto.setNombre(AgenteAdministrador.nombreAcciones[accionAManejar]);

            ConceptosLlenos = false;
            



            
            
        
        // Se genera el comportamiento de registro
        RegistrarSubastador RPBehaviour = new RegistrarSubastador(this);
        contenedor = getContainerController();
        // Hasta que no haya al menos un comprador o un vendedor no inicia
        addBehaviour(RPBehaviour);
                       /*if (flusher==null) {
			flusher = new GCAgent( this, 1000);
			//addBehaviour( flusher);
		}*/
        //tiempoInicio = new GregorianCalendar().getTimeInMillis(); // Se guarda el tiempo de inicio
        //HiloContinuar EstadoSubastador = new HiloContinuar();
        //EstadoSubastador.start();
    }
	
    class RegistrarSubastador extends SimpleBehaviour {
        Agent agenteSubastador;
        boolean finalizado = false;
        IniciarSubasta ISBehaviour;
       
        
        public RegistrarSubastador(Agent a)
        {
            super(a);
            agenteSubastador = a;
            //ISBehaviour = new IniciarSubasta(a);

        }
        
        @SuppressWarnings("CallToThreadDumpStack")
        public void action()
        {
             try
            {

                // Se construye la descripcion usada como plantilla para la busqueda
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription templateSD = new ServiceDescription();

                templateSD.setName("Administrador");
                template.addServices(templateSD);

                // Solo se desea recibir un resultado a lo sumo
                SearchConstraints SC = new SearchConstraints();
                SC.setMaxResults(new Long(1));

                DFAgentDescription[] resultados = DFService.search(agenteSubastador, template, SC);
                if (resultados.length == 1) {
                    DFAgentDescription dfd = resultados[0];
                    agenteAdministrador = dfd.getName();//Aqui debe ser el subastador

                    ConceptoBursatil agenteBursatil = new ConceptoBursatil();
                    agenteBursatil.setIDBursatil(accionAManejar);
                    agenteBursatil.setNombre(getLocalName());
                    agenteBursatil.setSaldo(0);
                    agenteBursatil.setTipo(-1);

                    PredicadoRegistrarAgenteBursatil registrarAgente= new PredicadoRegistrarAgenteBursatil();
                    registrarAgente.setBursatil(agenteBursatil);

                    ACLMessage mensajeSalienteRegistrar = new ACLMessage(ACLMessage.SUBSCRIBE);
                    mensajeSalienteRegistrar.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                    mensajeSalienteRegistrar.setLanguage(codec.getName());
                    mensajeSalienteRegistrar.setOntology(ontologia.getName());
                    mensajeSalienteRegistrar.addReceiver(agenteAdministrador);////Aqui el subastador debe recibir el predicado registrar agente bursatil

                    getContentManager().fillContent(mensajeSalienteRegistrar, registrarAgente);

                    send(mensajeSalienteRegistrar);

                    MessageTemplate mt = MessageTemplate.and(
                        MessageTemplate.MatchLanguage(codec.getName()),
                        MessageTemplate.MatchOntology(ontologia.getName()));
                    ACLMessage  mensajeGeneral = blockingReceive(mt);

                    ContentElement mensajeEntrante = getContentManager().extractContent(mensajeGeneral);

                    if (mensajeGeneral != null && mensajeGeneral.getPerformative()==ACLMessage.CONFIRM &&
                        mensajeGeneral.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE)) {

                        if (mensajeEntrante instanceof PredicadoConfirmarRegistro) {
                            /*PredicadoConfirmarRegistro registrar = (PredicadoConfirmarRegistro)mensajeEntrante;
                            ConceptoRegistro registro = registrar.getRegistro();
                            idAgente = registro.getIDBursatil(); // Guarda el ID asignado por el subastador*/
                            //System.out.println(getLocalName()+" -> Me he registrado frente al administrador");
                            finalizado = true;
                        }
                    }


                }
            } catch (FIPAException fe) {
                fe.printStackTrace();
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }
            
        }

        public boolean done()
        {
        // Termina el comportamiento de recibir registros y llama a iniciar subasta
            if (finalizado) {


                    EstaEnCallMarket = false;/////////////////////////////////////////////////////////////////////

                ISBehaviour = new IniciarSubasta(agenteSubastador);
                addBehaviour(ISBehaviour);
                /////////Elimina los mensajes que no se han visto, para evitar que se acumulen
                //addBehaviour( flusher);
                /////////Elimina los mensajes que no se han visto, para evitar que se acumulen
            }
            return finalizado;
        }



    }
  
    class IniciarSubasta extends SimpleBehaviour {
        Agent agenteSubastador;
        
        boolean finalizado = false;
          
        @SuppressWarnings("element-type-mismatch")
        public IniciarSubasta(Agent a)
        {
            super(a);
            agenteSubastador = a;


            int i, j;
            if(AgenteAdministrador.permanenciaOfertas>1)
            {
                

                    for(i=0;i<ofertasCompra.size();i++)
                    {
                        int caducidad = ofertasCompra.get(i).getCaducidadOferta();
                        if(caducidad == AgenteAdministrador.permanenciaOfertas)
                        {
                            enviarNotCancelarCompra(ofertasCompra.get(i),"caduco", ofertasCompra.get(i).getProducto().getIDProducto());
                            //caducidadOfertasCompra.get(j).remove(i);
                            ofertasCompra.remove(i);
                            i--;
                            //System.out.println(getLocalName()+" -> Se elimino por caducidad");
                        }
                        else
                            ofertasCompra.get(i).setCaducidadOferta(caducidad+1);
                            //caducidadOfertasCompra.get(j).set(i,caducidadOfertasCompra.get(j).get(i)+1 );
                    }

                    for(i=0;i<ofertasVenta.size();i++)
                    {
                        int caducidad = ofertasVenta.get(i).getCaducidadOferta();
                        if(caducidad==AgenteAdministrador.permanenciaOfertas)
                        {
                            enviarNotCancelarVenta(ofertasVenta.get(i),"caduco", ofertasVenta.get(i).getProducto().getIDProducto());
                            //caducidadOfertasVenta.get(j).remove(i);
                            ofertasVenta.remove(i);
                            i--;
                            //System.out.println(getLocalName()+" -> Se elimino por caducidad");
                        }
                        else
                            ofertasVenta.get(i).setCaducidadOferta(caducidad+1);
                            //caducidadOfertasVenta.get(j).set(i,caducidadOfertasVenta.get(j).get(i)+1 );
                    }
                

            }
                
            else
            {
                
                    ofertasCompra.clear();
                    ofertasVenta.clear();

                    //caducidadOfertasCompra.get(j).clear();
                    //caducidadOfertasVenta.get(j).clear();
                 
            }

            //onSimulation.TablaLibroOfertas(ofertasVentaTmp.get(onSimulation.ProductoActual), ofertasCompraTmp.get(onSimulation.ProductoActual));


            //ROBehaviour = new RecibirOfertas(a);
            

        }
          
        @SuppressWarnings("CallToThreadDumpStack")
        public void action()
        {
            try {
                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));
                ACLMessage  mensajeGeneral = blockingReceive(mt);

                ContentElement mensajeEntrante = getContentManager().extractContent(mensajeGeneral);

                if (mensajeGeneral != null && mensajeGeneral.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE))
                {
                    if(  mensajeGeneral.getPerformative() == ACLMessage.INFORM )
                    {

                        if (mensajeEntrante instanceof PredicadoInicioSubasta)
                        {
                            //System.out.println(getLocalName()+" -> Inicio de subasta");
                            //mostrarMensaje(mensajeGeneral,getLocalName());
                            periodoActual = AgenteAdministrador.simulacionActual;
                            simulacionCorriendo = true;
                            finalizado = true;
 


                        }
                    }
                }
            }catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }

            
        }

        public boolean done()
        {
            if(finalizado)
            {
                AgenteAdministrador.VolumenOfertadoCompra[accionAManejar] = 0;
                AgenteAdministrador.VolumenOfertadoVenta[accionAManejar] = 0;
                AgenteAdministrador.VolumenNegociado[accionAManejar] = 0;

                AgenteAdministrador.IDOrdenesCompra.get(accionAManejar).clear();
                AgenteAdministrador.IDOrdenesVenta.get(accionAManejar).clear();
                AgenteAdministrador.IDOrdenesCompraExito.get(accionAManejar).clear();
                AgenteAdministrador.IDOrdenesVentaExito.get(accionAManejar).clear();

               // System.err.println("*****Limpie todo el contenido");



                HiloEsperarOfertas tiempoOfertas = new HiloEsperarOfertas();
                tiempoOfertas.start();
                ROBehaviour = new RecibirOfertas(agenteSubastador);

                addBehaviour(ROBehaviour);
            }
            return finalizado;
        }
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
            

                if(EstaEnCallMarket == false)
                {
                    AgenteAdministrador.tiempoCallMarket[accionAManejar] = tiempoCallMarketFinal;
                    AgenteAdministrador.tiempoCallMarketAlea[accionAManejar] = tiempoAleatorioCallFinal;

                    System.out.println(getLocalName()+" -> Se entra a Session Call Market para accion "+accionAManejar+" -- Final del dia -------------- "+(tiempoSimulacion-(tiempoCallMarketFinal+tiempoAleatorioCallFinal)));
                    EstaEnCallMarket = true;
                    //InicioCallMarketSubastador(i);
                    //InicioCallMarketAgentes(i);
                    PrecioReferenciaOriginal = AgenteAdministrador.PrecioReferencia[accionAManejar];

                    InicioCallMarket(accionAManejar, "final");

                    //HiloEjecutarCallMarket ejecutar = new HiloEjecutarCallMarket(i,tiempoCallMarketFinal,tiempoAleatorioCallFinal);

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
                            System.out.println(getLocalName()+" -> MENSAJE FIN DE LA SUBASTA A LLEGADO - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                            finalizado = true;
                            //return;

                        }

                        /*else if(oferta instanceof PredicadoInicioCallMarket)
                            {
                                int idAccion = ((PredicadoInicioCallMarket)oferta).getProducto().getIDProducto();

                                    System.out.println(getLocalName()+" -> LLego mensaje Inicio Call Market para Accion - "+idAccion+ " - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                                    HiloEjecutarCallMarket tiempoOfertas;

                                    if(AgenteAdministrador.graficarLibro)
                                    AgenteAdministrador.onSimulation.serie(AgenteAdministrador.PrecioPorEncima[idAccion], AgenteAdministrador.PrecioPorDebajo[idAccion], AgenteAdministrador.PrecioReferencia[idAccion], -1, idAccion);

                                    AgenteAdministrador.CanalNegociacion(2.5f,idAccion);

                                    //InicioCallMarketAgentes(idAccion);

                                    long tiem = tiempoSimulacion-(tiempoCallMarketFinal+tiempoAleatorioCallFinal);
                                    long intra = tiempoCallMarketIntra + tiempoAleatorioCallIntra;
                                    long tiempoTrans = (((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+intra);
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
                                    

                                    if(AgenteAdministrador.graficarLibro)
                                    if(idAccion == AgenteAdministrador.onSimulation.ProductoActual)
                                    {
                                        ordenarOfertasCompra();//ordena desendentemente
                                        ordenarOfertasVenta();//ordena ascendentemente
                                        AgenteAdministrador.onSimulation.TablaLibroOfertas(ofertasVentaTmp, ofertasCompraTmp);
                                    }
                                    //EstaEnCallMarket[idAccion] = true;


                            }*/

                           /* else if(oferta instanceof PredicadoFinCallMarket)
                            {

                                //int idAccion = ((PredicadoFinCallMarket)oferta).getProducto().getIDProducto();
                                System.out.println(getLocalName()+" -> LLego mensaje de final de sesion Call Market para accion "+accionAManejar+" -  "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                                SessionCallMarket(accionAManejar);

                                //FinCallMarketAgentes(idAccion);

                                //CanalNegociacion(1f,idAccion);

                                if(AgenteAdministrador.graficarLibro)
                                AgenteAdministrador.onSimulation.serie(AgenteAdministrador.PrecioPorEncima[accionAManejar], AgenteAdministrador.PrecioPorDebajo[accionAManejar], AgenteAdministrador.PrecioReferencia[accionAManejar], -1, accionAManejar);

                               tiempoCallMarket = tiempoCallMarketIntra;
                               tiempoCallMarketAlea = tiempoAleatorioCallIntra;

                              // addBehabiour(flusher = new GCAgent( this, 1000));
                               /////////Elimina los mensajes que no se han visto, para evitar que se acumulen
                               //flusher = new GCAgent( agenteSubastador, 1000,idAccion);
                               //addBehaviour( flusher);
                              /////////Elimina los mensajes que no se han visto, para evitar que se acumulen

                               EstaEnCallMarket = false;

                               if(SesionFinal == true)
                               {

                                        FinDeSubastaAgentes();
                                        System.out.println(getLocalName()+" -> MENSAJE FIN DE LA SUBASTA - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                                       finalizado = true;
     
                               }
                               if(AgenteAdministrador.graficarLibro)
                                mostrarInfoPrecios(0);
                                

                            }*/
                    }
                    /*
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
                    }*/

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
                                //predComp.getOfertaCompra().setIDOfertaCompra(AgenteAdministrador.contadorOfertasCompra++);
                                //predComp.getOfertaCompra().setIDOfertaCompra(contadorOfertasC());periodoActual
                                predComp.getOfertaCompra().setPeriodo(periodoActual);
                                IDProducto = predComp.getProducto().getIDProducto();
                                float precio = predComp.getOfertaCompra().getPrecioCompra();

                                //System.out.println(getLocalName()+" -> Oferta compra de "+emisor.getLocalName()+" -- Cantidad = "+ predComp.getOfertaCompra().getCantidad()+" --- Precio = "+ predComp.getOfertaCompra().getPrecioCompra());
                                /*if(accionAManejar != IDProducto)
                                    System.out.println(getLocalName()+" -> accionAManejar != IDProducto "+accionAManejar+" - "+IDProducto);*/


                                if(AgenteAdministrador.PrecioPorEncima[IDProducto] >= precio && AgenteAdministrador.PrecioPorDebajo[IDProducto] <= precio)
                                {
                                    predComp.getOfertaCompra().setIDOfertaCompra(AgenteAdministrador.contadorOfertasC());
                                    ofertasCompra.add(new OfertaCompra(emisor, predComp.getOfertaCompra(), predComp.getProducto(),1));
                                    
                                    AgenteAdministrador.VolumenOfertadoCompra[accionAManejar]+=predComp.getOfertaCompra().getPrecioCompra()*predComp.getOfertaCompra().getCantidad();
                                    //caducidadOfertasCompra.get(IDProducto).add(1);
                                    
                                    AgenteAdministrador.IDOrdenesCompra.get(accionAManejar).add(predComp.getOfertaCompra().getIDOfertaCompra());
                                    /*if(IDProducto == 0)
                                    {System.err.println(getLocalName()+" -> Tamm "+AgenteAdministrador.IDOrdenesCompra.get(accionAManejar).size());
                                    System.err.println(getLocalName()+" -> VolOfet "+AgenteAdministrador.VolumenOfertadoCompra[accionAManejar]);
                                    }*/

                                    
                                    OfCompra = true;

                                    if(ConceptosLlenos == false)
                                    {
                                        producto =  predComp.getProducto();
                                        ConceptosLlenos = true;
                                    }

                                    AgenteAdministrador.bufferOfertasCompra.add(new InfoOfertaCompra(predComp.getOfertaCompra().getIDOfertaCompra(), predComp.getOfertaCompra().getIDComprador(), IDProducto, predComp.getOfertaCompra().getPrecioCompra(), AgenteAdministrador.simulacionActual, predComp.getOfertaCompra().getCantidad()));

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
                                    //System.out.println(getLocalName()+" -> Oferta compra Rechazada de "+emisor.getLocalName()+" -- Cantidad = "+ predComp.getOfertaCompra().getCantidad()+" --- Precio = "+ predComp.getOfertaCompra().getPrecioCompra()+" - R - "+(rechazadasC++));
                                    enviarNotCancelarCompra(new OfertaCompra(emisor, predComp.getOfertaCompra(), predComp.getProducto(),1),"tarde",IDProducto);
                                    aceptada = false;
                                }
                            }

                            //OFERTA DE VENTA
                            else if (oferta instanceof PredicadoRegistrarVenta) {
                                PredicadoRegistrarVenta predVenta = ((PredicadoRegistrarVenta)oferta);
                                emisor = mensajeOfertas.getSender();
                                predVenta.getOfertaVenta().setPeriodo(periodoActual);
                                //predVenta.getOfertaVenta().setIDOfertaVenta(AgenteAdministrador.contadorOfertasVenta++);
                                //predVenta.getOfertaVenta().setIDOfertaVenta(contadorOfertasV());
                                IDProducto = predVenta.getProducto().getIDProducto();
                                float precio = predVenta.getOfertaVenta().getPrecioVenta();
                                //System.out.println(getLocalName()+" -> Oferta venta de "+emisor.getLocalName()+" -- Cantidad = "+ predVenta.getOfertaVenta().getCantidad()+" --- Precio = "+ predVenta.getOfertaVenta().getPrecioVenta());

                                /*if(accionAManejar != IDProducto)
                                    System.out.println(getLocalName()+" -> accionAManejar != IDProducto "+accionAManejar+" - "+IDProducto);*/

                                if(AgenteAdministrador.PrecioPorEncima[IDProducto] >= precio && AgenteAdministrador.PrecioPorDebajo[IDProducto] <= precio)
                                {
                                    predVenta.getOfertaVenta().setIDOfertaVenta(AgenteAdministrador.contadorOfertasV());
                                    ofertasVenta.add(new OfertaVenta(emisor, predVenta.getOfertaVenta(), predVenta.getProducto(),1));
                                    //caducidadOfertasVenta.get(IDProducto).add(1);
                                    AgenteAdministrador.VolumenOfertadoVenta[accionAManejar]+=predVenta.getOfertaVenta().getPrecioVenta()*predVenta.getOfertaVenta().getCantidad();
                                   AgenteAdministrador.IDOrdenesVenta.get(accionAManejar).add(predVenta.getOfertaVenta().getIDOfertaVenta()); 
                                    
                                    // contVen++;
                                    OfCompra = false;

                                    //Llenar la informacion para el envio de las notificaciones de las sesiones CallMarket
                                    if(ConceptosLlenos == false)
                                    {
                                        producto =  predVenta.getProducto();
                                        ConceptosLlenos = true;
                                    }


                                    AgenteAdministrador.bufferOfertasVenta.add(new InfoOfertaVenta(predVenta.getOfertaVenta().getIDOfertaVenta(), predVenta.getOfertaVenta().getIDVendedor(), IDProducto, predVenta.getOfertaVenta().getPrecioVenta(), AgenteAdministrador.simulacionActual, predVenta.getOfertaVenta().getCantidad()));

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
                                    //System.out.println(getLocalName()+" -> Oferta venta Rechazada de "+emisor.getLocalName()+" -- Cantidad = "+ predVenta.getOfertaVenta().getCantidad()+" --- Precio = "+ predVenta.getOfertaVenta().getPrecioVenta()+" - R - "+(rechazadasV++));
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
                                    if(EstaEnCallMarket == false){
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


                                        if(AgenteAdministrador.graficarLibro)
                                        {
                                            mostrarInfoPrecios(1);
                                            if(IDProducto == AgenteAdministrador.onSimulation.ProductoActual)
                                            {
                                                ordenarOfertasCompra();//ordena desendentemente
                                                ordenarOfertasVenta();//ordena ascendentemente
                                                AgenteAdministrador.onSimulation.TablaLibroOfertas(ofertasVentaTmp, ofertasCompraTmp);
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
                //System.out.println(getLocalName()+" -> Entro al metodo de done - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));

                /*MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));

                 ACLMessage mensaje = myAgent.receive(mt);
                while(mensaje!=null)
                {
                    mostrarMensaje(mensaje,getLocalName());
                     mensaje = myAgent.receive(mt);

                }*/


              
                

                if (simulacionActual < AgenteAdministrador.cantidadSimulaciones) {
                    simulacionActual++;
                    
                    IniciarSubasta ISBehaviour = new IniciarSubasta(myAgent);
                    addBehaviour(ISBehaviour);

                    //removeBehaviour(this);
                    
                    //tiempoInicio = new GregorianCalendar().getTimeInMillis(); // Se guarda el tiempo de inicio
                }
                
                System.out.println(getLocalName()+" -> Va a salir del metodo de done - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                
               
            }
            return finalizado;
        }
    }

    private /*synchronized*/ void mostrarInfoPrecios(int tipo)
    {
            float mayorVenta,menorVenta,mayorCompra,menorCompra;

            mayorVenta = menorVenta = mayorCompra = menorCompra = 0;

            if(ofertasVenta.size() > 0 )
            {
                mayorVenta = Collections.max(ofertasVenta).getInfo().getPrecioVenta();
                menorVenta = Collections.min(ofertasVenta).getInfo().getPrecioVenta();
            }
            if(ofertasCompra.size() > 0)
            {
                mayorCompra = Collections.max(ofertasCompra).getInfo().getPrecioCompra();
                menorCompra = Collections.min(ofertasCompra).getInfo().getPrecioCompra();
            }

            float mayor = mayorVenta;
            float menor = menorVenta;

            if(mayorCompra> mayor)
                mayor = mayorCompra;

            if(menorCompra< menor)
                menor = menorCompra;

            //System.out.println(mayor+"     "+menor);
            //tipo 1 es para CallMarket, tipo 0 Continuous
            AgenteAdministrador.onSimulation.TablaInformacion(producto.getNombre(),tipo,mayor,menor,false, accionAManejar);
            //AgenteAdministrador.onSimulation.TablaInformacion(AgenteAdministrador.nombreAcciones[accionAManejar],tipo,mayor,menor,false, accionAManejar);
    }

    private /*synchronized*/ void ordenarOfertasVenta()
    {
        /*ofertasVentaTmp = new ArrayList<OfertaVenta>();

        for (int i=0; i<ofertasVenta.size(); i++) {
            if (ofertasVenta.get(i).getProducto().getIDProducto() == idAccion) {
                ofertasVentaTmp.add(ofertasVenta.get(i));
            }
        }
        contVenTmp = ofertasVentaTmp.size();*/

        obtenerOfertasVenta();

        try {
            if (!ofertasVentaTmp.isEmpty()) {
                if (ofertasVentaTmp.size() > 1) {
                    Collections.sort(ofertasVentaTmp);
                }
            }
        } catch (java.lang.IllegalArgumentException e) {
           // e.printStackTrace();
            //System.err.println(ofertasCompraTmp);
            System.err.println(getLocalName()+"!!!!!!!!!!!!!!-"+ofertasVentaTmp.size());
        }
    }

    private /*synchronized*/ void ordenarOfertasCompra()
    {
        /*ofertasCompraTmp = new ArrayList<OfertaCompra>();

        for (int i=0; i<ofertasCompra.size(); i++) {
            if (ofertasCompra.get(i).getProducto().getIDProducto() == idAccion) {
                ofertasCompraTmp.add(ofertasCompra.get(i));
            }
        }
        contComTmp = ofertasCompraTmp.size();*/

        obtenerOfertasCompra();
        
        try{
        if(!ofertasCompraTmp.isEmpty())
        {
            if(ofertasCompraTmp.size()>1)
            {
                Collections.sort(ofertasCompraTmp);
                Collections.reverse(ofertasCompraTmp);

                /*for(int i=0;i<ofertasCompraTmp.size();i++)
                    System.out.println("****** "+ofertasCompraTmp.get(i).getInfo().getPrecioCompra());*/
            }
        }
        }
        catch(java. lang.IllegalArgumentException e)
        {
            //e.printStackTrace();
            System.err.println(getLocalName()+"!!!!!!!!-"+ofertasCompraTmp.size());
            //System.err.println(ofertasCompraTmp);
        }

         /*for (int i=0; i<ofertasCompra.get(idAccion).size(); i++) {
           System.out.println(ofertasCompraTmp.get(i).getInfo().getPrecioCompra()+"  ----  "+ofertasCompra.get(idAccion).get(i).getInfo().getPrecioCompra());

        }*/

        
    }

  

    //////////////////////////
    /////////////NUEVO///////
    ////////////////////////
    ///////////////////////



    ////Obtiene las ofertas de venta de una determinada accion, conservando el orden de presedencia de las mismas
    ///por el tiempo de llegada, adicionalmente guarda el indice de la ultima oferta que ha llegado
    private /*synchronized*/ void obtenerOfertasVenta()
    {
        ofertasVentaTmp =  new ArrayList<OfertaVenta>(ofertasVenta);

        /*for (int i=0; i<ofertasVenta.size(); i++) {
            if (ofertasVenta.get(i).getProducto().getIDProducto() == idAccion) {
                ofertasVentaTmp.add(ofertasVenta.get(i));
            }
        }*/

        

       // System.out.println(getLocalName()+ "  -  "+ofertasVenta.get(idAccion).size()+"  --  "+ofertasVentaTmp.size());
        //contVenTmp = ofertasVentaTmp.get(idAccion).size();

    }

    ////Obtiene las ofertas de compra de una determinada accion, conservando el orden de presedencia de las mismas
    ///por el tiempo de llegada, adicionalmente guarda el indice de la ultima oferta que ha llegado
    private /*synchronized*/ void obtenerOfertasCompra()
    {
        ofertasCompraTmp =  new ArrayList<OfertaCompra>(ofertasCompra);

        /*for (int i=0; i<ofertasCompra.size(); i++) {
             
                ofertasCompraTmp.add(ofertasCompra.get(idAccion).get(i));
            
        }*/

        //ofertasCompraTmp = ofertasCompra.get(idAccion);

        //System.out.println(getLocalName()+ "  -  "+ofertasCompra.get(idAccion).size()+"  --  "+ofertasCompraTmp.size());

        //contComTmp = ofertasCompraTmp.get(idAccion).size();

    }

    

    //Se envia la notificacion que se a pasado de una sesion Continuous a CallMarket, para una determinada accion
    @SuppressWarnings("CallToThreadDumpStack")
    private void InicioCallMarketSubastador(int Accion)
    {
            EstaEnCallMarket = true;
            System.out.println(getLocalName()+" -> Enviar mensaje Inicio Call Market para accion S- "+Accion+" - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            try {
                PredicadoInicioCallMarket inicioCM = new PredicadoInicioCallMarket();
                ACLMessage mensajeInicioCallMarket= new ACLMessage(ACLMessage.INFORM);
                inicioCM.setProducto(producto);


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
    private void InicioCallMarketAgentes(int Accion, String momento)
    {
        //System.out.println(getLocalName()+" INICIO MENSAJE INICIO CALL MARKET Agentes - "+Accion+" - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
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

                inicioCM.setProducto(producto);
                inicioCM.setMomento(momento);


                mensajeInicioCallMarket.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeInicioCallMarket.setLanguage(codec.getName());
                mensajeInicioCallMarket.setOntology(ontologia.getName());
                getContentManager().fillContent(mensajeInicioCallMarket, inicioCM);

                for (int i=0; i<numAgentes;i++) {
                    if (AgenteAdministrador.participantes[i] != null)
                        mensajeInicioCallMarket.addReceiver(AgenteAdministrador.participantes[i]);
                }
                send(mensajeInicioCallMarket);
                //System.out.println(getLocalName()+" -> FIN MENSAJE INICIO CALL MARKET Agentes - "+Accion+" - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));

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
        System.out.println(getLocalName()+" -> MENSAJE FIN CALL MARKET PARA SUBASTADOR PARA ACCION "+Accion+" - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
           
            try {
                PredicadoFinCallMarket finCM = new PredicadoFinCallMarket();
                ACLMessage mensajeFinCallMarket = new ACLMessage(ACLMessage.INFORM);
                finCM.setProducto(producto);


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
    private void FinCallMarketAgentes(int Accion, String momento)
    {
        
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

                finCM.setProducto(producto);
                finCM.setMomento(momento);


                mensajeFinCallMarket.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeFinCallMarket.setLanguage(codec.getName());
                mensajeFinCallMarket.setOntology(ontologia.getName());
                getContentManager().fillContent(mensajeFinCallMarket, finCM);

                for (int i=0; i<numAgentes;i++) {
                    if (AgenteAdministrador.participantes[i] != null)
                        mensajeFinCallMarket.addReceiver(AgenteAdministrador.participantes[i]);
                }
                System.out.println(getLocalName()+" -> INICIO MENSAJE FIN CALL MARKET Para agentes - "+Accion+" - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                send(mensajeFinCallMarket);

            System.out.println(getLocalName()+" -> FIN MENSAJE FIN CALL MARKET Para agentes - "+Accion+" - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    private void FinCallMarketAdministrador()
    {
        //System.out.println(getLocalName()+" -> INICIO MENSAJE FIN CALL MARKET Para adminsitrador - "+accionAManejar+" - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
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

                finCM.setProducto(producto);
                finCM.setMomento("final");


                mensajeFinCallMarket.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                mensajeFinCallMarket.setLanguage(codec.getName());
                mensajeFinCallMarket.setOntology(ontologia.getName());
                getContentManager().fillContent(mensajeFinCallMarket, finCM);


                mensajeFinCallMarket.addReceiver(agenteAdministrador);

                send(mensajeFinCallMarket);

            System.out.println(getLocalName()+" -> SE ENVIO FIN CALL MARKET Para Administrador - "+accionAManejar+" - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
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
                //AgenteAdministrador.bufferCalcesCM.add(new InfoCalce(AgenteAdministrador.contadorCalcesCM++, comprador.getInfo().getIDOfertaCompra(), vendedor.getInfo().getIDOfertaVenta(), idAccion, precio, precio, AgenteAdministrador.simulacionActual, cantidad));
                AgenteAdministrador.bufferCalcesCM.add(new InfoCalce(0, comprador.getInfo().getIDOfertaCompra(), vendedor.getInfo().getIDOfertaVenta(), idAccion, precio, precio, AgenteAdministrador.simulacionActual, cantidad));
            else
            {
                //AgenteAdministrador.bufferCalces.add(new InfoCalce(AgenteAdministrador.contadorCalces++, comprador.getInfo().getIDOfertaCompra(), vendedor.getInfo().getIDOfertaVenta(), idAccion, precio, precio, AgenteAdministrador.simulacionActual, cantidad));
                AgenteAdministrador.bufferCalces.add(new InfoCalce(0, comprador.getInfo().getIDOfertaCompra(), vendedor.getInfo().getIDOfertaVenta(), idAccion, precio, precio, AgenteAdministrador.simulacionActual, cantidad));
                AgenteAdministrador.CalcesEnRunTime++;
                //System.out.println( AgenteAdministrador.CalcesEnRunTime);
                /*try {
                    Origen = new FileWriter("CalceRunTime.txt");
                    archivo = new PrintWriter(Origen);
                    CalcesEnRunTime++;
                    archivo.println(CalcesEnRunTime);
                    archivo.close();
                } catch (IOException ex) {
                    Logger.getLogger(AgenteSubastadorAccion.class.getName()).log(Level.SEVERE, null, ex);
                }*/
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
                //System.out.println(getLocalName()+" -> MENSAJE FIN DE LA SUBASTA a sido enviado - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }
    }




    //Segun la oferta que desate la función, se procede a verificar si es posible realizar un calce, de ser asi,
    //llama a la funcion para envar las notificaciones y adicionalmente eliminar las calzadas
    @SuppressWarnings("element-type-mismatch")
    private /*synchronized*/ boolean SessionContinuousMejorada(int idAccion, boolean compra)
    {
        int i, posicion ;
        int cantidad;
    	boolean calceExitoso = false;

        if(AgenteAdministrador.graficarLibro)
        mostrarInfoPrecios(0);

        obtenerOfertasCompra();
        //obtenerOfertasVenta(idAccion);


        //System.out.println(getLocalName()+" LLego oferta");

        if(compra)
        {  //System.out.println( ofertasCompra.get(idAccion).size()+" --------- "+ofertasCompraTmp.get(idAccion).size()+"------Antes de calzar--------C "+idAccion);

            posicion = ofertasCompra.size()-1;
            if(posicion < 0) return false;
            float precioCompra = ofertasCompra.get(posicion).getInfo().getPrecioCompra();
            float precioVenta;
            for(i=0; i< ofertasVenta.size(); i++ )
            {
                /*cantidad = ofertasCompra.get(idAccion).get(posicion).getInfo().getCantidad();
                if(cantidad == 0)
                    break;*/

                precioVenta = ofertasVenta.get(i).getInfo().getPrecioVenta();

                //System.out.println()

                if(precioCompra >= precioVenta)
                {
                     if(precioVenta == AgenteAdministrador.PrecioPorEncima[idAccion] || precioVenta == AgenteAdministrador.PrecioPorDebajo[idAccion])
                    {

                        System.out.println(getLocalName()+" -> Se entra a Session Call Market por posible calce en el canal Accion "+idAccion+"!!!!!!!!");
                        EstaEnCallMarket = true;
                        //InicioCallMarketSubastador(idAccion);
                        //InicioCallMarketAgentes(idAccion);
                        PrecioReferenciaOriginal = AgenteAdministrador.PrecioReferencia[accionAManejar];
                        AgenteAdministrador.PrecioReferencia[accionAManejar] = precioVenta;

                        InicioCallMarket(idAccion, "intra");

                        return false;
                    }
                    else
                    {
                        int cantidadCompra =  ofertasCompra.get(posicion).getInfo().getCantidad();
                        int cantidadVenta = ofertasVenta.get(i).getInfo().getCantidad();
                        cantidad = Math.min(cantidadCompra,cantidadVenta );
                        ofertasCompra.get(posicion).getInfo().setCantidad(cantidadCompra - cantidad);
                        ofertasVenta.get(i).getInfo().setCantidad(cantidadVenta - cantidad);

                        AgenteAdministrador.VolumenNegociado[accionAManejar]+=(2*(cantidad*precioVenta));
                        
                        AgenteAdministrador.IDOrdenesCompraExito.get(accionAManejar).add(ofertasCompra.get(posicion).getInfo().getIDOfertaCompra());
                        AgenteAdministrador.IDOrdenesVentaExito.get(accionAManejar).add(ofertasVenta.get(i).getInfo().getIDOfertaVenta());
                        
                        enviarNotificacionesCalce(ofertasCompra.get(posicion),ofertasVenta.get(i),precioVenta, cantidad, idAccion,false);

                        //System.out.println(getLocalName()+" -> Calce a un precio de  ---  "+precioVenta);

                        calceExitoso = true;

                        if(AgenteAdministrador.graficarLibro)
                        AgenteAdministrador.onSimulation.serie(AgenteAdministrador.PrecioPorEncima[idAccion], AgenteAdministrador.PrecioPorDebajo[idAccion], AgenteAdministrador.PrecioReferencia[idAccion], precioVenta, idAccion);
                        //System.out.println(precioVenta+"  ----  "+idAccion+"  ----  "+Libro.ProductoActual);

                        if(ofertasCompra.get(posicion).getInfo().getCantidad()==0)
                        {
                            //System.out.println(getLocalName()+" --- Elimino en calce CA  -C-  "+ofertasCompra.get(idAccion).size());
                            ofertasCompra.remove(posicion);
                            //System.out.println(getLocalName()+" --- Elimino en calce CD  -C-  "+ofertasCompra.get(idAccion).size());
                            break;
                        }
                        if(ofertasVenta.get(i).getInfo().getCantidad()==0)
                        {
                            //System.out.println(getLocalName()+" --- Elimino en calce VA -C-  "+ofertasVenta.get(idAccion).size());
                            
                            ofertasVenta.remove(i);
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
            posicion = ofertasVenta.size()-1;
            if(posicion < 0) return false;
            float precioVenta = ofertasVenta.get(posicion).getInfo().getPrecioVenta();
            float precioCompra;
            for(i=0; i< ofertasCompra.size(); i++ )
            {
                cantidad = ofertasVenta.get(posicion).getInfo().getCantidad();
                if(cantidad == 0)
                    break;

                precioCompra = ofertasCompra.get(i).getInfo().getPrecioCompra();

                if(precioCompra >= precioVenta)
                {
                    if(precioCompra == AgenteAdministrador.PrecioPorEncima[idAccion] || precioCompra == AgenteAdministrador.PrecioPorDebajo[idAccion])
                    {
                        System.out.println(getLocalName()+" -> Se entra a Session Call Market por posible calce en el canal Accion "+idAccion+"!!!!!!!!");
                        EstaEnCallMarket = true;
                        //InicioCallMarketSubastador(idAccion);
                        //InicioCallMarketAgentes(idAccion);
                        PrecioReferenciaOriginal = AgenteAdministrador.PrecioReferencia[accionAManejar];
                        AgenteAdministrador.PrecioReferencia[accionAManejar] = precioCompra;
                        InicioCallMarket(idAccion, "intra");


                        return false;
                    }
                    else
                    {
                        int cantidadCompra =  ofertasCompra.get(i).getInfo().getCantidad();
                        int cantidadVenta = ofertasVenta.get(posicion).getInfo().getCantidad();
                        cantidad = Math.min(cantidadCompra, cantidadVenta);
                        ofertasCompra.get(i).getInfo().setCantidad(cantidadCompra - cantidad);
                        ofertasVenta.get(posicion).getInfo().setCantidad(cantidadVenta - cantidad);
                        
                        AgenteAdministrador.VolumenNegociado[accionAManejar]+=(2*(cantidad*precioCompra));
                        
                        AgenteAdministrador.IDOrdenesCompraExito.get(accionAManejar).add(ofertasCompra.get(i).getInfo().getIDOfertaCompra());
                        AgenteAdministrador.IDOrdenesVentaExito.get(accionAManejar).add(ofertasVenta.get(posicion).getInfo().getIDOfertaVenta());
                        
                        enviarNotificacionesCalce(ofertasCompra.get(i),ofertasVenta.get(posicion),precioCompra, cantidad, idAccion,false);
                        calceExitoso = true;

                        //System.out.println(getLocalName()+" -> Calce a un precio de  ---  "+precioCompra);

                        if(AgenteAdministrador.graficarLibro)
                        AgenteAdministrador.onSimulation.serie(AgenteAdministrador.PrecioPorEncima[idAccion], AgenteAdministrador.PrecioPorDebajo[idAccion], AgenteAdministrador.PrecioReferencia[idAccion], precioCompra, idAccion);
                        //System.out.println(precioCompra+"  ----  "+idAccion+"  ----  "+Libro.ProductoActual);
                        if(ofertasCompra.get(i).getInfo().getCantidad()==0)
                        {
                            //System.out.println(getLocalName()+" --- Elimino en calce CA  -V-  "+ofertasCompra.get(idAccion).size());
                            ofertasCompra.remove(i);
                            //System.out.println(getLocalName()+" --- Elimino en calce CD  -V-  "+ofertasCompra.get(idAccion).size());
                            i--;
                        }
                        if(ofertasVenta.get(posicion).getInfo().getCantidad()==0)
                        {
                            //System.out.println(getLocalName()+" --- Elimino en calce VA  -V-  "+ofertasVenta.get(idAccion).size());
                            ofertasVenta.remove(posicion);
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
        if(AgenteAdministrador.graficarLibro)
        if(idAccion == AgenteAdministrador.onSimulation.ProductoActual)
         AgenteAdministrador.onSimulation.TablaLibroOfertas(ofertasVenta, ofertasCompra);/////

        return calceExitoso;
    }
    

   private  /*synchronized*/ boolean SessionCallMarket(int idAccion)
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
        if(ofertasCallSession.isEmpty())
        {
            System.out.println(getLocalName()+" -> No hay ofertas para negociar en Call Market para la accion "+idAccion+"!!!!!!!!!!!");
            AgenteAdministrador.CanalNegociacion((float)1,idAccion);
            return false;
        }

        for(i=1;i<ofertasCallSession.size();i++)
        {
            if(ofertasCallSession.get(0).getCalce()==ofertasCallSession.get(i).getCalce())
                contMaxAdj++;
            else
                break;
        }

        if(ofertasCallSession.get(0).getCalce()>0)
        {
                System.out.println(getLocalName()+" -> La cantidad a negociar en Call Market para la accion "+idAccion+" es "+ofertasCallSession.get(0).getCalce());

            if(contMaxAdj==1)
            {
                precio = ofertasCallSession.get(0).getPrecio();

            }
            else
            {

                //System.out.println("La cantidad a negociar es "+ofertasCallSession.get(0).getCalce());
                OfertaCallMarket ofertasDesvalance[] = new OfertaCallMarket[contMaxAdj];
                OfertaCallMarket ofertasAux = new OfertaCallMarket();

                for(i=0;i<contMaxAdj;i++)
                    ofertasDesvalance[i]=ofertasCallSession.get(i);

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

                    ordenarOfertasCompra();
                    ordenarOfertasVenta();


                    Collections.reverse(ofertasVentaTmp); //las de venta estarian ordenadas de mayor a menor
                    Collections.reverse(ofertasCompraTmp);//las de compra estarian ordenadas de menor a mayor,

//System.out.println( ofertasCompra.get(idAccion).size()+" --------- "+ofertasCompraTmp.get(idAccion).size()+"------Despues de ordenar-------- "+idAccion);

                    int indiceVentas, indiceCompras, cantidad, indiceVentasAux, cantidadtotal;
                    cantidadtotal = 0;

                    for(indiceVentas=0; indiceVentas< ofertasVentaTmp.size(); indiceVentas++ )
                        if(ofertasVentaTmp.get(indiceVentas).getInfo().getPrecioVenta()<=precio)
                            break;

                    for(indiceCompras=0; indiceCompras< ofertasCompraTmp.size(); indiceCompras++ )
                        if(ofertasCompraTmp.get(indiceCompras).getInfo().getPrecioCompra()>=precio)
                            break;


                    indiceVentasAux = indiceVentas;

                    boolean marcarPrecio = false;
                    
                    try{
                    for(i=indiceCompras; i< ofertasCompraTmp.size(); i++ )
                    {
                            for(j=indiceVentas;j< ofertasVentaTmp.size();j++)
                            {
                                if(ofertasCompraTmp.get(i).getInfo().getCantidad()>0)
                                {
                                    if(ofertasVentaTmp.get(j).getInfo().getCantidad()>0)
                                    {
                                        cantidad = Math.min(ofertasCompraTmp.get(i).getInfo().getCantidad(), ofertasVentaTmp.get(j).getInfo().getCantidad());
                                        ofertasCompraTmp.get(i).getInfo().setCantidad(ofertasCompraTmp.get(i).getInfo().getCantidad() - cantidad);
                                        ofertasVentaTmp.get(j).getInfo().setCantidad(ofertasVentaTmp.get(j).getInfo().getCantidad() - cantidad);

                                        AgenteAdministrador.VolumenNegociado[accionAManejar]+=(2*(cantidad*precio));
                                        
                                        AgenteAdministrador.IDOrdenesCompraExito.get(accionAManejar).add(ofertasCompraTmp.get(i).getInfo().getIDOfertaCompra());
                                        AgenteAdministrador.IDOrdenesVentaExito.get(accionAManejar).add(ofertasVentaTmp.get(j).getInfo().getIDOfertaVenta());
                                        
                                        enviarNotificacionesCalce(ofertasCompraTmp.get(i),ofertasVentaTmp.get(j),precio, cantidad, idAccion,true);

                                        calceExitoso = true;
                                        cantidadtotal+=cantidad;
                                        
                                        //Se quita estas dos lineas cuando se quiere que siempre se marque precio
                                        if(precio*cantidad>UVR*66000)
                                        marcarPrecio = true;
                                       

                                    }
                                    else
                                        indiceVentas++;
                                }
                                else
                                    break;
                            }
                      
                    }
                    
                    } catch (IndexOutOfBoundsException e) {
                    System.err.println(getLocalName() + " -> Excepcion de Index en eliminar Calzados Call Market");
                }catch( NullPointerException e)
                {
                    System.err.println(getLocalName()+" -> Excepcion NullPointer en Eliminar Calzados Call Market");
                }

               
                    //Se habilita esta linea si se quiere que siempre marque precio
                    //marcarPrecio = true;
                    
                if(marcarPrecio)
                {
                    AgenteAdministrador.PrecioReferencia[idAccion] = precio;
                   /*if(!marcarPrecio)
                   System.out.println(getLocalName() + " ------No sobrepaso las 66000 UVR -------- Cantidad total negociada - " + cantidadtotal + " - Volumen " + (precio * cantidadtotal));
                   */
                }
                else
                {
                    AgenteAdministrador.PrecioReferencia[idAccion] = PrecioReferenciaOriginal;
                    System.out.println(getLocalName() + " ------No sobrepaso las 66000 UVR -------- Cantidad total negociada - " + cantidadtotal + " - Volumen " + (precio * cantidadtotal));
                }
                eliminarCalzadosCallMarket(indiceCompras,indiceVentasAux, idAccion);
           }
        else
            System.out.println(getLocalName() + " -> Cantidad para negociar es 0, no se negociara en Call Market para accion "+idAccion+"!!!!!!!!!!!!!!!!!!!!!!");

        AgenteAdministrador.CanalNegociacion((float)1,idAccion);
        eliminarPorFueraDelCanal(idAccion);



        if(AgenteAdministrador.graficarLibro)
        if(idAccion == AgenteAdministrador.onSimulation.ProductoActual)
         AgenteAdministrador.onSimulation.TablaLibroOfertas(ofertasVenta, ofertasCompra);/////


           return calceExitoso;
    }

   //Obtiene en un solo array, todas las ofertas pero ordenadas por precio, sin importar que que sean de compra o de venta pues
    //adicionalmente mira las cantidades que pueden comprar o vender
    private /*synchronized*/ void obtenerOfertasCallMarketMejorado(int idAccion)
    {
        obtenerOfertasCompra();
        obtenerOfertasVenta();
        OfertaCallMarket ofertaAux;
        ofertasCallSession = new ArrayList<OfertaCallMarket>();
        Set<Float> preciosO = new HashSet<Float>();

        int i,j, cantidadC, cantidadV;


        for (i=0; i<ofertasCompra.size(); i++)
        {
              ofertasCompraTmp.add(ofertasCompra.get(i));
              preciosO.add(ofertasCompra.get(i).getInfo().getPrecioCompra());

        }

        for (i=0; i<ofertasVenta.size(); i++)
        {
                ofertasVentaTmp.add(ofertasVenta.get(i));
                preciosO.add(ofertasVenta.get(i).getInfo().getPrecioVenta());
        }

        i=0;
        float precios[] = new float[preciosO.size()];
        for(float x: preciosO)
            precios[i++] = x;

        for(int k = 0; k < precios.length; k++ )
        {
            cantidadC = 0;
            for(i=0; i<ofertasCompraTmp.size(); i++)
            {
                if(precios[k] <= ofertasCompraTmp.get(i).getInfo().getPrecioCompra())
                    cantidadC+= ofertasCompraTmp.get(i).getInfo().getCantidad();
            }

            cantidadV = 0;
            for(j=0; j<ofertasVentaTmp.size(); j++)
            {
                if(precios[k] >= ofertasVentaTmp.get(j).getInfo().getPrecioVenta())
                    cantidadV+= ofertasVentaTmp.get(j).getInfo().getCantidad();
            }

            ofertaAux = new OfertaCallMarket();

            ofertaAux.setCantidadC(cantidadC);
            ofertaAux.setCantidadV(cantidadV);
            ofertaAux.setCalce( Math.min(cantidadC, cantidadV));
            ofertaAux.setPrecio(precios[k]);
            ofertaAux.setDesvalance(Math.abs(cantidadC-cantidadV));

            ofertasCallSession.add(ofertaAux);

        }

        Collections.sort(ofertasCallSession);
        Collections.reverse(ofertasCallSession);


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
                if (ofertasCompraTmp.get(i).getInfo().getCantidad() > 0)
                {
                    ofertasCompra.get(i).getInfo().setCantidad(ofertasCompraTmp.get(i).getInfo().getCantidad());
                }
                else {
                    //System.out.println(k+"    " +ofertasCompraTmp.get(idAccion).size() +"   " +compra+"   "+ ofertasCompra.get(idAccion).size());
                    ofertasCompra.remove(i);
                    //caducidadOfertasCompra.get(idAccion).remove(i);
                    ofertasCompraTmp.remove(i);
                    
                    //System.out.println(k+"    " +ofertasCompraTmp.get(idAccion).size() +"   " +compra+"   "+ ofertasCompra.get(idAccion).size());
                    k--;
                    i--;
                }
            }
            else
            {

                if (ofertasVentaTmp.get(i).getInfo().getCantidad() > 0)
                    ofertasVenta.get(i).getInfo().setCantidad(ofertasVentaTmp.get(i).getInfo().getCantidad());
                else {
                    //System.out.println(k+"    " +ofertasVentaTmp.get(idAccion).size() +"   " +compra+"   "+ofertasVenta.get(idAccion).size() );
                    ofertasVenta.remove(i);
                    //caducidadOfertasVenta.get(idAccion).remove(i);
                    ofertasVentaTmp.remove(i);
                    //System.out.println(k+"    " +ofertasVentaTmp.get(idAccion).size() +"   " +compra+"   "+ofertasVenta.get(idAccion).size() );
                    
                    k--;
                    i--;

                }

            }
        }


        if(compra==false)
        {
            j = ofertasVentaTmp.size()-1;
//System.out.println(" Venta   " +ofertasVentaTmp.get(idAccion).size() +"   " +j+"   "+ofertasVenta.get(idAccion).size() );

            if(ofertasVentaTmp.get(j).getInfo().getCantidad() > 0)
                ofertasVenta.get(j).getInfo().setCantidad(ofertasVentaTmp.get(j).getInfo().getCantidad());
            else
            {
                ofertasVenta.remove(j);
                ofertasVentaTmp.remove(j);
                //caducidadOfertasVenta.get(idAccion).remove(j);
                

            }

        }
        else
        {
            j = ofertasCompraTmp.size()-1;
            //System.out.println(" Compra   " +ofertasCompraTmp.get(idAccion).size() +"   " +j+"   "+ofertasCompra.get(idAccion).size() );
            if(ofertasCompraTmp.get(j).getInfo().getCantidad() > 0)
                ofertasCompra.get(j).getInfo().setCantidad(ofertasCompraTmp.get(j).getInfo().getCantidad());
            else
            {
                ofertasCompra.remove(j);
                ofertasCompraTmp.remove(j);
                //caducidadOfertasCompra.get(idAccion).remove(j);
                
            }
        }


         //Libro.TablaResultados.setViewportView(generarTabla());
        //Libro.metodo(ofertasVentaTmp.get(idAccion), ofertasCompraTmp.get(idAccion));/////

    }

    @SuppressWarnings("element-type-mismatch")
    private /*synchronized*/ void eliminarCalzadosCallMarket(int indCompra, int indVenta, int Accion)
    {
        try {
            int i, posicion;

            //System.out.println( ofertasCompra.get(Accion).size()+" --------- "+ofertasCompraTmp.get(Accion).size()+"-------Compra-------CallMarket "+Accion);
            for (i = indCompra; i < ofertasCompraTmp.size(); i++) {
                posicion = ofertasCompra.indexOf(ofertasCompraTmp.get(i));
                //System.out.println("Posicion C " +posicion);
                int cantidad = ofertasCompraTmp.get(i).getInfo().getCantidad();
                if (cantidad > 0) {
                    ofertasCompra.get(posicion).getInfo().setCantidad(cantidad);
                } else {
                    //System.out.println( ofertasCompra.get(Accion).size()+" ----C----- "+ofertasCompraTmp.get(Accion).size());
                    ofertasCompra.remove(posicion);
                    ofertasCompraTmp.remove(i);
                    //caducidadOfertasCompra.get(Accion).remove(posicion);
                    //System.out.println( ofertasCompra.get(Accion).size()+" ----C------ "+ofertasCompraTmp.get(Accion).size());

                    i--;

                }

            }

            //el problema es que tiene que buscar las ofertas
            //System.out.println( ofertasVenta.get(Accion).size()+" --------- "+ofertasVentaTmp.get(Accion).size()+"-----Venta---------CallMarket "+Accion);
            for (i = indVenta; i < ofertasVentaTmp.size(); i++) {
                posicion = ofertasVenta.indexOf(ofertasVentaTmp.get(i));
                //System.out.println("Posicion v " +posicion);
                int cantidad = ofertasVentaTmp.get(i).getInfo().getCantidad();
                if (cantidad > 0) {
                    ofertasVenta.get(posicion).getInfo().setCantidad(cantidad);
                } else {
                    //System.out.println( ofertasVenta.get(Accion).size()+" --------- "+ofertasVentaTmp.get(Accion).size()+"-----Venta---CallMarket "+Accion);
                    ofertasVenta.remove(posicion);
                    //caducidadOfertasVenta.get(Accion).remove(posicion);
                    ofertasVentaTmp.remove(i);
                    //System.out.println( ofertasVenta.get(Accion).size()+" --------- "+ofertasVentaTmp.get(Accion).size()+"-----Venta---CallMarket "+Accion);

                    i--;

                }
            }

        } catch (IndexOutOfBoundsException e) {
            System.err.println(getLocalName() + " -> Excepcion de Index en eliminar Calzados Call Market");
        }catch( NullPointerException e)
        {
            System.err.println(getLocalName()+" -> Excepcion NullPointer en Eliminar Calzados Call Market");
        }


       // EstaEnCallMarket[Accion] = false;


    }

    @SuppressWarnings("element-type-mismatch")
    public /*synchronized*/ void eliminarPorFueraDelCanal(int Accion)
    {
        try {
            int i;
            for (i = 0; i < ofertasVenta.size(); i++) {
                if (ofertasVenta.get(i).getInfo().getPrecioVenta() > AgenteAdministrador.PrecioPorEncima[Accion] || ofertasVenta.get(i).getInfo().getPrecioVenta() < AgenteAdministrador.PrecioPorDebajo[Accion]) {
                    enviarNotCancelarVenta(ofertasVenta.get(i), "canal", ofertasVenta.get(i).getProducto().getIDProducto());
                    ofertasVenta.remove(i);
                    //System.out.println(getLocalName()+"------Se cancelo una de venta---------------");
                    //caducidadOfertasVenta.get(Accion).remove(i);
                    i--;
                }
            }


            for (i = 0; i < ofertasCompra.size(); i++) {
                if (ofertasCompra.get(i).getInfo().getPrecioCompra() > AgenteAdministrador.PrecioPorEncima[Accion] || ofertasCompra.get(i).getInfo().getPrecioCompra() < AgenteAdministrador.PrecioPorDebajo[Accion]) {
                    enviarNotCancelarCompra(ofertasCompra.get(i), "canal", ofertasCompra.get(i).getProducto().getIDProducto());
                    //System.out.println(getLocalName()+"------Se cancelo una de compra---------------");
                    ofertasCompra.remove(i);
                    //caducidadOfertasCompra.get(Accion).remove(i);
                    i--;
                }
            }
        } catch (IndexOutOfBoundsException e)
        {
            System.err.println(getLocalName()+" -> Excepcion de Index en Eliminar por fuera del Canal");
        }
        catch( NullPointerException e)
        {
            System.err.println(getLocalName()+" -> Excepcion NullPointer en Eliminar por fuera del Canal");
        }
    }
    
    
    ///////////////////////////////
    ////////////////////////////////
    ////////////////////////////////
    ////////////////////////////////




   

    class HiloEjecutarCallMarket extends java.lang.Thread {
        int Accion;
        long duracionSesion;
        long aleatorioSesion;
        String momento;
        public HiloEjecutarCallMarket(int idAccion, long duracion, long aleatorio, String momento)
        {
             //HiloEjecutarCallMarket tiempoOfertas = new HiloEjecutarCallMarket();
             Accion = idAccion;
             duracionSesion = duracion;
             aleatorioSesion = aleatorio;
             this.momento = momento;
             //this.setPriority(this.MAX_PRIORITY);
             this.start();
           
        }

        @Override
        @SuppressWarnings("CallToThreadDumpStack")
        public void run()
        {
            try {

                if(AgenteAdministrador.graficarLibro)
                mostrarInfoPrecios(1);
                
                //CanalNegociacion((float)2.5,Accion);
                long tiempo = (long)aleatorio((float)(duracionSesion+aleatorioSesion),(float)(duracionSesion-aleatorioSesion));
                System.out.println(getLocalName()+" -> Entro al Hilo para Call market de accion "+Accion+", duración de la sesion de "+tiempo);
               
                sleep(tiempo);

                

            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            
            
            //FinCallMarketAgentes(Accion);
            //FinCallMarketSubastador(Accion);
            FinCallMarket(Accion,momento);
            



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

            if(AgenteAdministrador.graficarLibro)
            {
                AgenteAdministrador.onSimulation = new Libro2(cAcciones,tiempo,tiempoFi);
                AgenteAdministrador.onSimulation.setVisible(true);
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



    // ---------- Message print-out --------------------------------------

	static long t0 = System.currentTimeMillis();



        void InicioCallMarket(int accion, String momento)
        {

            InicioCallMarketAgentes(accion,momento);
            System.out.println(getLocalName()+" -> Se envio mensaje a agentes de Inicio Call Market para Accion - "+accion+ " - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
            HiloEjecutarCallMarket tiempoOfertas;


            if(AgenteAdministrador.graficarLibro)
            AgenteAdministrador.onSimulation.serie(AgenteAdministrador.PrecioPorEncima[accion], AgenteAdministrador.PrecioPorDebajo[accion], AgenteAdministrador.PrecioReferencia[accion], -1, accion);

            if(momento.equals("intra"))
            AgenteAdministrador.CanalNegociacion(2.5f,accion);

            //InicioCallMarketAgentes(idAccion);

            long tiem = tiempoSimulacion-(tiempoCallMarketFinal+tiempoAleatorioCallFinal);
            long intra = tiempoCallMarketIntra + tiempoAleatorioCallIntra;
            long tiempoTrans = (((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+intra);
            //System.out.println(getLocalName() + " -> Tiempo transcurrido de la simulacion hasta ahora ----- "+tiempoTrans);
            if(tiempoTrans>=tiem)
            {
                SesionFinal = true;
                AgenteAdministrador.tiempoCallMarket[accionAManejar] = tiempoCallMarketFinal;
                AgenteAdministrador.tiempoCallMarketAlea[accionAManejar] = tiempoAleatorioCallFinal;

                tiempoOfertas = new HiloEjecutarCallMarket(accion, tiempoCallMarketFinal, tiempoAleatorioCallFinal,momento);
            }
            else
            tiempoOfertas = new HiloEjecutarCallMarket(accion, tiempoCallMarketIntra, tiempoAleatorioCallIntra,momento);

            //tiempoOfertas.start();


            if(AgenteAdministrador.graficarLibro)
            if(accion == AgenteAdministrador.onSimulation.ProductoActual)
            {
                ordenarOfertasCompra();//ordena desendentemente
                ordenarOfertasVenta();//ordena ascendentemente
                AgenteAdministrador.onSimulation.TablaLibroOfertas(ofertasVentaTmp, ofertasCompraTmp);
            }
            //EstaEnCallMarket[idAccion] = true;



        }



       void FinCallMarket(int Accion, String momento)
        {

                    FinCallMarketAgentes(Accion,momento);
                    //System.out.println(getLocalName()+" -> Se envio FINAL DE SESION CALL MARKET A AGENTES "+Accion+" -  "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                    SessionCallMarket(Accion);

                    //FinCallMarketAgentes(idAccion);

                    //CanalNegociacion(1f,idAccion);

                    if(AgenteAdministrador.graficarLibro)
                    AgenteAdministrador.onSimulation.serie(AgenteAdministrador.PrecioPorEncima[Accion], AgenteAdministrador.PrecioPorDebajo[Accion], AgenteAdministrador.PrecioReferencia[Accion], -1, Accion);

                   AgenteAdministrador.tiempoCallMarket[accionAManejar] = tiempoCallMarketIntra;
                   AgenteAdministrador.tiempoCallMarketAlea[accionAManejar] = tiempoAleatorioCallIntra;

                  // addBehabiour(flusher = new GCAgent( this, 1000));
                   /////////Elimina los mensajes que no se han visto, para evitar que se acumulen
                   //flusher = new GCAgent( agenteSubastador, 1000,idAccion);
                   //addBehaviour( flusher);
                  /////////Elimina los mensajes que no se han visto, para evitar que se acumulen

                   EstaEnCallMarket = false;

                   if(SesionFinal == true)
                   {
                       

                            //FinDeSubastaAgentes();
                            System.out.println(getLocalName()+" -> MENSAJE FIN DE LA SUBASTA - "+(((new Date()).getTime()-AgenteAdministrador.horaInicial.getTime())+tiempoCallMarketIntra + tiempoAleatorioCallIntra));
                           FinDeSubasta();
                           simulacionCorriendo = false;
                           FinCallMarketAdministrador();
                           
                           //ROBehaviour.action();
                            //finalizado = true;

                            //return;
                        
                   }
                   if(AgenteAdministrador.graficarLibro)
                    mostrarInfoPrecios(0);

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

        


}


