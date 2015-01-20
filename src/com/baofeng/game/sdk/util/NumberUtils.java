package com.baofeng.game.sdk.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NumberUtils {
	/** Reusable Long constant for zero. */
    public static final Long LONG_ZERO = new Long(0L);
    /** Reusable Long constant for one. */
    public static final Long LONG_ONE = new Long(1L);
    /** Reusable Long constant for minus one. */
    public static final Long LONG_MINUS_ONE = new Long(-1L);
    /** Reusable Integer constant for zero. */
    public static final Integer INTEGER_ZERO = new Integer(0);
    /** Reusable Integer constant for one. */
    public static final Integer INTEGER_ONE = new Integer(1);
    /** Reusable Integer constant for minus one. */
    public static final Integer INTEGER_MINUS_ONE = new Integer(-1);
    /** Reusable Short constant for zero. */
    public static final Short SHORT_ZERO = new Short((short) 0);
    /** Reusable Short constant for one. */
    public static final Short SHORT_ONE = new Short((short) 1);
    /** Reusable Short constant for minus one. */
    public static final Short SHORT_MINUS_ONE = new Short((short) -1);
    /** Reusable Byte constant for zero. */
    public static final Byte BYTE_ZERO = new Byte((byte) 0);
    /** Reusable Byte constant for one. */
    public static final Byte BYTE_ONE = new Byte((byte) 1);
    /** Reusable Byte constant for minus one. */
    public static final Byte BYTE_MINUS_ONE = new Byte((byte) -1);
    /** Reusable Double constant for zero. */
    public static final Double DOUBLE_ZERO = new Double(0.0d);
    /** Reusable Double constant for one. */
    public static final Double DOUBLE_ONE = new Double(1.0d);
    /** Reusable Double constant for minus one. */
    public static final Double DOUBLE_MINUS_ONE = new Double(-1.0d);
    /** Reusable Float constant for zero. */
    public static final Float FLOAT_ZERO = new Float(0.0f);
    /** Reusable Float constant for one. */
    public static final Float FLOAT_ONE = new Float(1.0f);
    /** Reusable Float constant for minus one. */
    public static final Float FLOAT_MINUS_ONE = new Float(-1.0f);
    /**
	 * 获取日期状态
	 * DATE_STATE_YEAR 年
	 */
	public static int DATE_STATE_YEAR = 0;
	/**
	 * 获取日期状态
	 * DATE_STATE_MONTH 月
	 */
	public static int DATE_STATE_MONTH = 1;
	/**
	 * 获取日期状态
	 * DATE_STATE_DAY 日
	 */
	public static int DATE_STATE_DAY = 2;
	/**
	 * 获取日期状态
	 * DATE_STATE_WEEK 星期
	 */
	public static int DATE_STATE_WEEK = 3;

    /**
     * <p><code>NumberUtils</code> instances should NOT be constructed in standard programming.
     * Instead, the class should be used as <code>NumberUtils.stringToInt("6");</code>.</p>
     *
     * <p>This constructor is public to permit tools that require a JavaBean instance
     * to operate.</p>
     */
    public NumberUtils() {
        super();
    }

    /**
     * <p>Convert a <code>String</code> to an <code>int</code>, returning
     * <code>zero</code> if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, <code>zero</code> is returned.</p>
     *
     * <pre>
     *   NumberUtils.toInt(null) = 0
     *   NumberUtils.toInt("")   = 0
     *   NumberUtils.toInt("1")  = 1
     * </pre>
     *
     * @param str  the string to convert, may be null
     * @return the int represented by the string, or <code>zero</code> if
     *  conversion fails
     * @since 2.1
     */
    public static int toInt(String str) {
        return toInt(str, 0);
    }


    /**
     * <p>Convert a <code>String</code> to an <code>int</code>, returning a
     * default value if the conversion fails.</p>
     *
     * <p>If the string is <code>null</code>, the default value is returned.</p>
     *
     * <pre>
     *   NumberUtils.toInt(null, 1) = 1
     *   NumberUtils.toInt("", 1)   = 1
     *   NumberUtils.toInt("1", 0)  = 1
     * </pre>
     *
     * @param str  the string to convert, may be null
     * @param defaultValue  the default value
     * @return the int represented by the string, or the default if conversion fails
     * @since 2.1
     */
    public static int toInt(String str, int defaultValue) {
        if(str == null || str.length() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

	/**
	 * 获取行数
	 * @param len
	 * @return
	 */
	public static int getRowsNumber(int len, int curColNumber){
		if(len%curColNumber == 0){
			return len/curColNumber;
		}else{
			return len/curColNumber + 1;
		}
	}
	
	/**
	 * 返回日期
	 * @param dateString
	 * @param state 0年 1月 2日 3星期几
	 */
	public static int getDate(String dateString, int state){
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			
			Date date = format.parse(dateString);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			
			switch(state){
			case 0:
				return c.get(Calendar.YEAR);
			case 1:
				return c.get(Calendar.MONTH)+1;
			case 2:
				return c.get(Calendar.DATE);
			case 3:
				return c.get(Calendar.DAY_OF_WEEK)-1;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 获得星期几
	 * @param week
	 * @return
	 */
	public static String getWeek(int week){
		switch(week){
			case 1:
				return "星期一";
			case 2:
				return "星期二";
			case 3:
				return "星期三";
			case 4:
				return "星期四";
			case 5:
				return "星期五";
			case 6:
				return "星期六";
			default:
				return "星期日";
		}
	}
	
	/**
	 * 对double数据进行取精度计算
	 * @param value 值 
	 * @param scale 取精度位数
	 * @param roundingMode 精度计算后的数据 
	 * 示例 round(1.000000, 2 , BigDecimal.ROUND_HALF_UP)
	 * @return
	 */
	 public static double round(double value, int scale, int roundingMode) {   
		 BigDecimal bd = new BigDecimal(value);   
		 bd = bd.setScale(scale, roundingMode);   
		 double d = bd.doubleValue();   
		 bd = null;   
		 return d;   
	}  
}
