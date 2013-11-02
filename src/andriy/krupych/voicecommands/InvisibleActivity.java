package andriy.krupych.voicecommands;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class InvisibleActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// start service if it's not running yet
		startService(new Intent(this, RecognitionService.class));
		CommandsRecognizer.get().start();
	}

}
