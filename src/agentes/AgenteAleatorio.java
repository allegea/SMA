/*
 * AgenteAleatorio.java
 */

package agentes;


public class AgenteAleatorio extends Generico
{

    boolean[] CorreccionProbabilidades;
    public double[] probabilidadComprar;
    public double[] probabilidadVender;
//********************************************************************************************************************************************************
//FUNCION DE INICIALIZACION DEL AGENTE
//********************************************************************************************************************************************************    
    @Override
    protected void setup()
    {		
        /*try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
            
        }*/
        
        
        objeto = new java.util.Random() ;
        
        Object[] args = getArguments();
        numAcciones = args.length-3;
        cantidadXAccion = new int[numAcciones];
        probabilidadTransarAccion = new float[numAcciones];
        precioPromedioAccion = new float[numAcciones];
        probabilidadComprar = new double[numAcciones];
        probabilidadVender = new double[numAcciones];
        tiempoEsperadoPorAccion = new int[numAcciones];
        decisionCambiarprobabilidad  = new int[numAcciones];
        cantidadOfertada = new int[numAcciones];
        saldoOfertado  = new float[numAcciones];
        cantidadesaOfertar = new int[numAcciones];
        OfertaEnviada = new boolean[numAcciones];
        OfertaEliminada = new boolean[numAcciones];
        CorreccionProbabilidades = new boolean[numAcciones];
        /**********************************************************************/
        // CAMBIAR ESTO!!!!!!!!!!!!!!!!!!!!
        /**********************************************************************/
        //for (int i = 0; i < numAcciones; ++i)
          //  cantidadesaOfertar[i] = 2;
        //System.out.println(this.getName());
        float probabilidadCom = 0;
        float probabilidadVen = 0;

        for (int i=0; i<numAcciones; i++)
            tiempoEsperadoPorAccion[i] = 1;
        
        if (args != null && args.length > 0) {
            saldo = Float.parseFloat(args[0].toString()); //saldo del agente
            probabilidadCom = Float.parseFloat(args[1].toString());
            probabilidadVen = Float.parseFloat(args[2].toString());
            for (int i=3; i<args.length; i++)
                cantidadXAccion[i-3] = Integer.parseInt(args[i].toString()); //saldo del agente
        }

        //probabilidadCom = objeto.nextFloat();
        //probabilidadVen = objeto.nextFloat();

        //System.out.println(getLocalName() +"Compra "+probabilidadCom);
        //System.out.println(getLocalName() +"Venta "+probabilidadVen);

        for(int i=0; i<numAcciones;i++) {
           /* probabilidadComprar[i] = aleatorio(0.5f, 0f);
            probabilidadVender[i] = aleatorio(0.5f, 0f);*/

            probabilidadComprar[i] = probabilidadCom;
            probabilidadVender[i] = probabilidadVen;

            decisionCambiarprobabilidad[i] = 0;

            OfertaEnviada[i] = false;
            OfertaEliminada[i] = false;

            CorreccionProbabilidades[i] = false;
        }

//        cantidadesaOfertar[0] = 2;
       // cantidadesaOfertar[1] = 3;
        nombre = 	this.getLocalName();
        tipoAgente = 2; //Corresponde al ID de los aleatorios


        registrarServidioAgente();


        RealizarRegistro RRBehaviour = new RealizarRegistro(this);
        addBehaviour(RRBehaviour); // Se llama al comportamiento de registrarse
    }

	

    ///////////////////////////////////////////
    ///////////////////////////////////////////
    ///////////////////////////////////////////
    ///////////////////////////////////////////

    /*void seleccionarAccionesAComprar()
    {
        float aleatorio;
        float ultimoPrecio;

        for (int i=0; i<numAcciones; i++)
        {
            int n = AgenteSubastador.historicoSubastas.get(i).size();
            ultimoPrecio = AgenteSubastador.historicoSubastas.get(i).get(n-1).getPrecioCierre();
           
            if (decisionCambiarprobabilidad[i] > 6) {
                probabilidadComprar[i] = aleatorio(0.5f, 0.2f);
                probabilidadVender[i] = aleatorio(0.5f, 0.2f);
                decisionCambiarprobabilidad[i] = 0;
            }

            aleatorio = aleatorio(1, 0);
            if (aleatorio <= probabilidadComprar[i] && saldo > AgenteSubastador.paquetesMinimos[i]*ultimoPrecio*1.05)
                probabilidadTransarAccion[i] = 1;
        }
    }
    
    void seleccionarAccionesAVender()
    {
        for (int i=0; i<numAcciones; i++) {
            float aleatorio = aleatorio(1, 0);

            if (aleatorio <= probabilidadVender[i] && cantidadXAccion[i] > 0) {
                if ((probabilidadComprar[i] < probabilidadVender[i] && probabilidadTransarAccion[i] <= 1 && probabilidadTransarAccion[i] >= 0))
                    probabilidadTransarAccion[i] = -1;
            }

            if (probabilidadTransarAccion[i] == 0)
                decisionCambiarprobabilidad[i]++;
        }
    }*/

    void parametrosIniciarSubasta()
    {
        //subastaActiva = true;
        //seleccionarAccionesAComprar();
        //seleccionarAccionesAVender();

        seleccionarOfertas();

        for (int i=0; i<numAcciones; i++) {
            cantidadOfertada[i] = 0;
            saldoOfertado[i] = 0;
        }
    }

    private void seleccionarOfertas()
    {
        float aleatorio;

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
                probabilidadTransarAccion[i] = 1;
            else if ( (probabilidadComprar[i] < aleatorio) && (aleatorio <= (probabilidadComprar[i] + probabilidadVender[i]) ) && cantidadXAccion[i] > 0)
                probabilidadTransarAccion[i] = -1;
            else
                probabilidadTransarAccion[i] = 0;

            //System.out.println(getLocalName()+" - " +probabilidadTransarAccion[i]);
        }

    }

    void registroTecnico(){}
    void seleccionarAccionesAVender(){}
    void seleccionarAccionesAComprar(){}
}