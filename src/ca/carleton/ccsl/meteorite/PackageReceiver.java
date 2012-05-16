package ca.carleton.ccsl.meteorite;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ca.carleton.ccsl.meteorite.UAppIDUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;


public class PackageReceiver extends BroadcastReceiver {
	private static final String TAG = "Meteorite-PackageReceiver";
	String appName;
	@Override
	public void onReceive(Context context, Intent intent) {
		String pkgName = intent.getDataString().substring(8); //Intent data formatted as "package:com.example.package"
		Log.v(TAG, "Received a package intent for package " + pkgName);
		Context ctx = context;
		try {
			appName = ctx.getPackageManager().getApplicationInfo(pkgName, 0).name;
		} catch (NameNotFoundException e) {
			Log.v(TAG, "Error finding package info");
			e.printStackTrace();
		}
		String uAppID = UAppIDUtils.getUAppID(pkgName);
		Log.v(TAG, "UAppID: "+ uAppID);
		context = ctx.getApplicationContext();
		//CharSequence text = "New package: "+pkgName+" \nUAppID:ed13a07f1286...0bebe443b846"+"\n(Querying servers...)";
		CharSequence text = "New package: "+pkgName;
		int duration = 10;

		Toast toast = Toast.makeText(context, text, duration);
		toast.show();
	}

}
