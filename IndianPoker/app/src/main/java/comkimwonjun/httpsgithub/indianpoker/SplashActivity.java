package comkimwonjun.httpsgithub.indianpoker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Created by KimWonJun on 10/30/2017.
 */

public class SplashActivity extends Activity {
    SharedPreferences userInfo;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.splash_acitivty);

        userInfo = getSharedPreferences("UserInfo", MODE_PRIVATE);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(userInfo.getBoolean("AutoLogin", false))
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
    }
}