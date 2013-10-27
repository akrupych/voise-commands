package andriy.krupych.voicecommands;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;

/**
 * Gains full media button focus.
 * Redirects all button events to {@link MediaButtonReceiver}.
 * This service should be started with "startService" command.
 * @author Andriy Krupych
 */
public class RecognitionService extends Service {
	
	/**
	 * Log tag
	 */
	public static final String TAG = RecognitionService.class.getName();

	/**
	 * Broadcast receiver to get media button events
	 */
	private MediaButtonReceiver mMediaButtonReceiver = new MediaButtonReceiver();
	/**
	 * Audio manager to gain media button focus
	 */
	private AudioManager mAudioManager;
	/**
	 * Required for "registerMediaButtonEventReceiver" command
	 */
	private ComponentName mReceiverComponentName;
	/**
	 * This timer regains media button focus with some interval
	 */
	private Timer mMediaButtonFocusControl;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
		// create singletons instances; they will be used by receiver then
		CommandsRecognizer.create(getApplicationContext());
		CommandsProcessor.create(this);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mReceiverComponentName = new ComponentName(this, MediaButtonReceiver.class);
		// we have to register receiver both in code and in manifest
		registerReceiver(mMediaButtonReceiver, new IntentFilter(Intent.ACTION_MEDIA_BUTTON));
		startMediaButtonFocusControl();
	}

	/**
	 * Starts timer that regains media button focus with some interval.
	 * This is necessary when media player (or another app) gains focus to itself after launch.
	 * In this case we won't receive no media button events.
	 */
	private void startMediaButtonFocusControl() {
		mMediaButtonFocusControl = new Timer();
		mMediaButtonFocusControl.schedule(new TimerTask() {
			@Override
			public void run() {
				mAudioManager.registerMediaButtonEventReceiver(mReceiverComponentName);
			}
			@Override
			public boolean cancel() {
				mAudioManager.unregisterMediaButtonEventReceiver(mReceiverComponentName);
				return super.cancel();
			}
		}, 0, 5000);
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
		unregisterReceiver(mMediaButtonReceiver);
		mMediaButtonFocusControl.cancel();
		CommandsRecognizer.get().destroy();
	}

}
