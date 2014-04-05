package Mundo;

public class Reciente {
	private double latitud1;
	private double longitud1;
	private double latitud2;
	private double longitud2;
	private double calificacion;
	private boolean modificado;
	private String tipo;
	private int veces;
	private String color;
	
	public Reciente(String latitud1, String longitud1, String latitud2, String longitud2, String tipo, int calificacion){
		this.latitud1 = Double.parseDouble(latitud1);
		this.longitud1 = Double.parseDouble(longitud1);
		this.latitud2 = Double.parseDouble(latitud2);
		this.longitud2 = Double.parseDouble(longitud2);
		this.calificacion = calificacion;
		this.tipo = tipo;
		modificado = false;
		veces = 0;
		color = "YELLOW";
	}
	public String darTipo(){
		return tipo;
	}
	public boolean darModificado() {
		return modificado;
	}

	public double darCalificacion(){
		return calificacion;
	}

	public int darVecesModificado() {
		return veces;
	}
	
	public String darColor(){
		return color;
	}
	
	public void cambiarColor(String color){
		this.color = color;
	}

	public double darLatitud1(){
		return latitud1;
	}
	
	public double darLongitud1(){
		return longitud1;
	}
	public double darLatitud2(){
		return latitud1;
	}
	
	public double darLongitud2(){
		return longitud1;
	}
	
	public void cambiarCalificacion(double calificacion){
		this.calificacion += calificacion;
	}
	
	public void reducirCalificacion(Double porcentaje){
		this.calificacion = this.calificacion - (this.calificacion*porcentaje);
	}
	
	public void fueModificado(){
		veces++;
	}
}
