package comkimwonjun.httpsgithub.indianpoker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

public class OrderingActivity extends AppCompatActivity {
    final static String TAG = "INDIAN_CHECK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        SocketClient.mSocket.on("NoticeOrder", orderingListener);
        SocketClient.mSocket.on("reCardSelect", reCardSelectListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SocketClient.mSocket.off("NoticeOrder", orderingListener);
        SocketClient.mSocket.off("reCardSelect", reCardSelectListener);
    }

    public void onClick(View v) {
        TextView statusText = findViewById(R.id.orderingNoticeText);
        statusText.setText("상대가 선택중");

        int cardNum = ((int) (Math.random() * 10) + 1);
        Log.d(TAG, "cardNum = " + cardNum);
        Toast.makeText(getApplicationContext(), "번호 : " + cardNum, Toast.LENGTH_LONG).show();

        SocketClient.mSocket.emit("orderingCardSelect", cardNum);
    }

    private Emitter.Listener orderingListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boolean isFirst = (boolean) args[0];
                    Log.d(TAG, "선인가요 : " + isFirst);
                    Toast.makeText(getApplicationContext(), "선인가요 : " + isFirst, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(OrderingActivity.this, GamePlayActivity.class);
                    intent.putExtra("isFirst", isFirst);
                    startActivity(intent);
                }
            });
        }
    };

    private Emitter.Listener reCardSelectListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(OrderingActivity.this, "카드를 다시 선택해주세요", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
}
