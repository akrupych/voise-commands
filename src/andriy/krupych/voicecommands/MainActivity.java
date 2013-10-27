package andriy.krupych.voicecommands;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	public static final String TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// start service if it's not running yet
		startService(new Intent(this, RecognitionService.class));
	}
	
	public void onClickStartService(View button) {
		startService(new Intent(this, RecognitionService.class));
	}

	public void onClickStopService(View button) {
		stopService(new Intent(this, RecognitionService.class));
	}
	
	public void onClickSpeak(View button) {
		CommandsRecognizer.get().start();
	}
	
	@Override
	protected void onDestroy() {
		// leave the service running
		super.onDestroy();
	}

}
