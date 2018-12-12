package com.amazonaws.lambda.myhoroscopebuddy4;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

public class MyHoroscopeBuddy implements MyHoroscopeConstants {

	String zodiacSignImageURL = "";

	public String getZodiacSign(int day, int month) {

		String zodiacSign = "";

		if ((month == 12 && day >= 22 && day <= 31) || (month == 1 && day >= 1 && day <= 19))
			zodiacSign = "Capricorn";
		else if ((month == 1 && day >= 20 && day <= 31) || (month == 2 && day >= 1 && day <= 17))
			zodiacSign = "Aquarius";
		else if ((month == 2 && day >= 18 && day <= 29) || (month == 3 && day >= 1 && day <= 19))
			zodiacSign = "Pisces";
		else if ((month == 3 && day >= 20 && day <= 31) || (month == 4 && day >= 1 && day <= 19))
			zodiacSign = "Aries";
		else if ((month == 4 && day >= 20 && day <= 30) || (month == 5 && day >= 1 && day <= 20))
			zodiacSign = "Taurus";
		else if ((month == 5 && day >= 21 && day <= 31) || (month == 6 && day >= 1 && day <= 20))
			zodiacSign = "Gemini";
		else if ((month == 6 && day >= 21 && day <= 30) || (month == 7 && day >= 1 && day <= 22))
			zodiacSign = "Cancer";
		else if ((month == 7 && day >= 23 && day <= 31) || (month == 8 && day >= 1 && day <= 22))
			zodiacSign = "Leo";
		else if ((month == 8 && day >= 23 && day <= 31) || (month == 9 && day >= 1 && day <= 22))
			zodiacSign = "Virgo";
		else if ((month == 9 && day >= 23 && day <= 30) || (month == 10 && day >= 1 && day <= 22))
			zodiacSign = "Libra";
		else if ((month == 10 && day >= 23 && day <= 31) || (month == 11 && day >= 1 && day <= 21))
			zodiacSign = "Scorpio";
		else if ((month == 11 && day >= 22 && day <= 30) || (month == 12 && day >= 1 && day <= 21))
			zodiacSign = "Sagittarius";
		else
			zodiacSign = "";

		return zodiacSign;
	}

	public String getHoroscope(String interval, String dob) {
		String dateArray[] = dob.split("-");
		int month = Integer.parseInt(dateArray[1]);
		int day = Integer.parseInt(dateArray[2]);

		String zodiacSign = getZodiacSign(day, month); // getZodiacSign(int month,int day)
		setZodiacSignImageURL(zodiacSign.toLowerCase());

		System.out.println("INTERVAL.... " + interval + " .... Zodia Sign:" + zodiacSign);
		try {
			String horoscopeText = getHoroscopeText(zodiacSign, interval);
			return horoscopeText;
		} catch (Exception e) {
			e.printStackTrace();
			return "Could not call remote horoscope service";
		}
	}

	public void setZodiacSignImageURL(String inputZodiacSign) {
		if (inputZodiacSign != null) {
			zodiacSignImageURL = (S3_BUCKET_URL + inputZodiacSign.toLowerCase() + ".jpg");
		} else {
			zodiacSignImageURL = "";
		}
	}

	public String getZodiacSignImageURL() {
		return zodiacSignImageURL;
	}

	public String getHoroscopeText(String inputZodiacSign, String inputInterval) throws Exception {
		URL obj = new URL(HOROSCOPE_SERVICE_URL + inputInterval + "/" + inputZodiacSign);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		// optional default is GET
		con.setRequestMethod("GET");
		// add request header
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		// int responseCode = con.getResponseCode();

		// System.out.println("\n$$$$$$ Sending 'GET' request to URL : " +
		// HOROSCOPE_SERVICE_URL);
		// System.out.println("$$$$$ Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer responseBuffer = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			responseBuffer.append(inputLine);
		}
		in.close();

		// print in String
		System.out.println(responseBuffer.toString());

		// Read JSON response and print
		JSONObject myResponse = new JSONObject(responseBuffer.toString());

		/*
		 * System.out.println("result after Reading JSON Response");
		 * System.out.println("data - " + myResponse.getString("horoscope"));
		 */

		String responseStrng = "Your zodiac sign is " + inputZodiacSign + ".  "
				+ myResponse.getString("horoscope").replaceAll("\\['", "").replaceAll("\\']", "");
		return responseStrng;

	}
}
