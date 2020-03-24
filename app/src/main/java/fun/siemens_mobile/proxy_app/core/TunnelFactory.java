package fun.siemens_mobile.proxy_app.core;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import fun.siemens_mobile.proxy_app.tunnel.Config;
import fun.siemens_mobile.proxy_app.tunnel.RawTunnel;
import fun.siemens_mobile.proxy_app.tunnel.Tunnel;
import fun.siemens_mobile.proxy_app.tunnel.httpconnect.HttpConnectConfig;
import fun.siemens_mobile.proxy_app.tunnel.httpconnect.HttpConnectTunnel;

public class TunnelFactory {

    public static Tunnel wrap(SocketChannel channel, Selector selector) {
        return new RawTunnel(channel, selector);
    }

    public static Tunnel createTunnelByConfig(InetSocketAddress destAddress, Selector selector) throws Exception {
        if (destAddress.isUnresolved()) {
            Config config = ProxyConfig.Instance.getDefaultTunnelConfig(destAddress);
            if (config instanceof HttpConnectConfig) {
                return new HttpConnectTunnel((HttpConnectConfig) config, selector);
            }
            throw new Exception("The config is unknow.");
        } else {
            return new RawTunnel(destAddress, selector);
        }
    }

}
