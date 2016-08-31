package com.cgl.vrlauncher;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements OnClickListener {

	private Button btn1,btn2;
	private Service_1 service_1;
	private Service_2 service_2;
	private String TAG = getClass().getName();
	
	/*private ServiceConnection conn1 = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected");
			service_1=null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected");
			service_1 = Service_1.Stub.asInterface(service);
		}
	};
	
	private ServiceConnection conn2 = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "onServiceDisconnected");
			service_2=null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "onServiceConnected");
			service_2 = Service_2.Stub.asInterface(service);
		}
	};*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		try {
		    Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
		    intent.setData(Uri.parse("package:com.cgl.vrlauncher"));
		    startActivity(intent);

		} catch (ActivityNotFoundException e) {
		    e.printStackTrace();
		}
		
		setContentView(R.layout.main);
		
		btn1 = (Button) findViewById(R.id.button1);
		btn2 = (Button) findViewById(R.id.button2);
		
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
	
	}
	
	@Override
	protected void onDestroy() {
		//unbindService(conn1);
		//unbindService(conn2);
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.button1:
			Intent i1 = new Intent(Main.this,Service1.class);
			startService(i1);
			//bindService(i1, conn1, Context.BIND_AUTO_CREATE);
			
			Intent i2 = new Intent(Main.this,Service2.class);
			startService(i2);
			//bindService(i2, conn2, Context.BIND_AUTO_CREATE);
			break;

		case R.id.button2:
			Intent ii1 = new Intent(Main.this,Service1.class);
			stopService(ii1);
			//bindService(i1, conn1, Context.BIND_AUTO_CREATE);
			
			Intent ii2 = new Intent(Main.this,Service2.class);
			stopService(ii2);
			break;
		}
	}

}
