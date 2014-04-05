package Mundo;

//----------
//NIP
//Proyecto: Protect-Block
//Clase: Sistema
//Version: 0.2
//Creador: Alejandro Lovera - 24/09/2013
//Modificado: 26/09/2013
//----------

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


/**
 * Clase modeladora del sistema principal y servidor
 * @author Messie
 */
public class Sistema{

	//----------
	// CONSTANTES
	//----------

	/**
	 * Constante que modela el puerto por el cual se reciben y envían los mensajes de la comunicacion entre el servidor y el cliente.
	 */
	private static final int PUERTO = 5000;

	//----------
	// ATRIBUTOS
	//----------

	/**
	 * Atributo que modela el socket servidor de la conexion
	 */
	private ServerSocket socket;

	/**
	 * Atributo que modela el arreglo donde se almacenan los Usuarios afiliados (registrados) al sistema
	 */
	private ArrayList<Usuario> usuarios;

	/**
	 * Atributo que modela el arreglo donde se almacenan los usernames de los Usuarios afiliados (registrados) al sistema
	 */
	private ArrayList<String> usernames;

	private ArrayList<Reciente> recientes;
	private ArrayList<Reporte> reportes;

	private Date arranque;

	private String nombreArchivo; 
	private int conexiones;

	private File archivo;

	private Manejador manejador;

	//----------
	// CONSTRUCTOR
	//----------
	/**
	 * Constructor de la clase Sistema, inicializador de atributos
	 * @throws Exception 
	 */
	public Sistema() throws Exception{

		usuarios = new ArrayList<Usuario>();
		usernames = new ArrayList<String>();
		recientes = new ArrayList<Reciente>();
		reportes = new ArrayList<Reporte>();
		arranque = new Date();
		conexiones = 0;
		nombreArchivo = "./data/Reportes_WeReport.txt";

		archivo = new File( nombreArchivo );

		if( archivo.exists( ) )
		{
			// El archivo existe: se debe recuperar de allí el estado del modelo del mundo
			try
			{
				BufferedReader reader = new BufferedReader(new FileReader(archivo));
				String linea = reader.readLine();
				while(linea != null){
					String[] datos = linea.split("##");
					String latitud1 = datos[1];
					String longitud1 = datos[2];
					String latitud2 = datos[3];
					String longitud2 = datos[4];

					String tipo = datos[5];
					String calificacion = datos[6];
					String usuario = datos[8];
					SimpleDateFormat formatoDelTexto = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",Locale.ENGLISH);
					String fecha = datos[7];
					Date fech = (Date) formatoDelTexto.parse(fecha);


					Reporte nuevo = new Reporte(latitud1, longitud1, latitud2, longitud2, tipo, Integer.parseInt(calificacion), fech, usuario);
					reportes.add(nuevo);
					linea = reader.readLine();
				}
				reader.close();
			}
			catch( Exception e )
			{
				throw new Exception( "Error fatal: imposible restaurar el estado del programa (" + e.getMessage( ) + ")" );
			}
		}
		else{
			archivo.createNewFile();
		}

		manejador = new Manejador(System.currentTimeMillis(), this);
		manejador.start();
		System.out.println("Bienvenido NIP a Servidor WeReport (" + new Date()+ ")");
		System.out.println("Se inicia con " + reportes.size() + " reportes.");
		System.out.println("Esperando conexion");
	}

	//----------
	// MÉTODOS
	//----------

	/**
	 * Método que devuelve la lista de los usuarios afiliados (registrados) al sistema
	 * @return usuarios - lista de Usuarios
	 */
	public ArrayList<Usuario> darUsuarios(){
		return usuarios;
	}

	/**
	 * Método que devuelve la lista de los usernames de los usuarios afiliados (registrados) al sistema
	 * @return usernames - lista de usernames
	 */
	public ArrayList<String> darUsername(){
		return usernames;
	}

	/**
	 * Método que crea un nuevo Usuario dados los elementos que entran por parámetro y lo agrega a la lista de Usuarios afiliados
	 * @param nombre - nombre del nuevo usuario; nombre != null
	 * @param apellido - apellido del nuevo usuario; apellido != null
	 * @param correo - correo del nuevo usuario; correo != null
	 * @param username - username del nuevo usuario; username != null, username no se encuentra en uso
	 * @param password - password del nuevo usuario; password != null
	 */
	public void agregarUsuario(String nombre, String apellido, String correo, String username, String password){
		if(buscarUsername(username)){
			//Lanzar Excepcion, username existe
		}
		else{
			Usuario nuevo = new Usuario(nombre, apellido, username, correo, password);
			usuarios.add(nuevo);
		}
	}

	/**
	 * Método que busca un usuario dado su username en la lista de usuarios registrados
	 * @param username - username buscado en la lista
	 * @return booleano que dice si existe, o no, un usuario con el username que entra por parámetro. TRUE existe un usuario con dicho username. FALSO dlc.
	 */
	public boolean buscarUsername(String username){
		boolean existe = true;
		for(int i = 0; i < usernames.size() && existe; i++){
			String user = usernames.get(i);
			if(user.equals(username)){
				existe = false;
			}
		}
		return !existe;
	}

