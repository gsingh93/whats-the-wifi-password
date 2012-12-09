package com.gulshansingh.whatsthewifipassword;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.gulshansingh.whatsthewifipassword.MainActivity.AsyncTaskCompleteListener;

public class NetworkInterface {

	private static class GetPasswordTask extends AsyncTask<Void, Void, String> {

		private static final String URL = "http://www.gulshansingh.com/dev/whatsthewifipassword/wifipass.txt";

		private Context context;
		private AsyncTaskCompleteListener<String> listener;

		public GetPasswordTask(Context context,
				AsyncTaskCompleteListener<String> l) {
			this.context = context;
			this.listener = l;
		}

		@Override
		protected String doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(URL);

			String password = null;
			try {
				HttpResponse response = client.execute(httpGet);

				if (response.getStatusLine().getStatusCode() == 200) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(response.getEntity()
									.getContent()));
					password = br.readLine();
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return password;
		}

		protected void onPostExecute(String password) {
			if (password != null) {
				SharedPreferences.Editor editor = PreferenceManager
						.getDefaultSharedPreferences(context).edit();
				editor.putString("password", password);
				editor.putLong("timestamp", System.currentTimeMillis());
				editor.commit();

				if (listener != null) {
					listener.onComplete(password);
				}
			}
		}
	}

	public static boolean updatePassword(Context context,
			AsyncTaskCompleteListener<String> l) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			new GetPasswordTask(context, l).execute();
		} else {
			return false;
		}
		return true;
	}

	public static boolean updatePassword(Context context) {
		return updatePassword(context, null);
	}
}
