package com.example.naqi.bebettermuslim.Utils;

import com.example.naqi.bebettermuslim.models.CustomeModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class JavaUtils {
    public static CustomeModel checkPrayerTimings(Calendar cal, ArrayList<String> prayerNames, ArrayList<String> prayerTimes, ArrayList<String> prayerTimesTomorrow) throws ParseException {
        CustomeModel customeModel = new CustomeModel();
        Date[] formattedTimings = formatTimings(cal, prayerTimes);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa", Locale.US);
        Date currentDate = new Date(System.currentTimeMillis());
        if (formattedTimings != null)
            if (currentDate.before(formattedTimings[0])) {
                customeModel.setPrayerName(prayerNames.get(0));
                customeModel.setPrayerTime(prayerTimes.get(0));
                customeModel.setPrayerTimeLong(dateTimeFormat.parse(dateFormat.format(new Date(System.currentTimeMillis())) + " " + prayerTimes.get(0).replace(" ", ":00 ")).getTime());
                customeModel.setPosition(0);
            }
        for (int i = 0; i < formattedTimings.length - 1; i++) {
            if (currentDate.after(formattedTimings[i]) && currentDate.before(formattedTimings[i + 1])) {
                customeModel.setPrayerName(prayerNames.get(i + 1));
                customeModel.setPrayerTime(prayerTimes.get(i + 1));
                customeModel.setPrayerTimeLong(dateTimeFormat.parse(dateFormat.format(new Date(System.currentTimeMillis())) + " " + prayerTimes.get(i + 1).replace(" ", ":00 ")).getTime());
                customeModel.setPosition(i + 1);
            }
        }
        if (currentDate.after(formattedTimings[formattedTimings.length - 1])) {
            customeModel.setPrayerName(prayerNames.get(0));
            customeModel.setPrayerTime(prayerTimesTomorrow.get(0));
            Calendar calen = Calendar.getInstance();
            calen.setTimeInMillis(System.currentTimeMillis());
            calen.add(Calendar.DAY_OF_MONTH, 1);
            customeModel.setPrayerTimeLong(dateTimeFormat.parse(dateFormat.format(calen.getTime()) + " " + prayerTimesTomorrow.get(0).replace(" ", ":00 ")).getTime());
            customeModel.setPosition(0);
        }
        return customeModel;
    }

    private static Date[] formatTimings(Calendar cal, ArrayList<String> prayerTimes) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:aa", Locale.US);

        Date[] formattedTimings;

            String dateString = dateFormat.format(cal.getTime());
            formattedTimings = new Date[prayerTimes.size()];
            for (int i = 0; i < formattedTimings.length; i++) {
                formattedTimings[i] = dateTimeFormat.parse(dateString + " " + prayerTimes.get(i).replace(" ", ":00 "));
            }
            return formattedTimings;
        }


}
