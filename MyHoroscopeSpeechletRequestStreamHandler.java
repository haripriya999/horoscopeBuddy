package com.amazonaws.lambda.myhoroscopebuddy4;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

public class MyHoroscopeSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
	private static final Set<String> supportedApplicationIds;
	static {
		/*
		 * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit"
		 * the relevant Alexa Skill and put the relevant Application Ids in this Set.
		 */
		supportedApplicationIds = new HashSet<String>();
		supportedApplicationIds.add("amzn1.ask.skill.e371c943-89f3-4928-9e66-1ae33a0da9f4");
	}

	public MyHoroscopeSpeechletRequestStreamHandler() {
		super(new MyHoroscopeBuddySpeechlet4(), supportedApplicationIds);
	}
}
