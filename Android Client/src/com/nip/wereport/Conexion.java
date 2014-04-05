package com.nip.wereport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import com.google.android.gms.maps.model.LatLng;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

public class Conexion{

	private Socket socket;

	private PrintWriter out;

	private BufferedReader in;

	public static final String IP = "192.168.43.22";
	public static final int PUERTO = 5000;
	private Context context;
	private MainActivity main;
	private boolean conexionTerminada;

	public Conexion(MainActivity main) {
		this.main = main;
		this.context = main.getApplicationContext();
		conexionTerminada = false;
	}

	public void conectar()
	{
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					socket = new Socket(IP, PUERTO);
					System.out.println("Is the Socket null? " + socket==null);
					out = new PrintWriter(socket.getOutputStream(), true);
					System.out.println("Is the OutputStream null? " + out==null);			
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					System.out.println("Is the InputStreamReader null? " + in==null);
					System.out.println("Conexión establecida con el servidor de manera exitosa");
					recibirMensajes();
				} catch (Exception e) {
					System.out.println(e.getMessage()+ "ERROR");
					e.printStackTrace();
				}				
			}
		});
		t.start();
	}

	/**
	 * Método que modela la recepción y envío de los mensajes de comunicación con el cliente asociado a esta conexión
	 * @throws Exception 
	 */
	private void recibirMensajes() throws Exception{
		String respuesta = null;
		while( !conexionTerminada ){
			String comando = in.readLine();
			System.out.println(comando);
			if(comando.startsWith("INF##")){
				//Al entrar acá significa que esta en formato AUT##Username##Password
				String[] datos = comando.split("##");
				String lati1 = datos[1];
				String long1 = datos[2];
				String lati2 = datos[3];
				String long2 = datos[4];
				double lat1 = Double.parseDouble(lati1);
				double lng1 = Double.parseDouble(long1);
				double lat2 = Double.parseDouble(lati2);
				double lng2 = Double.parseDouble(long2);
				
				String color = datos[5];
				int col=0;
				if (color.equals("YELLOW")) {
					col = Color.YELLOW;
				}
				else if (color.equals("RED")) {
					col = Color.RED;
				}
				else if(color.equals("WHITE"))
				{
					col = Color.WHITE;
				}
				System.out.println("Polyline: "+lat1+", "+lng1+", "+lat2+", "+lng2+", "+col);
				main.createPolylineFromLatLngs(lat1, lng1, lat2, lng2, col,main.darMapa());
			}
		}
	}


	public void desconectar() throws IOException{

		out.println("DES");
		out.close();
		out = null;

		in.close();
		in = null;

		socket.close();
		socket = null;
		System.out.println("Se cerró la conexión con el servidor.");

	}

	protected void reportar(String... a) {
		String res="";
		//		try {
		String lati1 = a[0];
		String long1 = a[1];
		String lati2 = a[2];
		String long2 = a[3];
		String tipo = a[4];
		String rating = a[5];
		String user = a[6];
		//MENSAJE PARA EL SERVER

		res="REP##"+lati1+"##"+long1+"##"+lati2+"##"+long2+"##"+tipo+"##"+rating+"##"+user+"##";
		//			socket = new Socket(IP, PUERTO);
		//			System.out.println("Is the Socket null? " + socket==null);
		//			out = new PrintWriter(socket.getOutputStream(), true);
		//			System.out.println("Is the OutputStream null? " + out==null);			
		//			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		//			System.out.println("Is the InputStreamReader null? " + in==null);
		//		} catch (Exception e) {
		//			res="EXCEPTION##"+e.getMessage();
		//		}
		System.out.println("Mensaje del reporte: "+res);
		System.out.println("PrintWriter es nulo?" + out==null);
		out.println(res);
	}

	public void enviarUbicacion(LatLng latLng1calleMarcada,
			LatLng latLng2calleMarcada) {
		double lati1 = latLng1calleMarcada.latitude;
		double long1 = latLng1calleMarcada.longitude;
		double lati2 = latLng2calleMarcada.latitude;
		double long2 = latLng2calleMarcada.longitude;
		String res="POS##"+lati1+"##"+long1+"##"+lati2+"##"+long2;
		System.out.println("Entró a enviar su ubicación: " + res);
		out.println(res);
	}
}
