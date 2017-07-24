package browser.com.kudos.webviewexample;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    BWebView webView;
    EditText etInput;

    BWebViewClient client;
    BWebCrhomeClient webCrhomeClient;

    TextView tvGo;

    public ProgressBar pbProcessing;
    int shortAnimTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        etInput = (EditText) findViewById(R.id.etInput);
        webView = (BWebView) findViewById(R.id.webView);
        tvGo = (TextView) findViewById(R.id.tvGo);
        pbProcessing = (ProgressBar) findViewById(R.id.pbProcessing);

        client = new BWebViewClient(webView);
        webView.setWebViewClient(client);
        webView.setCallback(new BWebView.OnWebViewCallback() {
            @Override
            public void onUpdate(String url) {
                etInput.setText(url);
            }

            @Override
            public void onUpdateProgress(int prog) {
                updateProgress(prog);
            }
        });

        webCrhomeClient = new BWebCrhomeClient(webView);
        webView.setWebChromeClient(webCrhomeClient);

        tvGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = BrowserUnit.queryWrapper(etInput.getText().toString());
                webView.loadUrl(url);
            }
        });
    }

    public void updateProgress(int progress) {
        if (progress < 100){
            pbProcessing.setVisibility(View.VISIBLE);

            if (progress > pbProcessing.getProgress()) {
                ObjectAnimator animator = ObjectAnimator.ofInt(pbProcessing, "progress", progress);
                animator.setDuration(shortAnimTime);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.start();
            } else if (progress < pbProcessing.getProgress()) {
                ObjectAnimator animator = ObjectAnimator.ofInt(pbProcessing, "progress", 0, progress);
                animator.setDuration(shortAnimTime);
                animator.setInterpolator(new DecelerateInterpolator());
                animator.start();
            }
        } else {
            pbProcessing.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (webView != null && webView.canGoBack()){
            webView.goBack();
        } else {
            onBackPressed();
        }

        return true;
    }
}