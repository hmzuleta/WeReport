package Mundo;

import java.util.ArrayList;

import org.joda.time.*;

import sun.util.calendar.BaseCalendar.Date;

public class Manejador extends Thread{
	
	private Sistema sistema;
	private DateTime arranque;
	private boolean recalculado;
	private ArrayList<Reciente> recientes;
	private long millis;
	
	public Manejador(long millis, Sistema sistema){
		this.sistema = sistema;
		recalculado = false;
		this.arranque = new DateTime();
		this.millis = millis;
		recientes = new ArrayList<Reciente>();
	}
	
	public void run(){
		long asda = System.currentTimeMillis();
		while(true){
			long van = System.currentTimeMillis() - asda;
			if(van > 30000){
				System.out.println("\t" + "Manejador: " + "\t" + "Recalculando");
				millis = System.currentTimeMillis();
				arranque = new DateTime();
				asda = System.currentTimeMillis();
				recientes = sistema.recalcular();
			}
		}
	}

}
