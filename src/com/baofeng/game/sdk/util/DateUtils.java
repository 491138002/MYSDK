package com.baofeng.game.sdk.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author sailor
 * 
 */
public class DateUtils  {
	public static String MONTH_ENAME[] = { "JAN", "FEB", "MAR", "APR", "MAY",
			"JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
	public static String DAY_CNAME[] = { "周日", "周一", "周二", "周三", "周四", "周五",
			"周六" };
	// 默认日期格式
	public static final String[] parsePatterns = new String[] {
			"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "HH:mm",
			"yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd","yyyyMMdd",
			"MM月dd日 HH:mm", "yyyyMMddHHmmssSSS", "yyyy年MM月dd日 HH:mm",
			"MM-dd HH:mm", "yyyy年MM月dd日"
	// 这里可以增加更多的日期格式，用得多的放在前面
	};

	/**
	 * 根据当前给定的日期获取当前天是星期几(中国版的)
	 * 
	 * @param date
	 *            任意时间
	 * @return
	 */
	public static String getChineseWeek(Date date) {
		final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
				"星期六" };
		Calendar c = getCalendar(date);
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		return dayNames[dayOfWeek - 1];

	}

	/**
	 * 
	 * @param date
	 *            格式 2011-5-12
	 * @return 星期
	 */
	public static String getWeek(String date) {
		Date dat = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			dat = simpleDateFormat.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return getChineseWeek(dat);
	}

