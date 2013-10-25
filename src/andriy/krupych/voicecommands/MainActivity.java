package andriy.krupych.voicecommands;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void onClickStartService(View button) {
		startService(new Intent(this, MediaButtonService.class));
	}

	public void onClickStopService(View button) {
		stopService(new Intent(this, MediaButtonService.class));
	}

}
