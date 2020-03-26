package com.tadigital.magento.core.util;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Request;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test {

    private static final String PROTOCOL = "http";
    private static final int PORT = 4502;
    private static final String HOST = "localhost";
    private static final String COOKIE_NAME = "login-token";
    private static final String SET_COOKIE_NAME = "Set-Cookie";
    private static final String path = "/content/ascentreference/glueguns.html";

    public static void main(String[] args) {
        String postCall = String.format("%s://%s:%s/j_security_check", PROTOCOL, HOST, PORT);
        String getCall = String.format("%s://%s:%s%s", PROTOCOL, HOST, PORT, path);
        try {
            List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("j_username", "admin"));
            nameValuePairs.add(new BasicNameValuePair("j_password", "admin"));
            nameValuePairs.add(new BasicNameValuePair("j_validate", "true"));
           HttpResponse response = Request.Post(postCall).bodyForm(nameValuePairs).execute().returnResponse();
            int status = response.getStatusLine().getStatusCode();
            if (status == 200){
                Header[] headers = response.getHeaders(SET_COOKIE_NAME);
                for (Header header : headers) {
                    System.out.println(header);
                    String headerValue = header.getValue();
                    if (headerValue.startsWith(COOKIE_NAME + "=")) {
                        int index = headerValue.indexOf(';');
                        if (index > 0) {
                           String token = headerValue.substring(COOKIE_NAME.length() + 1, index);
                           HttpResponse httpResponse = Request.Get(getCall).setHeader("Cookie", String.format("%s=%s", COOKIE_NAME, token)).execute().returnResponse();
                           if (httpResponse.getStatusLine().getStatusCode()==200){
                               System.out.println(EntityUtils.toString(httpResponse.getEntity()));
                           }
                        }
                    }
                }
            }
        } catch (IOException  e) {
            System.out.println(e.getMessage());
        }
    }
}
