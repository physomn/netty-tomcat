package com.java.tomcat;

import com.java.tomcat.http.IRequest;
import com.java.tomcat.http.IResponse;
import com.java.tomcat.http.IServlet;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.EventExecutorGroup;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ITomcat {

    private int port;

    private Map<String, IServlet> servletMapping = new HashMap<String, IServlet>();

    public ITomcat(int port) {
        this.port = port;
    }

    public void init() {
        try {
            String webInf = this.getClass().getResource("/").getPath();
            InputStream in = new FileInputStream(webInf + "/web.properties");
            Properties properties = new Properties();
            properties.load(in);

            for (Object k : properties.keySet()) {
                String key = k.toString();
                if (key.endsWith("url")) {
                    String url = properties.getProperty(key);
                    String servletName = key.replaceAll("\\.url$", "");
                    Object object = Class.forName(properties.getProperty(servletName + ".className")).newInstance();
                    servletMapping.put(url, (IServlet) object);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel client) throws Exception {
                            client.pipeline().addLast(new HttpResponseEncoder());
                            client.pipeline().addLast(new HttpRequestDecoder());
                            client.pipeline().addLast(new ITomcatHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = server.bind(this.port).sync();
            System.out.println("服务器已启动，端口：" + this.port);
            future.channel().closeFuture().sync();
            System.out.println("ttttt");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class ITomcatHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(msg);
            if (msg instanceof HttpRequest) {
                HttpRequest req = (HttpRequest) msg;

                IRequest request = new IRequest(ctx, req);
                IResponse response = new IResponse(ctx, req);

                String url = request.getUrl();
                if (servletMapping.containsKey(url)) {
                    servletMapping.get(url).service(request, response);
                } else {
                    response.write("404 - NOT FOUND");
                }
            }
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
        }
    }

    public static void main(String[] args) {
        new ITomcat(8080).start();
    }
}
