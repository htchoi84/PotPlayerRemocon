package com.htchoi.potplayerremote;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class keyboard_Activity extends Activity {
	private final String TAG = "keyboard_Activity";
	private ViewGroup mContainer;
	//private Button mKeyboard_btn;
	private TextView mKeyboard_View_btn;
	private static String Keyboard_Toggle_Status;
	private static String KeyBoardData;

	int Shift_Check = None_KeyStatus;
	private static final int Shift_press = 1;
	private static final int Shift_release = 2;
	private static final int None_KeyStatus = 3;

	// subCMD //activity_keyboard-CMD
	public static byte DOWN = 0x01;
	public static byte UP = 0x03;

	static PowerManager.WakeLock mWakeLock;

	private SoundPool sound_pool;
	private int sound_beep;
	float StreamVolume;
	AudioManager audioMgr;

	String[] Keyboard_Line1 = null;
	String[] Keyboard_Line2 = null;
	String[] Keyboard_Line3 = null;
	String[] Keyboard_Line4 = null;
	String[] Keyboard_Line5 = null;
	String[] Keyboard_Line6 = null;

	String[] Keyboard_Hangul_Line3 = null;
	String[] Keyboard_Hangul_Line4 = null;
	String[] Keyboard_Hangul_Line5 = null;

	String[] Keyboard_Shitf_Line2 = null;
	String[] Keyboard_Shitf_Line3 = null;
	String[] Keyboard_Shitf_Line4 = null;
	String[] Keyboard_Shitf_Line5 = null;
	String[] Keyboard_Shitf_Line6 = null;

	String[] Keyboard_Hangul_Shitf_Line2 = null;
	String[] Keyboard_Hangul_Shitf_Line3 = null;
	String[] Keyboard_Hangul_Shitf_Line4 = null;
	String[] Keyboard_Hangul_Shitf_Line5 = null;
	String[] Keyboard_Hangul_Shitf_Line6 = null;

	String[] Keyboard_Cap_Line3 = null;
	String[] Keyboard_Cap_Line4 = null;
	String[] Keyboard_Cap_Line5 = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //화면 안꺼지게..
		setContentView(R.layout.activity_keyboard);

		Log.i(TAG, "onCreate");
		mContainer = (ViewGroup) findViewById(R.id.keyboard_layout);

		PowerManager pm = (PowerManager) this
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "Keyboard");

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		initSound();
	}

	OnTouchListener ButtonTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if (MotionEvent.ACTION_DOWN == event.getAction()) {
				playSound();

				v.setBackgroundResource(R.drawable.btn_background_click);

				KeyBoardData = (String) v.getTag();

				if (KeyBoardData.equals("Shift")) {
					if (Keyboard_Toggle_Status.equals("eng")) {
						KeyboardButton_init("eng_Shift");
						Shift_Check = Shift_press;
					} else if (Keyboard_Toggle_Status.equals("hangul")) {
						KeyboardButton_init("hangul_Shift");
						Shift_Check = Shift_press;
					}
				} else if (Keyboard_Toggle_Status.equals("hangul_Shift")) {
					KeyboardButton_init("hangul");
				} else if (Keyboard_Toggle_Status.equals("eng_Shift")) {
					KeyboardButton_init("eng");
				}
				if (MainActivity.Socket_Thread != null)
                    MainActivity.Socket_Thread.Send_KeyboardCMD("DOWN",
							KeyBoardData, Keyboard_Toggle_Status);

			}
			if (MotionEvent.ACTION_UP == event.getAction()) {
				v.setBackgroundResource(R.drawable.btn_background);

				if (KeyBoardData.equals("한/영")) {
					if (Keyboard_Toggle_Status.equals("eng")) {
						KeyboardButton_init("hangul");
					} else if (Keyboard_Toggle_Status.equals("hangul")) {
						KeyboardButton_init("eng");
					}
				} else if (KeyBoardData.equals("Cap")) {
					if (Keyboard_Toggle_Status.equals("eng")) {
						KeyboardButton_init("CapsLock");
					} else if (Keyboard_Toggle_Status.equals("CapsLock")) {
						KeyboardButton_init("eng");
					}
				}

				if (Shift_Check == Shift_release) {
					if (MainActivity.Socket_Thread != null)
						MainActivity.Socket_Thread.Send_KeyboardCMD("UP",
								"Shift", Keyboard_Toggle_Status);
					try {
						Thread.sleep(1);
					} catch (Exception e) {
					}
					Shift_Check = None_KeyStatus;
					// return true;
				} else if (Shift_Check == Shift_press
						&& KeyBoardData.equals("Shift")) {
					Shift_Check = Shift_release;
					return true;
				}
				if (Shift_Check == None_KeyStatus) {
					KeyBoardData = (String) v.getTag();
					if (MainActivity.Socket_Thread != null)
						MainActivity.Socket_Thread.Send_KeyboardCMD("UP",
								KeyBoardData, Keyboard_Toggle_Status);
				}

			}
			return true;
		}
	};

	private void initSound() {
		audioMgr = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		sound_pool = new SoundPool(5, AudioManager.STREAM_RING, 0);
		sound_beep = sound_pool.load(this, R.raw.button_sound, 1);
	}

	public void playSound() {
		StreamVolume = (float) audioMgr
				.getStreamVolume(AudioManager.STREAM_RING);
		sound_pool.play(sound_beep, StreamVolume, StreamVolume, 0, 0, 1f);
	}

	private void Keyboard_init() {
		Keyboard_Line1 = new String[] { "Esc", "F1", "F2", "F3", "F4", "F5",
				"F6", "F7", "F8", "F9", "F10", "F11", "F12" };
		Keyboard_Line2 = new String[] { "`", "1", "2", "3", "4", "5", "6", "7",
				"8", "9", "0", "-", "=" };
		Keyboard_Line3 = new String[] { "Tab", "q", "w", "e", "r", "t", "y",
				"u", "i", "o", "p", "[", "]" };
		Keyboard_Line4 = new String[] { "Cap", "a", "s", "d", "f", "g", "h",
				"j", "k", "l", ";", "'", "←" };
		Keyboard_Line5 = new String[] { "Shift", "z", "x", "c", "v", "b", "n",
				"m", ",", ".", "△", "Enter" };
		Keyboard_Line6 = new String[] { "Ctrl", "Win", "Alt", "space", "한/영",
				"\\", "/", "Del", "◁", "▽", "▷" };

		Keyboard_Hangul_Line3 = new String[] { "Tab", "ㅂ", "ㅈ", "ㄷ", "ㄱ", "ㅅ",
				"ㅛ", "ㅕ", "ㅑ", "ㅐ", "ㅔ", "[", "]" };
		Keyboard_Hangul_Line4 = new String[] { "Cap", "ㅁ", "ㄴ", "ㅇ", "ㄹ", "ㅎ",
				"ㅗ", "ㅓ", "ㅏ", "ㅣ", ";", "'", "←" };
		Keyboard_Hangul_Line5 = new String[] { "Shift", "ㅋ", "ㅌ", "ㅊ", "ㅍ",
				"ㅠ", "ㅜ", "ㅡ", ",", ".", "△", "Enter" };

		Keyboard_Shitf_Line2 = new String[] { "~", "!", "@", "#", "$", "%",
				"^", "&", "*", "(", ")", "_", "+" };
		Keyboard_Shitf_Line3 = new String[] { "Tab", "Q", "W", "E", "R", "T",
				"Y", "U", "I", "O", "P", "{", "}" };
		Keyboard_Shitf_Line4 = new String[] { "Cap", "A", "S", "D", "F", "G",
				"H", "J", "K", "L", ":", "\"", "←" };
		Keyboard_Shitf_Line5 = new String[] { "Shift", "Z", "X", "C", "V", "B",
				"N", "M", "<", ">", "△", "Enter" };
		Keyboard_Shitf_Line6 = new String[] { "Ctrl", "Win", "Alt", "space",
				"한/영", "|", "?", "Del", "◁", "▽", "▷" };

		Keyboard_Hangul_Shitf_Line2 = new String[] { "~", "!", "@", "#", "$",
				"%", "^", "&", "*", "(", ")", "_", "+" };
		Keyboard_Hangul_Shitf_Line3 = new String[] { "Tab", "ㅃ", "ㅉ", "ㄸ", "ㄲ",
				"ㅆ", "ㅛ", "ㅕ", "ㅑ", "ㅒ", "ㅖ", "{", "}" };
		Keyboard_Hangul_Shitf_Line4 = new String[] { "Cap", "ㅁ", "ㄴ", "ㅇ", "ㄹ",
				"ㅎ", "ㅗ", "ㅓ", "ㅏ", "ㅣ", ":", "'", "←" };
		Keyboard_Hangul_Shitf_Line5 = new String[] { "Shift", "ㅋ", "ㅌ", "ㅊ",
				"ㅍ", "ㅠ", "ㅜ", "ㅡ", "<", ">", "△", "Enter" };
		Keyboard_Hangul_Shitf_Line6 = new String[] { "Ctrl", "Win", "Alt",
				"space", "한/영", "|", "?", "Del", "◁", "▽", "▷" };

		Keyboard_Cap_Line3 = new String[] { "Tab", "Q", "W", "E", "R", "T",
				"Y", "U", "I", "O", "P", "[", "]" };
		Keyboard_Cap_Line4 = new String[] { "Cap", "A", "S", "D", "F", "G",
				"H", "J", "K", "L", ";", "'", "←" };
		Keyboard_Cap_Line5 = new String[] { "Shift", "Z", "X", "C", "V", "B",
				"N", "M", ",", ".", "△", "Enter" };
	}

	private String KeyboardButton_init(String Toggle) {
		Keyboard_Toggle_Status = Toggle;
		mContainer.removeAllViews();
		//키보드 가로(행) 루프
		for (int raw = 1; raw <= 6; raw++) {
			LinearLayout linear = new LinearLayout(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
			linear.setLayoutParams(params);
			//linear.setBackgroundResource(R.drawable.black_background);
			linear.setOrientation(LinearLayout.HORIZONTAL);
			//linear.setPadding(-5, 0, -5, 0);
			int arraylength = Keyboard_Line1.length;
			if (raw == 5)
				arraylength = Keyboard_Line5.length;
			if (raw == 6)
				arraylength = Keyboard_Line6.length;

			//키보드 가로(행) 안의 각 버튼 루프
			for (int column = 0; column < arraylength; column++) {

				mKeyboard_View_btn = new TextView(this);
				mKeyboard_View_btn.setLayoutParams(new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
				mKeyboard_View_btn.setTextColor(Color.BLACK);
				mKeyboard_View_btn.setTextSize(15);
				mKeyboard_View_btn.setTypeface(null, Typeface.BOLD);
				mKeyboard_View_btn.setGravity(Gravity.CENTER);

				mKeyboard_View_btn.setBackgroundResource(R.drawable.btn_background);
				mKeyboard_View_btn.setOnTouchListener(ButtonTouchListener);
				if (raw == 1) {
					mKeyboard_View_btn.setTag(Keyboard_Line1[column]);
					mKeyboard_View_btn.setText(Keyboard_Line1[column]);
					linear.addView(mKeyboard_View_btn);
				} else if (raw == 2) {
					mKeyboard_View_btn.setTag(Keyboard_Line2[column]);
					if (Toggle.equals("eng")) {
						mKeyboard_View_btn.setText(Keyboard_Line2[column]);
					} else if (Toggle.equals("hangul_Shift")) {
						mKeyboard_View_btn
								.setText(Keyboard_Hangul_Shitf_Line2[column]);
					} else if (Toggle.equals("eng_Shift")) {
						mKeyboard_View_btn.setText(Keyboard_Shitf_Line2[column]);
					} else
						mKeyboard_View_btn.setText(Keyboard_Line2[column]);
					linear.addView(mKeyboard_View_btn);
				} else if (raw == 3) {
					mKeyboard_View_btn.setTag(Keyboard_Line3[column]);
					if (Toggle.equals("eng")) {
						mKeyboard_View_btn.setText(Keyboard_Line3[column]);
					} else if (Toggle.equals("hangul")) {
						mKeyboard_View_btn.setText(Keyboard_Hangul_Line3[column]);
					} else if (Toggle.equals("hangul_Shift")) {
						mKeyboard_View_btn
								.setText(Keyboard_Hangul_Shitf_Line3[column]);
					} else if (Toggle.equals("eng_Shift")) {
						mKeyboard_View_btn.setText(Keyboard_Shitf_Line3[column]);
					} else if (Toggle.equals("CapsLock")) {
						mKeyboard_View_btn.setText(Keyboard_Cap_Line3[column]);
					}
					linear.addView(mKeyboard_View_btn);
				} else if (raw == 4) {
					mKeyboard_View_btn.setTag(Keyboard_Line4[column]);
					if (Toggle.equals("eng")) {
						mKeyboard_View_btn.setText(Keyboard_Line4[column]);
					} else if (Toggle.equals("hangul")) {
						mKeyboard_View_btn.setText(Keyboard_Hangul_Line4[column]);
					} else if (Toggle.equals("hangul_Shift")) {
						mKeyboard_View_btn.setText(Keyboard_Hangul_Shitf_Line4[column]);
					} else if (Toggle.equals("eng_Shift")) {
						mKeyboard_View_btn.setText(Keyboard_Shitf_Line4[column]);
					} else if (Toggle.equals("CapsLock")) {
						mKeyboard_View_btn.setText(Keyboard_Cap_Line4[column]);
					}
					linear.addView(mKeyboard_View_btn);
				} else if (raw == 5) {
					mKeyboard_View_btn.setTag(Keyboard_Line5[column]);
					if (Toggle.equals("eng")) {
						mKeyboard_View_btn.setText(Keyboard_Line5[column]);
					} else if (Toggle.equals("hangul")) {
						mKeyboard_View_btn.setText(Keyboard_Hangul_Line5[column]);
					} else if (Toggle.equals("hangul_Shift")) {
						mKeyboard_View_btn.setText(Keyboard_Hangul_Shitf_Line5[column]);
					} else if (Toggle.equals("eng_Shift")) {
						mKeyboard_View_btn.setText(Keyboard_Shitf_Line5[column]);
					} else if (Toggle.equals("CapsLock")) {
						mKeyboard_View_btn.setText(Keyboard_Cap_Line5[column]);
					}
					linear.addView(mKeyboard_View_btn);
				} else if (raw == 6) {
					mKeyboard_View_btn.setTag(Keyboard_Line6[column]);
					if (Toggle.equals("hangul_Shift")) {
						mKeyboard_View_btn
								.setText(Keyboard_Hangul_Shitf_Line6[column]);
					} else if (Toggle.equals("eng_Shift")) {
						mKeyboard_View_btn.setText(Keyboard_Shitf_Line6[column]);
					} else
						mKeyboard_View_btn.setText(Keyboard_Line6[column]);
					linear.addView(mKeyboard_View_btn);
				}
			}
			mContainer.addView(linear);
		}
		return Keyboard_Toggle_Status;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mWakeLock != null && mWakeLock.isHeld())
			mWakeLock.release();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (mWakeLock != null)
			mWakeLock.acquire();
		Keyboard_init();
		KeyboardButton_init("eng");
	}
}
