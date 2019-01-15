package com.xd.akvarij;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class DataReadWrite {

    private String fileName = "test123.txt";
    private StringBuilder sb = new StringBuilder();

    private StringBuilder arrayListToString(ArrayList<Data> data) {
        for (int i = 0; i < data.size(); i++) {
            sb.append(
                    data.get(i).alive + "," +
                    data.get(i).lifeStage + "," +
                    data.get(i).age + "," +
                    data.get(i).gender + "," +
                    data.get(i).hunger + "," +
                    data.get(i).vision + "\\n"
            );
        }
        return sb;
    }

    public void writeToFile(ArrayList<Data> data, Context context) {
        try {
            sb = arrayListToString(data);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(fileName, Context.MODE_PRIVATE));
            outputStreamWriter.write(sb.toString());
            outputStreamWriter.close();
            sb.delete(0, sb.length());
            Log.d("DataReadWrite", "data writing done");
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public StringBuilder readFromFile(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            InputStream inputStream = context.openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return stringBuilder;
    }
}