	/**
	 * 【由date类型获取字符串类型】
	 * 
	 * @param date
	 * @param parsePattern
	 *            :转换后的格式，如"yyyy-MM-dd" 或"yyyy年MM月dd日"
	 * @return
	 * @author liuwei
	 */
	public static String getStringByDate(Date date, String parsePattern) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(parsePattern);
		String dateStr = dateFormat.format(date);
		return dateStr;
	}

	/**
	 * 使用默认的日期格式将字符串转换为日期
	 * 
	 * @param str
	 *            要转换的字符串
	 * @return 转换后的日期
	 * @throws ParseException
	 *             默认匹配的日期格式  yyyy-MM-dd
	 */
	public static Date parseDate(String str) throws ParseException {
		
		return parseDate(str, parsePatterns[1]);
	}

	/**
	 * 使用给定的日期格式将字符串转换为日期
	 * 
	 * @param str
	 *            要转换的字符串
	 * @param parsePattern
	 *            日期格式字符串
	 * @return 转换后的日期
	 * @throws ParseException
	 *             日期格式不匹配
	 */
	public static Date parseDate(String str, String parsePattern)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(parsePattern);
		   Date date = null;
		   try {
		    date = format.parse(str);
		   } catch (ParseException e) {
		    e.printStackTrace();
		   }
		   return date;

	}

	/**
	 * @description 【获取当前日期日历】
	 * @param date
	 * @return
	 * @author zhangyun
	 */
	public static Calendar getCalendar(Date date) {
		if (date == null)
			return null;
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		return c;
	}

	/**
	 * @description 【date加months月】 如果要想直接用date对象，请这样调用 addMonth(getCalendar(Date
	 *              date), int months)
	 * @param c
	 * @param months
	 * @return
	 * @author zhangyun
	 */
	public static Date addMonth(Calendar c, int months) {
		if (c == null)
			return null;
		c.add(Calendar.MONTH, months);
		return c.getTime();
	}

	/**
	 * @description 【date加days日】 如果要想直接用date对象，请这样调用 addDay(getCalendar(Date
	 *              date), int days)
	 * @param c
	 * @param days
	 * @return
	 * @author zhangyun
	 */
	public static Date addDay(Calendar c, int days) {
		if (c == null)
			return null;
		c.add(Calendar.DAY_OF_MONTH, days);
		return c.getTime();
	}
	
	
	public static Date addDay(Date date, int days) {
		Calendar c = getCalendar(date);
		if (c == null)
			return null;
		c.add(Calendar.DAY_OF_MONTH, days);
		return c.getTime();
	}

	/**
	 * 【获取时间间隔分钟】
	 * @param minDate
	 * @param maxDate
	 * @return
	 */
	public static double getIntervalMinutes(long minDate, long maxDate) {
		double mins = (maxDate - minDate) * 1.0 / 60;
		return mins;
	}
	/**
	 * @description 【获取时间间隔分钟】
	 * @param minDate
	 * @param maxDate
	 * @return
	 * @author zhangyun
	 */
	public static double getIntervalMinutes(Date minDate, Date maxDate) {
		double days = 0.0;
		if (minDate == null || maxDate == null) {
			return days;
		}
		try {
			long interval = maxDate.getTime() - minDate.getTime();
			days = Double.valueOf(interval) / 1000 / 60;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return days;
	}

	/**
	 * @description 【获取时间间隔小时】
	 * @param minDate
	 * @param maxDate
	 * @return
	 * @author zhangyun
	 */
	public static double getIntervalHours(Date minDate, Date maxDate) {
		double days = 0.0;
		if (minDate == null || maxDate == null) {
			return days;
		}
		try {
			days = getIntervalMinutes(minDate, maxDate) / 60;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return days;
	}

	/**
	 * @description 【校验是否隔夜】
	 * @param startTime
	 * @param endTime
	 * @author zhangyun
	 * @return
	 */
	public static boolean checkIsIntervalDay(Date startDate, Date endDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(startDate);
		int firstDay = c.get(Calendar.DAY_OF_MONTH);
		c.setTime(endDate);
		int lastDay = c.get(Calendar.DAY_OF_MONTH);
		return firstDay == lastDay ? true : false;
	}
	
	/**
	 * 校验是否隔年
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static boolean checkIsIntervalYear(Date startTime, Date endTime){
		boolean isIntervalDay = false;
		Calendar c = Calendar.getInstance();
		c.setTime(startTime);
		int startYear = c.get(Calendar.YEAR);
		c.setTime(endTime);
		int endYear = c.get(Calendar.YEAR);
		return endYear - startYear > 0;
	}

	/**
	 * @description 【获取时间间隔天】
	 * @param minDate
	 * @param maxDate
	 * @return
	 * @author zhangyun
	 */
	public static double getIntervalDays(Date minDate, Date maxDate) {
		Calendar c = Calendar.getInstance();
		c.setTime(minDate);
		int firstYear = c.get(Calendar.YEAR);
		int firstMonth = c.get(Calendar.MONTH);
		int firstDay = c.get(Calendar.DAY_OF_MONTH);
		c.setTime(maxDate);
		int lastYear = c.get(Calendar.YEAR);
		int lastMonth = c.get(Calendar.MONTH);
		int lastDay = c.get(Calendar.DAY_OF_MONTH);
		if(lastYear - firstYear > 0)
			return 365;
		else if(lastMonth - firstMonth > 0)
			return 30;
		else
			return lastDay - firstDay;
	}

	/**
	 * @Title :isValidDate
	 * @Description :验证yyyy-MM-dd 日期是否合法
	 * @params @param sDate
	 * @params @return
	 * @return boolean
	 * @throws
	 * 
	 */
	public static boolean isValidDate(String sDate) {
		String datePattern1 = "\\d{4}-\\d{2}-\\d{2}";
		String datePattern2 = "^((\\d{2}(([02468][048])|([13579][26]))"
				+ "[\\-\\/\\s]?((((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|"
				+ "(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?"
				+ "((0?[1-9])|([1-2][0-9])))))|(\\d{2}(([02468][1235679])|([13579][01345789]))[\\-\\/\\s]?("
				+ "(((0?[13578])|(1[02]))[\\-\\/\\s]?((0?[1-9])|([1-2][0-9])|(3[01])))|(((0?[469])|(11))[\\-\\/\\s]?"
				+ "((0?[1-9])|([1-2][0-9])|(30)))|(0?2[\\-\\/\\s]?((0?[1-9])|(1[0-9])|(2[0-8]))))))";
		if ((sDate != null)) {
			Pattern pattern = Pattern.compile(datePattern1);
			Matcher match = pattern.matcher(sDate);
			if (match.matches()) {
				pattern = Pattern.compile(datePattern2);
				match = pattern.matcher(sDate);
				return match.matches();
			} else {
				return false;
			}
		}
		return false;
	}
	
	public static int getIntervalYears(Date date1, Date date2) {
		Calendar firstCal = Calendar.getInstance();
		Calendar secondCal = Calendar.getInstance();

		firstCal.setTime(date1);
		secondCal.setTime(date2);

		int year = firstCal.get(Calendar.YEAR) - secondCal.get(Calendar.YEAR);
		int month = firstCal.get(Calendar.MONTH)
				- secondCal.get(Calendar.MONTH);
		int day = firstCal.get(Calendar.DAY_OF_MONTH)
				- secondCal.get(Calendar.DAY_OF_MONTH);
		
		//计算实际年份月份天数差
		year = year
				- ((month > 0) ? 0 : ((month < 0) ? 1 : ((day >= 0 ? 0 : 1))));
		month = (month < 0) ? (day > 0 ? 12 + month : 12 + month - 1)
                : (day >= 0 ? month : month - 1);
		
		firstCal.add(Calendar.MONTH, -1);
        day = (day < 0) ? (perMonthDays(firstCal)) + day : day;
        int timeStr = year;
        return timeStr;
		
	}


    //判断一个时间所在月有多少天
    public static int perMonthDays(Calendar cal) {
        int maxDays = 0;
        int month = cal.get(Calendar.MONTH);
        switch (month) {
        case Calendar.JANUARY:
        case Calendar.MARCH:
        case Calendar.MAY:
        case Calendar.JULY:
        case Calendar.AUGUST:
        case Calendar.OCTOBER:
        case Calendar.DECEMBER:
            maxDays = 31;
            break;
        case Calendar.APRIL:
        case Calendar.JUNE:
        case Calendar.SEPTEMBER:
        case Calendar.NOVEMBER:
            maxDays = 30;
            break;
        case Calendar.FEBRUARY:
            if (isLeap(cal.get(Calendar.YEAR))) {
                maxDays = 29;
            } else {
                maxDays = 28;
            }
            break;
        }
        return maxDays;
    }

    //判断某年是否是闰年
    public static boolean isLeap(int year) {
        boolean leap = false;
        if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
            leap = true;
        }
        return leap;
    }
	
    
    //获得当前系统时间
	public static String getDate(){
		SimpleDateFormat fromat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		c.get(Calendar.YEAR);
		c.get(Calendar.MONTH+1);
		c.get(Calendar.DAY_OF_MONTH);
		String time = fromat.format(c.getInstance().getTime());
		return time;
	}
	
	
	//时间判断
	public static int DateVaiue(String date1,String date2){
		int res = 0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		try {
			c1.setTime(df.parse(date1));
			c2.setTime(df.parse(date2));
			res = c1.compareTo(c2);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static int getDateForYear(String dptDate ,String birthDate){
		int year = 0;
		try {
			if(StringUtil.isEmpty(dptDate)){
				dptDate = getDate();
			}
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			Date dptDates = df.parse(dptDate);
			Date birDates = df.parse(birthDate);
			year = DateUtils.getIntervalYears(dptDates,birDates);
			
		} catch (Exception e) {
		}
		
		return year;
	}
	
	public static String getDateByLong(long timeLong, String parsePattern){
		String result = "";
		try{
			Date date = getCalendarByLong(timeLong * 1000).getTime();
			result = getStringByDate(date, parsePattern);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static Date getDateByLong2(long timeLong, String parsePattern){
		Date result = new Date();
		try{
			result = getCalendarByLong(timeLong * 1000).getTime();
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getTimeOffsetStrByLong(long timeLong){
		String result = "";
		try{
			Date curDate = new Date();
			Date pDate = getCalendarByLong(timeLong * 1000).getTime();
			
			long curTimeLong = getCalendar(curDate).getTimeInMillis() / 1000;
			
			if(checkIsIntervalYear(pDate, curDate)){
				//大于一年
				result = getStringByDate(pDate, parsePatterns[8]);
			}else if(getIntervalDays(pDate, curDate) > 1){
				//大于一天，显示日期
				result = getStringByDate(pDate, parsePatterns[6]);
			}else{
				//小于一天，显示时间
				if(checkIsIntervalDay(pDate, curDate)){
					//当天
					if(curTimeLong - timeLong <= 0){
						result = "刚刚";
					}else if((curTimeLong - timeLong) < 60){
						result = (curTimeLong - timeLong) + "秒之前";
					}else if((curTimeLong - timeLong) / 60 < 60){
						result = ((curTimeLong - timeLong) / 60) + "分钟前";
					}else{
						result = "今天 " + getStringByDate(pDate, parsePatterns[2]);
					}
				}else{
					//昨天
					result = "昨天 " + getStringByDate(pDate, parsePatterns[2]);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 将long类型转换为Calendar类型
	 * @param timeLong
	 * @return
	 */
	public static Calendar getCalendarByLong(long timeLong){
		Calendar c = null;
		try{
			c = Calendar.getInstance();
			c.setTimeInMillis(timeLong);
		}catch(Exception e){
			e.printStackTrace();
		}
		return c;
	}
	
	// 获取当前时间戳与指定时间的差值字符串
	public static String getTimeOffsetStr(Calendar offsetC) {
		Calendar nowC = Calendar.getInstance();
		long mills_offset = nowC.getTimeInMillis() - offsetC.getTimeInMillis();
		String util = "";
		if (mills_offset > 0)
			util = "前";
		else
			util = "后";

		mills_offset = Math.abs(mills_offset);

		int min_split = 60 * 1000;
		if (mills_offset < min_split) {
			// 小于1分钟
			return mills_offset / 1000 + "秒" + util;
		} else {
			int hour_split = 60 * min_split;
			int day_split = 24 * hour_split;
			if (mills_offset < hour_split) {
				// 小于1小时
				long min = mills_offset / min_split;
				long sec = mills_offset % min_split / 1000;
				String minStr = min + "分";
				String secStr = sec + "秒";
				return minStr + secStr + util;
			} else if (mills_offset < day_split) {
				// 小于一天
				long hour = mills_offset / hour_split;
				long min = mills_offset % hour_split / min_split;
				String hourStr = hour + "时";
				String minStr = min + "分";
				return hourStr + minStr + util;
			} else {
				// 大于一天
				long day = mills_offset / day_split;
				return day + "天" + util;
			}
		}
	}
	
	/**
	 * 比较时间
	 * @param dateLong
	 * @return
	 */
	public static boolean compareByLong(long dateLong){
		try{
			Calendar c = Calendar.getInstance();
			long curDateLong = c.getTimeInMillis();
			return (dateLong * 1000) > curDateLong;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
}
