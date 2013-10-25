package com.nip.wereport;

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
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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

	private String[] drawerListViewItems;
	private DrawerLayout drawerLayout;
	private ListView drawerListView;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	//-----------------------------------
	// Constructor del fragment
	//-----------------------------------
	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final Toast login = Toast.makeText(this, "No se ha implementado el login", Toast.LENGTH_SHORT);
		final Toast refresh = Toast.makeText(this, "No se ha implementado el refresh", Toast.LENGTH_SHORT);
		final Toast options = Toast.makeText(this, "No se ha implementado el refresh", Toast.LENGTH_SHORT);
		final Toast about = Toast.makeText(this, "No se ha implementado el refresh", Toast.LENGTH_SHORT);

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
				switch(pos) {
				//TODO Iniciar sesion
				case 1:
					login.show();
					break;
					//TODO Actualizar mapa
				case 2:
					refresh.show();
					break;
					//TODO Actualizar mapa
				case 3:
					options.show();
					break;
				case 4:
					about.show();
					break;
				default:
				}
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

		final GoogleMap map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		//para que solo muestre a bogota
		map.setMyLocationEnabled(true);
		//Desactiva la rotacion del mapa
		map.getUiSettings().setRotateGesturesEnabled(false);

		//Toasts de bienvenida e instrucciones
		Toast.makeText(this, "Bienvenido a WeReport",  Toast.LENGTH_LONG).show();
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
					//
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
		AlertDialog.Builder builder = new AlertDialog.Builder( new ContextThemeWrapper(this, android.R.style.Theme_Holo));
		builder.setMessage("¿Qué pasó?")
		.setTitle("Reporte")	

		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(getApplicationContext(), "Se reportó la calle!", Toast.LENGTH_LONG).show();
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				dialog.cancel();

			}
		});
		//-------------------------------------
		// Creación del spinner para el dialogo
		//-------------------------------------
		Context mContext = this;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.spinner1,null);

		Spinner s = (Spinner) layout.findViewById(R.id.spinner1);
		System.out.println(s==null);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.report_arrays, R.layout.spinner_item);
		s.setAdapter(adapter);

		builder.setView(layout);

		final AlertDialog dialog = builder.create();

		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				dialog.show();

			}
		});

	}

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
			addressB=addressSplit[0]+"-"+addressSplit[2]+", Bogotá";
			LatLng b = getLatLongFromAddress(addressB);


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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @SuppressWarnings("rawtypes")
		@Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            Toast.makeText(MainActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
            drawerLayout.closeDrawer(drawerListView);
 
        }
    }

}
