/*
 * AgenteMercado.java
 */

package agentes;

import basedatos.*;
import ontologias.*;

import java.sql.*;
import java.util.*;
import java.util.Date;
import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.*;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;


public class AgenteMercado extends Agent
{
    private Codec codec = new SLCodec(); //Se crea un lenguaje para la ontologia
    private Ontology ontologia = DoblePuntaOntology.getInstance();//se crea la ontologia
    private AID agenteSubastador;
    private String nombre;
    private int[] numOfertasCompra;
    private int[] numOfertasVenta;
    private int[] paqueteMinimo;
    private String[] nombreAcciones;
    private float[] promedioVolumenCompra;
    private float[] promedioVolumenVenta;
    private double[] rentabilidadPromedioHist;
    private double[] volumenPromedioHist;
    private double[] desviacionEstVolumen;
    private double[] desvEstandarRentabilidadHist;
    private int idAccion= 0;
    private int idAgente= 0;
    private int numAcciones = 0;
    private int cantidadOfertas = 0;
    private int promedioOfertas = 2;
    private int cantidadOfertasCompra = 0;
    private int cantidadOfertasVenta = 0;
    private int cantidadOfertasCompraParcial = 0;
    private int cantidadOfertasVentaParcial = 0;
    private int accionCompra = 0;
    private int accionVenta = 0;
    private int limiteSuperiorOfertas;
    private float saldo = 0;
    private boolean ofertarCompra = true;
    private boolean subastaActiva = true;

    public static float[] precioATransar;
    public static int[] volumenATransar;

    Random objeto = new java.util.Random();
    jade.wrapper.AgentContainer contenedorBursatil;
    ConexionBD BD;
    ConexionBD BD2;
    ConexionBD BDProducto;
    ResultSet r = null;
    ResultSet r2 = null;
    ResultSet productos = null;
    
//********************************************************************************************************************************************************
//FUNCION DE INICIALIZACION DEL AGENTE
//********************************************************************************************************************************************************    
    protected void setup()
    {
        System.out.println(this.getLocalName()+" -------OOOOOOOOOOOOOOOOOOOO------");
        Object[] args = getArguments();

        if (args != null && args.length > 0) {
            numAcciones = Integer.parseInt(args[0].toString());
        }
        
        nombre = 	this.getLocalName();
        saldo = -1;
        numOfertasCompra = new int[numAcciones];
        numOfertasVenta = new int[numAcciones];
        paqueteMinimo = new int[numAcciones];
        promedioVolumenVenta = new float[numAcciones];
        promedioVolumenCompra  = new float[numAcciones];

        nombreAcciones = new String[numAcciones];

        int numeroAcciones = 0;
        try {
            BDProducto = new ConexionBD("mysql");
            productos = BDProducto.consulta("SELECT count(IDProducto) FROM producto");
            if (productos.next())
                numeroAcciones = productos.getInt(1);
            BDProducto.cerrarConexion();

            BD = new ConexionBD("mysql");
            for (int i=0; i<numeroAcciones; i++) {
                r = BD.consulta("SELECT paqueteMinimo, nombre  FROM producto WHERE IDProducto="+i);
                if (r.next())
                    paqueteMinimo[i] = r.getInt(1);
                    nombreAcciones[i] = r.getString(2);
            }
            BD.cerrarConexion();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Registrar el servicio
        try {
            // Se crea una lista de servicios de agente
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());

            // Se crea una descripcion de servicio
            ServiceDescription sd = new ServiceDescription();
            
            sd.setName("Mercado"); //

            // Se define el tipo de servicio
            sd.setType("Agente del mercado que genera un balance dentro de este");
            
            // Se define la ontologa del servicio
            sd.addOntologies("DoblePunta");
            
            // Se define la ontologa del agente
            dfd.addOntologies("DoblePunta");

            // Se especifica el lenguaje que deben "hablar" los agentes que acceden al servicio
            sd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
            
            // Se especifica el lenguaje "habla" el agente
            dfd.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
            

            // Se agreran las propiedades que hagan falta
            //sd.addProperties(new Property(""));

            // Se agrega el servicio a la lista de servicios
            dfd.addServices(sd);

            DFService.register(this, dfd);

            // Se construye la descripcion usada como plantilla para la busqueda
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription templateSD = new ServiceDescription();

            templateSD.setName("RealizarCalce");
            template.addServices(templateSD);

            // Solo se desea recibir un resultado a lo sumo
            SearchConstraints SC = new SearchConstraints();
            SC.setMaxResults(new Long(1));

            DFAgentDescription[] resultados = DFService.search(this, template, SC);
            if (resultados.length == 1) {
                DFAgentDescription dfd2 = resultados[0];
                agenteSubastador = dfd2.getName();//Aqui debe ser el subastador
            }

        } catch (FIPAException fe) {
            fe.printStackTrace();
        } catch (java.lang.NumberFormatException num) {
            num.printStackTrace();
        }
        
        getContentManager().registerLanguage(codec);    // Se registra el lenguaje
        getContentManager().registerOntology(ontologia); // Se registra la ontologia
        contenedorBursatil = getContainerController();

        ParticiparEnSubasta PSBehaviour = new ParticiparEnSubasta(this);
        addBehaviour(PSBehaviour);
  }

