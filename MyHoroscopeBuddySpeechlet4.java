package com.amazonaws.lambda.myhoroscopebuddy4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.StandardCard;

public class MyHoroscopeBuddySpeechlet4 implements SpeechletV2, MyHoroscopeConstants {

	private String zodiacSignImageURL = "";

	private static final Logger log = LoggerFactory.getLogger(MyHoroscopeBuddySpeechlet4.class);

	@Override
	public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
		log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
				requestEnvelope.getSession().getSessionId());
	}

	@Override
	public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
		log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
				requestEnvelope.getSession().getSessionId());

		String speechOutput = WELCOME_MESG;
		String repromptText = WELCOME_REPROMPT_MESG;

		// Here we are prompting the user for input
		return newAskResponse(speechOutput, repromptText);
	}

	@Override
	public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
		IntentRequest request = requestEnvelope.getRequest();
		log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
				requestEnvelope.getSession().getSessionId());

		Session session = requestEnvelope.getSession();
		Intent intent = request.getIntent();
		String intentName = (intent != null) ? intent.getName() : null;
		System.out.println("&&&&&&&&&&&&&&&&&&&&&& :" + intentName);
		
		if ("GetHoroscopeIntent".equals(intentName)) {
			Slot intervalSlot = intent.getSlot(HOROSCOPE_INTERVAL_SLOT);
			Slot dateSlot = intent.getSlot(DATE_OF_BIRTH_SLOT);
			if (dateSlot != null && dateSlot.getValue() != null) {
				return handleDateDialogRequest(intent, session);
			} else if (intervalSlot != null && intervalSlot.getValue() != null) {
				return handleIntervalDialogRequest(intent, session);
			} else {
				return handleNoInputDialogRequest(intent, session);
			}
		} else if ("AMAZON.HelpIntent".equals(intentName)) {
			return getHelp();
		} else if ("AMAZON.StopIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");
			return SpeechletResponse.newTellResponse(outputSpeech);
		} else if ("AMAZON.CancelIntent".equals(intentName)) {
			PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
			outputSpeech.setText("Goodbye");

			return SpeechletResponse.newTellResponse(outputSpeech);
		} else {
			String errorSpeech = ERROR_MESG;
			return newAskResponse(errorSpeech, errorSpeech);
		}
	}

	private SpeechletResponse handleDateDialogRequest(Intent intent, Session session) {
		Slot dobSlot = intent.getSlot(DATE_OF_BIRTH_SLOT);
		String dobValue = dobSlot.getValue();
		// Interval value from session and call get final response
		// If Interval value is not set then set date-of-birth in the session then
		// prompt for interval
		if (session.getAttributes().containsKey(SESSION_INTERVAL_KEY)) {
			String sessionInterval = (String) session.getAttribute(SESSION_INTERVAL_KEY);
			return getFinalResponse(sessionInterval, dobValue);
		} else {
			// set dob in session and prompt for time
			session.setAttribute(SESSION_DOB_KEY, dobValue);
			String speechOutput = ASK_INTERVAL_MESG;
			String repromptText = ASK_INTERVAL_REPROMPT_MESG;

			return newAskResponse(speechOutput, repromptText);
		}
	}

	private SpeechletResponse handleNoInputDialogRequest(Intent intent, Session session) {

		if (session.getAttributes().containsKey(SESSION_DOB_KEY)) {
			// get date re-prompt
			String speechOutput = ASK_DATE_OF_BIRTH_REPROMPT_MESG;

			// repromptText is the speechOutput
			return newAskResponse(speechOutput, speechOutput);
		} else {
			// get Place re-prompt
			String repromptText = ASK_DATE_OF_BIRTH_MESG;
			String speechOutput = ASK_DATE_OF_BIRTH_REPROMPT_MESG;

			return newAskResponse(speechOutput, repromptText);
		}
	}

	private SpeechletResponse handleIntervalDialogRequest(Intent intent, Session session) {
		Slot intervalSlot = intent.getSlot(HOROSCOPE_INTERVAL_SLOT);
		String intervalValue = intervalSlot.getValue();

		// Get date-of-birth value from session and call getFinalResponse
		// If date-of-birth value is not set then set Interval in the session then
		// prompt for date of birth

		if (session.getAttributes().containsKey(SESSION_DOB_KEY)) {
			String sessionDOB = (String) session.getAttribute(SESSION_DOB_KEY);
			return getFinalResponse(intervalValue, sessionDOB);
		} else {
			// set interval in session and prompt for date
			session.setAttribute(SESSION_INTERVAL_KEY, intervalValue);
			String speechOutput = ASK_DATE_OF_BIRTH_MESG;
			String repromptText = ASK_DATE_OF_BIRTH_MESG;

			return newAskResponse(speechOutput, repromptText);
		}
	}

	private SpeechletResponse newAskResponse(String stringOutput, String repromptText) {
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(stringOutput);

		PlainTextOutputSpeech repromptOutputSpeech = new PlainTextOutputSpeech();
		repromptOutputSpeech.setText(repromptText);
		Reprompt reprompt = new Reprompt();
		reprompt.setOutputSpeech(repromptOutputSpeech);

		return SpeechletResponse.newAskResponse(outputSpeech, reprompt);
	}

	private SpeechletResponse getFinalResponse(String interval, String dob) {

		MyHoroscopeBuddy horoscopeBuddy = new MyHoroscopeBuddy();

		String speechOutput = "";
		try {
			speechOutput = horoscopeBuddy.getHoroscope(interval, dob);
			zodiacSignImageURL = horoscopeBuddy.getZodiacSignImageURL();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// Create the plain text output
		PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
		outputSpeech.setText(speechOutput);
		System.out.println("@@@@@@@@@: " + zodiacSignImageURL);

		StandardCard card = new StandardCard();
		card.setTitle("Zodiac Sign ");
		card.setText(speechOutput);
		com.amazon.speech.ui.Image zodiacimage = new com.amazon.speech.ui.Image();
		zodiacimage.setLargeImageUrl(zodiacSignImageURL);
		card.setImage(zodiacimage);

		return SpeechletResponse.newTellResponse(outputSpeech, card);
	}

	@Override
	public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
		log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
				requestEnvelope.getSession().getSessionId());

		// any cleanup logic goes here
	}

	private SpeechletResponse getHelp() {
		String speechOutput = HELP_MESG;
		String repromptText = HELP_REPROMPT_MESG;

		return newAskResponse(speechOutput, repromptText);
	}
}
