package com.htchoi.potplayerremote;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

/**
 * Created by htchoi on 2017-06-21.
 */

public class RemoconFragment extends Fragment {
    private final String TAG = "remote_control";

    //sub cmd - 각 동영상 플레이어 정의 값..
    private static byte SELECTED_REMOTE;
    private static byte GOMPLAYER = 0x01;
    private static byte ALSHOW = 0x02;
    private static byte WINMEDIAPLAYER = 0x03;
    private static byte POTPLAYER = 0x04;
    //private static byte KMPP = 0x04;
    //private static byte WINDOW = 0x06;

    //action
    private static byte POWER_BTN = 0x00;

    private static byte PLAY_BTN = 0x01;

    private static byte OK_BTN = 0x02;

    private static byte UP_BTN = 0x03;

    private static byte DOWN_BTN = 0x04;

    private static byte RIGHT_BTN = 0x05;

    private static byte LEFT_BTN = 0x06;

    private static byte STOP_BTN = 0x07;

    private static byte FF_BTN = 0x08;

    private static byte RW_BTN = 0x09;

    private static byte VOLUMEUP_BTN = 0x0a;

    private static byte VOLUMEDOWN_BTN = 0x0b;

    private static byte OPEN_BTN = 0x0c;

    private static byte MUTE_BTN = 0x0d;

    private static byte CANCEL_BTN = 0x0e;

    private static byte TAB_BTN = 0x0f;

    Context mContext;

    private Vibrator mVibrator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.fragment_remocon, container, false);

        mContext = getContext();
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        init(layout);
        SELECTED_REMOTE = POTPLAYER;

        return layout;
    }

    public void init(View view) {

        ImageButton imageButtonRW = (ImageButton) view.findViewById(R.id.rw_Button);
        imageButtonRW.setOnClickListener(imageButtonRWClickListener);

        ImageButton imageButtonPlay = (ImageButton) view.findViewById(R.id.play_Button);
        imageButtonPlay.setOnClickListener(imageButtonPlayClickListener);

        ImageButton imageButtonStop = (ImageButton) view.findViewById(R.id.stop_Button);
        imageButtonStop.setOnClickListener(imageButtonStopClickListener);

        ImageButton imageButtonFF = (ImageButton) view.findViewById(R.id.ff_Button);
        imageButtonFF.setOnClickListener(imageButtonFFClickListener);

        ImageButton imageButtonPower = (ImageButton) view.findViewById(R.id.power_Button);
        imageButtonPower.setOnClickListener(imageButtonPowerClickListener);

        ImageButton imageButtonUp = (ImageButton) view.findViewById(R.id.up_Button);
        imageButtonUp.setOnClickListener(imageButtonUpClickListener);

        ImageButton imageButtonDown = (ImageButton) view.findViewById(R.id.down_Button);
        imageButtonDown.setOnClickListener(imageButtonDownClickListener);

        ImageButton imageButtonLeft = (ImageButton) view.findViewById(R.id.left_Button);
        imageButtonLeft.setOnClickListener(imageButtonLeftClickListener);

        ImageButton imageButtonRight = (ImageButton) view.findViewById(R.id.right_Button);
        imageButtonRight.setOnClickListener(imageButtonRightClickListener);

        ImageButton imageButtonOK = (ImageButton) view.findViewById(R.id.ok_Button);
        imageButtonOK.setOnClickListener(imageButtonOKClickListener);

        ImageButton imageButtonOpen = (ImageButton) view.findViewById(R.id.open_Button);
        imageButtonOpen.setOnClickListener(imageButtonOPENClickListener);

        ImageButton imageButtonMute = (ImageButton) view.findViewById(R.id.mute_Button);
        imageButtonMute.setOnClickListener(imageButtonMUTEClickListener);

        ImageButton imageButtonCancel = (ImageButton) view.findViewById(R.id.cancel_Button);
        imageButtonCancel.setOnClickListener(imageButtonCANCELClickListener);

        ImageButton imageButtonTab = (ImageButton) view.findViewById(R.id.tab_Button);
        imageButtonTab.setOnClickListener(imageButtonTABClickListener);
    }

    public void startVibrate() {
        if(mVibrator != null)
            mVibrator.vibrate(100);
    }

    OnClickListener imageButtonRWClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "RW_button onClick");
            startVibrate();
            // MainActivity.Socket_Thread.SendData("remote-cmd+RW");
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, RW_BTN);
        }
    };

    OnClickListener imageButtonPlayClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Play_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, PLAY_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();
        }
    };

    OnClickListener imageButtonStopClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Stop_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, STOP_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();
        }
    };

    OnClickListener imageButtonFFClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "FF_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, FF_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();
        }
    };

    OnClickListener imageButtonPowerClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "POWER_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, POWER_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();
        }
    };

    OnClickListener imageButtonUpClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Up_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, UP_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();
        }
    };

    OnClickListener imageButtonDownClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Down_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, DOWN_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();
        }
    };

    OnClickListener imageButtonLeftClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Left_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, LEFT_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();
        }
    };

    OnClickListener imageButtonRightClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Right_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, RIGHT_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();
        }
    };

    OnClickListener imageButtonOKClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "OK_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, OK_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();

        }
    };

    OnClickListener imageButtonOPENClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "OPEN_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, OPEN_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();

        }
    };

    OnClickListener imageButtonMUTEClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "MUTE_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, MUTE_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();

        }
    };

    OnClickListener imageButtonCANCELClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "CANCEL_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, CANCEL_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();

        }
    };

    OnClickListener imageButtonTABClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "TAB_button onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, TAB_BTN);
            else
                Toast.makeText(mContext, "Connect하고 진행하세요.", Toast.LENGTH_SHORT)
                        .show();

        }
    };

    /*OnItemSelectedListener remote_ListItemSelectedListener = new OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int positon,
                                   long id) {
            Log.i(TAG, "Remote List Item Selseted : "+id);
            switch(positon){
                case 0: // 곰 플레이어
                    SELECTED_REMOTE = GOMPLAYER;
                    return;
                case 1:
                    SELECTED_REMOTE = ALSHOW;
                    return;
                case 2:
                    SELECTED_REMOTE = WINMEDIAPLAYER;
                    return;
                case 3:
                    SELECTED_REMOTE = DAUMPOTPLAYER;
                    return;
                default :
                    return;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
        }
    };*/

    /** 액티비티에서 프래그먼트로 키이벤트 전달되는 함수 */
    public void onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            Log.i(TAG, "onKeyDown KEYCODE_VOLUME_UP");
            if (MainActivity.Socket_Thread != null) {
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, VOLUMEUP_BTN);
            }

        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            Log.i(TAG, "onKeyDown KEYCODE_VOLUME_DOWN");
            if (MainActivity.Socket_Thread != null) {
                MainActivity.Socket_Thread.Send_RemoteCMD(SELECTED_REMOTE, VOLUMEDOWN_BTN);
            }
        }
    }
}
