package com.spec.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * A reference Web page: http://www.odi.ch/prog/design/datetime.php
 * @author paul
 *
 */
public class Time_Util {
	static public void main( String args[] ) throws ParseException{
	}
	
	static public final String DEFAULT_DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss Z";
	
	static public String now(){
		return now( DEFAULT_DATE_FORMAT_NOW );
	}
	
	static public String now( String dataFormat ) {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
	    return sdf.format(cal.getTime());
	}
	
	
	
	static public String dateToString( Date date, String dataFormat ){
	    Calendar cal = Calendar.getInstance();
	    cal.setTime( date );
	    SimpleDateFormat sdf = new SimpleDateFormat(dataFormat);
	    
	    return sdf.format(cal.getTime());
	}
		
	static public Date stringToDate( String strTime, DateFormat dataFormat ) throws ParseException{
		return ( Date ) dataFormat.parse( strTime );
	}
	
	/**
	 * //TimeZone.getTimeZone("GMT+04:30") 
	 * @param date
	 * @return
	 */
	static public int getHour( Date date, TimeZone tz ){
	    Calendar cal = new GregorianCalendar( tz );
	    cal.setTime( date );
	   
	    return cal.get(Calendar.HOUR_OF_DAY);
	}
	
	static public boolean isInTimeDuration( Date d, Date startDate, Date endDate ){
		if ( d.compareTo( startDate ) < 0 ) {
			_debug.println( "  " + d + " earlier than " + startDate );
			return false;
		}
		
		if ( d.compareTo( endDate ) > 0 ) {
			_debug.println( "  " + d + " later than " + endDate );
			return false;
		}
		
		return true;
	}
	
	/**
	 * 
	 * @param strStartTime
	 * @param imeDFMstartTimeDFM
	 * @param strEndTime
	 * @param endTimeDFM
	 * @return milli-seconds of interval
	 * @throws ParseException
	 */
	static public double computeTimeInterval( String strStartTime, DateFormat imeDFMstartTimeDFM, 
											String strEndTime, DateFormat endTimeDFM ) throws ParseException
	{
//		Debug.println( "compute time interval: " + strStartTime + "\t" + strEndTime );
		Date startDate	= Time_Util.stringToDate( strStartTime, imeDFMstartTimeDFM );
		Date endDate	= Time_Util.stringToDate( strEndTime, endTimeDFM );
		
		double interval = (double) (endDate.getTime() - startDate.getTime());
//		Debug.println( "\n\tInterval = " + interval );
		
		return interval;
	}
}
