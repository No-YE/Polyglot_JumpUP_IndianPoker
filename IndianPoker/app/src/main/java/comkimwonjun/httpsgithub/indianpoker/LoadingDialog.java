package comkimwonjun.httpsgithub.indianpoker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by KimWonJun on 11/7/2017.
 */

public class LoadingDialog extends Dialog {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.loading_dialog);

        textView = findViewById(R.id.loadingText);
    }

    LoadingDialog(Context context, String s) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);           //노타이틀
        textView.setText(s);
    }
}
