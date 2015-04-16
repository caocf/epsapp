package com.epeisong.utils;

import java.text.DecimalFormat;

/**
 * 数字，金额类等
 * 
 * @author Jack
 * 
 */
public class DoubleUtil {

    private static final DecimalFormat MONEYFORMAT_NOE = new DecimalFormat("0.##");

    public static String moneyFormatNoE(double dd) {
    	return MONEYFORMAT_NOE.format(dd);
    }
    
}
