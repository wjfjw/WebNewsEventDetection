package newsEventDetection.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeConversion 
{
	public static String getTimeString(long time) 
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
		String dateString = formatter.format(calendar.getTime());
		
		return dateString;
	}
}
