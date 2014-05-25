/*
 * Generico.java
 */

package agentes;

import basedatos.*;
import ontologias.*;

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


abstract class Generico extends Agent
{
    public Codec codec = new SLCodec(); //Se crea un lenguaje para la ontologia
    public Ontology ontologia = DoblePuntaOntology.getInstance();//se crea la ontologia
    public AID agenteSubastador;
    public AID agenteAdministrador;
    public String nombre;
    public int[] cantidadXAccion;
    public int[] tiempoEsperadoPorAccion;
    public int[] decisionCambiarprobabilidad;
    public int[] cantidadOfertada;
    public int[] cantidadesaOfertar;
    public float[] probabilidadTransarAccion;
    public float[] precioPromedioAccion;
    public float[] saldoOfertado;
    public int idAgente= -1;
    public int numAcciones;
    public float saldo = 0;
    public boolean subastaActiva = true;
    public jade.wrapper.AgentContainer contenedorBursatil;
    public ConexionBD BD;
    /*Cantidad de números que genera la semilla
     * Best 16402 1313766063150
Best 18262 1313766128988
Best 19539 43332
Best 20681 304449
Best 21953 393468
Best 21956 514467
     * 
     * 
     */
    public Random objeto;
    public int tipoAgente;
    public boolean[] OfertaEnviada;
    public boolean[] OfertaEliminada;
    //public Date horaInicial;
    public HiloOfertas[] hilosOfertas;
    

    
//********************************************************************************************************************************************************
//FUNCION DE INICIALIZACION DEL AGENTE
//********************************************************************************************************************************************************    
   

	class RealizarRegistro extends SimpleBehaviour {
        Agent agente;
        ParticiparEnSubasta PSBehaviour;
        boolean finalizado = false;

        public RealizarRegistro(Agent a) 
        {
            super(a);
            agente = a;
            PSBehaviour = new ParticiparEnSubasta(a);
        }

