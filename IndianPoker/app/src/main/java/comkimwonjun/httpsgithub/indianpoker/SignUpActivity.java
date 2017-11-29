package comkimwonjun.httpsgithub.indianpoker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import comkimwonjun.httpsgithub.indianpoker.Retrofit.RetrofitClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private EditText et_id, et_password, et_ch_password;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        et_id = findViewById(R.id.et_id);
        et_password = findViewById(R.id.et_password);
        et_ch_password = findViewById(R.id.et_checkpassword);
    }

    public void OnClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                //loadingDialog = new LoadingDialog(SignUpActivity.this, "회원가입 요청중");
                //loadingDialog.show();
                String id = et_id.getText().toString();
                String password = et_password.getText().toString();
                String chpassword = et_ch_password.getText().toString();

                if (!id.isEmpty() && !password.isEmpty() && !chpassword.isEmpty()) {
                    if (password.equals(chpassword))
                        registerProcess(id, password);
                    else {
                        //loadingDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "재확인 비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //loadingDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "회원 가입할 아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void registerProcess(String id, String password) {
        Call<ResponseBody> repoCall = RetrofitClient.getInterface().register(id, password);
        repoCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                //loadingDialog.dismiss();
                switch (response.code()) {
                    case 200:
                        Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplication(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case 409:
                        Toast.makeText(getApplicationContext(), "이미 존재하는 ID입니다", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                //loadingDialog.dismiss();
                Toast.makeText(getApplicationContext(), "서버와 연결 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
