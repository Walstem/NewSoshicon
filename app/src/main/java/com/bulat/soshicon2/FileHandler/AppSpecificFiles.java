package com.bulat.soshicon2.FileHandler;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class AppSpecificFiles {
    public void FirstWriteFile(String filename, String content, Context context) throws IOException {

        File file = new File(context.getFilesDir(), filename);

        //We write the data entered by the user into a file
        FileWriter filewriter = new FileWriter(file);
        System.out.println(file.getName()+ file.exists());
        BufferedWriter out = new BufferedWriter(filewriter);
        out.write(content);

        out.close();
    }
    public void WriteFile(String filename, String content, Context context) throws IOException {

        String data = ReadFile(context, filename);
        FirstWriteFile(filename, data + content, context);
    }
    public String ReadFile(Context context, String filename) throws FileNotFoundException {

        FileReader reader = new FileReader(context.getFilesDir() + filename);
        Scanner scan = new Scanner(reader);
        String data = scan.nextLine();
        System.out.println(data);

        return data;
    }
}
