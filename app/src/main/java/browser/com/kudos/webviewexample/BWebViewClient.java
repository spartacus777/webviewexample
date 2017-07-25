package browser.com.kudos.webviewexample;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.github.rwitzel.streamflyer.core.ModifyingReader;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.apache.commons.io.input.ReaderInputStream;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import static android.content.ContentValues.TAG;

public class BWebViewClient extends WebViewClient {

    public static final String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.4; en-us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";


    private BWebView webView;
    public OkHttpClient okHttpClient;


    public BWebViewClient(BWebView webView){
        super();
        this.webView = webView;

        okHttpClient = new OkHttpClient();
        okHttpClient.setWriteTimeout(20, TimeUnit.SECONDS);
        okHttpClient.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        if (view.getTitle() == null || view.getTitle().isEmpty()) {
            webView.update("no title", url);
        } else {
            webView.update(view.getTitle(), url);
        }

    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        webView.onPageLoaded();
    }

    @Override
    @Deprecated
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return shouldOverrideUrlLoadingPrivate(view, url);
    }

    @Override
    @TargetApi(24)
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return shouldOverrideUrlLoadingPrivate(view, request.getUrl().toString());
    }

    private boolean shouldOverrideUrlLoadingPrivate(WebView view, String url) {

        //custom url redirect
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Deprecated
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
//        return handleInterceptRequest(view, url, null);
        return null;
    }

    @Override
    @RequiresApi(21)
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//        return handleInterceptRequest(view, request.getUrl().toString(), request);
        return null;
    }

    @TargetApi(21)
    private void inflateHeaders(Request.Builder builder, WebResourceRequest request){
        Map<String, String> map = request.getRequestHeaders();
        for (String key : map.keySet()){
            builder.addHeader(key, map.get(key));
        }
    }

    /**
     * Tried to override web resources here, but failied since auth got broken then
     */
    private WebResourceResponse handleInterceptRequest(WebView view, String url, WebResourceRequest request){
        try {

            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .header("User-Agent", MOBILE_USER_AGENT);

            if (request != null){
                inflateHeaders(builder, request);
            }

            // On Android API >= 21 you can get request method and headers
            // As I said, we need to only display "simple" page with resources
            // So it's GET without special headers
            final Call call = okHttpClient.newCall(builder.build());

            final Response response = call.execute();

            ResponseBody body = response.body();

            MediaType contentType = body.contentType();

            String mime = contentType.type() + "/" + contentType.subtype();

            String encode = null;
            if (contentType.charset() != null) {
                encode = contentType.charset().name();
            }

//            response.headers().;

            InputStream is = body.byteStream();

            is = applyRegexOnStream(is);

            Log.d(TAG, "[LOADED] '" + mime + "' for: " + url);

            return new WebResourceResponse(mime, encode, is);
        } catch (Exception e) {
            Log.d(TAG, "ERROR");
            return null;
        }
    }

    private InputStream applyRegexOnStream(InputStream inputStream) {
        Map<Pattern, String> regex = new HashMap<>();
        regex.put(Pattern.compile("<h3>([^<]*)<\\/h3>"), "hi from parcer");

        for (Pattern pattern : regex.keySet()) {

            String replacement = regex.get(pattern);
            String charsetName = "ISO-8859-1";

            try {
                // byte stream as character stream
                Reader originalReader = new InputStreamReader(inputStream, charsetName);

                // create the modifying reader
                Reader modifyingReader = new ModifyingReader(originalReader, new MyRegexModifier(pattern, replacement));

                // character stream as byte stream
                inputStream = new ReaderInputStream(modifyingReader, charsetName);

            } catch (UnsupportedEncodingException e) {
            }
        }

        return inputStream;
    }


}