        public void action() 
        {
            try
            {

                // Se construye la descripcion usada como plantilla para la busqueda
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription templateSD = new ServiceDescription();

                ///templateSD.setName("RealizarCalce");
                templateSD.setName("Administrador");
                template.addServices(templateSD);

                // Solo se desea recibir un resultado a lo sumo
                SearchConstraints SC = new SearchConstraints();
                SC.setMaxResults(new Long(1));

                DFAgentDescription[] resultados = DFService.search(agente, template, SC);
                if (resultados.length == 1) {
                    DFAgentDescription dfd = resultados[0];
                    //agenteSubastador = dfd.getName();//Aqui debe ser el subastador
                    agenteAdministrador = dfd.getName();

                    ConceptoBursatil agenteBursatil = new ConceptoBursatil();
                    agenteBursatil.setIDBursatil(idAgente);
                    agenteBursatil.setNombre(nombre);
                    agenteBursatil.setSaldo(saldo);
                    agenteBursatil.setTipo(tipoAgente);
                    
                    PredicadoRegistrarAgenteBursatil registrarAgente= new PredicadoRegistrarAgenteBursatil();
                    registrarAgente.setBursatil(agenteBursatil);

                    ACLMessage mensajeSalienteRegistrar = new ACLMessage(ACLMessage.SUBSCRIBE);
                    mensajeSalienteRegistrar.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                    mensajeSalienteRegistrar.setLanguage(codec.getName());
                    mensajeSalienteRegistrar.setOntology(ontologia.getName());
                    //mensajeSalienteRegistrar.addReceiver(agenteSubastador);////Aqui el subastador debe recibir el predicado registrar agente bursatil
                    mensajeSalienteRegistrar.addReceiver(agenteAdministrador);

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
                            PredicadoConfirmarRegistro registrar = (PredicadoConfirmarRegistro)mensajeEntrante;
                            ConceptoRegistro registro = registrar.getRegistro();
                            idAgente = registro.getIDBursatil(); // Guarda el ID asignado por el subastador
                            finalizado = true;
                        }
                    }

                    if(tipoAgente == 1)
                        registroTecnico();
                    else
                    {
                        //AgenteAdministrador.saldosAgentes.set(idAgente, saldo);
                        AgenteAdministrador.saldosAgentes[idAgente]= saldo;
                        for(int i=0; i<numAcciones; i++) {
                            //AgenteAdministrador.accionesxagente.get(idAgente).set(i, cantidadXAccion[i]);
                            AgenteAdministrador.accionesxagente[idAgente][i]= cantidadXAccion[i];
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
            if(finalizado) {
                //System.out.println(getLocalName()+" -> ME HE REGISTRADO");
                hilosOfertas = new HiloOfertas[numAcciones];
                /*for(int i=0; i< numAcciones;i++)
                    hilosOfertas[i] = new HiloOfertas();*/
                //OfertasAEnviar = new EnviarOferta[numAcciones];
                addBehaviour(PSBehaviour);

            }
                    return finalizado;
        }

    }
    
    class ParticiparEnSubasta extends SimpleBehaviour
    {
        Agent agente;
        ParticiparEnSubasta PSBehaviour;
        boolean[] finCallMarket = new boolean[numAcciones];
        int contadorCM = 0;
        
        public ParticiparEnSubasta(Agent a)
        {
            super(a);
            agente = a;
            Arrays.fill(finCallMarket, false);
        }

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

                        if (mensajeEntrante instanceof PredicadoInicioSubasta) {


                            /*EnviarOfertaBehaviour a = new EnviarOfertaBehaviour(myAgent,idAgente*1000+1000,0);
                            addBehaviour(a);
                            System.out.println(getLocalName()+" Agrego comportamiento - ");*/
                            //horaInicial = new Date();
                            
                            // Se lanza el hilo de las ofertas, calculando el numero
                            // de ofertas que se van a realizar y el tiempo para
                            // ofertar.



                            subastaActiva = true;
                            
                            parametrosIniciarSubasta();

                            for (int i = 0; i < numAcciones; ++i) {

                                OfertaEliminada[i] = false;
                                OfertaEnviada[i] = false;

                                if (probabilidadTransarAccion[i] != 0)
                                {
                                    long tiempo = AgenteAdministrador.tiempoSimulacion;//-(AgenteAdministrador.tiempoCallMarketFinal+AgenteAdministrador.tiempoAleatorioCallFinal);
                                    hilosOfertas[i] = new HiloOfertas(i, tiempo);
                                    //hilosOfertas[i].setParametros(i, tiempo);
                                     //System.out.println(getLocalName()+ " -> Se ha creado el hilo -  "+hilosOfertas[i].getId()+"  - "+i);
                                     hilosOfertas[i].start();

                                     //hilosOfertas[i].start();
                                    //hilosOfertas[i].interrupt();
                                    /*long tiempoEspera = (long)aleatorio(AgenteSubastador.tiempoSimulacion, 0);
                                    System.out.println(getLocalName()+ " -> Se genera comportamiento "+tiempoEspera);
                                    EnviarOferta OfertasAEnviar = new EnviarOferta(agente,tiempoEspera,i);
                                    addBehaviour(OfertasAEnviar);*/
                                }
                            }
                            /*if (numOfertas != 0) {
                                tiempo = AgenteAdministrador.tiempoSimulacion/numOfertas;
                                while (accion < numAcciones && probabilidadTransarAccion[accion] == 0) {
                                    ++accion;
                                }
                                if (accion < numAcciones) {
                                    new HiloOfertas(accion, tiempo);
                                }
                            }*/

                        }

                        else if (mensajeEntrante instanceof PredicadoNotificarVenta) {
                            float precioTransaccion = ((PredicadoNotificarVenta)mensajeEntrante).getNotificacionVenta().getPrecio();
                            int cantidad = ((PredicadoNotificarVenta)mensajeEntrante).getNotificacionVenta().getCantidad();
                            int accion = ((PredicadoNotificarVenta)mensajeEntrante).getNotificacionVenta().getIDProducto();

                            //System.out.println(getLocalName()+" Realice una Venta");

                            saldo += precioTransaccion*cantidad;
                            //AgenteAdministrador.saldosAgentes.set(idAgente, saldo);
                            AgenteAdministrador.saldosAgentes[idAgente]= saldo;

                            cantidadXAccion[accion] -= cantidad;
                            cantidadOfertada[accion]-=cantidad;
                            
                            if(tipoAgente != 1)
                            {
                                if (cantidadXAccion[accion] == 0)
                                    precioPromedioAccion[accion] = 0;
                            }

                           // AgenteAdministrador.accionesxagente.get(idAgente).set(accion, cantidadXAccion[accion]);
                            AgenteAdministrador.accionesxagente[idAgente][accion]= cantidadXAccion[accion];
                            // Si todavia no ha vendido una accion que queria vender y recibe notificacion
                            // de venta, resetea la probabilidad de vender
                            if (probabilidadTransarAccion[accion] >= -1 && probabilidadTransarAccion[accion] < 0) {
                                probabilidadTransarAccion[accion] = 0;
                            }
                        }

                        else if (mensajeEntrante instanceof PredicadoNotificarCompra) {
                            float precioTransaccion = ((PredicadoNotificarCompra)mensajeEntrante).getNotificacionCompra().getPrecio();
                            int cantidad = ((PredicadoNotificarCompra)mensajeEntrante).getNotificacionCompra().getCantidad();
                            int accion = ((PredicadoNotificarCompra)mensajeEntrante).getNotificacionCompra().getIDProducto();

                            //System.out.println(getLocalName()+" Realice una Compra");

                            if(tipoAgente != 1)
                            precioPromedioAccion[accion] = (precioPromedioAccion[accion]*cantidadXAccion[accion] + precioTransaccion*cantidad)/(cantidadXAccion[accion] + cantidad);

                            saldo -= precioTransaccion*cantidad;
                            //AgenteAdministrador.saldosAgentes.set(idAgente, saldo);
                            AgenteAdministrador.saldosAgentes[idAgente]= saldo;
                            cantidadXAccion[accion] += cantidad;
                            saldoOfertado[accion] -= precioTransaccion*cantidad;

                            //AgenteAdministrador.accionesxagente.get(idAgente).set(accion, cantidadXAccion[accion]);
                            AgenteAdministrador.accionesxagente[idAgente][accion]= cantidadXAccion[accion];
                            
                            // Si todavia no ha comprado una accion que queria comprar y recibe notificacion
                            // de compra, resetea la probabilidad de comprar
                            if (probabilidadTransarAccion[accion] <= 1 && probabilidadTransarAccion[accion] > 0) {
                                probabilidadTransarAccion[accion] = 0;
                            }
                        }

                        else if (mensajeEntrante instanceof PredicadoFinSubasta) {

                            subastaActiva = false;
                            ////////////////////////////////////////////////////
                            for (int i = 0; i < numAcciones; ++i)
                            {
                                   if (probabilidadTransarAccion[i] != 0)
                                    {

                                       if(hilosOfertas[i]!= null)
                                       {
                                           //if(hilosOfertas[i].isAlive())
                                                {
                                                    //System.out.println(getLocalName()+" -> Hilo para enviar oferta se ha parado por final de subasta  - "+hilosOfertas[i].getId()+"  - "+i);
                                                    hilosOfertas[i].interrupt();
                                                    hilosOfertas[i] = null;
                                                    
                                                }
                                         
                                        }
                                       System.gc();

                                    }
                            }/////////////////////////////////////////////////////


                            
                            //Si el saldo es cero y no tiene acciones, el agente debe morir
                            int totalAcciones = 0;
                            for (int i=0; i<numAcciones; i++)
                                totalAcciones += cantidadXAccion[i];
                            if (saldo == 0 && totalAcciones == 0)
                                doDelete();

                           /* // A todos las probabilidades de ofertar una accion se le multiplica 0.75
                            for (int i = 0; i < numAcciones; ++i) {
                                if (probabilidadTransarAccion[i] != 0)
                                    probabilidadTransarAccion[i] *= 0.75;
                            }*/

                        }

                        else if (mensajeEntrante instanceof PredicadoEnEspera) {
                              //System.out.println(getLocalName() +" -> La oferta que realice no calzo");
                        }

                        else if (mensajeEntrante instanceof PredicadoInicioCallMarket)
                        {
                            String nombre = ((PredicadoInicioCallMarket)mensajeEntrante).getProducto().getNombre();
                           // System.out.println(getLocalName()+" --- Se ha iniciado Call Market --- "+nombre+" ----");
                           int i=((PredicadoInicioCallMarket)mensajeEntrante).getProducto().getIDProducto();
                           String momento = ((PredicadoInicioCallMarket)mensajeEntrante).getMomento();
                           // for (int i = 0; i < numAcciones; ++i)
                            //{
                                if (probabilidadTransarAccion[i] != 0)
                                {
                                    if(OfertaEnviada[i] == false)
                                    {
                                        ////////////////////////////////////////////////////
                                        if(hilosOfertas[i] != null)
                                        //if(hilosOfertas[i].isAlive())
                                        {
                                            //System.out.println(getLocalName()+" -> Hilo para enviar oferta se ha parado por inicio de Call Market - "+hilosOfertas[i].getId()+"  - "+i);
                                            hilosOfertas[i].interrupt();
                                            hilosOfertas[i] = null;
                                        }
                                        //System.gc();

                                        //long tiempo = (long)aleatorio((float)(AgenteAdministrador.tiempoCallMarket+AgenteAdministrador.tiempoCallMarketAlea),(float)(AgenteAdministrador.tiempoCallMarket-AgenteAdministrador.tiempoCallMarketAlea));
                                        hilosOfertas[i] = new HiloOfertas(i, (AgenteAdministrador.tiempoCallMarket[i]-AgenteAdministrador.tiempoCallMarketAlea[i]));
                                       // hilosOfertas[i].setParametros(i, tiempo);
                                        hilosOfertas[i].start();
                                        ////////////////////////////////////////////////////


                                        /*removeBehaviour(OfertasAEnviar);
                                        long tiempoEspera = (long)aleatorio(AgenteAdministrador.tiempoCallMarket+AgenteAdministrador.tiempoCallMarketAlea,AgenteAdministrador.tiempoCallMarket-AgenteAdministrador.tiempoCallMarketAlea);
                                        EnviarOferta OfertasAEnviar = new EnviarOferta(agente,tiempoEspera,i);
                                        addBehaviour(OfertasAEnviar);*/
                                    }
                                         
                                }
                            //}
                           
                        }

                        else if (mensajeEntrante instanceof PredicadoFinCallMarket)
                        {
                            String nombre = ((PredicadoFinCallMarket)mensajeEntrante).getProducto().getNombre();
                            //System.out.println(getLocalName()+" --- Se ha Finalizado Call Market --- "+nombre+" ----");
                            int i=((PredicadoFinCallMarket)mensajeEntrante).getProducto().getIDProducto();
                            String momento = ((PredicadoFinCallMarket)mensajeEntrante).getMomento();

                            if(momento.equalsIgnoreCase("final"))
                            {
                                finCallMarket[i]=true;
                                contadorCM++;
                                if(contadorCM == numAcciones)
                                {
                                    subastaActiva = false;
                                    ////////////////////////////////////////////////////

                                               System.gc();




                                    //Si el saldo es cero y no tiene acciones, el agente debe morir
                                    int totalAcciones = 0;
                                    for (int j=0; j<numAcciones; j++)
                                        totalAcciones += cantidadXAccion[j];
                                    if (saldo == 0 && totalAcciones == 0)
                                        doDelete();

                                    //System.out.println(getLocalName()+" Ultimo mensaje de Fin CM a llegado, la simulacion a terminado");
                                    
                                }
                            }

                            if (probabilidadTransarAccion[i] != 0)
                                {
                                    /////////////////////////
                                    //if(OfertaEnviada[i] == false)
                                    {

                                        if(hilosOfertas[i]!=null)
                                        {
                                            //if(hilosOfertas[i].isAlive())
                                            {
                                                //System.out.println(getLocalName()+" -> Hilo para enviar oferta se ha parado por FINAL de CM - "+hilosOfertas[i].getId()+"  - "+i);
                                                hilosOfertas[i].interrupt();
                                                hilosOfertas[i] = null;
                                            }
                                           // System.gc();
                                        }

                                        /*long tiempo = (long)aleatorio((float)(AgenteSubastador.tiempoCallMarket+AgenteSubastador.tiempoCallMarketAlea),(float)(AgenteSubastador.tiempoCallMarket-AgenteSubastador.tiempoCallMarketAlea));
                                        hilosOfertas[i] = new HiloOfertas(i, tiempo);
                                        hilosOfertas[i].start();*/
                                      
                                    }
                                    //////////////////////////

                                    //removeBehaviour(OfertasAEnviar);

                                }
                           // }
                        }
                    }
                    else if(mensajeGeneral.getPerformative() == ACLMessage.CANCEL)
                        {
                            if (mensajeEntrante instanceof PredicadoCancelarVenta)
                            {
                                String concepto = ((PredicadoCancelarVenta)mensajeEntrante).getConcepto();
                                //System.out.println(getLocalName()+" - Me Cancelaron una Venta");
                                int producto = ((PredicadoCancelarVenta)mensajeEntrante).getIDProducto();
                                    int cantidad = ((PredicadoCancelarVenta)mensajeEntrante).getOfertaVenta().getCantidad();
                                    float precio = ((PredicadoCancelarVenta)mensajeEntrante).getOfertaVenta().getPrecioVenta();
                                    int periodo = ((PredicadoCancelarVenta)mensajeEntrante).getOfertaVenta().getPeriodo();


                                 cantidadOfertada[producto]-=cantidad;

                                if(concepto.equals("caduco"))
                                {
                                    //System.out.println("caduco-----");
                                }
                                if(concepto.equals("canal"))
                                {
                                    
                                    if(periodo==AgenteAdministrador.simulacionActual)
                                    {
                                        if(OfertaEnviada[producto] == true)
                                        {
                                            OfertaEliminada[producto] = true;

                                            long tiempoRes = ((new Date()).getTime()- AgenteAdministrador.horaInicial.getTime());
                                            //System.out.println(getLocalName()+" Oferta Venta eliminada por estar por fuera del canal - Precio - "+precio+" - cantidad - "+cantidad+" - Accion "+producto+" - Periodo - "+periodo);

                                            /*if(tiempoRes<AgenteSubastador.tiempoSimulacion)
                                            {
                                                long tiempoEspera = (long)aleatorio(AgenteSubastador.tiempoSimulacion -tiempoRes, 0);
                                                OfertasAEnviar = new EnviarOferta(myAgent,tiempoEspera,producto);
                                                addBehaviour(OfertasAEnviar);
                                            }*/
                                            /////////////////////////////////////////////////////
                                            if(tiempoRes<AgenteAdministrador.tiempoSimulacion)
                                            {
                                               hilosOfertas[producto] = new HiloOfertas(producto, AgenteAdministrador.tiempoSimulacion -tiempoRes);
                                                //hilosOfertas[producto].setParametros(producto, AgenteSubastador.tiempoSimulacion -tiempoRes);
                                                //System.out.println(getLocalName()+ " -> Se crea hilo CV -  "+hilosOfertas[producto].getId()+"  - "+producto);
                                                hilosOfertas[producto].start();
                                            }
                                            //else
                                            //System.out.println(getLocalName()+" No se volvio a enviar oferta, ya se termino el periodo");

                                            /////////////////////////////////////////////////////
                                        }
                                    }
                                    else
                                    {
                                        //System.out.println(getLocalName()+" Oferta VIAJA DE Venta eliminada por estar por fuera del canal - Precio - "+precio+" - cantidad - "+cantidad+" - Accion "+producto+" - Periodo - "+periodo);
                                    }
                                }
                                if(concepto.equals("tarde"))
                                {
                                    /*int producto = ((PredicadoCancelarVenta)mensajeEntrante).getIDProducto();
                                    float  precio = ((PredicadoCancelarVenta)mensajeEntrante).getOfertaVenta().getPrecioVenta();*/

                                    //System.out.println(getLocalName()+" Oferta venta eliminada por llegar tarde - "+precio);

                                    //System.out.println(getLocalName()+" Oferta Venta eliminada por llegar tarde - "+precio);
                                    if(OfertaEnviada[producto] == true)
                                    {
                                        OfertaEliminada[producto] = true;

                                        long tiempoRes = ((new Date()).getTime()- AgenteAdministrador.horaInicial.getTime());
                                        //System.out.println(getLocalName()+" Oferta Venta eliminada por estar por fuera del canal -- Tiempo transcurrido de simulacion " + tiempoRes);

                                        /*if(tiempoRes<AgenteSubastador.tiempoSimulacion)
                                        {
                                            long tiempoEspera = (long)aleatorio(AgenteSubastador.tiempoSimulacion -tiempoRes, 0);
                                            OfertasAEnviar = new EnviarOferta(agente,tiempoEspera,producto);
                                            addBehaviour(OfertasAEnviar);
                                        }*/
                                        /////////////////////////////////////////////////////
                                        if(tiempoRes<AgenteAdministrador.tiempoSimulacion)
                                        {
                                            hilosOfertas[producto] = new HiloOfertas(producto, AgenteAdministrador.tiempoSimulacion -tiempoRes);
                                            //hilosOfertas[producto].setParametros(producto, AgenteSubastador.tiempoSimulacion -tiempoRes);
                                            //System.out.println(getLocalName()+ " -> Se crea hilo CV -  "+hilosOfertas[producto].getId()+"  - "+producto);
                                            hilosOfertas[producto].start();
                                        }
                                        //else
                                        //System.out.println(getLocalName()+" No se volvio a enviar oferta, ya se termino el periodo");

                                        /////////////////////////////////////////////////////
                                    }
                                }

                            }
                            if (mensajeEntrante instanceof PredicadoCancelarCompra)
                            {
                                String concepto = ((PredicadoCancelarCompra)mensajeEntrante).getConcepto();
                                //System.out.print(getLocalName()+" - Me Cancelaron una Compra - ");

                                int producto = ((PredicadoCancelarCompra)mensajeEntrante).getIDProducto();
                                    int periodo = ((PredicadoCancelarCompra)mensajeEntrante).getOfertaCompra().getPeriodo();
                                    int cantidad = ((PredicadoCancelarCompra)mensajeEntrante).getOfertaCompra().getCantidad();
                                    float precio = ((PredicadoCancelarCompra)mensajeEntrante).getOfertaCompra().getPrecioCompra();

                                    saldoOfertado[producto] -= precio*cantidad;

                                if(concepto.equals("caduco"))
                                {
                                   // System.out.println("caduco-----");
                                }
                                if(concepto.equals("canal"))
                                {
                                    
                                    if(periodo==AgenteAdministrador.simulacionActual)
                                    {
                                        if(OfertaEnviada[producto] == true)
                                        {
                                            OfertaEliminada[producto] = true;

                                            long tiempoRes = ((new Date()).getTime()- AgenteAdministrador.horaInicial.getTime());
                                            //System.out.println(getLocalName()+" Oferta Compra eliminada por estar por fuera del canal - Precio - "+precio+" - cantidad - "+cantidad+" Accion "+producto+" - Periodo - "+periodo);

                                            /*if(tiempoRes<AgenteSubastador.tiempoSimulacion)
                                            {
                                                long tiempoEspera = (long)aleatorio(AgenteSubastador.tiempoSimulacion -tiempoRes, 0);
                                                OfertasAEnviar = new EnviarOferta(agente,tiempoEspera,producto);
                                                addBehaviour(OfertasAEnviar);
                                            }*/
                                            /////////////////////////////////////////////////////
                                            if(tiempoRes<AgenteAdministrador.tiempoSimulacion)
                                            {
                                                hilosOfertas[producto] = new HiloOfertas(producto, AgenteAdministrador.tiempoSimulacion -tiempoRes);
                                                //hilosOfertas[producto].setParametros(producto, AgenteSubastador.tiempoSimulacion -tiempoRes);
                                                //System.out.println(getLocalName()+ " -> Se crea hilo CC -  "+hilosOfertas[producto].getId()+"  - "+producto);
                                                hilosOfertas[producto].start();
                                            }
                                            //else
                                            //System.out.println(getLocalName()+" No se volvio a enviar oferta, ya se termino el periodo");

                                            /////////////////////////////////////////////////////
                                       }
                                    }
                                    else
                                    {
                                        //System.out.println(getLocalName()+" Oferta VIEJA DE Compra eliminada por estar por fuera del canal - Precio - "+precio+" - cantidad - "+cantidad+" Accion "+producto+" - Periodo - "+periodo);
                                    }
                                }
                                if(concepto.equals("tarde"))
                                {

                                    /*int producto = ((PredicadoCancelarCompra)mensajeEntrante).getIDProducto();
                                    float  precio = ((PredicadoCancelarCompra)mensajeEntrante).getOfertaCompra().getPrecioCompra();*/

                                   // System.out.println(getLocalName()+" Oferta compra eliminada por llegar tarde - "+precio);
                                    

                                    //System.out.println(getLocalName()+" Oferta Venta eliminada por llegar tarde - "+precio);
                                    if(OfertaEnviada[producto] == true)
                                    {
                                        OfertaEliminada[producto] = true;

                                        long tiempoRes = ((new Date()).getTime()- AgenteAdministrador.horaInicial.getTime());
                                        //System.out.println(getLocalName()+" Oferta Venta eliminada por estar por fuera del canal -- Tiempo transcurrido de simulacion " + tiempoRes);

                                        /*if(tiempoRes<AgenteSubastador.tiempoSimulacion)
                                        {
                                            long tiempoEspera = (long)aleatorio(AgenteSubastador.tiempoSimulacion -tiempoRes, 0);
                                            OfertasAEnviar = new EnviarOferta(agente,tiempoEspera,producto);
                                            addBehaviour(OfertasAEnviar);
                                        }*/
                                        /////////////////////////////////////////////////////
                                        if(tiempoRes<AgenteAdministrador.tiempoSimulacion)
                                        {
                                            hilosOfertas[producto] = new HiloOfertas(producto, AgenteAdministrador.tiempoSimulacion -tiempoRes);
                                            //hilosOfertas[producto].setParametros(producto, AgenteSubastador.tiempoSimulacion -tiempoRes);
                                            //System.out.println(getLocalName()+ " -> Se crea hilo CC -  "+hilosOfertas[producto].getId()+"  - "+producto);
                                            hilosOfertas[producto].start();
                                        }
                                        //else
                                        //System.out.println(getLocalName()+" No se volvio a enviar oferta, ya se termino el periodo");

                                        /////////////////////////////////////////////////////
                                    }
                                }
                            }
                        }
                        else if(mensajeGeneral.getPerformative()==ACLMessage.CONFIRM)
                                {
                                    if (mensajeEntrante instanceof PredicadoConfirmarOferta) {
                                    //System.out.println(getLocalName() +" -> Recibida confirmacion de oferta");
                                    //break;
                                    }

                                }
                    

                }
            } catch (jade.content.lang.Codec.CodecException ce) {
                ce.printStackTrace();
            } catch (jade.content.onto.OntologyException oe) {
                oe.printStackTrace();
            }
        }
        public boolean done()
        {
            
            
            if(!subastaActiva)
            {
               PSBehaviour = new ParticiparEnSubasta(agente);
                addBehaviour(PSBehaviour);
                //System.out.println(getLocalName() +" -> metodo done!!!!!!!!!!!!!!!");
               /* contadorCM = 0;
                Arrays.fill(finCallMarket, false);
                subastaActiva=true;*/

            }
            return !subastaActiva;
        }
    }
    
    class HiloOfertas extends java.lang.Thread
    {
        int accion;
        long tiempo;

        public HiloOfertas(int idProducto, long tiempoOf)
        {
            accion = idProducto;
            tiempo = tiempoOf;
            //if(tipoAgente==1)
            //System.out.println(getLocalName()+ " -> Constructor del hilo -  "+this.getId()+" - "+probabilidadTransarAccion[accion]);
            //this.start();
        }

        public HiloOfertas()
        {

            //System.out.println(getLocalName()+ " -> Constructor del hilo -  "+this.getId());
            //this.start();
        }

        @Override
        protected void finalize() throws Throwable
        {
              //do finalization here
              super.finalize(); //not necessary if extending Object.
              //System.out.println(getLocalName()+ " -> Destructor del hilo -  "+this.getId());
        }

        public void setParametros(int idProducto, long tiempoOf)
        {
            accion = idProducto;
            tiempo = tiempoOf;
        }

        @Override
        public void run()
        {
                if(subastaActiva) 
                {
                    
                    //Modificar cuando y cuantas veces manda oferta, dependiendo de las sesiones
                    long tiempoEspera = (long)aleatorio(tiempo, 0);
                   
                    try {
                        sleep(tiempoEspera);
                        
                       // System.out.println(getLocalName() + " - Genero oferta");
                        OfertaEnviada[accion] = true;
                        generarOferta(accion, this.getId());
                    //System.out.println(getLocalName() + " - Genero oferta");


                    } catch(InterruptedException e) {
                        //e.printStackTrace();
                        //System.out.println(getLocalName() + " - "+this.getId()+" El hilo ha sido interrumpido - "+accion);
                        return;
                    }
//System.out.println(getLocalName() + " - Genero oferta dfsfsdf");

                    
                 }
    
        }
    }

    /*class EnviarOfertaBehaviour extends WakerBehaviour {

            int indice = -1;
         public EnviarOfertaBehaviour(Agent a, long time, int indice )
        {
            super(a,time);
                    this.indice = indice;

        }

        @Override
        protected void onWake() {

         System.out.println( getLocalName()+"... 11111111111111 - "+indice );
         //EnviarOfertaBehaviour a = new EnviarOfertaBehaviour(myAgent,1000, indice);
         //addBehaviour(a);

      }

     }*/

    public float aleatorio(float limiteSuperior, float limiteInferior)
    {
        return (objeto.nextFloat())*(limiteSuperior-limiteInferior)+limiteInferior;
    }

    /*public long aleatorio(long limiteSuperior, long limiteInferior)
    {
        return (objeto.nextLong())*(limiteSuperior-limiteInferior)+limiteInferior;
    }*/
    
    public void generarOferta(int accion, long id)
    {
        String tipo = "";
        float precioGenerado;
        int cantidad;

        try {
            if (probabilidadTransarAccion[accion] > 0 && probabilidadTransarAccion[accion] <= 1)
                tipo = "Comprar";
            if (probabilidadTransarAccion[accion] >= -1 && probabilidadTransarAccion[accion] < 0)
                tipo = "Vender";
            //System.out.println(getLocalName()+" - Tipo - "+ tipo);

            ACLMessage mensajeSaliente = new ACLMessage(ACLMessage.PROPOSE);
            mensajeSaliente.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
            mensajeSaliente.setLanguage(codec.getName());
            mensajeSaliente.setOntology(ontologia.getName());
            //mensajeSaliente.addReceiver(agenteSubastador); //Aqui el subastador debe recibir el predicado registrar compra
            mensajeSaliente.addReceiver(AgenteAdministrador.subastadores[accion]);

                //while(true)
                {
                    if (tipo.equalsIgnoreCase("Comprar")) {
                

                    ConceptoProducto producto = new ConceptoProducto();
                    producto.setDescripcion("Accion de la Bolsa de valores de colombia");
                    producto.setNombre(AgenteAdministrador.nombreAcciones[accion]);
                    producto.setIDProducto(accion);

                    //LLenar el concepto oferta de compra
                    ConceptoOfertaCompra conceptoOfertaComp = new ConceptoOfertaCompra();
                    conceptoOfertaComp.setIDOfertaCompra(123);//ESTO HAY QUE MODIFICARLO

                    // El precio del producto escogido corresponde a: ultimo precio del historico +/- 5%(ultimo precio historico)
                    /*int n = AgenteSubastador.historicoSubastas.get(accion).size();
                    float ultimoPrecio = 0;

                    while (n-- > 0) {
                        ultimoPrecio = AgenteSubastador.historicoSubastas.get(accion).get(n).getPrecioPromedio();
                        if (ultimoPrecio != 0 && ultimoPrecio != -1)
                            break;
                    }

                    ultimoPrecio = AgenteSubastador.PrecioReferencia[accion];*/

                    if(OfertaEliminada[accion] == true)
                        precioGenerado = generarPrecioSobreCanal(accion, true);
                    else
                        precioGenerado = generarPrecio(accion);

                    cantidad = generarCantidad(accion); //cantidadesaOfertar[accion]; //Modificar la cantidad

                    //cantidad = 100;

                    //System.out.println(getLocalName()+ " --- Cantidad generada para compra fue de  ----- "+cantidad+" ---- Con precio ----"+precioGenerado);
                    //System.out.println(getLocalName()+ " --- "+(cantidad > 0 && saldo > (precioGenerado*cantidad + saldoOfertado[accion])));

                    if (cantidad > 0 && saldo > (precioGenerado*cantidad + saldoOfertado[accion])) {
                        // Verifica que el saldo alcance para comprar todas las acciones

                        conceptoOfertaComp.setPrecioCompra(precioGenerado);
                        conceptoOfertaComp.setIDComprador(idAgente);
                        conceptoOfertaComp.setFecha(new Date().toString());
                        conceptoOfertaComp.setCantidad(cantidad);
                        //conceptoOfertaComp.setCantidad(cantidad*AgenteSubastador.paquetesMinimos[accion]);

                        
                        //System.out.println(getLocalName()+" - "+id+" - Oferta Compra - Precio: "+precioGenerado+" - Cantidad: "+cantidad+" - "+accion);////////////////////

                        saldoOfertado[accion] += precioGenerado*cantidad;
                        //LLenar el predicado de registrar compra
                        PredicadoRegistrarCompra registrarCompra = new PredicadoRegistrarCompra();
                        registrarCompra.setOfertaCompra(conceptoOfertaComp);
                        registrarCompra.setProducto(producto);

                        getContentManager().fillContent(mensajeSaliente, registrarCompra);
                        send(mensajeSaliente);

                        /*MessageTemplate mt = MessageTemplate.and(
                                MessageTemplate.MatchLanguage(codec.getName()),
                                MessageTemplate.MatchOntology(ontologia.getName()));

                        ACLMessage mensajeRespuesta = blockingReceive(mt);
                        ContentElement mensajeEntrante = getContentManager().extractContent(mensajeRespuesta);
                        if(mensajeRespuesta != null)
                        {
                            if(mensajeRespuesta.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE))
                            {
                                if(mensajeRespuesta.getPerformative()==ACLMessage.CONFIRM)
                                {
                                    if (mensajeEntrante instanceof PredicadoConfirmarOferta) {
                                    //System.out.println(getLocalName() +" -> Recibida confirmacion de oferta");
                                    break;
                                    }

                                }
                                else if(mensajeRespuesta.getPerformative()==ACLMessage.CANCEL)
                                {
                                    if (mensajeEntrante instanceof PredicadoCancelarCompra) {
                                    String concepto = ((PredicadoCancelarCompra)mensajeEntrante).getConcepto();
                                        System.out.println(getLocalName()+" - "+id+" - Me Cancelaron una Compra Tarde");//System.out.println(getLocalName() +" -> Recibida confirmacion de oferta");
                                    }
                                    else 
                                    {
                                        System.out.println(getLocalName()+" - "+id+" - No Predicado Cancelar Compra");
                                        dumpMessage(mensajeRespuesta,getLocalName());
                                    }


                                }
                                else
                                    {
                                        System.out.println(getLocalName()+" - "+id+" - No Performative Cancelar");
                                        dumpMessage(mensajeRespuesta,getLocalName());
                                    }

                            }
                            else
                            {
                                System.out.println(getLocalName()+" - "+id+" - No Protocolo FIPA_PROPOSE");
                                dumpMessage(mensajeRespuesta,getLocalName());
                            }

                        }
                        else
                        {
                           System.out.println(getLocalName()+" - Mensaje respuesta fue nulo - Compra");
                           System.out.println(getLocalName()+" - Mensaje respuesta fue nulo - Compra");
                           System.out.println(getLocalName()+" - Mensaje respuesta fue nulo - Compra");
                           System.out.println(getLocalName()+" - Mensaje respuesta fue nulo - Compra");
                        }*/

                    }
                    else System.out.println(getLocalName()+" - No me dio para comprar - idA - "+accion+"- P="+precioGenerado+" - C="+cantidad+" - Mi Saldo - "+saldo+" - "+AgenteAdministrador.contadorNCompra());
                 
            } else if (tipo.equalsIgnoreCase("Vender")) {

                    ConceptoProducto producto = new ConceptoProducto();
                    producto.setDescripcion("Accion de la Bolsa de valores de colombia");
                    producto.setNombre(AgenteAdministrador.nombreAcciones[accion]);
                    producto.setIDProducto(accion);

                    //LLenar el concepto oferta de venta
                    ConceptoOfertaVenta conceptoOfertaVent = new ConceptoOfertaVenta();
                    conceptoOfertaVent.setIDOfertaVenta(123);//ESTO HAY QUE MODIFICARLO

                    // El precio del producto escogido corresponde a: ultimo precio del historico +/- 5%(ultimo precio historico)
                    /*int n = AgenteSubastador.historicoSubastas.get(accion).size();
                    float ultimoPrecio = 0;

                    while (n-- > 0) {
                        ultimoPrecio = AgenteSubastador.historicoSubastas.get(accion).get(n).getPrecioPromedio();
                        if (ultimoPrecio != 0 && ultimoPrecio != -1)
                            break;
                    }

                    ultimoPrecio = AgenteSubastador.PrecioReferencia[accion];*/

                    if(OfertaEliminada[accion] == true)
                        precioGenerado = generarPrecioSobreCanal(accion, false);
                    else
                        precioGenerado = generarPrecio(accion);

                    cantidad = generarCantidad(accion);//cantidadesaOfertar[accion];

                    //cantidad = 100;

                    //System.out.println(getLocalName()+ " --- Cantidad generada para venta fue de  ----- "+cantidad+" ---- Con precio ----"+precioGenerado);
                    //System.out.println(getLocalName()+ " --- "+((cantidadOfertada[accion] + cantidad/**AgenteSubastador.paquetesMinimos[accion]*/) <  cantidadXAccion[accion] && cantidad > 0 ));

                    if((cantidadOfertada[accion] + cantidad/**AgenteSubastador.paquetesMinimos[accion]*/) <  cantidadXAccion[accion] && cantidad > 0 ) {
                        conceptoOfertaVent.setPrecioVenta(precioGenerado);
                        conceptoOfertaVent.setIDVendedor(idAgente);
                        conceptoOfertaVent.setFecha(new Date().toString());
                        conceptoOfertaVent.setCantidad(cantidad);

                        //conceptoOfertaComp.setCantidad(cantidad*AgenteSubastador.paquetesMinimos[accion]);

                        cantidadOfertada[accion] += conceptoOfertaVent.getCantidad();

                        
                        //System.out.println(getLocalName()+" - "+id+" - Oferta Venta - Precio: "+precioGenerado+" - Cantidad: "+cantidad+" - "+accion);////////////////////

                        //LLenar el predicado de registrar compra
                        PredicadoRegistrarVenta registrarVenta = new PredicadoRegistrarVenta();
                        registrarVenta.setOfertaVenta(conceptoOfertaVent);
                        registrarVenta.setProducto(producto);

                        getContentManager().fillContent(mensajeSaliente, registrarVenta);
                        send(mensajeSaliente);

                        /*MessageTemplate mt = MessageTemplate.and(
                                MessageTemplate.MatchLanguage(codec.getName()),
                                MessageTemplate.MatchOntology(ontologia.getName()));

                        ACLMessage mensajeRespuesta = blockingReceive(mt);
                        ContentElement mensajeEntrante = getContentManager().extractContent(mensajeRespuesta);
                        if(mensajeRespuesta != null)
                        {

                            if(mensajeRespuesta.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE))
                            {
                                if(mensajeRespuesta.getPerformative()==ACLMessage.CONFIRM)
                                {
                                    if (mensajeEntrante instanceof PredicadoConfirmarOferta) {
                                    //System.out.println(getLocalName() +" -> Recibida confirmacion de oferta");
                                    break;
                                    }

                                }
                                else if(mensajeRespuesta.getPerformative()==ACLMessage.CANCEL)
                                {
                                    if (mensajeEntrante instanceof PredicadoCancelarVenta) {
                                    String concepto = ((PredicadoCancelarVenta)mensajeEntrante).getConcepto();
                                        System.out.println(getLocalName()+" - "+id+" - Me Cancelaron una Venta Tarde");//System.out.println(getLocalName() +" -> Recibida confirmacion de oferta");
                                    }
                                    else
                                    {
                                        System.out.println(getLocalName()+" - "+id+" - No Predicado Cancelar Compra");
                                        dumpMessage(mensajeRespuesta,getLocalName());
                                    }


                                }
                                else
                                    {
                                        System.out.println(getLocalName()+" - "+id+" - No Performative Cancelar");
                                        dumpMessage(mensajeRespuesta,getLocalName());
                                    }

                            }
                            else
                            {
                                System.out.println(getLocalName()+" - "+id+" - No Protocolo FIPA_PROPOSE");
                                dumpMessage(mensajeRespuesta,getLocalName());
                            }

                        }
                        else
                         {
                           System.out.println(getLocalName()+" - Mensaje respuesta fue nulo - Venta");
                           System.out.println(getLocalName()+" - Mensaje respuesta fue nulo - Venta");
                           System.out.println(getLocalName()+" - Mensaje respuesta fue nulo - Venta");
                           System.out.println(getLocalName()+" - Mensaje respuesta fue nulo - Venta");
                        }*/


                    }
                   else System.out.println(getLocalName()+" - No me dio para vender - idA - "+accion+" - P="+precioGenerado+" - C="+cantidad+" - Mi cantidad - "+cantidadXAccion[accion]+" - "+AgenteAdministrador.contadorNVenta());
                }
                
            }
        } catch (jade.content.lang.Codec.CodecException ce) {
            ce.printStackTrace();
        } catch (jade.content.onto.OntologyException oe) {
            oe.printStackTrace();
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public float generarPrecioSobreCanal(int Accion, boolean compra)
    {
        float precioGenerado;
        
        if(compra)
            precioGenerado = aleatorio(AgenteAdministrador.PrecioPorEncima[Accion],AgenteAdministrador.PrecioReferencia[Accion]);
        else
            precioGenerado = aleatorio(AgenteAdministrador.PrecioReferencia[Accion],AgenteAdministrador.PrecioPorDebajo[Accion]);

        return precioGenerado;
    }

    public float generarPrecio(int Accion)
    {
        float precioGenerado = aleatorio((1+AgenteAdministrador.alphaPrecioOfertas)*AgenteAdministrador.PrecioPorEncima[Accion],(1-AgenteAdministrador.alphaPrecioOfertas)*AgenteAdministrador.PrecioPorDebajo[Accion]);

        /*while(precioGenerado > AgenteSubastador.PrecioPorEncima[idAccion] || precioGenerado < AgenteSubastador.PrecioPorDebajo[idAccion])
            precioGenerado = aleatorio((float)1.10*precioATransar[idAccion], (float)0.9*precioATransar[idAccion]);*/
        if(precioGenerado > AgenteAdministrador.PrecioPorEncima[Accion])
        {
            precioGenerado = AgenteAdministrador.PrecioPorEncima[Accion];
           // System.out.println(getLocalName()+" Oferta por Encima");
        }

        if(precioGenerado < AgenteAdministrador.PrecioPorDebajo[Accion])
        {
            precioGenerado = AgenteAdministrador.PrecioPorDebajo[Accion];
            //System.out.println(getLocalName()+" Oferta por Debajo");
        }

        return precioGenerado;
    }

    public int generarCantidad(int Accion)
    {
       // int volumen = (int)aleatorio(2*AgenteAdministrador.cantidadesATransar[Accion],AgenteAdministrador.cantidadesATransar[Accion]/2);
         int volumen = (int)aleatorio(4*AgenteAdministrador.cantidadesATransar[Accion],AgenteAdministrador.cantidadesATransar[Accion]/4);
        return volumen;
    }

    public void registrarServidioAgente()
    {
        // Registrar el servicio
        try {
            // Se crea una lista de servicios de agente
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());

            // Se crea una descripcion de servicio
            ServiceDescription sd = new ServiceDescription();

            sd.setName("Bursatil"); // Si es vendedor el servicio es vender acciones

            // Se define el tipo de servicio
            sd.setType("Agente del mercado accionario que desea participar en la negociación");

            // Se define la ontologa del servicio
            sd.addOntologies("DoblePunta");

            // Se define la ontologa del agente
            dfd.addOntologies("DoblePunta");

            // Se especifica el lenguaje que deben "hablar" los agentes que acceden al servicio
            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);

            // Se especifica el lenguaje "habla" el agente
            dfd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);

            // Se agrega el servicio a la lista de servicios
            dfd.addServices(sd);

            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        } catch (java.lang.NumberFormatException num) {
            num.printStackTrace();
        }

        getContentManager().registerLanguage(codec);    // Se registra el lenguaje
        getContentManager().registerOntology(ontologia); // Se registra la ontologia
        contenedorBursatil = getContainerController();
    }

    protected void takeDown()
       {
          try {
              DFService.deregister(this);
          //System.out.println(this.getLocalName()+" -> He muerto y ya no estoy registrado");
          }
          catch (Exception e) {}
       }

    	public void dumpMessage( ACLMessage msg , String name)
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

    abstract void seleccionarAccionesAComprar();
    abstract void seleccionarAccionesAVender();
    abstract void registroTecnico();
    abstract void parametrosIniciarSubasta();

}