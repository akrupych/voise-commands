package andriy.krupych.voicecommands.matchers;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

/**
 * General interface for matcher algorithms.
 * Matcher gets recognized suggestion and map with all available apps.
 * Then, some matching algorithm should be applied.
 * Resulting app intent should be returned to client.
 * @author Andrew
 */
public interface CommandsMatcher {
	
	Intent findMatchingApp(List<String> suggestions,
			HashMap<String, ApplicationInfo> allApps, Context context);
	
}
