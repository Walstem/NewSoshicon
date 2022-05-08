package com.bulat.soshicon2.BottomNavigation.event;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

class receivingEvent extends AsyncTask<String, String, String> {
    String filename;
    String start, end;

    receivingEvent(String filename, int start, int end){
        this.filename = filename;
        this.start = Integer.toString(start);
        this.end = Integer.toString(end);
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://j911147y.beget.tech/" + filename);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("start", start));
        nameValuePairs.add(new BasicNameValuePair("end", end));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //Execute and get the response.
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert response != null;
            HttpEntity entity = response.getEntity();

            int n = 0;
            char[] buffer = new char[1024 * 4];
            InputStreamReader reader = new InputStreamReader(entity.getContent(), "UTF8");
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);

            httpclient.getConnectionManager().shutdown();
            return writer.toString();

        } catch (IOException e) {
            e.printStackTrace();

            httpclient.getConnectionManager().shutdown();
            return null;
        }
    }
}