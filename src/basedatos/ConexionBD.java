 package basedatos;

 import java.sql.*;

 /**
  * En esta base de datos se tienen varias tablas:
  * calce: IDCalce, IDOfertaCompra, IDOfertaVenta, IDProducto, precioSugerido, fecha
  * bursatiles: IDAgente, saldo, fecha
  * producto: IDProducto, nombre, descripcion
  *
  * @author Sebas
  */
 public class ConexionBD
 { 	
     private String bdOracle = "simab";	// el nombre de la base de datos de oracle
 	 private String bdMySql = "simabcanal"; // el nombre de la base de datos de MySQL
 	 private String login = "root";  // login
 	 private String password = "root"; // password
 	 private String urlMySql = "jdbc:mysql://localhost/"+bdMySql;   //direccion de la BD, en la universidad es 10.1.12.229
                                                                        //Sala PIT 168.176.125.201
 	 private String urlOracle = "jdbc:oracle:thin:@localhost:1521:"+bdOracle;
 	
 	 private Connection conn = null; // Guarda una instancia de la conexion con la BD
 	 private Statement stmt; // Guarda los resultados de los comandos de SQL
 	
  	 public ConexionBD(String sgbd)  // Constructor
  	 {
  	     if(sgbd.equalsIgnoreCase("mysql")) {
  		     conexionMySQL();
             //System.out.println("Conexion con BD MySQL");
  		 } else if(sgbd.equalsIgnoreCase("oracle")) {
  			 conexionORACLE();
             //System.out.println("Conexion con BD Oracle");
  		 }
  	 }
  	
  	
 	 public void conexionORACLE()
 	 {
 	     try {
 		     Class.forName ("oracle.jdbc.driver.OracleDriver");  //Carga el plugin para conexiones con BD JDBC
 			 conn = DriverManager.getConnection (urlOracle, login, password);    //Obtiene la conexion
 			 if (conn != null) {
 			//	System.out.println("Conexion jdbc:oracle:thin:@200.24.8.35:1521:xue ... Ok");
 			     stmt = conn.createStatement(); 		// Inicializa stmt
 			 }
 		 } catch(SQLException ex) {
 		     System.err.println(ex);
 		 } catch(ClassNotFoundException ex) {
 			 System.err.println(ex);
 		 } catch(Exception ex) {
 			 System.err.println(ex);
 		 }
 	}
 	
 	public void conexionMySQL()
        {
 	    try {
 			Class.forName("com.mysql.jdbc.Driver").newInstance();   //Carga el plugin para conexiones con BD JDBC por MySQL
 			conn = DriverManager.getConnection(urlMySql, login, password);
 			if (conn != null) {
 				////System.out.println("Conexion a base de datos "+urlMySql+" ... Ok");
 				stmt = conn.createStatement();
 			}
 		} catch(SQLException ex) {
 			System.err.println(ex);
 		} catch(ClassNotFoundException ex) {
 			System.err.println(ex);
 		} catch(Exception ex) {
 			System.err.println(ex);
 		}
 	}
 
 	
 	public ResultSet consulta(String consul)
 	{
 		ResultSet r = null;
 		try {
 			r = stmt.executeQuery(consul.toLowerCase()); // Realiza una consulta
	 	} catch(SQLException ex) {
 			//System.err.println("No se pudo realizar la consulta indicada");
 			//System.err.println("\n"+consul+"\n");
 		}
        
 		return r;
 	}
 	
 	public void commit()
 	{
 		try {
 			stmt.execute("commit"); // Determina la terminacion de una conexion con la BD
	 	} catch(SQLException ex) {
 			System.err.println("problemas en commit");
 		}
 	}
 	
 	public String ejecutar(String consul, String tipo)
 	{
 		String error = "noError";
 		try {
 			stmt.execute(consul);	// Ejecuta una consulta
	 	} catch(SQLException ex) {
 			System.err.println("Mi ERROR en ConexionBD.ejecutar " + ex);
 			error = ex.getMessage();
 			System.out.println("SQLException.getMessage() = " + error);
                        System.out.println("************************* = " + tipo +" - "+consul);
 			
 			error = error.substring(0,9);
 		/*	
 			if(error.equals("ORA-00001"))
 			{
 				error = "ViolacionDeClavePrimaria";
 			}
 			else if(error.equals("ORA-01400"))
 			{
 				error = "CamposNulos";
 			}
 			else if(error.equals("ORA-00913"))
 			{
 				error = "demasiadosValores";
 			}
 			else if(error.equals("ORA-00904"))
 			{
 				error = "identificadorNoValido";
 			}
 			else
 			{
 				error = "OtroError";
 			}
 			*/
 		}
 		return error;
 	}
 	
 	
 	public void limpiar()
 	{
 		try {
 			stmt.clearBatch();
 		} catch(SQLException sqlex) {
 			System.err.println("Prolemas al limpiar el statement");
 		}
 	}
  	 	 	
 	public void cerrarConexion()
 	{ 		
 		try {
 			stmt.close();
 			conn.close();
 		} catch(SQLException ex) {
 			System.err.println("ERROR: No se cerro la conexion con la BD");
 		}
 		//System.err.println("Conexion Cerrada !!\n");
 	}
        
    public String insertarCalce( int IDOfertaCompra, int IDOfertaVenta, int IDProducto, 
                                float precioCompra, float precioVenta, int fecha, int cantidad, int repeticion)
    {
        String resp = "noError";

        //resp = ejecutar("INSERT INTO calce VALUES( '"+IDCalce+"','"+IDOfertaCompra+"','"+IDOfertaVenta+"','"+IDProducto+"','"+precioCompra+"','"+precioVenta+"','"+fecha+"','"+cantidad+"','"+repeticion+"' )","insertarCalce");
        resp = ejecutar("INSERT INTO calce (IDOfertaCompra, IDOfertaVenta, IDProducto, precioCompra, precioVenta, fecha, cantidad, repeticion) VALUES ( '"+IDOfertaCompra+"','"+IDOfertaVenta+"','"+IDProducto+"','"+precioCompra+"','"+precioVenta+"','"+fecha+"','"+cantidad+"','"+repeticion+"' )","insertarCalce");
        //System.out.println("**** Se ingreso un calce en la BD!! ****");

        return resp;
    }

    public String insertarCalceCM( int IDOfertaCompra, int IDOfertaVenta, int IDProducto,
                                float precioCalce, int cantidad, int fecha, int repeticion)
    {
        String resp = "noError";

        //resp = ejecutar("INSERT INTO calcecm VALUES( '"+IDCalce+"','"+IDOfertaCompra+"','"+IDOfertaVenta+"','"+IDProducto+"','"+precioCalce+"','"+cantidad+"','"+fecha+"','"+repeticion+"' )","insertarCalceCM");
        resp = ejecutar("INSERT INTO calcecm (IDOfertaCompra, IDOfertaVenta, IDProducto, precioCalce, cantidad, fecha, repeticion) VALUES ( '"+IDOfertaCompra+"','"+IDOfertaVenta+"','"+IDProducto+"','"+precioCalce+"','"+cantidad+"','"+fecha+"','"+repeticion+"' )","insertarCalceCM");
        //System.out.println("**** Se ingreso un calce en la BD!! ****");

        return resp;
    }

    public String insertarAgente(int IDAgente, String nombre, int tipo)
    {
        String resp = "noError";

        resp = ejecutar("INSERT INTO bursatil VALUES( '"+IDAgente+"','"+nombre+"','"+tipo+"' )","insertarAgente");
        //System.out.println("**** Se ingreso un agente en la BD!! ****");

        return resp;
    }

    public String insertarSaldoXAgente(int IDAgente, int fecha, float saldo, int repeticion)
    {
        String resp = "noError";

        resp = ejecutar("INSERT INTO saldoxagente VALUES( '"+IDAgente+"','"+fecha+"','"+saldo+"','"+repeticion+"')","insertarSaldoXAgente");
        //System.out.println("**** Se ingreso un saldoXagente en la BD!! ****");

        return resp;
    }

    public String insertarAccionesXAgente(int IDAgente, int IDProducto, int fecha, int cantidad, int repeticion)
    {
        String resp = "noError";

        resp = ejecutar("INSERT INTO accionesxagente VALUES( '"+IDAgente+"','"+IDProducto+"','"+fecha+"','"+cantidad+"','"+repeticion+"')","insertarAccionesXAgente");
        //System.out.println("**** Se ingreso una accionXagente en la BD!! ****");
        return resp;
    }

    public String insertarCotizacion(int IDProducto, int fecha, float precioInicio, float precioCierre,
            float precioMax, float precioMin, float precioPromedio, int cantidad, int repeticion)
    {
        String resp = "noError";

        resp = ejecutar("INSERT INTO cotizacion VALUES( '"+IDProducto+"','"+fecha+"',"+
                (precioInicio == -1 ? "null": ("'"+precioInicio+"'"))+","+
                (precioCierre == -1 ? "null": ("'"+precioCierre+"'"))+","+
                (precioMax == -1 ? "null": ("'"+precioMax+"'"))+","+
                (precioMin == -1 ? "null": ("'"+precioMin+"'"))+","+
                (precioPromedio == -1 ? "null": ("'"+precioPromedio+"'"))+",'"+cantidad+"','"+repeticion+"')","insertarCotizacion");
        //System.out.println("**** Se ingreso una cotizacion en la BD!! ****");

        return resp;
    }

    public String insertarCotizacionDesdeArchivo(int IDProducto, int fecha, String precioInicio, String precioCierre,
            String precioMax, String precioMin, String precioPromedio, String cantidad, String repeticion)
    {
        String resp = "noError";

         resp = ejecutar("INSERT INTO cotizacion VALUES( '"+IDProducto+"','"+fecha+"',"+
                (precioInicio.equals("-1") ? "null": ("'"+precioInicio+"'"))+","+
                (precioCierre.equals("-1") ? "null": ("'"+precioCierre+"'"))+","+
                (precioMax.equals("-1") ? "null": ("'"+precioMax+"'"))+","+
                (precioMin.equals("-1") ? "null": ("'"+precioMin+"'"))+","+
                (precioPromedio.equals("-1") ? "null": ("'"+precioPromedio+"'"))+",'"+cantidad+"','"+repeticion+"')","insertarCotizacionDesdeArchivo");
        //System.out.println("**** Se ingreso una cotizacion en la BD!! ****");

        return resp;
    }

    public String insertarProducto(int IDProducto, String nombre, String descripcion, int paqueteMinimo)
    {
        String resp = "noError";

        resp = ejecutar("INSERT INTO producto VALUES( '"+IDProducto+"','"+nombre+"','"+descripcion+"','"+paqueteMinimo+"')","insertarProducto");
        System.out.println("**** Se ingreso un producto en la BD!! ****");

        return resp;
    }

    public String insertarOfertaCompra( int IDOfertaCompra, int IDAgente, int IDProducto, float precioCompra, int fecha, int cantidad, int repeticion)
    {
        String resp = "noError";

        resp = ejecutar("INSERT INTO ofertacompra VALUES( '"+IDOfertaCompra+"','"+IDAgente+"','"+IDProducto+"','"+precioCompra+"','"+fecha+"','"+cantidad+"','"+repeticion+"')","insertarOfertaCompra");
        //resp = ejecutar("INSERT INTO ofertacompra (IDAgente, IDProducto, precioCompra, fecha, cantidad, repeticion) VALUES ( '"+IDAgente+"','"+IDProducto+"','"+precioCompra+"','"+fecha+"','"+cantidad+"','"+repeticion+"')","insertarOfertaCompra");
        //System.out.println("**** Se ingreso una oferta de compra en la BD!! ****");

        return resp;
    }

    public String insertarOfertaVenta( int IDOfertaventa ,int IDAgente, int IDProducto, float precioVenta, int fecha, int cantidad, int repeticion)
    {
        String resp = "noError";

        resp = ejecutar("INSERT INTO ofertaventa  VALUES ( '"+IDOfertaventa+"','"+IDAgente+"','"+IDProducto+"','"+precioVenta+"','"+fecha+"','"+cantidad+"','"+repeticion+"')","insertarOfertaVenta");
        //resp = ejecutar("INSERT INTO ofertaventa (IDAgente, IDProducto, precioVenta, fecha, cantidad, repeticion) VALUES ( '"+IDAgente+"','"+IDProducto+"','"+precioVenta+"','"+fecha+"','"+cantidad+"','"+repeticion+"')","insertarOfertaVenta");
        //System.out.println("**** Se ingreso una oferta de venta en la BD!! ****");

        return resp;
    }

    public String insertarAnalisisTecnico(int idagente, int idproducto, int fecha, String indicador, float valor, int repeticion)
    {
        String resp = "noError";

        resp = ejecutar("INSERT INTO analisistecnico VALUES( '"+idagente+"','"+idproducto+"','"+fecha+"','"+indicador+"','"+valor+"','"+repeticion+"')","insertarAnalisisTecnico");

        return resp;
    }

    public String inicializarBD()
    {
        String resp = "noError";

        System.out.println("Inicializando BD...");
        resp = ejecutar("DELETE FROM calce","DELETE FROM");
        resp = ejecutar("DELETE FROM calcecm","DELETE FROM");
        resp = ejecutar("DELETE FROM bursatil","DELETE FROM");
        resp = ejecutar("DELETE FROM producto","DELETE FROM");
        resp = ejecutar("DELETE FROM ofertacompra","DELETE FROM");
        resp = ejecutar("DELETE FROM ofertaventa","DELETE FROM");
        resp = ejecutar("DELETE FROM accionesxagente","DELETE FROM");
        resp = ejecutar("DELETE FROM cotizacion","DELETE FROM");
        resp = ejecutar("DELETE FROM saldoxagente","DELETE FROM");
        resp = ejecutar("DELETE FROM analisistecnico","DELETE FROM");
        resp = ejecutar("DELETE FROM preciofundamental","DELETE FROM");
        resp = ejecutar("DELETE FROM estrategiadaptativo","DELETE FROM");

        resp = ejecutar("TRUNCATE calce","TRUNCATE");
        resp = ejecutar("TRUNCATE calcecm","TRUNCATE");
        /*resp = ejecutar("TRUNCATE ofertacompra","TRUNCATE");
        resp = ejecutar("TRUNCATE ofertaventa","TRUNCATE");*/


        //resp = ejecutar("INSERT INTO bursatil VALUES (0,'aA24',2),(0,'aA23',2),(2,'aA25',2),(3,'aA64',2),(4,'aA65',2),(5,'aA30',2),(6,'aA31',2),(7,'aA32',2),(8,'aA29',2),(9,'aA66',2),(10,'aA67',2),(11,'aA28',2),(12,'aA26',2),(13,'aA27',2),(14,'aA68',2),(15,'aA69',2),(16,'aA70',2),(17,'aA71',2),(18,'aA35',2),(19,'aA72',2),(20,'aA73',2),(21,'aA1',2),(22,'aA2',2),(23,'aA74',2),(24,'aA34',2),(25,'aA75',2),(26,'aA76',2),(27,'aA77',2),(28,'aA78',2),(29,'aA33',2),(30,'aA3',2),(31,'aA39',2),(32,'aA36',2),(33,'aA79',2),(34,'aA38',2),(35,'aA4',2),(36,'aA80',2),(37,'aA81',2),(38,'aA37',2),(39,'aA5',2),(40,'aA43',2),(41,'aA82',2),(42,'aA83',2),(43,'aA42',2),(44,'aA84',2),(45,'aA41',2),(46,'aA40',2),(47,'aA85',2),(48,'aA44',2),(49,'aA86',2),(50,'aA45',2),(51,'aA46',2),(52,'aA6',2),(53,'aA87',2),(54,'aA48',2),(55,'aA88',2),(56,'aA49',2),(57,'aA47',2),(58,'aA89',2),(59,'aA7',2),(60,'aA90',2),(61,'aA8',2),(62,'aA91',2),(63,'aA50',2),(64,'aA9',2),(65,'aA92',2),(66,'aA51',2),(67,'aA93',2),(68,'aA52',2),(69,'aA10',2),(70,'aA94',2),(71,'aA53',2),(72,'aA95',2),(73,'aA12',2),(74,'aA13',2),(75,'aA96',2),(76,'aA14',2),(77,'aA11',2),(78,'aA97',2),(79,'aA54',2),(80,'aA98',2),(81,'aA55',2),(82,'aA99',2),(83,'aA100',2),(84,'aA15',2),(85,'aA56',2),(86,'aA57',2),(87,'aA16',2),(88,'aA58',2),(89,'aA17',2),(90,'aA59',2),(91,'aA60',2),(92,'aA18',2),(93,'aA61',2),(94,'aA22',2),(95,'aA21',2),(96,'aA63',2),(97,'aA62',2),(98,'aA20',2),(99,'aA19',2)","Prueba");

        System.out.println("Ya se inicializo la BD...");
        return resp;
    }
}
