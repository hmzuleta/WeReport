package com.nip.wereport;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MainActivity extends FragmentActivity {

	/**
	 * Constante para los límites geográficos de la cámara inicial; bogotá.
	 */
	private LatLngBounds BOGOTA = new LatLngBounds(new LatLng(4.59354,-74.26964), new LatLng(4.79952,-73.98262));

	public final static String ROBO = "ROBO";
	public final static String PANDILLAS = "PANDILLAS";
	public final static String POLICIA = "POLICIA";
	public final static String DROGAS = "DROGAS";
	public final static String DISTURBIOS = "DISTURBIOS";
	public final static String LUZ = "LUZ";

	private String[] drawerListViewItems;
	private DrawerLayout drawerLayout;
	private ListView drawerListView;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String phoneSerial;
	private Context contextForDialog = null;
	private Conexion conexion;
	private Menu menu;

	//ULTIMA CALLE MARCADA, PARA HACER EL REPORTE
	private LatLng latLng1calleMarcada;
	private LatLng latLng2calleMarcada;

	private GoogleMap map;

	//-----------------------------------
	// Constructor del fragment
	//-----------------------------------
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		TelephonyManager tManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
		phoneSerial = tManager.getDeviceId();
		//		final Toast login = Toast.makeText(this, "No se ha implementado el login", Toast.LENGTH_SHORT);
		//		final Toast refresh = Toast.makeText(this, "No se ha implementado el refresh", Toast.LENGTH_SHORT);
		//		final Toast options = Toast.makeText(this, "No se ha implementado el refresh", Toast.LENGTH_SHORT);
		//		final Toast about = Toast.makeText(this, "No se ha implementado el refresh", Toast.LENGTH_SHORT);


		contextForDialog = this;
		// get list items from strings.xml
		drawerListViewItems = getResources().getStringArray(R.array.items);
		// get ListView defined in activity_main.xml
		drawerListView = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		drawerListView.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_listview_item, drawerListViewItems));

		// App Icon 
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		actionBarDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				drawerLayout,         /* DrawerLayout object */
				R.drawable.ic_launcher,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
				);

		// Set actionBarDrawerToggle as the DrawerListener
		drawerLayout.setDrawerListener(actionBarDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true); 
		drawerListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int pos, long id)
			{
				//				switch(pos) {
				//				//TODO Iniciar sesion
				//				case 1:
				//					login.show();
				//					break;
				//					//TODO Actualizar mapa
				//				case 2:
				//					refresh.show();
				//					break;
				//					//TODO Actualizar mapa
				//				case 3:
				//					options.show();
				//					break;
				//				case 4:
				//					about.show();
				//					break;
				//				default:
				//				}
				drawerLayout.closeDrawer(drawerListView);
			}
		});
		// App Icon 
		drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mTitle = mDrawerTitle = getTitle();
		actionBarDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				drawerLayout,         /* DrawerLayout object */
				R.drawable.ic_launcher,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
				){

			/** Called when a drawer has settled in a completely closed state. */
			@Override
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		// Set actionBarDrawerToggle as the DrawerListener
		drawerLayout.setDrawerListener(actionBarDrawerToggle);
		drawerLayout.setMinimumWidth(new DisplayMetrics().widthPixels*50);
		drawerListView.setOnItemClickListener(new DrawerItemClickListener());
		getActionBar().setDisplayHomeAsUpEnabled(true);

		GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		System.out.println(GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()));
		map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		//para que solo muestre a bogota
		map.setMyLocationEnabled(true);
		//Desactiva la rotacion del mapa
		map.getUiSettings().setRotateGesturesEnabled(false);
		try {
			conexion = new Conexion(this);
			conexion.conectar();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		//Toasts de bienvenida e instrucciones
		Toast.makeText(this, "Bienvenido a WeReport",  Toast.LENGTH_SHORT).show();
		Toast bienvenida = Toast.makeText(this, "Toque una calle y luego la dirección para hacer un reporte",  Toast.LENGTH_LONG);
		bienvenida.show();

		//----------------------------------
		// Setup del click listener
		//----------------------------------
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			Marker marker;
			Polyline p;
			@Override
			public void onMapClick(LatLng point) {
				//Remueve el marcador si ya existe, y crea otro nuevo
				if(marker!=null)
					marker.remove();
				//TODO: Implementar Thread para que el mapa no se traba mientras encuentra la calle
				Toast.makeText(getApplicationContext(), "Identificando calle...", Toast.LENGTH_LONG).show();
				List<Address> a = getAddress(point); 
				if (a != null){
					BitmapDescriptor bmd = BitmapDescriptorFactory.fromResource(R.drawable.pointer);
					marker= map.addMarker(new MarkerOptions().position(point)
							.title(a.get(0).getAddressLine(0)).snippet(getAddress(point).get(0).getAddressLine(1)+" - "+getAddress(point).get(0).getAddressLine(2)).icon(bmd));
					marker.showInfoWindow();
					getAddress(point);

					if (p!=null)
						p.remove();
					p = crearPolyline(a, map);
				}
				else
				{
					Toast.makeText(getApplicationContext(), "Dirección no disponible para este punto", Toast.LENGTH_LONG).show();
				}

			}
		});


		//----------------------------------
		// Setup de la camara del mapa
		//----------------------------------
		map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition arg0) {
				// Mostrar Bogota cuando inicia
				map.moveCamera(CameraUpdateFactory.newLatLngBounds(BOGOTA, 10));
				// Remove listener to prevent position reset on camera move.
				map.setOnCameraChangeListener(null);
			}
		});


		//-----------------------------------
		// Creación del dialogo para reportar
		//-----------------------------------	

		Context mContext = this;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.spinner1,null);

		final Spinner s = (Spinner) layout.findViewById(R.id.spinner1);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.report_arrays, R.layout.spinner_item);
		s.setAdapter(adapter);		
		AlertDialog.Builder builder = new AlertDialog.Builder( new ContextThemeWrapper(this, android.R.style.Theme_Holo));



		builder.setView(layout);
		builder.setMessage("¿Qué pasó?")
		.setTitle("Reporte")	

		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				//Alerta para el rating del reporte
				final AlertDialog.Builder alert = new AlertDialog.Builder(contextForDialog);
				//Input text para el rating del reporte
				final EditText input = new EditText(getApplicationContext());		
				InputFilter[] filterArray = new InputFilter[1];
				filterArray[0] = new InputFilter.LengthFilter(3);
				input.setFilters(filterArray);
				input.setFilters(new InputFilter[]{ new InputFilterMinMax("1", "100")});
				input.setRawInputType(InputType.TYPE_CLASS_PHONE);

				alert.setTitle("De 1 a 100 califique el evento");
				if (input.getParent() == null) {
					alert.setView(input);
				} else {
					EditText a = null; //set it to null
					a=input;
					// now initialized yourView and its component again
					alert.setView(a);
				}
				int tipoReporteSpinner = s.getSelectedItemPosition();
				System.out.println("Indice: "+tipoReporteSpinner);
				String tipo = "";
				//
				switch(tipoReporteSpinner){
				case 0:
					tipo = ROBO;
					break;
				case 1:
					tipo = PANDILLAS;
					break;
				case 2:
					tipo = POLICIA;
					break;
				case 3:
					tipo = DROGAS;
					break;
				case 4:
					tipo = DISTURBIOS;
					break;
				case 5:
					tipo = LUZ;
					break;
				}
