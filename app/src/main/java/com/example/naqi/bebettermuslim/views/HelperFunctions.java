package com.example.naqi.bebettermuslim.views;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import com.example.naqi.bebettermuslim.Utils.Constants;
import org.joda.time.Chronology;
import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.IslamicChronology;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;

import static com.example.naqi.bebettermuslim.Utils.Constants.MONTHS;

/**
 * Created by naqi on 22,January,2019
 */
public class HelperFunctions {

    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public String readXMLFromAssets(Context context, String fileName) {


        InputStream iStream = null;
        try {
            iStream = context.getAssets().open(fileName);
            int length = iStream.available();
            byte[] data = new byte[length];
            iStream.read(data);
            return new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String readXMLFromFile(String fileName) {

        File file = new File(fileName);

        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();

            return text.toString();

        } catch (IOException e) {
            e.printStackTrace();
            //You'll need to add proper error handling here
            return "404";
        }
    }


    public static String getCodeFromNumber(String number) {

        char[] numberArray = number.toCharArray();

        String character = "";

        for (int i = 0; i < numberArray.length; i++) {
            int intNumber = ((int) numberArray[i] - 48);
            character += Character.toString((char) (intNumber + 1632));
        }

        return character;
    }

    public static String getNumberFromCode(String code) {

        char codeArray[] = code.toCharArray();

        String numbers = "";

        for (int i = 0; i < codeArray.length; i++) {
            int intNumber = (int) codeArray[i] - 1632;
            numbers += Character.toString((char) (intNumber + 48));
        }

        return numbers;
    }

    public static String formatNumber(int number) {

        String string = new String();

        if (number < 10) {
            string = "00" + number;
        } else if (number >= 10 && number < 100) {
            string = "0" + number;
        } else {
            string = String.valueOf(number);
        }

        return string;
    }

    public static String stringForTime(int timeMs) {

        StringBuilder mFormatBuilder = new StringBuilder();
        Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        return new JSONObject(readTextFromURL(urlString));
    }

    public String getIslamicDate(Calendar cal) {
        Chronology iso = ISOChronology.getInstanceUTC();
        Chronology hijri = IslamicChronology.getInstanceUTC();

        LocalDate todayIso = new LocalDate(cal, iso);
        LocalDate todayHijri = new LocalDate(todayIso.toDateTimeAtStartOfDay(),
                hijri);

        return Constants.MONTHS[todayHijri.getMonthOfYear() - 1] + " " + todayHijri.getDayOfMonth() + ", " + todayHijri.getYear() + " AH";

    }

    public static String readTextFromURL(String urlString) throws IOException {

        HttpURLConnection urlConnection = null;

        URL url = new URL(urlString);

        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        urlConnection.setDoOutput(true);

        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

        char[] buffer = new char[1024];

        String jsonString = new String();

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        return sb.toString();
    }

    public static String DownloadImageIfNotExist(String imageUrl, String fileName){
        fileName = !fileName.endsWith(".jpg") ? fileName.concat(".jpg") : fileName;
        File storagePath = Environment.getExternalStorageDirectory();
        File imageFile = new File(storagePath, fileName);
        try {
            if (!imageFile.exists()) {
                InputStream input = new URL(imageUrl).openStream();
                OutputStream output = new FileOutputStream(storagePath + "/" + fileName);
                char[] buffer = new char[1024];
                try {
                    byte[] buffers = new byte[1024];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(buffers, 0, buffer.length)) >= 0) {
                        output.write(buffers, 0, bytesRead);
                    }
                } finally {
                    input.close();
                    output.close();
                }
            }
            return imageFile.getAbsolutePath();
        }catch (IOException e){
            return null;
        }
    }
}
