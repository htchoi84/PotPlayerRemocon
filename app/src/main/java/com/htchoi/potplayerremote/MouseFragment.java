package com.htchoi.potplayerremote;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

/**
 * Created by htchoi on 2017-06-21.
 */

public class MouseFragment extends Fragment {
    private final String TAG = "MouseActivity";
    private short touchedX, touchedY;
    private short beforeX = 0, beforeY = 0;
    private int sendXpoint, sendYpoint;
    private View touchpad_view;
    private String action_str;
    private String subCMD_str;
    private short Mouse_Sensitivity;
    private short before_Sensitivity;
    private DiscreteSeekBar Sensitivity_seekBar;

    private View mouse_wheel_view;
    private Button LeftButton;
    private Button RightButton;

    private Vibrator mVibrator;

    // subCMD //fragment_mouse-CMD
    public static byte TOUCH = 0x01;
    public static byte LBUTTON = 0x02;
    public static byte RBUTTON = 0x03;
    public static byte KEYBOARD = 0x04;
    public static byte SENSITIVITY = 0x05;
    public static byte MOUSE_WHEEL = 0x06;

    // action //fragment_mouse-CMD
    public static byte DOWN = 0x01;
    public static byte MOVE = 0x02;
    public static byte UP = 0x03;

    public static final String KYCHT_REMOTE_PREFERENCES_NAME = "kycht_remote.preferences_name";
    public static final String KEY_MOUSE_SENSITIVITY = "kycht_remote.mouseactivity.sensitivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.fragment_mouse, container, false);

        // seekbar
        Mouse_Sensitivity = (short) getSensitivity_Preferences(); // ???????????????
        // preference??? ????????? ?????? ????????? ???????????? = 4
        before_Sensitivity = Mouse_Sensitivity; // ???????????? ?????? before??? current ????????????
        Sensitivity_seekBar = (DiscreteSeekBar) layout.findViewById(R.id.Sensitivity_seekBar);
        Sensitivity_seekBar.setOnProgressChangeListener(Sensityvity_OnSeekBarChangeListener);
        Sensitivity_seekBar.setProgress(Mouse_Sensitivity - 1);

        // setting

        touchpad_view = (View) layout.findViewById(R.id.touchpad_view);
        touchpad_view.setOnTouchListener(TouchpadView_TouchListener);
        LeftButton = (Button) layout.findViewById(R.id.touchpad_left_button);
        LeftButton.setOnTouchListener(LeftButton_TouchListener);
        RightButton = (Button) layout.findViewById(R.id.touchpad_right_button);
        RightButton.setOnTouchListener(RightButton_TouchListener);

