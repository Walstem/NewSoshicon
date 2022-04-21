package com.bulat.soshicon2.asynctasks;


import android.os.AsyncTask;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;


//класс совершает hhtp зарос на сервер, возращает данные в зависимости от переданного sql запроса
public class SendQuery extends AsyncTask<String, String, String> {
    public String filename;
    // конструктор который определяет к какому файлу будет совершаться запрос
    //input_request_handler.php (скрипт для ввода данных в бд)
    //get_request_handler.php  (скрипт для получения данных в бд)
    public SendQuery(String filename){
        this.filename = filename;
    }

    @Override
    protected String doInBackground(String... query) {

        try {
            URLConnection connection = new URL("http://j911147y.beget.tech/" + filename + query[0]).openConnection();
            System.out.println("http://j911147y.beget.tech/" + filename + query[0]);
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();

            BufferedReader r  = new BufferedReader(new InputStreamReader(connection.getInputStream(), Charset.forName("UTF-8")));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();

        }
        catch ( IOException e) {
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println(result);
    }
}