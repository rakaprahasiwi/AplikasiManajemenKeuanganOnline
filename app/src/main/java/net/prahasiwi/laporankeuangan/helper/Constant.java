package net.prahasiwi.laporankeuangan.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by PRAHASIWI on 06/04/2018.
 */

public class Constant {

    public static final String ID = "id";
    public static final String IMAGE = "image";
    public static final String KEYIMAGE = "key_image";
    public static final String TYPE = "type";
    public static final String CATEGORY = "category";
    public static final String VALUE = "value";
    public static final String VALUE_PLUS = "value_plus";
    public static final String DESCRIBE = "describe";
    public static final String DATE = "date";
    public static final String PATTERN_DATABASE = "yyyy-MM-dd";
    public static final String PATTERN_TV2 = "dd MMM yyyy";
    public static final String PATTERN_TV = "EEEE, dd MMM yyyy";
    public static final String regEx = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}\\b";
    public static String addTitik(String digits) {

        String result = digits;
        if (digits.length() <= 3)
            return digits; // If the original value has 3 digits or  less it returns that value
        for (int i = 0; i < (digits.length() - 1) / 3; i++) {
            int commaPos = digits.length() - 3 - (3 * i);  // comma position in each cicle
            result = result.substring(0, commaPos) + "." + result.substring(commaPos);
        }
        return result;
    }

    public static String changeFormatToView(String givenDate){ //convert (yyyy-MM-dd) to (EEEE, dd MMM yyyy)
        String inputPattern = PATTERN_DATABASE,outputPattern = PATTERN_TV;
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String reqiredDate = null;
        try {
            date = inputFormat.parse(givenDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reqiredDate = outputFormat.format(date);
        return reqiredDate;
    }

    public static String changeFormatToView2(String givenDate){ //convert (EEEE, dd MMM yyyy) to (yyyy-MM-dd)
        String inputPattern = PATTERN_TV,outputPattern2 = PATTERN_TV2;
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern2);

        Date date = null;
        String reqiredDate = null;
        try {
            date = inputFormat.parse(givenDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reqiredDate = outputFormat.format(date);
        return reqiredDate;
    }

    public static String changeFormatToDatabase(String givenDate){ //convert (EEEE, dd MMM yyyy) to (yyyy-MM-dd)
        String inputPattern = PATTERN_TV,outputPattern = PATTERN_DATABASE;
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String reqiredDate = null;
        try {
            date = inputFormat.parse(givenDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        reqiredDate = outputFormat.format(date);
        return reqiredDate;
    }

    public static int sumList(List<Integer> L) { //sum of element list
        int sum = 0;
        if (L != null) {
            for (int i = 0; i < L.size(); i++) {
                sum = sum + L.get(i);
            }
        }
        return sum;
    }
}

