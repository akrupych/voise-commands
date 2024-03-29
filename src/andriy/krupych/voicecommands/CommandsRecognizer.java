package andriy.krupych.voicecommands;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

/**
 * Singleton class responsible for speech recognition and processing.
 * Uses {@link SpeechRecognizer} for speech-to-text transforming and performs actions in response.
 * @author Andriy Krupych
 */
public class CommandsRecognizer {
	
	///////////////////////////////////// STATIC PART /////////////////////////////////////////
	
	/**
	 * Singleton instance
	 */
	private static CommandsRecognizer mInstance;
	
	/**
	 * Creates new recognizer instance.
	 * @param context - required for {@link SpeechRecognizer}
	 */
	public static void create(Context context) {
		if (mInstance == null)
			mInstance = new CommandsRecognizer(context);
	}
	
	/**
	 * Returns singleton instance
	 */
	public static CommandsRecognizer get() {
		return mInstance;
	}
	
	//////////////////////////////////// INSTANCE PART ///////////////////////////////////////

	/**
	 * Recognizer context. Used everywhere:)
	 */
	private Context mContext;
	/**
	 * Wrapped SpeechRecognizer that's doing all the work
	 */
	private SpeechRecognizer mSpeechRecognizer;
	/**
	 * Used for simplification of {@link SpeechRecognizer} usage
	 */
	private Intent mSpeechRecognizerIntent;
	
	/**
	 * Private constructor that creates {@link SpeechRecognizer} object
	 * @param context - context for {@link SpeechRecognizer} object
	 */
	private CommandsRecognizer(Context context) {
		log("creating recognizer");
		mContext = context;
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
		mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
		mSpeechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		mSpeechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
	}

	/**
	 * Checks if speech recognition is available in this environment.
	 */
	public boolean isAvailable() {
		List<ResolveInfo> activities =
				mContext.getPackageManager().queryIntentActivities(mSpeechRecognizerIntent, 0);
		return activities.size() > 0;
	}
	
	/**
	 * Starts speech recording and recognition.
	 */
	public void start() {
		log("starting recognizer");
		mSpeechRecognizer.startListening(mSpeechRecognizerIntent);
	}
	
	/**
	 * Must be called if you don't want to leak service connection.
	 */
	public void destroy() {
		log("destroying recognizer");
		mSpeechRecognizer.destroy();
	}

	/**
	 * {@link SpeechRecognizer} callback
	 */
	private RecognitionListener mRecognitionListener = new RecognitionListener() {

		public void onReadyForSpeech(Bundle params) {
			log("onReadyForSpeech");
		}

		public void onBeginningOfSpeech() {
			log("onBeginningOfSpeech");
		}

		public void onRmsChanged(float rmsdB) {
//			log("onRmsChanged " + rmsdB);
		}

		public void onBufferReceived(byte[] buffer) {
			log("onBufferReceived " + buffer.length);
		}

		public void onEndOfSpeech() {
			log("onEndofSpeech");
		}

		public void onError(int error) {
			log("onError " + error);
		}

		public void onResults(Bundle results) {
			List<String> suggestions = results.getStringArrayList(
					SpeechRecognizer.RESULTS_RECOGNITION);
			log(TextUtils.join(", ", suggestions));
			CommandsProcessor.get().process(suggestions);
			toast(suggestions.get(0));
		}

		public void onPartialResults(Bundle partialResults) {
			log("onPartialResults");
		}

		public void onEvent(int eventType, Bundle params) {
			log("onEvent " + eventType);
		}
	};

	/**
	 * Simple toasting
	 */
	private void toast(String string) {
		Toast.makeText(mContext, string, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Simple logging
	 */
	private void log(String string) {
		Log.d(CommandsRecognizer.class.getName(), string);
	}

}
