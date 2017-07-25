package browser.com.kudos.webviewexample.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebViewDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.regex.Pattern;

public class BrowserUnit {
    public static final int PROGRESS_MAX = 100;
    public static final int PROGRESS_MIN = 0;
    public static final String SUFFIX_HTML = ".html";
    public static final String SUFFIX_PNG = ".png";
    public static final String SUFFIX_TXT = ".txt";

    public static final int FLAG_BOOKMARKS = 0x100;
    public static final int FLAG_HISTORY = 0x101;
    public static final int FLAG_HOME = 0x102;
    public static final int FLAG_NINJA = 0x103;

    public static final String MIME_TYPE_TEXT_HTML = "text/html";
    public static final String MIME_TYPE_TEXT_PLAIN = "text/plain";
    public static final String MIME_TYPE_IMAGE = "image/*";

    public static final String BASE_URL = "file:///android_asset/";

//    public static final String BOOKMARK_TYPE = "<DT><A HREF=\"{url}\" ADD_DATE=\"{time}\">{title}</A>";
//    public static final String BOOKMARK_TITLE = "{title}";
//    public static final String BOOKMARK_URL = "{url}";
//    public static final String BOOKMARK_TIME = "{time}";

    public static final String INTRODUCTION_EN = "ninja_introduction_en.html";
    public static final String INTRODUCTION_ZH = "ninja_introduction_zh.html";

    public static final String EXTRA_URL = "urlExtra";


    public static final String GOOGLE_PATH_WWW = "https://www.google.com";
    public static final String GOOGLE_PATH = "https://google.com";
    public static final String BING_PATH_WWW = "https://www.bing.com/";
    public static final String BING_PATH = "https://bing.com/";

    public static final String YOUTUBE_PATH_WWW = "https://www.youtube.com/";
    public static final String YOUTUBE_PATH = "https://youtube.com/";
    public static final String YOUTUBE_M_PATH = "https://m.youtube.com/";

    public static final String SEARCH_ENGINE_GOOGLE = "https://www.google.com/search?q=";
    public static final String SEARCH_ENGINE_DUCKDUCKGO = "https://duckduckgo.com/?q=";
    public static final String SEARCH_ENGINE_STARTPAGE = "https://startpage.com/do/search?query=";
    public static final String SEARCH_ENGINE_BING = "https://www.bing.com/search?q=";
    public static final String SEARCH_ENGINE_BAIDU = "http://www.baidu.com/s?wd=";
    public static final String SEARCH_ENGINE_YAHOO = "https://search.yahoo.com/search?p=";
    public static final String SEARCH_ENGINE_YANDEX = "https://yandex.ua/search/?text=";



    public static final String UA_DESKTOP = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
    public static final String MOBILE_USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.4; en-us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

    public static final String URL_ENCODING = "UTF-8";
    public static final String URL_ABOUT_BLANK = "about:blank";
    public static final String URL_SCHEME_ABOUT = "about:";
    public static final String URL_SCHEME_MAIL_TO = "mailto:";
    public static final String URL_SCHEME_MAGNET_LINK = "magnet:";
    public static final String URL_SCHEME_FILE = "file://";
    public static final String URL_SCHEME_FTP = "ftp://";
    public static final String URL_SCHEME_HTTP = "http://";
    public static final String URL_SCHEME_HTTPS = "https://";
    public static final String URL_SCHEME_INTENT = "intent://";

    public static final String URL_PREFIX_GOOGLE_PLAY = "www.google.com/url?q=";
    public static final String URL_SUFFIX_GOOGLE_PLAY = "&sa";
    public static final String URL_PREFIX_GOOGLE_PLUS = "plus.url.google.com/url?q=";
    public static final String URL_SUFFIX_GOOGLE_PLUS = "&rct";

    public static boolean isURL(String url) {
        if (url == null) {
            return false;
        }

        url = url.toLowerCase(Locale.getDefault());
        if (url.startsWith(URL_ABOUT_BLANK)
                || url.startsWith(URL_SCHEME_MAIL_TO)
                || url.startsWith(URL_SCHEME_FILE)
                || url.startsWith(URL_SCHEME_MAGNET_LINK)) {
            return true;
        }

        String regex = "^((ftp|http|https|intent)?://)"                      // support scheme
                + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" // ftp的user@
                + "(([0-9]{1,3}\\.){3}[0-9]{1,3}"                            // IP形式的URL -> 199.194.52.184
                + "|"                                                        // 允许IP和DOMAIN（域名）
                + "([0-9a-z_!~*'()-]+\\.)*"                                  // 域名 -> www.
                + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\."                    // 二级域名
                + "[a-z]{2,6})"                                              // first level domain -> .com or .museum
                + "(:[0-9]{1,4})?"                                           // 端口 -> :80
                + "((/?)|"                                                   // a slash isn't required if there is no file name
                + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$";
        Pattern pattern = Pattern.compile(regex);
        boolean patternTrue =  pattern.matcher(url).matches();

        if (patternTrue){
            return true;
        }

        try {
            new URL(url);
            return true;
        }
        catch (MalformedURLException e) {
            return false;
        }
    }


//    public static String getDomainName(String url) throws URISyntaxException {
//        URI uri = new URI(url);
//        String domain = uri.getHost();
//        if (TextUtils.isNullOrEmpty(domain) || domain.length() < 4){
//            return domain;
//        }
//
//        return domain.startsWith("www.") ? domain.substring(4) : domain;
//    }

