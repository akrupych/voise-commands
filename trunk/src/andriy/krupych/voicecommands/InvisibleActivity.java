package andriy.krupych.voicecommands;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class InvisibleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// start service if it's not running yet
		startService(new Intent(this, RecognitionService.class));
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						if (CommandsRecognizer.get() != null) {
							CommandsRecognizer.get().start();
							cancel();
						}
					}
				});
			}
		}, 0, 1000);
		finish();
	}

}
