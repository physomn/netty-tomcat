package com.java.tomcat.servlet;

import com.java.tomcat.http.IRequest;
import com.java.tomcat.http.IResponse;
import com.java.tomcat.http.IServlet;

public class SecondServlet extends IServlet {
    public void doPost(IRequest request, IResponse response) {
        response.write("This is second servlet.");
    }

    public void doGet(IRequest request, IResponse response) {
        doPost(request, response);
    }
}
