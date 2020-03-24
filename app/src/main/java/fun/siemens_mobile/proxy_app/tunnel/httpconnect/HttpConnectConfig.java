package fun.siemens_mobile.proxy_app.tunnel.httpconnect;

import android.net.Uri;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;

import fun.siemens_mobile.proxy_app.tunnel.Config;

public class HttpConnectConfig extends Config {

    private String UserName;
    private String Password;

    public static HttpConnectConfig parse(String proxyInfo) {

        HttpConnectConfig config = new HttpConnectConfig();
        Uri uri = Uri.parse(proxyInfo);
        String userInfoString = uri.getUserInfo();
        if (userInfoString != null) {
            String[] userStrings = userInfoString.split(":");
            config.UserName = userStrings[0];
            if (userStrings.length >= 2) {
                config.Password = userStrings[1];
            }
        }
        config.ServerAddress = new InetSocketAddress(uri.getHost(), uri.getPort());
        return config;
    }

    /*
    private static String decideProtocol(String url) throws IOException {
        if ("https".equals(new URL(url).getProtocol())) {
            return "https://" + url;
        } else if ("http".equals(new URL(url).getProtocol())) {
            return "http://" + url;
        }else {
            return null;
        }
    }
     */

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return this.toString().equals(o.toString());
    }

    @Override
    public String toString() {
        return String.format("http://%s:%s@%s", UserName, Password, ServerAddress);
    }
}
