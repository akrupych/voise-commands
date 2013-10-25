package andriy.krupych.voicecommands;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Receives media button events and processes them. 
 * @author Andriy Krupych
 */
public class MediaButtonReceiver extends BroadcastReceiver {

	private static final String TAG = MediaButtonReceiver.class.getName();

	@Override
	public void onReceive(Context context, Intent intent) {
		KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
		Log.d(TAG, "onReceive " + event.getAction());
		if (event.getAction() == KeyEvent.ACTION_DOWN)
			CommandsRecognizer.get().start();
	}

}
