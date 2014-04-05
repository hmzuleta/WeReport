package com.nip.wereport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class DialogoCuenta extends AlertDialog {
	private String user;
	
	private String pw;

	protected DialogoCuenta(Context context) {
		super(context);
		setTitle("Mi cuenta");
		setMessage("Ingrese su usuario");
		final EditText input = new EditText(context);
		setView(input);
		setButton(1, "Ok", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		});
	}

}
