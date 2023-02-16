package com.htchoi.potplayerremote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class IntroActivity extends Activity {
	private final String TAG = "IntroActivity";
	private Context mContext;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		mContext = getApplicationContext();
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.startview);
		mHandler.sendEmptyMessageDelayed(1,1500);

	}
	
	public Handler mHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(Message msg) {
			Intent mintent =new Intent(mContext,IpConnectActivity.class);
			mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    startActivity(mintent);

			finish();
		};
	};

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		super.onPause();

	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}
}
