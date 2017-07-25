package browser.com.kudos.webviewexample.proxy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Proxy;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import org.apache.http.HttpHost;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import browser.com.kudos.webviewexample.App;

import static android.content.Context.WIFI_SERVICE;


public class IPHelper {

    public static ServerSocket serverSocket;

    public static int PORT = 8880;

    private static boolean first = true;

    public static String getIP() {
        return wifiIpAddress(App.getContext());
    }

    public static void startSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                DataInputStream dataInputStream = null;
                DataOutputStream dataOutputStream = null;

                try {
                    serverSocket = new ServerSocket(PORT);

                    Log.d(LOG_TAG, "Listening :" + PORT);
                } catch (IOException e) {
                    Log.d(LOG_TAG, "Error creatings");
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                while (true) {
                    try {
                        Log.d(LOG_TAG, "serverSocket.accept()");
                        socket = serverSocket.accept();
                        Log.d(LOG_TAG, "serverSocket.accept() after");
                        String address = IPHelper.serverSocket.getInetAddress().getHostAddress();

                        Log.d(LOG_TAG, "adddress ip: " + address);

                        dataInputStream = new DataInputStream(socket.getInputStream());
                        dataOutputStream = new DataOutputStream(socket.getOutputStream());

                        Log.d(LOG_TAG, "adddress ip: " + address);
                        Log.d(LOG_TAG, "message: " + dataInputStream.readUTF());

                        dataOutputStream.writeUTF("Hello!");
                        Log.d(LOG_TAG, "Hello");
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.d(LOG_TAG, " IOException");
                    } finally {
                        if (socket != null) {
                            try {
                                socket.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.d(LOG_TAG, " socket.close();");
                            }
                        }

                        if (dataInputStream != null) {
                            try {
                                dataInputStream.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.d(LOG_TAG, " IOException");
                            }
                        }

                        if (dataOutputStream != null) {
                            try {
                                dataOutputStream.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                                Log.d(LOG_TAG, " IOException");
                            }
                        }
                    }
                }
            }
        }).start();

    }

    public static String wifiIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e(LOG_TAG, "Unable to get host address.");
            ipAddressString = null;
        }

        Log.e(LOG_TAG, "ip : " + ipAddressString);
        return ipAddressString;
    }

