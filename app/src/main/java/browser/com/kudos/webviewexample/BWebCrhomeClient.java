package browser.com.kudos.webviewexample;

import android.webkit.WebChromeClient;
import android.webkit.WebView;

public class BWebCrhomeClient extends WebChromeClient {

    private BWebView mWebView;


    public BWebCrhomeClient(BWebView webView){
        this.mWebView = webView;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        mWebView.update(newProgress);
    }

}
