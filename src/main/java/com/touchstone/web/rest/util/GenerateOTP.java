package com.touchstone.web.rest.util;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class GenerateOTP {

	HashMap<String,Integer> otp  = new HashMap<>();
	HashMap<String,Date> otpDate = new HashMap<>();
	
	public int storeOTP(String mobile)
	{
		int randomPin = (int)(Math.random()*9000)+1000;
		otp.put(mobile, randomPin);
		System.out.println(randomPin);
		Calendar calendar = Calendar.getInstance();
		long t = calendar.getTimeInMillis();
		Date afterThirtyMin = new Date(t+60000*30);
		otpDate.put(mobile, afterThirtyMin);
		return randomPin;
	}
	
	/**
	 * verify that OTP is valid and not expired.
	 * @return short
	 * 0 = invalid
	 * 1 = Verified
	 * 2 = OTP expired
	*/
	public short checkOTP(String mobile, int otp){
		if(this.otp.get(mobile) != null){
			Date currentDate = new Date();
			if(currentDate.compareTo(this.otpDate.get(mobile)) <=0){
				if(this.otp.get(mobile) == otp)
					return 1;
				else
					return 0;
			}
			else
			{
				return 2;
			}
		}
		else
			return 0;
	}
	
	public void removeOtp(String mobile) {
		if(this.otp.get(mobile) != null){
			this.otp.remove(mobile);
		}
	}
	
}