    class ParticiparEnSubasta extends SimpleBehaviour 
    {
        Agent agente;
        ParticiparEnSubasta PSBehaviour;
        
        public ParticiparEnSubasta(Agent a)
        {
            super(a);
            agente = a;
        }

        public void action() 
        {
            try {
                MessageTemplate mt = MessageTemplate.and(
                    MessageTemplate.MatchLanguage(codec.getName()),
                    MessageTemplate.MatchOntology(ontologia.getName()));
                ACLMessage  mensajeGeneral = blockingReceive(mt);

                ContentElement mensajeEntrante = getContentManager().extractContent(mensajeGeneral);

                if (mensajeGeneral != null &&
                    mensajeGeneral.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE)) {

                    if( mensajeGeneral.getPerformative() == ACLMessage.INFORM )
                    {
                        if (mensajeEntrante instanceof PredicadoInicioSubasta) {
                            if (AgenteSubastador.simulacionActual == 1) {
                                generarParametrosPrecioCantidad();
                            }
                            precioATransar = new float[numAcciones];
                            volumenATransar = new int[numAcciones];

                            for (int i=0; i<numAcciones; i++) {
                                int n = AgenteSubastador.historicoSubastas.get(i).size();
                                float ultimoPrecio = 0;

                                // Se busca el último precio
                                while (n-- > 0) {
                                    ultimoPrecio = AgenteSubastador.historicoSubastas.get(i).get(n).getPrecioCierre();
                                    if (ultimoPrecio != 0 && ultimoPrecio != -1)
                                        break;
                                }

                                ultimoPrecio = AgenteSubastador.PrecioReferencia[i];

                                double normal1 = 0;
                                // Un numero aleatorio con Distribucion normal es: N(0,1)=SUM1,12(ai)/6
                                for (int j=1; j<=12; j++) {
                                    normal1 += aleatorio(1, 0);
                                }
                                normal1 -= 6;
                                // Pt=Pt-1 + Pt-1*RProm + Pt-1*DesvEstandar*N(0,1)
                                precioATransar[i] = (float)(ultimoPrecio + ultimoPrecio*rentabilidadPromedioHist[i] +
                                        ultimoPrecio*desvEstandarRentabilidadHist[i]*normal1);
                                double normal2 = 0;
                                // Un numero aleatorio con Distribucion normal es: N(0,1)=SUM1,12(ai)/6
                                for (int j=1; j<=12; j++) {
                                    normal2 += aleatorio(1, 0);
                                }
                                normal2 -= 6;

                                volumenATransar[i] = (int)(volumenPromedioHist[i] + normal2*desviacionEstVolumen[i]);
                                volumenATransar[i] = Math.max(volumenATransar[i], 0);
                            }

                            limiteSuperiorOfertas = AgenteSubastador.numAgentes*2;
                            for (int i=0; i < numAcciones; i++) {
                                numOfertasCompra[i] = (int)aleatorio(limiteSuperiorOfertas,limiteSuperiorOfertas/4)+1;
                                numOfertasVenta[i] = (int)aleatorio(limiteSuperiorOfertas,limiteSuperiorOfertas/4)+1;
                            }
                            subastaActiva = true;

                            cantidadOfertasCompraParcial = 0;
                            cantidadOfertasVentaParcial = 0;
                            cantidadOfertasCompra  = 0;
                            cantidadOfertasVenta  = 0;
                            accionCompra = 0;
                            accionVenta = 0;

                            for (int i=0; i< numAcciones; i++) {
                                cantidadOfertasCompra += numOfertasCompra[i];
                                cantidadOfertasVenta += numOfertasVenta[i];
                            }

                            cantidadOfertas = cantidadOfertasVenta + cantidadOfertasCompra;
                        }

                        else if (mensajeEntrante instanceof PredicadoNotificarVenta) {
                            float precioTransaccion = ((PredicadoNotificarVenta)mensajeEntrante).getNotificacionVenta().getPrecio();
                            int cantidad = ((PredicadoNotificarVenta)mensajeEntrante).getNotificacionVenta().getCantidad();
                            saldo += precioTransaccion*cantidad;
                            //System.out.println(getLocalName()+"----------- Realice una Venta-------------");
                        }

                        else if (mensajeEntrante instanceof PredicadoNotificarCompra) {
                            float precioTransaccion = ((PredicadoNotificarCompra)mensajeEntrante).getNotificacionCompra().getPrecio();
                            int cantidad = ((PredicadoNotificarCompra)mensajeEntrante).getNotificacionCompra().getCantidad();
                            saldo -= precioTransaccion*cantidad;

                            //System.out.println(getLocalName()+"--------- Realice una Compra-----------------");
                        }

                        else if (mensajeEntrante instanceof PredicadoFinSubasta) {
                            subastaActiva = false;
                            //System.out.println(getLocalName() +" -> Fin de subasta");
                        }

                        else if (mensajeEntrante instanceof PredicadoEnEspera) {
                              //System.out.println(getLocalName() +" -> La oferta que realice no calzo");
                        }

                        else if (mensajeEntrante instanceof PredicadoInicioCallMarket)
                        {
                            String nombre = ((PredicadoInicioCallMarket)mensajeEntrante).getProducto().getNombre();
                            System.out.println(getLocalName()+" --- Se ha iniciado Call Market --- "+nombre+" ----");
                        }

                        else if (mensajeEntrante instanceof PredicadoFinCallMarket)
                        {
                            String nombre = ((PredicadoFinCallMarket)mensajeEntrante).getProducto().getNombre();
                            System.out.println(getLocalName()+" --- Se ha Finalizado Call Market --- "+nombre+" ----");
                        }

                        if (subastaActiva) {
                           // new HiloOfertas();
                        }
                    }
                    else
                    {
                        if(mensajeGeneral.getPerformative() == ACLMessage.CANCEL)
                        {
                            if (mensajeEntrante instanceof PredicadoCancelarVenta)
                            {
                                String concepto = ((PredicadoCancelarVenta)mensajeEntrante).getConcepto();
                                //System.out.println(getLocalName()+" - Me Cancelaron una Venta");
                                if(concepto.equals("caduco"))
                                {
                                    //System.out.println("caduco-----");
                                }
                                if(concepto.equals("canal"))
                                {
                                    //System.out.println("canal-----");
                                }

                            }
                            if (mensajeEntrante instanceof PredicadoCancelarCompra)
                            {
                                String concepto = ((PredicadoCancelarCompra)mensajeEntrante).getConcepto();
                                //System.out.print(getLocalName()+" - Me Cancelaron una Compra - ");
                                if(concepto.equals("caduco"))
                                {
                                    //System.out.println("caduco-----");
                                }
                                if(concepto.equals("canal"))
                                {
                                    //System.out.println("canal-----");
                                }
                            }
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
            PSBehaviour = new ParticiparEnSubasta(agente);
            addBehaviour(PSBehaviour);

            return !subastaActiva;
        }
    }

    
    class HiloOfertas extends java.lang.Thread
    {
        public HiloOfertas()
        {
            this.start();
        }
        
        public void run()
        {
            long tiempoEspera = AgenteSubastador.tiempoSimulacion;
            if (cantidadOfertas != 0)
                tiempoEspera = (long)aleatorio(AgenteSubastador.tiempoSimulacion/cantidadOfertas, 0);
           
            if (subastaActiva) {
                float precioGenerado;
                int cantidad = -1;

                try {
                    

        
                   // System.out.println(getLocalName()+" --- " +AgenteSubastador.PrecioPorDebajo[idAccion]+" ----- "+AgenteSubastador.PrecioPorEncima[idAccion] +" ---- "+idAccion);

                    if (ofertarCompra) {
                        ofertarCompra = false;
                        /////////GENERA LAS OFERTAS DE COMPRA/////////////////
                        if (cantidadOfertasCompraParcial <= cantidadOfertasCompra) {///MIRA LA CANTIDAD DE OFERTAS DE COMPRA QUE DEBERIA ENVIAR///
                            
                            if(numOfertasCompra[accionCompra] >= 0) {//MIRA LA CANTIDAD DE OFERTAS DE COMPRA DE ACCIONCOMPRA
                                sleep(tiempoEspera);
                                ACLMessage mensajeSaliente = new ACLMessage(ACLMessage.PROPOSE);
                                mensajeSaliente.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                                mensajeSaliente.setLanguage(codec.getName());
                                mensajeSaliente.setOntology(ontologia.getName());
                                mensajeSaliente.addReceiver(agenteSubastador); //Aqui el subastador debe recibir el predicado registrar compra

                                idAccion = accionCompra;

                                ConceptoProducto producto = new ConceptoProducto();
                                producto.setDescripcion("Accion de la Bolsa de valores de colombia");
                                producto.setNombre(nombreAcciones[idAccion]);
                                producto.setIDProducto(idAccion);


                                //LLenar el concepto oferta de compra
                                ConceptoOfertaCompra conceptoOfertaComp = new ConceptoOfertaCompra();
                                conceptoOfertaComp.setIDOfertaCompra(123);//ESTO HAY QUE MODIFICARLO

                                // El precio del producto escogido corresponde a: ultimo precio del historico +/- 5%(ultimo precio
                                cantidad = ((int)aleatorio(10,1))*paqueteMinimo[idAccion];

                                //GENERA EL PRECIO ALEATORIO COMO EL PROMEDIO ENTRE EL ULTIMO PRECIO COnumOfertasCompraIDO Y EL GENERADO
                                int n = AgenteSubastador.historicoSubastas.get(idAccion).size();
                                float ultimoPrecio = 0;
                                while (n-- > 0) {
                                    ultimoPrecio = AgenteSubastador.historicoSubastas.get(idAccion).get(n).getPrecioCierre();
                                    if (ultimoPrecio != 0 && ultimoPrecio != -1)
                                        break;
                                }
                               // precioGenerado = aleatorio((float)1.10*precioATransar[idAccion], (float)0.9*precioATransar[idAccion]);


                                precioGenerado = generarPrecio(idAccion);

                                conceptoOfertaComp.setPrecioCompra(precioGenerado);
                                conceptoOfertaComp.setIDComprador(idAgente);
                                conceptoOfertaComp.setFecha(new Date().toString());
                                conceptoOfertaComp.setCantidad(cantidad);

                                

                                //LLenar el predicado de registrar compra
                                PredicadoRegistrarCompra registrarCompra = new PredicadoRegistrarCompra();
                                registrarCompra.setOfertaCompra(conceptoOfertaComp);
                                registrarCompra.setProducto(producto);

                                getContentManager().fillContent(mensajeSaliente, registrarCompra);
                                send(mensajeSaliente);

                                //System.out.println(getLocalName()+" - Oferta Compra");///////////////////////////////////

                                MessageTemplate mt = MessageTemplate.and(
                                        MessageTemplate.MatchLanguage(codec.getName()),
                                        MessageTemplate.MatchOntology(ontologia.getName()));

                                ACLMessage mensajeRespuesta = blockingReceive(mt);
                                ContentElement mensajeEntrante = getContentManager().extractContent(mensajeRespuesta);

                                if (mensajeRespuesta != null && mensajeRespuesta.getPerformative()==ACLMessage.CONFIRM &&
                                    mensajeRespuesta.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE)) {
                                    if (mensajeEntrante instanceof PredicadoConfirmarOferta) {
                                        cantidadOfertasCompraParcial++;
                                        numOfertasCompra[accionCompra]--;
                                        accionCompra++;
                                        if (accionCompra == numAcciones)
                                            accionCompra = 0;

                                    }
                                }
                            } else {
                                while (numOfertasCompra[accionCompra] == 0) {
                                    accionCompra++;
                                    if (accionCompra == numAcciones)
                                        accionCompra = 0;
                                }
                            }
                        }
                    } else {
                        ofertarCompra = true;
                        /////////GENERA LAS OFERTAS DE VENTAA/////////////////
                        if(cantidadOfertasVentaParcial <= cantidadOfertasVenta) {///MIRA LA CANTIDAD DE OFERTAS DE VENTA QUE DEBERIA ENVIAR///
                            if(numOfertasVenta[accionVenta] >= 0) {//MIRA LA CANTIDAD DE OFERTAS DE VENTA DE ACCIONCOMPRA
                                sleep(tiempoEspera);

                                ACLMessage mensajeSaliente = new ACLMessage(ACLMessage.PROPOSE);
                                mensajeSaliente.setProtocol(FIPANames.InteractionProtocol.FIPA_PROPOSE);
                                mensajeSaliente.setLanguage(codec.getName());
                                mensajeSaliente.setOntology(ontologia.getName());
                                mensajeSaliente.addReceiver(agenteSubastador); //Aqui el subastador debe recibir el predicado registrar compra

                                idAccion = accionVenta;

                                ConceptoProducto producto = new ConceptoProducto();
                                producto.setDescripcion("Accion de la Bolsa de valores de colombia");
                                producto.setNombre(nombreAcciones[idAccion]);
                                producto.setIDProducto(idAccion);

                                //LLenar el concepto oferta de venta
                                ConceptoOfertaVenta conceptoOfertaVent = new ConceptoOfertaVenta();
                                conceptoOfertaVent.setIDOfertaVenta(123);//ESTO HAY QUE MODIFICARLO

                                // El precio del producto escogido corresponde a: ultimo precio del historico +/- 5%(ultimo precio historico)
                                cantidad = ((int)aleatorio(10,1))*paqueteMinimo[idAccion];

                                //GENERA EL PRECIO ALEATORIO COMO EL PROMEDIO ENTRE EL ULTIMO PRECIO COnumOfertasCompraIDO Y EL GENERADO
                                int n = AgenteSubastador.historicoSubastas.get(idAccion).size();
                                float ultimoPrecio = 0;
                                while (n-- > 0) {
                                    ultimoPrecio = AgenteSubastador.historicoSubastas.get(idAccion).get(n).getPrecioCierre();
                                    if (ultimoPrecio != 0 && ultimoPrecio != -1)
                                        break;
                                }

                                //precioGenerado = aleatorio((float)1.10*precioATransar[idAccion], (float)0.9*precioATransar[idAccion]);

                                precioGenerado = generarPrecio(idAccion);
                                
                                conceptoOfertaVent.setPrecioVenta(precioGenerado);
                                conceptoOfertaVent.setIDVendedor(idAgente);
                                conceptoOfertaVent.setFecha(new Date().toString());
                                conceptoOfertaVent.setCantidad(cantidad);


                                //System.out.println(getLocalName()+" - Oferta Venta");//////////////////////

                                //LLenar el predicado de registrar compra
                                PredicadoRegistrarVenta registrarVenta = new PredicadoRegistrarVenta();
                                registrarVenta.setOfertaVenta(conceptoOfertaVent);
                                registrarVenta.setProducto(producto);

                                getContentManager().fillContent(mensajeSaliente, registrarVenta);
                                send(mensajeSaliente);

                                MessageTemplate mt = MessageTemplate.and(
                                        MessageTemplate.MatchLanguage(codec.getName()),
                                        MessageTemplate.MatchOntology(ontologia.getName()));

                                ACLMessage mensajeRespuesta = blockingReceive(mt);
                                ContentElement mensajeEntrante = getContentManager().extractContent(mensajeRespuesta);

                                if (mensajeRespuesta != null && mensajeRespuesta.getPerformative()==ACLMessage.CONFIRM &&
                                    mensajeRespuesta.getProtocol().equals(FIPANames.InteractionProtocol.FIPA_PROPOSE)) {
                                    if (mensajeEntrante instanceof PredicadoConfirmarOferta) {
                                        cantidadOfertasVentaParcial++;
                                        numOfertasVenta[accionVenta]--;
                                        accionVenta++;
                                        if (accionVenta == numAcciones)
                                            accionVenta = 0;
                                    }
                                }
                            } else {
                                while (numOfertasVenta[accionVenta] == 0) {
                                    accionVenta++;
                                    if (accionVenta == numAcciones)
                                        accionVenta = 0;
                                }
                            }
                        }
                    }

                } catch (jade.content.lang.Codec.CodecException ce) {
                    ce.printStackTrace();
                } catch (jade.content.onto.OntologyException oe) {
                    oe.printStackTrace();
                } catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }
    
    private float aleatorio(float limiteSuperior, float limiteInferior)
    {
        return (objeto.nextFloat())*(limiteSuperior-limiteInferior)+limiteInferior;
    }

    /*
     * Funcion que calcula la rentabilidad promedio, la desviacion estandar
     * de los precios promedio del historico. Además calcula los parametros
     * a y b para la regresion lineal de los volumenes del historico
     */


    private float generarPrecio(int Accion)
    {
        float precioGenerado = aleatorio((float)(1+AgenteSubastador.alphaPrecioOfertas)*precioATransar[Accion], (float)(1-AgenteSubastador.alphaPrecioOfertas)*precioATransar[Accion]);

        /*while(precioGenerado > AgenteSubastador.PrecioPorEncima[idAccion] || precioGenerado < AgenteSubastador.PrecioPorDebajo[idAccion])
            precioGenerado = aleatorio((float)1.10*precioATransar[idAccion], (float)0.9*precioATransar[idAccion]);*/
        if(precioGenerado > AgenteSubastador.PrecioPorEncima[Accion])
        {
            precioGenerado = AgenteSubastador.PrecioPorEncima[Accion];
           // System.out.println(getLocalName()+" Oferta por Encima");
        }

        if(precioGenerado < AgenteSubastador.PrecioPorDebajo[Accion])
        {
            precioGenerado = AgenteSubastador.PrecioPorDebajo[Accion];
            //System.out.println(getLocalName()+" Oferta por Debajo");
        }

        return precioGenerado;
    }
    private void generarParametrosPrecioCantidad()
    {
        int m, j, n;
        double[] preciosHistorico; // vector que guarda los precios del historico para cada accion
        double[] rentabilidades; // con los precios se calculan las rentabilidades
        double[] volumenes; // vector que guarda los volumenes del historico para cada accion

        rentabilidadPromedioHist = new double[numAcciones];
        volumenPromedioHist = new double[numAcciones];
        desviacionEstVolumen = new double[numAcciones];
        desvEstandarRentabilidadHist = new double[numAcciones];
        
        for (int i=0; i<numAcciones; i++) {
            // PARAMETROS PARA EL PRECIO: rentPromedio, DesvEstandar
            n = AgenteSubastador.historicoSubastas.get(i).size();
            preciosHistorico = new double[n];

            j = 0;
            for (int k=0; k<n; k++) {
                float p = AgenteSubastador.historicoSubastas.get(i).get(k).getPrecioCierre();
                if (p > 0) {
                    preciosHistorico[j++] = p; // j es el numero de precios del historico mayores que cero
                }
            }

            rentabilidades = new double[j-1]; // el tamano de las rentabilidades es uno menos que los precios
            double sum = 0;
            for (int k=0; k<j-1; k++) {
                rentabilidades[k] = Math.log(preciosHistorico[k+1]/preciosHistorico[k]);
                sum += rentabilidades[k];
            }
            rentabilidadPromedioHist[i] = sum/(j-1); // Rentabilidad promedio = SUM(r[]/(j-1))

            // DesvEstandar=SQRT(SUM(r[k]-rProm)**2/j-2)
            desvEstandarRentabilidadHist[i] = desviacionEstandar(rentabilidades);

            volumenes = new double[n];
            m = 0;
            sum = 0;
            for (int k=0; k<n; k++) {
                int v = AgenteSubastador.historicoSubastas.get(i).get(k).getCantidad();
                if (v > 0) {
                    volumenes[m++] = v; // m es el tamano del vector de volumenes mayores que cero
                }
            }
            volumenPromedioHist[i] = promedio(volumenes);

            desviacionEstVolumen[i] = desviacionEstandar(volumenes);
        }
    }

    private double desviacionEstandar (double[] x)
    {
        int n = x.length;
        double sumatoria = 0;
        double xPromedio;
        double desviacionEstandar;

        for (int i=0; i<n; i++) {
            sumatoria += x[i];
        }
        xPromedio = sumatoria/n;

        sumatoria = 0;
        for (int i=0; i<n; i++) {
            sumatoria += Math.pow(x[i] - xPromedio, 2);
        }
        desviacionEstandar = Math.sqrt(Math.abs(sumatoria)/(n-1));

        return desviacionEstandar;
    }

    private double promedio(double[] x)
    {
        int n = x.length;
        double sumatoria = 0;
        double xPromedio;

        for (int i=0; i<n; i++) {
            sumatoria += x[i];
        }
        xPromedio = sumatoria/n;

        return xPromedio;
    }

    protected void takeDown()
       {
          try { DFService.deregister(this);
          System.out.println(this.getLocalName()+" -> He muerto y ya no estoy registrado");}
          catch (Exception e) {}
       }

}