//				if(tipoReporteSpinner==0)
//				{
//					tipo = ROBO;
//				}
//				else if(tipoReporteSpinner==1)
//				{
//					tipo = PANDILLAS;
//				}
//				else if(tipoReporteSpinner==2)
//				{
//					tipo = POLICIA;
//				}
//				else if(tipoReporteSpinner==3)
//				{
//					tipo = DROGAS;
//				}
//				else if(tipoReporteSpinner==4)
//				{
//					tipo = DISTURBIOS;
//				}
//				else if(tipoReporteSpinner==5)
//				{
//					tipo = LUZ;
//				}
				final String tipof = tipo;
				alert.setPositiveButton(R.string.ok,new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						//ENVÍO DEL REPORTE
						Toast.makeText(getApplicationContext(), "Enviando reporte...", Toast.LENGTH_SHORT).show();
						System.out.println("La conexión es nula? "+conexion==null);
						if(conexion!=null) {
							//Conexion c = new Conexion(getApplicationContext());
							conexion.reportar(
									latLng1calleMarcada.latitude + "",
									latLng1calleMarcada.longitude + "",
									latLng2calleMarcada.latitude + "",
									latLng2calleMarcada.longitude + "", tipof,
									input.getText() + "", "Usuario-"
											+ phoneSerial);
							Toast.makeText(getApplicationContext(),
									"Se reportó la calle!", Toast.LENGTH_SHORT)
									.show();
						} 
						else {
							Toast.makeText(getApplicationContext(),	"No se pudo conectar al servidor. Intente más tarde", 
									Toast.LENGTH_LONG).show();
						}
					}
				});
				alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();

					}
				});
				alert.create();
				alert.show();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				dialog.cancel();

			}
		});
		final AlertDialog dialog = builder.create();

		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				dialog.show();

			}
		});

	}

	//CREAR POLYLINE METHOD
	private Polyline crearPolyline(List<Address> a2, GoogleMap map) {

		Address ad = a2.get(0);
		String address = ad.getAddressLine(0);
		System.out.println(address);

		if(address.contains("-"))
		{

		}

		//Saca "numCalle1 a numCalle2"
		String[] addressSplit = address.split("-");
		String addressA = null, addressB = null;
		Polyline p = null;


		try {

			//Saca "numCalle1" y "a numCalle2"
			String[] addressSplit2 = addressSplit[1].split(" a ");

			//Primera direccion para el polyline
			addressA=addressSplit[0]+"-"+addressSplit2[0]+", Bogotá";
			LatLng a = getLatLongFromAddress(addressA);
			latLng1calleMarcada = a;
			addressB=addressSplit[0]+"-"+addressSplit[2]+", Bogotá";
			LatLng b = getLatLongFromAddress(addressB);
			latLng2calleMarcada = b;


			System.out.println(addressA);
			System.out.println(addressB);
			p = map.addPolyline(new PolylineOptions().add(a,b).color(Color.BLUE));

		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "Procure tocar calles rectas", Toast.LENGTH_LONG).show();
		}

		return p;
	}

	/**
	 * Obtiene el LatLng de una dirección
	 * @param address La dirección a convertir
	 * @return Latitud y Longitud de la dirección
	 */
	private LatLng getLatLongFromAddress(String address)
	{
		LatLng p=null;
		Geocoder geoCoder = new Geocoder(this, Locale.getDefault());    
		try 
		{
			List<Address> addresses = geoCoder.getFromLocationName(address , 1);
			if (addresses.size() > 0) 
			{            
				p = new LatLng((addresses.get(0).getLatitude()),(addresses.get(0).getLongitude()));
				return p;
			}
		}
		catch(Exception e)
		{
			Toast.makeText(getApplicationContext(), "El servicio de Google Maps no se encuentra disponible.\n"
					+ "Intente más tarde.", Toast.LENGTH_LONG).show();
		}
		return p;
	}

	/**
	 * Obtiene la dirección para un LatLng
	 * @param point Latitud y longitud el punto cuya dirección se quiere encontrar
	 * @return La primera dirección encontrada para el punto
	 */
	public List<Address> getAddress(LatLng point) {
		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(this);
			if (point.latitude != 0 || point.longitude != 0) {
				addresses = geocoder.getFromLocation(point.latitude ,
						point.longitude, 1);
				String address = addresses.get(0).getAddressLine(0);
				String city = addresses.get(0).getAddressLine(1);
				String country = addresses.get(0).getAddressLine(2);
				System.out.println(address+" - "+city+" - "+country);

				return addresses;

			} else {
				Toast.makeText(this, "latitude and longitude are null",
						Toast.LENGTH_LONG).show();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void createPolylineFromLatLngs(final double lat1, final double lng1, final double lat2, final double lng2, final int c, final GoogleMap map)
	{
		MainActivity.this.runOnUiThread(new Runnable(){
		    public void run(){
		    	LatLng a  = new LatLng(lat1, lng1);
				LatLng b = new LatLng(lat2, lng2);
				Polyline p = map.addPolyline(new PolylineOptions().add(a,b).color(c));
				p.setVisible(true);
				System.out.println("Is the Polyline null? "+p==null);
				System.out.println("Creando PolyLine de coordenadas: 1="+lat1+","+lng1+" 2="+lat2+","+lng2);
		    }
		});
		
	}

	@Override
	public void onBackPressed()
	{
		try {
			conexion.desconectar();
			finish();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	} 
	
	public void toastErrorConexion()
	{
		Toast.makeText(getApplicationContext(), "Error al conectarse con el servidor.", Toast.LENGTH_SHORT).show();
	}
	
	public GoogleMap darMapa()
	{
		return map;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.map_refresh:
			Toast.makeText(getApplicationContext(), "Actualizando mapa...", Toast.LENGTH_LONG).show();
			conexion.enviarUbicacion(latLng1calleMarcada, latLng2calleMarcada);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@SuppressWarnings("rawtypes")
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			switch(position){
			case 1:
				break;
			};
			Toast.makeText(MainActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
			drawerLayout.closeDrawer(drawerListView);

		}

		//		private void iniciarSesion() {
		//			DialogoCuenta d = new DialogoCuenta(getApplicationContext());
		//			AlertDialog.Builder ad = new AlertDialog.Builder(getApplicationContext());
		//			ad.setTitle("");
		//		}
	}

}
