package comkimwonjun.httpsgithub.indianpoker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import comkimwonjun.httpsgithub.indianpoker.Retrofit.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    LoadingDialog loadingDialog;
    SharedPreferences userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userInfo = getSharedPreferences("UserInfo", MODE_PRIVATE);
        findViewById(R.id.loginReqBtn).setOnClickListener(onClickListener);
        findViewById(R.id.signUpBtn).setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.loginReqBtn:
                    //loadingDialog = new LoadingDialog(MainActivity.this, "로그인 중");
                    //loadingDialog.show();
                    /*EditText tmp;
                    String id, pw;
                    final SharedPreferences.Editor editor = userInfo.edit();

                    tmp = findViewById(R.id.editId);
                    id = tmp.getText().toString();
                    tmp = findViewById(R.id.editPassword);
                    pw = tmp.getText().toString();

                    Call<ResponseBody> repoCall = RetrofitClient.getInterface().reqLogin(id, pw);
                    repoCall.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            //loadingDialog.dismiss();
                            switch(response.code())
                            {
                                case 200:
                                    Log.d("CHECK", "로그인 성공");
                                    editor.putBoolean("AutoLogin", true);
                                    editor.apply();
                                    Intent intent = new Intent(getApplication(), MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                    break;
                                case 403:
                                    Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                            //loadingDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "서버와 연결 실패", Toast.LENGTH_SHORT).show();
                        }

                    });*/
                    Intent intent = new Intent(getApplication(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case R.id.signUpBtn:
                    intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    };
}