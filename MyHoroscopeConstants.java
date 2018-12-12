package com.amazonaws.lambda.myhoroscopebuddy4;

public interface MyHoroscopeConstants {

	final String DATE_OF_BIRTH_SLOT = "dob";
	final String HOROSCOPE_INTERVAL_SLOT = "interval";
	
	final String SESSION_DOB_KEY = "s_dob";
	final String SESSION_INTERVAL_KEY = "s_interval";
	
	final String S3_BUCKET_URL = "https://s3.amazonaws.com/zodiac-sign-images/";

	final String HOROSCOPE_SERVICE_URL = "http://horoscope-api.herokuapp.com/horoscope/";

	final String ERROR_MESG = "This is unsupported.  Please try something else.";
	final String WELCOME_MESG = "Welcome to My Horoscope Buddy Skill, You can ask me about your daily ro weekly or monthly or yearly horoscope"
			+ ". You can say my date of birth is twenth April and interval today";
	final String WELCOME_REPROMPT_MESG = "For instructions on what you can say, please say help me.";
	final String HELP_MESG = "For instructions on what you can say, please say help me.";
	final String HELP_REPROMPT_MESG = "You can say things like, my date of birth is october first and interval today"
			+ "or you can say exit... Now, what can I help you with?";

	final String ASK_INTERVAL_MESG = "Do you want predictions for today, week,  month or  year?";
	final String ASK_INTERVAL_REPROMPT_MESG = "Do you want predictions for today, week,  month or  year?";

	final String ASK_DATE_OF_BIRTH_MESG = "Please try again saying your date of birth, for example, October first";
	final String ASK_DATE_OF_BIRTH_REPROMPT_MESG = "Please try again saying your date of birth, for example, October first";

}