    public static String queryWrapper(String query) {
        // Use prefix and suffix to process some special links
        String temp = query.toLowerCase(Locale.getDefault());
        if (temp.contains(URL_PREFIX_GOOGLE_PLAY) && temp.contains(URL_SUFFIX_GOOGLE_PLAY)) {
            int start = temp.indexOf(URL_PREFIX_GOOGLE_PLAY) + URL_PREFIX_GOOGLE_PLAY.length();
            int end = temp.indexOf(URL_SUFFIX_GOOGLE_PLAY);
            query = query.substring(start, end);
        } else if (temp.contains(URL_PREFIX_GOOGLE_PLUS) && temp.contains(URL_SUFFIX_GOOGLE_PLUS)) {
            int start = temp.indexOf(URL_PREFIX_GOOGLE_PLUS) + URL_PREFIX_GOOGLE_PLUS.length();
            int end = temp.indexOf(URL_SUFFIX_GOOGLE_PLUS);
            query = query.substring(start, end);
        }

        if (isURL(query)) {
            if (query.startsWith(URL_SCHEME_ABOUT) || query.startsWith(URL_SCHEME_MAIL_TO) || query.startsWith(URL_SCHEME_MAGNET_LINK)) {
                return query;
            }

            if (!query.contains("://")) {
                query = URL_SCHEME_HTTPS + query;
            }

            return query;
        }

        try {
            query = URLEncoder.encode(query, URL_ENCODING);
        } catch (UnsupportedEncodingException u) {
        }

        return SEARCH_ENGINE_GOOGLE + query;
    }

    public static String urlWrapper(String url) {
        if (url == null) {
            return null;
        }

        String green500 = "<font color='#4CAF50'>{content}</font>";
        String gray500 = "<font color='#9E9E9E'>{content}</font>";

        if (url.startsWith(BrowserUnit.URL_SCHEME_HTTPS)) {
            String scheme = green500.replace("{content}", BrowserUnit.URL_SCHEME_HTTPS);
            url = scheme + url.substring(8);
        } else if (url.startsWith(BrowserUnit.URL_SCHEME_HTTP)) {
            String scheme = gray500.replace("{content}", BrowserUnit.URL_SCHEME_HTTP);
            url = scheme + url.substring(7);
        }

        return url;
    }

    public static boolean bitmap2File(Context context, Bitmap bitmap, String filename) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static Bitmap file2Bitmap(Context context, String filename) {
        try {
            FileInputStream fileInputStream = context.openFileInput(filename);
            fileInputStream.close();
            return BitmapFactory.decodeStream(fileInputStream);
        } catch (Exception e) {
            return null;
        }
    }


    public static long getCachSize(Context context){
        long size = 0;
        File dir = context.getCacheDir();
        File[] files = dir.listFiles();
        for (File f:files) {
            size = size+f.length();
        }

        return size;
    }

    public static String getFormatedByteLen(long bytes){
        double copy = bytes;

        int step = -1;
        while (copy > 1){
            copy /= 1024;
            ++step;
        }

        switch (step){
            case 0 :
                return bytes + " byte";
            case 1:
                return bytes/1024 + " KB";
            case 2:
                return bytes/(1024 * 1024) + " MB";
        }

        return "Unknown";
    }

    public static boolean clearCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }

            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    // CookieManager.removeAllCookies() must be called on a thread with a running Looper.
    public static void clearCookie(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.flush();
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean value) {
                }
            });
        } else {
            CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        }
    }

    public static void clearFormData(Context context) {
        WebViewDatabase.getInstance(context).clearFormData();
    }


    public static void clearPasswords(Context context) {
        WebViewDatabase.getInstance(context).clearHttpAuthUsernamePassword();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            WebViewDatabase.getInstance(context).clearUsernamePassword();
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }

        return dir != null && dir.delete();
    }
}
