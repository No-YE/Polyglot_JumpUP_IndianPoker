package comkimwonjun.httpsgithub.indianpoker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

import static com.github.nkzawa.socketio.client.Socket.EVENT_CONNECT;

public class MainActivity extends AppCompatActivity {
    final static String TAG = "INDIAN_CHECK";

    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "환영합니다", Toast.LENGTH_SHORT).show();
        setClickListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SocketClient.mSocket.off(Socket.EVENT_CONNECT, connectListener);
        SocketClient.mSocket.off("completeMatch", completeMatchListener);
    }

    private void setClickListener() {
        findViewById(R.id.GameRoomBtn).setOnClickListener(btnClickListener);
        findViewById(R.id.ProfileBtn).setOnClickListener(btnClickListener);
        findViewById(R.id.RankingBtn).setOnClickListener(btnClickListener);
        findViewById(R.id.SettingBtn).setOnClickListener(btnClickListener);
    }

    private View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = null;

            switch (view.getId()) {
                case R.id.ProfileBtn:
//                    intent = new Intent(MainActivity.this, ProfileActivity);
                    break;
                case R.id.RankingBtn:
//                    intent = new Intent(MainActivity.this, RankingActivity);
                    break;
                case R.id.SettingBtn:
//                    intent = new Intent(MainActivity.this, SettingActivity);
                    break;
                default:
                    try {
                        SocketClient.mSocket = IO.socket("http://10.0.2.2:3000");
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }

                    SocketClient.mSocket.on(EVENT_CONNECT, connectListener);
                    SocketClient.mSocket.on("completeMatch", completeMatchListener);
                    SocketClient.mSocket.connect();
                    //loadingDialog = new LoadingDialog(getApplicationContext(), "게임 찾는 중");
                    //loadingDialog.show();
                    break;
            }
        }
    };

    private Emitter.Listener connectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "서버와 연결");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "매칭 시작", Toast.LENGTH_SHORT).show();
                    SocketClient.mSocket.emit("requestGameMatch");
                }
            });
        }
    };

    private Emitter.Listener completeMatchListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "게임 시작", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), OrderingActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    };
}