	/**
	 * Método encargado de esperar y recibir conexiones y generar nuevas clases Conexion por cada unión cliente-servidor realizada
	 */
	public void recibirConexiones(){
		try{
			socket = new ServerSocket(PUERTO);
			while (true){
				//Esperar conexión
				Socket socketCliente = socket.accept();
				conexiones ++;
				System.out.println("Inicio de Conexión: Conexión "+ conexiones);
				PrintWriter out = new PrintWriter (socketCliente.getOutputStream(), true);
				BufferedReader in = new BufferedReader ( new InputStreamReader(socketCliente.getInputStream()));
				//Creador Hilo de conexion
				Conexion conexion = new Conexion(in, out, this);
				conexion.start();
			}
		}
		catch( IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Método encargado de conectar un username con su password retornando un Usuario
	 * @param username - username del usuario
	 * @param password - password del usuario
	 * @return Usuario del sistema asociado al mismo username y la misma password entrada por parámetro
	 */
	public Usuario conectar(String username, String password){
		boolean existe = true;
		Usuario user = null;
		for(int i =0; i<usuarios.size() && existe; i++){
			Usuario encontrado = usuarios.get(i);
			if(encontrado.darUsername().equals(username)){
				if(encontrado.darPassword().equals(password)){
					user = encontrado;
					existe = false;
				}
			}
		}
		return user;
	}

	public ArrayList<Reciente> recalcular(){
		if(recientes.size()!=0){
			System.out.println("\t" + "\t" + "Hay " + recientes.size() + " reportes recientes");
			System.out.println("\t" + "\t" + "ANTES: " + "\t" + "MODIFICADO = " + recientes.get(0).darVecesModificado());
			System.out.println("\t" + "\t" + "\t" + " COLOR = " + recientes.get(0).darColor());
			System.out.println("\t" + "\t" + "\t" + " CALIFICACION = " + recientes.get(0).darCalificacion());
			for(int i=0; i<recientes.size(); i++){
				Reciente rec = recientes.get(i);
				int cant = rec.darVecesModificado();
				double calificacion = rec.darCalificacion();
				if(cant > 3 && calificacion < 5){
					rec.cambiarColor("WHITE");
					rec.fueModificado();
					recientes.remove(i);
				}
				else{
					if(calificacion>50){
						rec.cambiarColor("RED");
						rec.fueModificado();
						rec.cambiarCalificacion(-calificacion*0.2);
					}
					else{
						rec.cambiarColor("YELLOW");
						rec.fueModificado();
						rec.cambiarCalificacion(-calificacion*0.5);
					}
				}

			}
			if(recientes.size()!=0){
				System.out.println("\t" + "\t" + "DESPUES: MODIFICADO = " + recientes.get(0).darVecesModificado());
				System.out.println("\t" + "\t" + "\t" + " COLOR = " + recientes.get(0).darColor());
				System.out.println("\t" + "\t" + "\t" + " CALIFICACION = " + recientes.get(0).darCalificacion());
			}
			else{
				System.out.println("\t" + "\t" + "\t" + "Vacio luego de recalcular");
			}
		}
		else{
			System.out.println("\t" + "\t" + "\t" + "No hay reportes recientes");
		}
		System.out.println("\t" + "\t" + "\t" + "Se termina de recalcular en " + new Date() + " con " + recientes.size() + " reportes recientes.");
		return recientes;
	}

	public void agregarReporte(String latitud1, String longitud1, String latitud2, String longitud2, String tipo, int calificacion, Date fecha, String user) throws Exception{
		Reporte nuevo = new Reporte(latitud1, longitud1, latitud2, longitud2, tipo, calificacion, fecha, user);
		reportes.add(nuevo);
		boolean encontrado = false;
		for(int i=0; i<recientes.size() && !encontrado; i++){
			Reciente rec = recientes.get(i);
			if(rec.darLatitud1()==Double.parseDouble(latitud1) && rec.darLongitud1()==Double.parseDouble(longitud1) && rec.darLatitud2()==Double.parseDouble(latitud2) && rec.darLongitud2()==Double.parseDouble(longitud2)){
				encontrado = true;
				rec.cambiarCalificacion(calificacion);
			}
		}
		if(!encontrado){
			Reciente reciente = new Reciente(latitud1, longitud1, latitud2, longitud2, tipo, calificacion);
			recientes.add(reciente);
		}

		try
		{
			BufferedWriter esc = new BufferedWriter(new FileWriter(archivo, true));
			esc.write(nuevo.toString());
			esc.close();
		}
		catch( Exception e )
		{
			throw new Exception( "Error fatal: imposible restaurar el estado del programa (" + e.getMessage( ) + ")" );
		}

	}

	public ArrayList<Reporte> darReportes(){
		return reportes;
	}

	public ArrayList<Reciente> darRecientes(){
		return recientes;
	}

	public static void main(String[] args) throws Exception {
		Sistema nuevo = new Sistema();
		nuevo.recibirConexiones();
	}

	public ArrayList<Reciente> darReportesCercanos(double lat1, double lng1, double lat2, double lng2) {
		System.out.println("Entró a buscar reportes recientes cercanos al usuario");
		ArrayList<Reciente> recientesCercanos = new ArrayList<Reciente>();
		System.out.println(recientes.size());
		for (int i = 0; i < recientes.size(); i++) {
			Reciente rec = recientes.get(i);
			System.out.println(rec.darTipo());
			if ((rec.darLatitud1()>=(lat1-0.001) && rec.darLatitud1()<=(lat1+0.001)) || 
					(rec.darLongitud1()>=(lng1-0.001) && rec.darLongitud1()<=(lng1+0.001)) ||
					(rec.darLatitud2()>=(lat2-0.001) && rec.darLatitud2()<=(lat2-0.001)) || 
					(rec.darLongitud2()>=(lng2-0.001) && rec.darLongitud2()<=(lng2+0.001))) 
			{
				//El reciente está dentro del rango y debe ser enviado
				recientesCercanos.add(rec);
				System.out.println("Se agregó un reciente");				
			}			
		}
		return recientesCercanos;
	}
}