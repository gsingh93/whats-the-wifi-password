package com.gulshansingh.whatsthewifipassword;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
			NetworkInterface.updatePassword(context);
	}
}
