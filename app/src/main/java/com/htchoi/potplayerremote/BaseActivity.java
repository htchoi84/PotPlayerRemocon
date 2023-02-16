package com.htchoi.potplayerremote;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;

import com.htchoi.potplayerremote.utils.AbBackPressCloseHandler;

/**
 * Created by htchoi on 2017-06-22.
 */

public class BaseActivity extends Activity {
    protected Context mContext;

    public AbBackPressCloseHandler mAbBackPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //화면 안꺼지게..

        mContext = this;
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
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        mAbBackPressCloseHandler.onBackPressed();  //2번 클릭시 종료되도록..
    }
}

