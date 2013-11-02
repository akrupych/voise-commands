package andriy.krupych.voicecommands.matchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

public class FirstWordMatcher implements CommandsMatcher {

	@Override
	public Intent findMatchingApp(List<String> suggestions,
			HashMap<String, ApplicationInfo> allApps, Context context) {
		// regexp: "first word in string"
		Pattern pattern = Pattern.compile("^\\w+");
		// for each suggestion
		for (String suggestion : suggestions) {
			// get starting word (it may be a whole string)
			String firstWord = suggestion.toLowerCase();
			Matcher matcher = pattern.matcher(suggestion);
			// if word is found, that's good;
			// in other case leave suggestion as-is
			if (matcher.find())
				firstWord = matcher.group().toLowerCase();
			// for each app
			for (Entry<String, ApplicationInfo> app : allApps.entrySet()) {
				// if app name contains parsed word (ignore case)
				if (app.getKey().toLowerCase().contains(firstWord)) {
					// get launcher intent for this app
					String packageName = app.getValue().packageName;
					Intent intent = context.getPackageManager()
							.getLaunchIntentForPackage(packageName);
					// if no correct intent found, continue search;
					// else, return this intent as a result
					if (intent != null) return intent;
				}
			}
		}
		// nothing found:(
		return null;
	}

}
