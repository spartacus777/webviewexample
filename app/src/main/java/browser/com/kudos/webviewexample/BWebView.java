package browser.com.kudos.webviewexample;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class BWebView extends WebView {

    private OnWebViewCallback callback;

    public interface OnWebViewCallback{
        void onUpdate(String url);
        void onUpdateProgress(int prog);
    }

    public BWebView(Context context) {
        super(context);
    }

    public void setCallback(OnWebViewCallback callback){
        this.callback = callback;
        initWebView();
    }

    public BWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebView();
    }

    public BWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initWebView();
    }

    private synchronized void initWebView() {

        setAlwaysDrawnWithCacheEnabled(true);
        setAnimationCacheEnabled(true);
        setDrawingCacheBackgroundColor(0x00000000);
        setDrawingCacheEnabled(true);
        setWillNotCacheDrawing(false);
        setSaveEnabled(true);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setBackground(null);
            getRootView().setBackground(null);
        }

        setFocusable(true);
        setFocusableInTouchMode(true);
        setHorizontalScrollBarEnabled(false);
        setVerticalScrollBarEnabled(false);
        setScrollbarFadingEnabled(true);


        //settings
        WebSettings webSettings = getSettings();

        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getContext().getCacheDir().toString());
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationDatabasePath(getContext().getFilesDir().toString());

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


        webSettings.setDefaultTextEncodingName(BrowserUnit.URL_ENCODING);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webSettings.setLoadsImagesAutomatically(true);
        } else {
            webSettings.setLoadsImagesAutomatically(false);
        }

        webSettings.setUserAgentString(BrowserUnit.MOBILE_USER_AGENT);


    }

    public void onPageLoaded(){
//        loadUrl("javascript:replace('Star', 'fuck from parcer')");
    }

    public void update(int prog){
        callback.onUpdateProgress(prog);
    }

    public void update(String title, String url){
        callback.onUpdate(url);
    }
}
