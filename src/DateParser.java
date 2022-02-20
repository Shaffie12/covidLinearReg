import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.time.temporal.ChronoUnit;

//class to parse the dates from the file so they can be used on a graph axis
public class DateParser {
    //var
    //constructor
    //method


    static long getDateDiff(String date1, String date2){
        SimpleDateFormat df =new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        try{
            Date first_date = df.parse(date1);
            Date second_date = df.parse(date2);
            return (ChronoUnit.DAYS.between(first_date.toInstant(),second_date.toInstant()));

        }
        catch (ParseException e){
            System.out.println("bad value for date");
            return -1;
        }

    }
}
