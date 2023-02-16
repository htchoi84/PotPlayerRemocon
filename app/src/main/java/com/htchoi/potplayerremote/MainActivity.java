package com.htchoi.potplayerremote;

/**
 * Created by htchoi on 2017-06-21.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.htchoi.potplayerremote.utils.AbBackPressCloseHandler;

public class MainActivity extends FragmentActivity{
    private final String TAG = "MainActivity";

    private final int INDEX_REMOCON = 0;
    private final int INDEX_TOUCHPAD = 1;

    private int tabIndex = INDEX_REMOCON;

    private Button tabReomocon;
    private Button tabTouchPad;

    private RemoconFragment mRemoconFragment = new RemoconFragment();
    private MouseFragment mMouseFragment = new MouseFragment();


    public static Socket_Thread Socket_Thread;

    public AbBackPressCloseHandler mAbBackPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //화면 안꺼지게..
        setContentView(R.layout.activity_main);

        showNetworkInfo(); // 3G/4G인지 WIFI 연결상태인지 확인후 다이얼로그 띄움

        // 위젯에 대한 참조
        tabReomocon = (Button)findViewById(R.id.tab_remocon);
        tabTouchPad = (Button)findViewById(R.id.tab_touch_pad);

        // 탭 버튼에 대한 리스너 연결
        tabReomocon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //리모컨 프래그먼트 실행
                tabReomocon.setSelected(true);
                tabTouchPad.setSelected(false);
                callFragment(INDEX_REMOCON);
            }
        });
        tabTouchPad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //터치패드 프래그먼트 실행
                tabReomocon.setSelected(false);
                tabTouchPad.setSelected(true);
                callFragment(INDEX_TOUCHPAD);
            }
        });

        mAbBackPressCloseHandler = new AbBackPressCloseHandler(this) {
            @Override
            protected int getDelayTime() {
                return 2000;
            }

            @Override
            protected String getToastMessage() {
                return "한번 더 뒤로가기를 누르면 종료됩니다";
            }
        };

        //리모큰을 디폴트로 실행
        tabReomocon.setSelected(true);
        tabTouchPad.setSelected(false);
        callFragment(INDEX_REMOCON);
    }

    @Override
    protected void onDestroy() {
        if (Socket_Thread != null) {
            Log.i(TAG, "Socket_Thread.isAlive : " + Socket_Thread.isAlive());
            Socket_Thread.Close_Socket();
            if (Socket_Thread.isAlive())
                Socket_Thread.stopThread();

            Socket_Thread = null;
        }
        IpConnectActivity.isConnected = false;

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mAbBackPressCloseHandler.onBackPressed();  //2번 클릭시 종료되도록..
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //리모컨 프래그먼트로 키이벤트 전달
        if(tabIndex == INDEX_REMOCON){
            mRemoconFragment.onKeyDown(keyCode, event);
        }

        return super.onKeyDown(keyCode, event);
    }

    private void callFragment(int frament_no){
        if (IpConnectActivity.isConnected == false) { // 연결이 안된 상황이면
            Toast.makeText(this, "Connect 후에 진행하세요!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        tabIndex = INDEX_REMOCON;

        // 프래그먼트 사용을 위해
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (frament_no){
            case INDEX_REMOCON:
                // '리모컨 프래그먼트' 호출
                transaction.replace(R.id.fragment_container, mRemoconFragment);
                transaction.commit();
                break;

            case INDEX_TOUCHPAD:
                // '터치패드 프래그먼트' 호출
                transaction.replace(R.id.fragment_container, mMouseFragment);
                transaction.commit();

                // '파워포인트 프래그먼트' 호출
                /*PowerPointFragment fragment2 = new PowerPointFragment();
                transaction.replace(R.id.fragment_container, fragment2);
                transaction.commit();*/
                break;
        }

    }

    public void showNetworkInfo() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // 3G
        boolean is3g4g = false;
        boolean isWifi = false;
        try{
            is3g4g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
            // WIFI
            isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                    .isConnectedOrConnecting();
        }catch(NullPointerException e){
            e.printStackTrace();
        }

        if (is3g4g) {
            // 3G/4G연결 중일때
            // Toast.makeText(this, "3G/4G 연결 중입니다. wifi 연결하세요.",
            // Toast.LENGTH_SHORT)
            // .show();
            AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
            alertDlg.setMessage("3G/4G 연결 중입니다.\n테더링으로 사용 중이 아니라면 \nwifi에 연결하고 실행하세요!!");
            alertDlg.setCancelable(false);  //back key 막기
            alertDlg.setPositiveButton("닫기",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int whichButton) {
                            //finish();
                        }
                    });
            alertDlg.show();
        } else if (isWifi) {
            // WIF 연결중일때
        } else {
        }
    }
}
