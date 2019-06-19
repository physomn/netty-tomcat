package com.java.tomcat.servlet;

import com.java.tomcat.http.IRequest;
import com.java.tomcat.http.IResponse;
import com.java.tomcat.http.IServlet;

public class FirstServlet extends IServlet {
    protected void doPost(IRequest request, IResponse response) {
        response.write("This is first servlet.");
    }

    protected void doGet(IRequest request, IResponse response) {
        doPost(request, response);
    }
}