//    public static String getIPAddress() {
//
//        ConnectivityManager connectivityManager = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//
//        if (info == null || !info.isConnected()){
//            return null;
//        }
//
//        switch (info.getType()) {
//            case ConnectivityManager.TYPE_WIFI: {
//                WifiManager wifi = (WifiManager) App.getContext().getSystemService(Context.WIFI_SERVICE);
//                if (wifi == null) break;
//                WifiInfo wifi_info = wifi.getConnectionInfo();
//                String ipAddress = "" + (wifi_info.getIpAddress());
//                return ipAddress;
//            }
//            case ConnectivityManager.TYPE_MOBILE: {
//                Enumeration<NetworkInterface> interfaceEnumerations = null;
//                try {
//                    interfaceEnumerations = NetworkInterface.getNetworkInterfaces();
//                    while(interfaceEnumerations.hasMoreElements()){
//                        NetworkInterface interfac = interfaceEnumerations.nextElement();
//                        Enumeration<InetAddress> inetAddresses = interfac.getInetAddresses();
//                        while(inetAddresses.hasMoreElements()){
//                            InetAddress address = inetAddresses.nextElement();
//                            if (!address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
//                                String addressIp = address.getHostAddress();
//                                if(TextUtils.isEmpty(addressIp) || !addressIp.matches("(\\d{1,3}.){3}\\d{1,3}")) continue;
//                                return addressIp;
//                            }
//                        }
//                    }
//                } catch (SocketException e) {
////                    e.printStackTrace();
//                }
//
//                break;
//            }
//        }
//
//        return null;
//    }

    public static void f() {
        try {
            Log.d(LOG_TAG, "Socket clientSocket = new Socket");
            Log.d(LOG_TAG, "getIPAddress() :" + getIP());
            Socket clientSocket = new Socket(getIP(), PORT);
            // 1024 is an arbitrary number, could be 512, 65535, etc
            byte[] buffer = new byte[1024];
            int ret = 0;
            while ((ret = clientSocket.getInputStream().read(buffer)) > 0) {
                Log.d(LOG_TAG, "hello");
                // from now on it's up to you what to do with the data you read
            }
            clientSocket.close();
            Log.d(LOG_TAG, "clientSocket.close();");
        } catch (Exception e) {
            Log.d(LOG_TAG, "exception" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean setProxy(WebView webview, String host, int port) {

        String applicationClassName = App.getContext().getPackageName();

        // 3.2 (HC) or lower
        if (Build.VERSION.SDK_INT <= 13) {
            return setProxyUpToHC(webview, host, port);
        }
        // ICS: 4.0
        else if (Build.VERSION.SDK_INT <= 15) {
            return setProxyICS(webview, host, port);
        }
        // 4.1-4.3 (JB)
        else if (Build.VERSION.SDK_INT <= 18) {
            return setProxyJB(webview, host, port);
        }
        // 4.4 (KK) & 5.0 (Lollipop)
        else {
            return setProxyKKPlus(webview, host, port, applicationClassName);
        }
    }


    private static final String LOG_TAG = "YYY";

    /**
     * Set Proxy for Android 3.2 and below.
     */
    @SuppressWarnings("all")
    private static boolean setProxyUpToHC(WebView webview, String host, int port) {
        Log.d(LOG_TAG, "Setting proxy with <= 3.2 API.");

        HttpHost proxyServer = new HttpHost(host, port);
        // Getting network
        Class networkClass = null;
        Object network = null;
        try {
            networkClass = Class.forName("android.webkit.Network");
            if (networkClass == null) {
                Log.e(LOG_TAG, "failed to get class for android.webkit.Network");
                return false;
            }
            Method getInstanceMethod = networkClass.getMethod("getInstance", Context.class);
            if (getInstanceMethod == null) {
                Log.e(LOG_TAG, "failed to get getInstance method");
            }
            network = getInstanceMethod.invoke(networkClass, new Object[]{webview.getContext()});
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error getting network: " + ex);
            return false;
        }
        if (network == null) {
            Log.e(LOG_TAG, "error getting network: network is null");
            return false;
        }
        Object requestQueue = null;
        try {
            Field requestQueueField = networkClass
                    .getDeclaredField("mRequestQueue");
            requestQueue = getFieldValueSafely(requestQueueField, network);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error getting field value");
            return false;
        }
        if (requestQueue == null) {
            Log.e(LOG_TAG, "Request queue is null");
            return false;
        }
        Field proxyHostField = null;
        try {
            Class requestQueueClass = Class.forName("android.net.http.RequestQueue");
            proxyHostField = requestQueueClass
                    .getDeclaredField("mProxyHost");
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error getting proxy host field");
            return false;
        }

        boolean temp = proxyHostField.isAccessible();
        try {
            proxyHostField.setAccessible(true);
            proxyHostField.set(requestQueue, proxyServer);
        } catch (Exception ex) {
            Log.e(LOG_TAG, "error setting proxy host");
        } finally {
            proxyHostField.setAccessible(temp);
        }

        Log.d(LOG_TAG, "Setting proxy with <= 3.2 API successful!");
        return true;
    }

    @SuppressWarnings("all")
    private static boolean setProxyICS(WebView webview, String host, int port) {
        try {
            Log.d(LOG_TAG, "Setting proxy with 4.0 API.");

            Class jwcjb = Class.forName("android.webkit.JWebCoreJavaBridge");
            Class params[] = new Class[1];
            params[0] = Class.forName("android.net.ProxyProperties");
            Method updateProxyInstance = jwcjb.getDeclaredMethod("updateProxy", params);

            Class wv = Class.forName("android.webkit.WebView");
            Field mWebViewCoreField = wv.getDeclaredField("mWebViewCore");
            Object mWebViewCoreFieldInstance = getFieldValueSafely(mWebViewCoreField, webview);

            Class wvc = Class.forName("android.webkit.WebViewCore");
            Field mBrowserFrameField = wvc.getDeclaredField("mBrowserFrame");
            Object mBrowserFrame = getFieldValueSafely(mBrowserFrameField, mWebViewCoreFieldInstance);

            Class bf = Class.forName("android.webkit.BrowserFrame");
            Field sJavaBridgeField = bf.getDeclaredField("sJavaBridge");
            Object sJavaBridge = getFieldValueSafely(sJavaBridgeField, mBrowserFrame);

            Class ppclass = Class.forName("android.net.ProxyProperties");
            Class pparams[] = new Class[3];
            pparams[0] = String.class;
            pparams[1] = int.class;
            pparams[2] = String.class;
            Constructor ppcont = ppclass.getConstructor(pparams);

            updateProxyInstance.invoke(sJavaBridge, ppcont.newInstance(host, port, null));

            Log.d(LOG_TAG, "Setting proxy with 4.0 API successful!");
            return true;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "failed to set HTTP proxy: " + ex);
            return false;
        }
    }

    /**
     * Set Proxy for Android 4.1 - 4.3.
     */
    @SuppressWarnings("all")
    private static boolean setProxyJB(WebView webview, String host, int port) {
        Log.d(LOG_TAG, "Setting proxy with 4.1 - 4.3 API.");

        try {
            Class wvcClass = Class.forName("android.webkit.WebViewClassic");
            Class wvParams[] = new Class[1];
            wvParams[0] = Class.forName("android.webkit.WebView");
            Method fromWebView = wvcClass.getDeclaredMethod("fromWebView", wvParams);
            Object webViewClassic = fromWebView.invoke(null, webview);

            Class wv = Class.forName("android.webkit.WebViewClassic");
            Field mWebViewCoreField = wv.getDeclaredField("mWebViewCore");
            Object mWebViewCoreFieldInstance = getFieldValueSafely(mWebViewCoreField, webViewClassic);

            Class wvc = Class.forName("android.webkit.WebViewCore");
            Field mBrowserFrameField = wvc.getDeclaredField("mBrowserFrame");
            Object mBrowserFrame = getFieldValueSafely(mBrowserFrameField, mWebViewCoreFieldInstance);

            Class bf = Class.forName("android.webkit.BrowserFrame");
            Field sJavaBridgeField = bf.getDeclaredField("sJavaBridge");
            Object sJavaBridge = getFieldValueSafely(sJavaBridgeField, mBrowserFrame);

            Class ppclass = Class.forName("android.net.ProxyProperties");
            Class pparams[] = new Class[3];
            pparams[0] = String.class;
            pparams[1] = int.class;
            pparams[2] = String.class;
            Constructor ppcont = ppclass.getConstructor(pparams);

            Class jwcjb = Class.forName("android.webkit.JWebCoreJavaBridge");
            Class params[] = new Class[1];
            params[0] = Class.forName("android.net.ProxyProperties");
            Method updateProxyInstance = jwcjb.getDeclaredMethod("updateProxy", params);

            updateProxyInstance.invoke(sJavaBridge, ppcont.newInstance(host, port, null));
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Setting proxy with >= 4.1 API failed with error: " + ex.getMessage());
            return false;
        }

        Log.d(LOG_TAG, "Setting proxy with 4.1 - 4.3 API successful!");
        return true;
    }

    // from https://stackoverflow.com/questions/19979578/android-webview-set-proxy-programatically-kitkat
    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    private static boolean setProxyKKPlus(WebView webView, String host, int port, String applicationClassName) {
        Log.d(LOG_TAG, "Setting proxy with >= 4.4 API.");

        Context appContext = webView.getContext().getApplicationContext();
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port + "");
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port + "");
        try {
            Class applictionCls = Class.forName(applicationClassName);
            Field loadedApkField = applictionCls.getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(appContext);
            Class loadedApkCls = Class.forName("android.app.LoadedApk");
            Field receiversField = loadedApkCls.getDeclaredField("mReceivers");
            receiversField.setAccessible(true);
            Map receivers = (Map) receiversField.get(loadedApk);
            for (Object receiverMap : receivers.values()) {
                for (Object rec : ((Map) receiverMap).keySet()) {
                    Class clazz = rec.getClass();
                    if (clazz.getName().contains("ProxyChangeListener")) {
                        Method onReceiveMethod = clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
                        Intent intent = new Intent(Proxy.PROXY_CHANGE_ACTION);

                        onReceiveMethod.invoke(rec, appContext, intent);
                    }
                }
            }

            Log.d(LOG_TAG, "Setting proxy with >= 4.4 API successful!");
            return true;
        } catch (ClassNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (NoSuchFieldException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (IllegalAccessException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (IllegalArgumentException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (NoSuchMethodException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (InvocationTargetException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        }
        return false;
    }

    private static Object getFieldValueSafely(Field field, Object classInstance) throws IllegalArgumentException, IllegalAccessException {
        boolean oldAccessibleValue = field.isAccessible();
        field.setAccessible(true);
        Object result = field.get(classInstance);
        field.setAccessible(oldAccessibleValue);
        return result;
    }


    /**
     * Convert byte array to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (int idx = 0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     *
     * @param str
     * @return array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception ex) {
            }
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param useIPv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix

                                String add = delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();

                                Log.d(LOG_TAG, "getIp : " + add);
                                return add;
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

}
