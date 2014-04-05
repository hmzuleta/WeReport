package Mundo;

import java.util.Date;

public class Reporte {

	private Double latitud1;
	private Double longitud1;
	private Double latitud2;
	private Double longitud2;
	private int calificacion;
	private String tipo;
	private Date fecha;
	private String user;
	
	public Reporte(String latitud1, String longitud1, String latitud2, String longitud2, String tipo, int calificacion, Date fecha, String user){
		this.latitud1 = Double.parseDouble(latitud1);
		this.longitud1 = Double.parseDouble(longitud1);
		this.latitud2 = Double.parseDouble(latitud2);
		this.longitud2 = Double.parseDouble(longitud2);
		this.calificacion = calificacion;
		this.tipo = tipo;
		this.fecha = fecha;
		if ( user != null){
			this.user = user;
		}
	}

	public Double getLatitud1() {
		return latitud1;
	}
	
	public Double getLongitud1(){
		return longitud1;
	}
	
	public Double getLatitud2() {
		return latitud1;
	}
	
	public Double getLongitud2(){
		return longitud1;
	}

	public void setLatitud1(String latitud1) {
		this.latitud1 = Double.parseDouble(latitud1);
	}
	
	public void setLongitud1(String longitud1) {
		this.longitud1 = Double.parseDouble(longitud1);
	}
	
	public void setLatitud2(String latitud2) {
		this.latitud2 = Double.parseDouble(latitud2);
	}
	
	public void setLongitud2(String longitud2) {
		this.longitud2 = Double.parseDouble(longitud2);
	}

	public int getCalificacion() {
		return calificacion;
	}

	public void setCalificacion(int calificacion) {
		this.calificacion = calificacion;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public void setTipo(String tipo){
		this.tipo = tipo;
	}
	
	public String getTipo(){
		return tipo;
	}
	
	public String toString(){
		String linea = "REP##";
		return linea + getLatitud1() + "##" + getLongitud1()+ "##" + getLatitud2() + "##" + getLongitud2()+ "##" + getTipo() + "##" + getCalificacion() + "##" + getFecha() + "##" + getUser() + "\n";
	}
}
