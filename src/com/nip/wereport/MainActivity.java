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
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nip.protectblock.R;

public class MainActivity extends FragmentActivity {

	private LatLngBounds BOGOTA = new LatLngBounds(new LatLng(4.59354,-74.26964), new LatLng(4.79952,-73.98262));


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

		final GoogleMap map = ((SupportMapFragment)  getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		//para que solo muestre a bogota

		map.setMyLocationEnabled(true);
		//Desactiva la rotacion del mapa
		map.getUiSettings().setRotateGesturesEnabled(false);
		System.out.println("Duracion: "+Toast.LENGTH_LONG);
		Toast.makeText(this, "Bienvenido a ProtectBlock",  Toast.LENGTH_LONG).show();
		Toast bienvenida = Toast.makeText(this, "Toque una calle y luego la direccion para hacer un reporte",  Toast.LENGTH_LONG);
		bienvenida.show();
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			Marker marker;
			Polyline p;
			@Override
			public void onMapClick(LatLng point) {
				//Remueve el marcador si ya existe, y crea otro nuevo
				if(marker!=null)
					marker.remove();
				List<Address> a = getAddress(point); 
				if (a != null){
					marker= map.addMarker(new MarkerOptions().position(point)
							.title(a.get(0).getAddressLine(0)).snippet(getAddress(point).get(0).getAddressLine(1)+" - "+getAddress(point).get(0).getAddressLine(2)));
					marker.showInfoWindow();
					getAddress(point);
					//
					if (p!=null)
						p.remove();
					p = crearPolyline(point, map);
				}else{
					Toast.makeText(getApplicationContext(), "Direccion no disponible para este punto", Toast.LENGTH_LONG).show();
				}
				
			}
		});

		map.setOnCameraChangeListener(new OnCameraChangeListener() {

			@Override
			public void onCameraChange(CameraPosition arg0) {
				// Mostrar Bogota cuando inicia
				map.moveCamera(CameraUpdateFactory.newLatLngBounds(BOGOTA, 10));
				// Remove listener to prevent position reset on camera move.
				map.setOnCameraChangeListener(null);
			}
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder( new ContextThemeWrapper(this, android.R.style.Theme_Holo));
		builder.setMessage("Seleccionar motivo")
				.setTitle("Reporte")	
		
				.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				               // User clicked OK button
				           }
				       })
					.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
	
				        	   dialog.cancel();
				        	   
				           }
				       });
		
		Context mContext = this;
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.spinner1,null);
		
		Spinner s = (Spinner) layout.findViewById(R.id.spinner1);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.report_arrays, R.layout.spinner_item);		
		s.setAdapter(adapter);
		
		builder.setView(layout);
							    
		final AlertDialog dialog = builder.create();
		
		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

	        public void onInfoWindowClick(Marker marker) {
	            dialog.show();

	        }
	    });
		
	}

	private Polyline crearPolyline(LatLng point, GoogleMap map) {
		
		List<Address> adlist = getAddress(point);
		Address ad = adlist.get(0);
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

			//Primera direcciï¿½n para el polyline
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
			e.printStackTrace();
		}
		return p;
	}

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

}