        mouse_wheel_view = (View) layout.findViewById(R.id.mouse_wheel_view);
        mouse_wheel_view.setOnTouchListener(MouseWheel_TouchListener);
        mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        Button keyboardBtn = (Button) layout.findViewById(R.id.btn_keyboard);
        keyboardBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mintent =new Intent(getActivity(),keyboard_Activity.class);
                startActivity(mintent);
            }
        });

        return layout;
    }


    public void startVibrate() {
        mVibrator.vibrate(100);  //1?????????
        /*long pattern[] = {0, 5000};
        mVibrator.vibrate(pattern, 0);
        try {
            Thread.sleep(100);
            mVibrator.cancel();
        } catch (Exception e) {
        }*/
    }

    private View.OnTouchListener TouchpadView_TouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            touchedX = (short) event.getX();
            touchedY = (short) event.getY();
            Log.i(TAG, "onTouchEvent touchedX : " + touchedX);
            Log.i(TAG, "onTouchEvent touchedY : " + touchedY);

            Log.i(TAG, "onTouchEvent getHeight : " + v.getHeight());
            Log.i(TAG, "onTouchEvent getWidth : " + v.getWidth());

            if (touchedX < 5 || touchedY < 5 || touchedX > v.getWidth() - 5
                    || touchedY > v.getHeight() - 5) {
                Log.i(TAG, "onTouchEvent return touchedX : " + touchedX);
                Log.i(TAG, "onTouchEvent return touchedY : " + touchedY);
                return false;
            }

            if (action == MotionEvent.ACTION_DOWN) {
                Log.i(TAG, "TouchpadView_TouchListener ACTION_DOWN");

                beforeX = touchedX; // ????????? ????????? ?????? ????????? ????????????.
                beforeY = touchedY;

                if (MainActivity.Socket_Thread != null) {
                    MainActivity.Socket_Thread.Send_MouseCMD("TOUCH",
                            "DOWN", touchedX, touchedY, (short) 0); // ?????? ????????? ??????

                    touchpad_view.setBackgroundResource(R.drawable.btn_background_click);
                }
                else
                    Toast.makeText(getActivity(), "Connect?????? ???????????????.",
                            Toast.LENGTH_SHORT).show();
            } else if (action == MotionEvent.ACTION_UP) {
                Log.i(TAG, "TouchpadView_TouchListener ACTION_UP");

                touchpad_view.setBackgroundResource(R.drawable.btn_background);

                if (MainActivity.Socket_Thread != null) {
                    MainActivity.Socket_Thread.Send_MouseCMD("TOUCH", "UP", touchedX, touchedY, (short) 0); // ?????? ????????? ??????
                }
            } else if (action == MotionEvent.ACTION_MOVE) {
                Log.i(TAG, "TouchpadView_TouchListener ACTION_MOVE");

                sendXpoint = touchedX - beforeX; // ?????? ?????? - ?????? ??????
                sendYpoint = touchedY - beforeY;

                if (MainActivity.Socket_Thread == null)
                    return true;

                if (sendXpoint > 100 || sendYpoint > 100 || sendXpoint < -100
                        || sendYpoint < -100)
                    return true;

                beforeX = touchedX; // ?????? ????????? ?????? ????????? ??????(????????? ???????????? ?????????)
                beforeY = touchedY;

                // ????????? ?????? ?????? ????????? ??????
                if (Mouse_Sensitivity == 7) // ???????????? + 3
                    MainActivity.Socket_Thread.Send_MouseCMD("TOUCH",
                            "MOVE", (short) (sendXpoint * 4),
                            (short) (sendYpoint * 4), (short) 0);
                else if (Mouse_Sensitivity == 6) // ???????????? + 2
                    MainActivity.Socket_Thread.Send_MouseCMD("TOUCH",
                            "MOVE", (short) (sendXpoint * 3),
                            (short) (sendYpoint * 3), (short) 0);
                else if (Mouse_Sensitivity == 5) // ???????????? + 1
                    MainActivity.Socket_Thread.Send_MouseCMD("TOUCH",
                            "MOVE", (short) (sendXpoint * 2),
                            (short) (sendYpoint * 2), (short) 0);
                else if (Mouse_Sensitivity == 4) // ????????????
                    MainActivity.Socket_Thread.Send_MouseCMD("TOUCH",
                            "MOVE", (short) (sendXpoint), (short) (sendYpoint),
                            (short) 0);
                else if (Mouse_Sensitivity == 3) // ?????? ?????? -1
                    MainActivity.Socket_Thread.Send_MouseCMD("TOUCH",
                            "MOVE", (short) (sendXpoint / 2),
                            (short) (sendYpoint / 2), (short) 0);
                else if (Mouse_Sensitivity == 2) // ???????????? -2
                    MainActivity.Socket_Thread.Send_MouseCMD("TOUCH",
                            "MOVE", (short) (sendXpoint / 3),
                            (short) (sendYpoint / 3), (short) 0);
                else if (Mouse_Sensitivity == 1) // ???????????? -3
                    MainActivity.Socket_Thread.Send_MouseCMD("TOUCH",
                            "MOVE", (short) (sendXpoint / 4),
                            (short) (sendYpoint / 4), (short) 0);

            }

            return true;
        }
    };

    // ????????? ??? ????????? ???
    private View.OnTouchListener MouseWheel_TouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            touchedX = (short) event.getX();
            touchedY = (short) event.getY();
            Log.i(TAG, "onTouchEvent touchedX : " + touchedX);
            Log.i(TAG, "onTouchEvent touchedY : " + touchedY);

            Log.i(TAG, "onTouchEvent getHeight : " + v.getHeight());
            Log.i(TAG, "onTouchEvent getWidth : " + v.getWidth());

            if (touchedX < 0 || touchedY < 0 || touchedX > v.getWidth()
                    || touchedY > v.getHeight()) {
                Log.i(TAG, "onTouchEvent return touchedX : " + touchedX);
                Log.i(TAG, "onTouchEvent return touchedY : " + touchedY);
                mouse_wheel_view
                        .setBackgroundResource(R.drawable.btn_background);
                return false;
            }

            if (action == MotionEvent.ACTION_DOWN) {
                Log.i(TAG, "MouseWheel_TouchListener ACTION_DOWN");
                //playSound_button();
                startVibrate();
                mouse_wheel_view
                        .setBackgroundResource(R.drawable.btn_background_click);

                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_MouseCMD("MOUSEWHEEL",
                            "DOWN", (short) 0, touchedY, (short) 0); // ?????? ?????????
                    // ??????
                else
                    Toast.makeText(getActivity(), "Connect?????? ???????????????.",
                            Toast.LENGTH_SHORT).show();
            } else if (action == MotionEvent.ACTION_UP) {
                Log.i(TAG, "MouseWheel_TouchListener ACTION_UP");
                mouse_wheel_view
                        .setBackgroundResource(R.drawable.btn_background);

                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_MouseCMD("MOUSEWHEEL",
                            "UP", (short) 0, touchedY, (short) 0); // ?????? ????????? ??????
            } else if (action == MotionEvent.ACTION_MOVE) {
                // startVibrate();
                Log.i(TAG, "MouseWheel_TouchListener ACTION_MOVE");
                mouse_wheel_view
                        .setBackgroundResource(R.drawable.btn_background_click);

                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_MouseCMD("MOUSEWHEEL",
                            "MOVE", (short) 0, touchedY, (short) 0); // ?????? ?????????
                // ??????
            }

            return true;
        }
    };

    // ????????? ?????? ?????? ???????????? ?????????
    private View.OnTouchListener LeftButton_TouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                //playSound_button();
                startVibrate();
                Log.i(TAG, "LeftButton_TouchListener ACTION_DOWN");

                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_MouseCMD("LBUTTON",
                            "DOWN", (short) 0, (short) 0, (short) 0); // ?????? ?????????
                    // ??????
                else
                    Toast.makeText(getActivity(), "Connect?????? ???????????????.",
                            Toast.LENGTH_SHORT).show();
            } else if (action == MotionEvent.ACTION_UP) {
                Log.i(TAG, "LeftButton_TouchListener ACTION_UP");
                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_MouseCMD("LBUTTON",
                            "UP", (short) 0, (short) 0, (short) 0); // ?????? ????????? ??????
            } else if (action == MotionEvent.ACTION_MOVE) {
            }
            return false;
        }
    };

    // ????????? ????????? ?????? ???????????? ?????????
    private View.OnTouchListener RightButton_TouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                //playSound_button();
                startVibrate();
                Log.i(TAG, "RightButton_TouchListener ACTION_DOWN");
                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_MouseCMD("RBUTTON",
                            "DOWN", (short) 0, (short) 0, (short) 0); // ?????? ?????????
                    // ??????
                else
                    Toast.makeText(getActivity(), "Connect?????? ???????????????.",
                            Toast.LENGTH_SHORT).show();
            } else if (action == MotionEvent.ACTION_UP) {
                Log.i(TAG, "RightButton_TouchListener ACTION_UP");
                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_MouseCMD("RBUTTON",
                            "UP", (short) 0, (short) 0, (short) 0); // ?????? ????????? ??????
            } else if (action == MotionEvent.ACTION_MOVE) {
            }
            return false;
        }
    };

    private DiscreteSeekBar.OnProgressChangeListener Sensityvity_OnSeekBarChangeListener = new DiscreteSeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            Log.i(TAG, "Seek bar progress : " + value);
            Mouse_Sensitivity = (short) (value + 1);

            if (before_Sensitivity != Mouse_Sensitivity) // sensitivity ?????? ???????????????
                // ?????? sound ??????
                //playSound_Sensitivity();
                startVibrate();

            setSensitivity_Preferences(Mouse_Sensitivity); // ?????? ??????
            before_Sensitivity = Mouse_Sensitivity;
        }

        @Override
        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

        }
    };

    /*
    private SeekBar.OnSeekBarChangeListener Sensityvity_OnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        // thumb ??? ???????????? ???????????? ?????????
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        // thumb ??? ???????????? ???????????? ?????????
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        // thumb ??? ????????? ????????? ????????? ?????? ?????????
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            Log.i(TAG, "Seek bar progress : " + progress);
            Mouse_Sensitivity = (short) (progress + 1);

            if (before_Sensitivity != Mouse_Sensitivity) // sensitivity ?????? ???????????????
                // ?????? sound ??????
                playSound_Sensitivity();

            setSensitivity_Preferences(Mouse_Sensitivity); // ?????? ??????
            before_Sensitivity = Mouse_Sensitivity;
        }
    };*/

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        // ????????? ??????
        super.onPause();
    }


    // touchpad ???????????? ???????????? ?????? ?????????
    public void touchpad_main_click(View v) {
        Log.i(TAG, "touchpad_main_click");
    }


	/*
     * @Override public boolean onKeyUp(int keyCode, KeyEvent event) { if
	 * (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) { Log.i(TAG,
	 * "onKeyUp KEYCODE_VOLUME_DOWN"); return true; } else if (keyCode ==
	 * KeyEvent.KEYCODE_VOLUME_UP) { Log.i(TAG, "onKeyUp KEYCODE_VOLUME_UP");
	 * return true; } return super.onKeyUp(keyCode, event); }
	 */

    public int getSensitivity_Preferences() {
        SharedPreferences Sensitivity_preference = getActivity().getSharedPreferences(
                KYCHT_REMOTE_PREFERENCES_NAME, 0);
        return (int) Sensitivity_preference.getInt(KEY_MOUSE_SENSITIVITY, 4);
    }

    public void setSensitivity_Preferences(int value) {
        SharedPreferences settings = getActivity().getSharedPreferences(
                KYCHT_REMOTE_PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_MOUSE_SENSITIVITY, value);
        editor.commit();
    }

}
