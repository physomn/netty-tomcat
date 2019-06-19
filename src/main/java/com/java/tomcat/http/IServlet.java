package com.java.tomcat.http;

public abstract class IServlet {

    public void service(IRequest request, IResponse response) {
        if ("GET".equalsIgnoreCase(request.getMethod())) {
            doGet(request, response);
        } else {
            doPost(request, response);
        }
    }

    protected abstract void doPost(IRequest request, IResponse response);

    protected abstract void doGet(IRequest request, IResponse response);
}
