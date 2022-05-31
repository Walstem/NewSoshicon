package com.bulat.soshicon2.BottomNavigation.account;

import static com.bulat.soshicon2.constants.constants.DATABASE;
import static com.bulat.soshicon2.constants.constants.ID;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

class UploadGallery extends AsyncTask<String, String, String> {
    Context context;
    String filename;
    byte[] imgArray;
    int numberPhotoGallery;

    UploadGallery(byte[] imgArray, String filename, int numberPhotoGallery) {
        this.imgArray = imgArray;
        this.filename = filename;
        this.numberPhotoGallery = numberPhotoGallery;
    }

    @Override
    protected String doInBackground(String... strings) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://j911147y.beget.tech/" + filename);
        SharedPreferences sp = context.getSharedPreferences(DATABASE, 0);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
        final String ConvertImage = Base64.encodeToString(imgArray, Base64.DEFAULT);

        nameValuePairs.add(new BasicNameValuePair("gallery_image", ConvertImage));
        nameValuePairs.add(new BasicNameValuePair("number_photo_gallery", Integer.toString(numberPhotoGallery)));
        nameValuePairs.add(new BasicNameValuePair("id", sp.getString(ID, "")));
        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        } catch (UnsupportedEncodingException e) {
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
            InputStreamReader reader = new InputStreamReader(entity.getContent(), StandardCharsets.UTF_8);
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);

        } catch (IOException e) {
            e.printStackTrace();
        }


        httpclient.getConnectionManager().shutdown();
        return null;
    }

    public static byte[] ReadFileOrSaveInDeviceGallery(Context context, String filename, int numberPhoto) throws IOException {
        String compressPath = context.getFilesDir() + "/compress_gallery_photo_" + numberPhoto + ".jpg";
        System.out.println(compressPath);

        //Создание объекта записи сжатой фографии на устройсвто
        FileOutputStream out = new FileOutputStream(compressPath);
        //Создание объекта чтения фото с устройства
        File file = new File(filename);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        //чтение
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        //запись
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        out.close();
        //запись пути к сжатой фотографии
        SharedPreferences sp = context.getSharedPreferences(DATABASE, 0);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("compress_gallery_photo_" + numberPhoto, compressPath);
        System.out.println("ЭТО ОНО2:" + sp.getString("compress_gallery_photo_" + numberPhoto, ""));
        ed.apply();
        System.out.println("1234" + compressPath);

        //сохранение пути к картинке

        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }
}