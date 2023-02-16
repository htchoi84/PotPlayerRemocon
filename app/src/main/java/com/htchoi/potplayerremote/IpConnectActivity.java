package com.htchoi.potplayerremote;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.pnikosis.materialishprogress.ProgressWheel;

public class IpConnectActivity extends BaseActivity {
	private final String TAG = "IpConnectActivity";
	public static final String KYCHT_REMOTE_PREFERENCES_NAME = "kycht_remote.preferences_name";
	public static final String KEY_REMOTE_IP = "kycht_remote.remoteactivity.IP";
	Context mContext;
	private EditText IP_edit_text;
	private InputMethodManager iManager;
	public static boolean isConnected = false;
	public static ProgressWheel mProgressWheel;
	private String edittext_IP;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ip_connect);

		mProgressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
		mProgressWheel.setBarColor(Color.BLUE);

		mContext = getApplicationContext();

		IP_edit_text = (EditText) findViewById(R.id.IP_Edittext);
		IP_edit_text.setText(getIP_Preferences()); // 최근에 접속했던 IP 읽어옴
	}


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
       @Override
       public void onReceive(Context context, Intent intent) {
           if(intent.getAction().equals("com.kycht.android_mouse_project.connect_OK")){
        	   Log.i(TAG, "Server Connect OK");
        	   isConnected = true;
        	   if(mProgressWheel.isShown())
				   mProgressWheel.stopSpinning();
   			   Toast.makeText(IpConnectActivity.this, "서버와 정상적으로 연결되었습니다. \nIP : " + edittext_IP,
					Toast.LENGTH_SHORT).show();
   			   //MainTabActivity.tabHost.setCurrentTab(1);  //tab 마우스 탭으로 옮김

			   Intent mintent =new Intent(mContext,MainActivity.class);
			   startActivity(mintent);

			   finish();

           }
       }
   };
   
	public Handler mHandler = new Handler() { // 핸들러 처리부분
		public void handleMessage(Message msg) { // 메시지를 받는부분
			switch (msg.what) { // 메시지 처리
			case 1:
				if (isConnected == false){
					Toast.makeText(IpConnectActivity.this, "서버 확인이 되지 않습니다. \nIP를 확인하세요.",
							Toast.LENGTH_SHORT).show();
				}
				Log.i(TAG, "Server Connect fail");
				if(mProgressWheel.isShown())
					mProgressWheel.stopSpinning();
				break;
			}
		};
	};
   
	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
		IntentFilter filter = new IntentFilter("com.kycht.android_mouse_project.connect_OK");
		registerReceiver(mReceiver, filter);
		iManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);  //키보드 히든 처리
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "onPause");
		// 키보드 숨김
		iManager.hideSoftInputFromWindow(IP_edit_text.getWindowToken(), 0);
		unregisterReceiver(mReceiver);
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();
	}

	public void ip_edit_click(View v) {
		Log.i(TAG, "ip_connect_click");
		// IP_edit_text.setText("");

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return super.onKeyUp(keyCode, event);
	}

	// connect버튼 눌렀을때 실행되는 콜백 메소드
	public void connect_btn_click(View v) {
		Log.i(TAG, "connect_btn_click");
		edittext_IP = IP_edit_text.getText().toString();
		if (checkIP(edittext_IP)) {
			Log.i(TAG, "connect_button_click edittext_IP : " + edittext_IP);

			setIP_Preferences(edittext_IP); // 다음에 접속시에 기억하도록 IP
											// 저장

			// Connect 버튼 클릭 후 키보드 숨김
			iManager.hideSoftInputFromWindow(IP_edit_text.getWindowToken(), 0);

			MainActivity.Socket_Thread = new Socket_Thread(edittext_IP , mContext);
			// MainActivity.Socket_Thread.start();
			//progressdialog = ProgressDialog.show(this, "", "서버 확인 중입니다..");
			isConnected = false;

			mProgressWheel.spin();

			if (MainActivity.Socket_Thread != null)
				MainActivity.Socket_Thread.Send_ConnectCMD();
			
			mHandler.sendEmptyMessageDelayed(1,7000);
			

		} else {
			Toast.makeText(IpConnectActivity.this, "정확한 IP 주소를 입력해 주세요.",
					Toast.LENGTH_SHORT).show();
		}
	}


	public String getIP_Preferences() {
		SharedPreferences IP_preference = this.getSharedPreferences(
				KYCHT_REMOTE_PREFERENCES_NAME, 0);
		return (String) IP_preference.getString(KEY_REMOTE_IP, "192.168.1.95");
	}

	public void setIP_Preferences(String value) {
		SharedPreferences settings = this.getSharedPreferences(
				KYCHT_REMOTE_PREFERENCES_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(KEY_REMOTE_IP, value);
		editor.commit();
	}

	public boolean checkIP(String IP) {
		Pattern p = Pattern
				.compile("([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})");
		Matcher m = p.matcher(IP);
		return m.matches();
	}

}
