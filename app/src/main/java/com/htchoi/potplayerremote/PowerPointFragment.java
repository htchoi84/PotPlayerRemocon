package com.htchoi.potplayerremote;

/**
 * Created by htchoi on 2017-06-21.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
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

import androidx.fragment.app.Fragment;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class PowerPointFragment extends Fragment {
    private final String TAG = "PowerPointActivity";

    private SensorManager mSensorManager;

    private SensorEventListener mSensorEventListener;

    private Sensor mGyroscopeSensor;

    private View Sensor_mouse_view;

    private Button Lbutton_view;

    private Button Rbutton_view;

    private Button button_begin_slide;

    private Button button_current_slide;

    private Button button_end_slide;

    private Button button_before_page;

    private Button button_next_page;

    private DiscreteSeekBar Sensitivity_seekBar;

    boolean isSensorStart = false;

    private short Sensormouse_Sensitivity;
    private short before_Sensitivity;

    // subCMD //pptmode-CMD
    public static byte PPTMODE_BEGIN_SLIDE = 0x01;

    public static byte PPTMODE_CURRENT_SLIDE = 0x02;

    public static byte PPTMODE_END_SLIDE = 0x03;

    public static byte PPTMODE_BEFORE_PAGE = 0x04;

    public static byte PPTMODE_NEXT_PAGE = 0x05;

    public static byte PPTMODE_SENSORMOUSE = 0x06;

    public static byte PPTMODE_LBUTTON = 0x07;

    public static byte PPTMODE_RBUTTON = 0x08;

    // action
    public static byte DOWN = 0x01;

    public static byte MOVE = 0x02;

    public static byte UP = 0x03;

    public static final String KYCHT_REMOTE_PREFERENCES_NAME = "kycht_remote.preferences_name";

    public static final String KEY_PPT_SENSITIVITY = "kycht_remote.pptactivity.sensitivity";

    private SoundPool sound_pool;
    private int sound_button;
    private int sound_Sensitivity;
    float StreamVolume;
    private AudioManager audioMgr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout layout = (RelativeLayout)inflater.inflate(R.layout.fragment_powerpoint, container, false);
        init(layout);

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

    public void init(RelativeLayout layout) {
        Log.i(TAG, "init");
        Sensormouse_Sensitivity = (short) getSensitivity_Preferences(); // 센서마우스에서
        // 감도
        // 저장할
        // preference

        before_Sensitivity = Sensormouse_Sensitivity; // 최초에는 감도 before와 current
        // 동일하게

        Sensitivity_seekBar = (DiscreteSeekBar) layout.findViewById(R.id.Sensitivity_seekBar);
        Sensitivity_seekBar.setOnProgressChangeListener(Sensityvity_OnSeekBarChangeListener);
        Sensitivity_seekBar.setProgress(Sensormouse_Sensitivity - 1);

        Sensor_mouse_view = (View) layout.findViewById(R.id.sensor_mouse_btn);
        Sensor_mouse_view.setOnTouchListener(SensorMouse_btn_TouchListener);

        Lbutton_view = (Button) layout.findViewById(R.id.mouse_left_btn);
        Lbutton_view.setOnTouchListener(Leftdown_btn_TouchListener);

        Rbutton_view = (Button) layout.findViewById(R.id.mouse_right_btn);
        Rbutton_view.setOnTouchListener(Rightdown_btn_TouchListener);

        button_begin_slide = (Button) layout.findViewById(R.id.Begin_Slide_Button);
        button_begin_slide.setOnClickListener(Button_BeginSlide_ClickListener);

        button_current_slide = (Button) layout.findViewById(R.id.Current_Slide_Button);
        button_current_slide
                .setOnClickListener(Button_CurrentSlide_ClickListener);

        button_end_slide = (Button) layout.findViewById(R.id.End_Slide_Button);
        button_end_slide.setOnClickListener(Button_EndSlide_ClickListener);

        button_before_page = (Button) layout.findViewById(R.id.before_page_Button);
        button_before_page.setOnClickListener(Button_BeforePage_ClickListener);

        button_next_page = (Button) layout.findViewById(R.id.next_page_Button);
        button_next_page.setOnClickListener(Button_NextPage_ClickListener);
    }

    /*private void initSound_button() {
        audioMgr = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        sound_pool = new SoundPool(5, AudioManager.STREAM_RING, 0);
        sound_button = sound_pool.load(getActivity(), R.raw.button_sound, 1);
    }

    private void initSound_Sensitivity() {
        sound_Sensitivity = sound_pool.load(getActivity(), R.raw.sensitivity_sound, 1);
    }

    public void playSound_button() {
        StreamVolume = (float) audioMgr
                .getStreamVolume(AudioManager.STREAM_RING);
        sound_pool.play(sound_button, StreamVolume, StreamVolume, 0, 0, 1f);
    }

    public void playSound_Sensitivity() {
        StreamVolume = (float) audioMgr
                .getStreamVolume(AudioManager.STREAM_RING);
        sound_pool
                .play(sound_Sensitivity, StreamVolume, StreamVolume, 0, 0, 1f);
    }*/

    private Vibrator mVibrator;
    public void startVibrate() {
        mVibrator.vibrate(100);  //1초진동
        /*long pattern[] = {0, 5000};
        mVibrator.vibrate(pattern, 0);
        try {
            Thread.sleep(100);
            mVibrator.cancel();
        } catch (Exception e) {
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mGyroscopeSensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorEventListener = new SensorListener();
        mSensorManager.registerListener(mSensorEventListener, mGyroscopeSensor,
                SensorManager.SENSOR_DELAY_GAME);

    }

    @Override
    public void onPause() {
        Log.i(TAG, "onPause");
        if (mSensorManager != null && mSensorEventListener != null)
            mSensorManager.unregisterListener(mSensorEventListener);
        super.onPause();

    }


    OnClickListener Button_BeginSlide_ClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "BeginSlide onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_PPTmodeCMD("BEGIN_SLIDE", "",
                        (short) 0, (short) 0, (short) 0);
        }
    };

    OnClickListener Button_CurrentSlide_ClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "CurrentSlide onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_PPTmodeCMD("CURRENT_SLIDE", "",
                        (short) 0, (short) 0, (short) 0);
        }
    };

    OnClickListener Button_EndSlide_ClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Button_EndSlide_ClickListener onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_PPTmodeCMD("END_SLIDE", "",
                        (short) 0, (short) 0, (short) 0);
        }
    };

    OnClickListener Button_BeforePage_ClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Button_BeforePage_ClickListener onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_PPTmodeCMD("BEFORE_PAGE", "",
                        (short) 0, (short) 0, (short) 0);
        }
    };

    OnClickListener Button_NextPage_ClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Button_NextPage_ClickListener onClick");
            startVibrate();
            if (MainActivity.Socket_Thread != null)
                MainActivity.Socket_Thread.Send_PPTmodeCMD("NEXT_PAGE", "",
                        (short) 0, (short) 0, (short) 0);
        }
    };

    private View.OnTouchListener Leftdown_btn_TouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("LBUTTON",
                            "DOWN", (short) 0, (short) 0, (short) 0);
                startVibrate();
            } else if (action == MotionEvent.ACTION_UP) {
                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("LBUTTON", "UP",
                            (short) 0, (short) 0, (short) 0);
            } else if (action == MotionEvent.ACTION_MOVE) {
            }
            return false;
        }
    };

    private View.OnTouchListener Rightdown_btn_TouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("RBUTTON",
                            "DOWN", (short) 0, (short) 0, (short) 0);

                startVibrate();
            } else if (action == MotionEvent.ACTION_UP) {
                if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("RBUTTON", "UP",
                            (short) 0, (short) 0, (short) 0);

            } else if (action == MotionEvent.ACTION_MOVE) {
            }
            return false;
        }
    };

    private View.OnTouchListener SensorMouse_btn_TouchListener = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                Sensor_mouse_view
                        .setBackgroundResource(R.drawable.pointer_press);
                startVibrate();
                isSensorStart = true;
            } else if (action == MotionEvent.ACTION_UP) {
                Sensor_mouse_view
                        .setBackgroundResource(R.drawable.pointer_normal);
                isSensorStart = false;
            } else if (action == MotionEvent.ACTION_MOVE) {
            }
            return true;
        }
    };

    private DiscreteSeekBar.OnProgressChangeListener Sensityvity_OnSeekBarChangeListener = new DiscreteSeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            Log.i(TAG, "Seek bar progress : " + value);
            Sensormouse_Sensitivity = (short) (value + 1);

            if (before_Sensitivity != Sensormouse_Sensitivity)
                startVibrate();

            setSensitivity_Preferences(Sensormouse_Sensitivity); // 감도 저장

            before_Sensitivity = Sensormouse_Sensitivity;
        }

        @Override
        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

        }
    };


    public int getSensitivity_Preferences() {
        SharedPreferences Sensitivity_preference = getActivity().getSharedPreferences(
                KYCHT_REMOTE_PREFERENCES_NAME, 0);
        return (int) Sensitivity_preference.getInt(KEY_PPT_SENSITIVITY, 4);
    }

    public void setSensitivity_Preferences(int value) {
        SharedPreferences settings = getActivity().getSharedPreferences(
                KYCHT_REMOTE_PREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(KEY_PPT_SENSITIVITY, value);
        editor.commit();
    }

    class SensorListener implements SensorEventListener { // inner class

        int curr_gyro_azimuth, curr_gyro_pitch, curr_gyro_roll;

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onSensorChanged(SensorEvent event) {

            if (isSensorStart == false || MainActivity.Socket_Thread == null)
                return;

            if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
                curr_gyro_azimuth = (int) (event.values[0] * 10f); // x축 기준으로 회전
                curr_gyro_pitch = (int) (event.values[1] * 10f); // Y축 기준으로 회전
                curr_gyro_roll = (int) (event.values[2] * 10f); // z축 기준으로 회전

                // 감도에 따른 각소도 전달
                if (Sensormouse_Sensitivity == 7) // 기본감도 + 3

                    MainActivity.Socket_Thread.Send_PPTmodeCMD("SENSORMOUSE",
                            "", (short) -(curr_gyro_roll * 4),
                            (short) -(curr_gyro_azimuth * 4), (short) 0);
                else if (Sensormouse_Sensitivity == 6) // 기본감도 + 2
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("SENSORMOUSE",
                            "", (short) -(curr_gyro_roll * 3),
                            (short) -(curr_gyro_azimuth * 3), (short) 0);
                else if (Sensormouse_Sensitivity == 5) // 기본감도 + 1
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("SENSORMOUSE",
                            "", (short) -(curr_gyro_roll * 2),
                            (short) -(curr_gyro_azimuth * 2), (short) 0);
                else if (Sensormouse_Sensitivity == 4) // 기본감도
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("SENSORMOUSE",
                            "", (short) -(curr_gyro_roll),
                            (short) -(curr_gyro_azimuth), (short) 0);
                else if (Sensormouse_Sensitivity == 3) // 기본 감도 -1
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("SENSORMOUSE",
                            "", (short) -(curr_gyro_roll / 2),
                            (short) -(curr_gyro_azimuth / 2), (short) 0);
                else if (Sensormouse_Sensitivity == 2) // 기본감도 -2
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("SENSORMOUSE",
                            "", (short) -(curr_gyro_roll / 3),
                            (short) -(curr_gyro_azimuth / 2), (short) 0);
                else if (Sensormouse_Sensitivity == 1) // 기본감도 -3
                    MainActivity.Socket_Thread.Send_PPTmodeCMD("SENSORMOUSE",
                            "", (short) -(curr_gyro_roll / 4),
                            (short) -(curr_gyro_azimuth / 4), (short) 0);
            }
        }
    }

}
