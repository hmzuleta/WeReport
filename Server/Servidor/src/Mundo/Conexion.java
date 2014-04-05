package Mundo;

//----------
//NIP
//Proyecto: Protect-Block
//Clase: Conexion
//Version: 0.2
//Creador: Alejandro Lovera - 24/09/2013
//Modificado: 26/09/2013
//----------

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Clase que modela la conexión del cliente con el servidor como un Thread para manejar
 * @author Messie
 */
public class Conexion extends Thread{

	//----------
	// ATRIBUTOS
	//----------

	/**
	 * Atributo que modela la clase que tiene los comandos del protocolo de comunicación
	 */
	private Protocolo protocolo;

	/**
	 * Atributo que modela el PrintWriter de la conexión
	 */
	private PrintWriter out;

	/**
	 * Atributo que modela el BufferedReader de la conexión
	 */
	private BufferedReader in;

	/**
	 * Atributo que modela el sistema principal con el que se realiza la lógica del mundo
	 */
	private Sistema sistema;


	//----------
	// CONSTRUCTOR
	//----------

	/**
	 * Constructor de la conexión con base en los parámetros enviados desde el servidor
	 * @param in - BufferedReader de la conexion; in != null
	 * @param out - PrintWriter de la conexion; out != null
	 * @param sistema - Sistema principal con el que se realiza la lógica; sistema != null
	 */
	public Conexion (BufferedReader in, PrintWriter out, Sistema sistema){
		this.in = in;
		this.out = out;
		this.sistema = sistema;
	}

	//----------
	// MÉTODOS
	//----------

	/**
	 * Metodo run del Thread conexión
	 */
	public void run(){
		try{
			recibirMensajes();
		}
		catch( IOException e){
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Método que modela la recepción y envío de los mensajes de comunicación con el cliente asociado a esta conexión
	 * @throws Exception 
	 */
	private void recibirMensajes() throws Exception{
		boolean conexionTerminada = false;
		String respuesta = null;
		while( !conexionTerminada ){
			String comando = in.readLine();
			System.out.println(comando);
			if(comando.startsWith(Protocolo.AUTENTICAR)){
				//Al entrar acá significa que esta en formato AUT##Username##Password
				String[] datos = comando.split("##");
				String username = datos[1];
				String password = datos[2];
				if(sistema.conectar(username, password) != null){
					respuesta = Protocolo.AUTENTICAR + "##" + Protocolo.ACEPTAR;
					out.println(respuesta);
				}
			}
			else if(comando.startsWith(Protocolo.REPORTE)){
				//Al entrar acá significa que esta en formato REP##Lat1##Long1##Lat2##Long2##Tipo##Calificacion##Usuario
				String[] datos = comando.split("##");
				String latitud1 = datos[1];
				String longitud1 = datos[2];
				String latitud2 = datos[3];
				String longitud2 = datos[4];
				String tipo = datos[5];
				int calificacion = Integer.parseInt(datos[6]);
				String usuario = datos[7];
				Date fecha = new Date();
				sistema.agregarReporte(latitud1, longitud1, latitud2, longitud2, tipo, calificacion, fecha, usuario);
				System.out.println("Servidor: " + Protocolo.ACEPTAR);
				out.println(Protocolo.ACEPTAR);
			}
			else if(comando.startsWith(Protocolo.POSICION)){
				System.out.println("Llegó una actualización de la posición");
				String[] datos = comando.split("##");
				String lati1 = datos[1];
				String long1 = datos[2];
				String lati2 = datos[3];
				String long2 = datos[4];
				
				double lat1 = Double.parseDouble(lati1);
				double lng1 = Double.parseDouble(long1);
				double lat2 = Double.parseDouble(lati2);
				double lng2 = Double.parseDouble(long2);
				System.out.println("PRE PRE PRE");
				ArrayList<Reciente> cercanos = sistema.darReportesCercanos(lat1,lng1,lat2,lng2);
				for (int i = 0; i < cercanos.size(); i++) 
				{
					Reciente r = cercanos.get(i);
					System.out.println("Se envió un reporte reciente: "+
							Protocolo.INFORMACION+"##"+r.darLatitud1()+"##"+r.darLongitud1()+"##"+
							r.darLatitud2()+"##"+r.darLongitud2()+"##"+r.darColor());
					out.println(Protocolo.INFORMACION+"##"+r.darLatitud1()+"##"+r.darLongitud1()+"##"+
							r.darLatitud2()+"##"+r.darLongitud2()+"##"+r.darColor());
				}
			}
			else if(comando.startsWith(Protocolo.DESCONECTAR)){
				//Al entrar acá significa que esta en formato DES##
				respuesta = Protocolo.DESCONECTAR + "##" + Protocolo.ACEPTAR;
				System.out.println("Servidor: " + respuesta);
				out.println(respuesta);
				conexionTerminada = true;
			}
			else if(comando.startsWith(Protocolo.ERROR)){

			}
			else if(comando.startsWith(Protocolo.ACEPTAR)){

			}
			else if(comando.startsWith(Protocolo.HOLA)){
				respuesta = Protocolo.ACEPTAR + "##" + new Date();
				System.out.println("Servidor: " + respuesta);
				out.println(respuesta);
			}
			else if(comando.equals(null)){
				respuesta = Protocolo.ERROR;
				System.out.println("Servidor: " + respuesta);
				out.println(respuesta);
			}
			//COMANDOS POR PROTOCOLO
		}
	}
}