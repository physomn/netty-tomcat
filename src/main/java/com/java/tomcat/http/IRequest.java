package com.java.tomcat.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

public class IRequest {

    private ChannelHandlerContext ctx;

    private HttpRequest request;

    public IRequest(ChannelHandlerContext ctx, HttpRequest request) {
        this.ctx = ctx;
        this.request = request;
    }

    public String getMethod() {
        return request.method().name();
    }

    public String getUrl() {
        return request.uri();
    }
}
