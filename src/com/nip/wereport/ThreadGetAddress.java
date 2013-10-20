package com.nip.wereport;

import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import android.location.Address;


public class ThreadGetAddress extends Thread {

	private List<Address> adlist;
	
	private LatLng p;
	
	private MainActivity m;
	
	public ThreadGetAddress(MainActivity main, LatLng point)
	{
		p = point;
		m = main;
		
	}
	
	public void run()
	{
		adlist = m.getAddress(p);
	}
	
	public List<Address> getList()
	{
		return adlist;
	}
}
