package comkimwonjun.httpsgithub.indianpoker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

public class GamePlayActivity extends AppCompatActivity {

    final static String TAG = "INDIAN_CHECK";

    boolean isFirst;
    int nChip;
    int nBetChip;
    int enemyNumChip;
    int enemyBetChip;
    int min;

    TextView cardNumText;
    TextView enemyCardNumText;
    TextView nChipText;
    TextView nBetChipText;
    TextView enemyNumChipText;
    TextView enemyBetChipText;
    TextView statusText;
    TextView nSeekBarText;
    Button betBtn;
    Button giveUpBtn;
    SeekBar betChipBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameplay);

        Intent intent = getIntent();
        isFirst = intent.getBooleanExtra("isFirst", false);

        cardNumText = findViewById(R.id.cardNumText);
        enemyCardNumText = findViewById(R.id.enemyCardNumText);
        nChipText = findViewById(R.id.nChipText);
        nBetChipText = findViewById(R.id.nBetChipText);
        enemyNumChipText = findViewById(R.id.enemyNumChipText);
        enemyBetChipText = findViewById(R.id.enemyNumBetChipText);
        statusText = findViewById(R.id.statusText);
        betBtn = findViewById(R.id.betBtn);
        betChipBar = findViewById(R.id.betChipBar);
        nSeekBarText = findViewById(R.id.nSeekBarText);
        giveUpBtn = findViewById(R.id.giveUpBtn);

        SocketClient.mSocket.on("InitialPlayerInfo", initialPlayerInfoListener);
        SocketClient.mSocket.on("TurnChanging", turnChangingListener);
        SocketClient.mSocket.on("BettingResult", bettingResultListener);

        statusText.setText("상대턴");

        betBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                betBtn.setClickable(false);
                giveUpBtn.setClickable(false);
                statusText.setText("상대 턴");
                betChipBar.setEnabled(false);
                betChipBar.setOnSeekBarChangeListener(null);

                nChip -= betChipBar.getProgress() + min;
                nBetChip += betChipBar.getProgress() + min;

                nChipText.setText(Integer.toString(nChip));
                nBetChipText.setText(Integer.toString(nBetChip));

                SocketClient.mSocket.emit("EndTurn", betChipBar.getProgress() + min);
            }
        });

        giveUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                betBtn.setClickable(false);
                giveUpBtn.setClickable(false);
                statusText.setText("상대 턴");
                betChipBar.setEnabled(false);
                betChipBar.setOnSeekBarChangeListener(null);

                nChipText.setText(Integer.toString(nChip));
                nBetChipText.setText(Integer.toString(nBetChip));

                SocketClient.mSocket.emit("EndTurn", -1);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        SocketClient.mSocket.disconnect();

        SocketClient.mSocket.off("InitialPlayerInfo");
        SocketClient.mSocket.off("TurnChanging");
        SocketClient.mSocket.off("BettingResult");
    }

    private void myTurn() {
        statusText.setText("내 턴");
        betBtn.setClickable(true);
        giveUpBtn.setClickable(true);
        betChipBar.setEnabled(true);

        betChipBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                nSeekBarText.setText(Integer.toString(i + min));
                nChipText.setText(Integer.toString(nChip - i - min));
                nBetChipText.setText(Integer.toString(nBetChip + i + min));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        betChipBar.setProgress(0);

        if(nBetChip == 1 && enemyBetChip == 1)
            min = 1;
        else
            min = enemyBetChip - nBetChip;

        Log.d(TAG, "최솟값 : " + min);

        if(nChip + nBetChip > enemyNumChip + enemyBetChip)
            betChipBar.setMax((enemyBetChip + enemyNumChip) - nBetChip - min);
        else
            betChipBar.setMax(nChip - min);
    }

    private Emitter.Listener initialPlayerInfoListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.d(TAG, "플레이어 초기화" +
                    "값1 : " + args[0] + " " +
                    "값2 : " + args[1] + " " +
                    "값3 : " + args[2] + " " +
                    "값4 : " + args[3] + " " +
                    "값5 : " + args[4]);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    nChip = (int) args[0];
                    nBetChip = (int) args[1];
                    int enemyCardNum = (int) args[2];
                    enemyNumChip = (int) args[3];
                    enemyBetChip = (int) args[4];

                    cardNumText.setText("?");
                    enemyCardNumText.setText(Integer.toString(enemyCardNum));
                    nChipText.setText(Integer.toString(nChip));
                    nBetChipText.setText(Integer.toString(nBetChip));
                    enemyNumChipText.setText(Integer.toString(enemyNumChip));
                    enemyBetChipText.setText(Integer.toString(enemyBetChip));
                    statusText.setText("상대 턴");
                    betBtn.setClickable(false);
                    betChipBar.setEnabled(false);

                    if(isFirst)
                        myTurn();
                }
            });
        }
    };

    private Emitter.Listener turnChangingListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "턴바뀜");
                    Log.d(TAG, "내꺼 : " + nChip + " 건거 : " + nBetChip + " 적꺼 : " + enemyNumChip + " 적건거 : " + enemyBetChip);
                    enemyNumChip = (int) args[0];
                    enemyBetChip = (int) args[1];
                    nChip = (int) args[2];
                    nBetChip= (int) args[3];

                    nChipText.setText(Integer.toString(nChip));
                    nBetChipText.setText(Integer.toString(nBetChip));
                    enemyNumChipText.setText(Integer.toString(enemyNumChip));
                    enemyBetChipText.setText(Integer.toString(enemyBetChip));

                    myTurn();
                }
            });
        }
    };

    private Emitter.Listener bettingResultListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    int cardNum = (int) args[0];
                    nChip = (int) args[1];
                    nBetChip = (int) args[2];
                    enemyNumChip = (int) args[3];
                    enemyBetChip = (int) args[4];

                    cardNumText.setText(Integer.toString(cardNum));
                    statusText.setText("결과 확인");

                    if(nChip == 0) {
                        Toast.makeText(getApplicationContext(), "패배", Toast.LENGTH_LONG).show();

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                SocketClient.mSocket.disconnect();

                                SocketClient.mSocket.off("InitialPlayerInfo");
                                SocketClient.mSocket.off("TurnChanging");
                                SocketClient.mSocket.off("BettingResult");

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 2000);
                    }
                    else if(enemyNumChip == 0) {
                        Toast.makeText(getApplicationContext(), "승리", Toast.LENGTH_LONG).show();

                        new Handler().postDelayed(new Runnable(){
                            @Override
                            public void run() {
                                SocketClient.mSocket.disconnect();

                                SocketClient.mSocket.off("InitialPlayerInfo");
                                SocketClient.mSocket.off("TurnChanging");
                                SocketClient.mSocket.off("BettingResult");

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 2000);
                    }
                    else
                        isFirst = !isFirst;
                    //서버에서 bettingResult 를 알리고 setTImeOut 으로 3초~5초정도 기다렸다가 InitialPlayerInfo 보내면서 게임 진행
                }
            });
        }
    };
}