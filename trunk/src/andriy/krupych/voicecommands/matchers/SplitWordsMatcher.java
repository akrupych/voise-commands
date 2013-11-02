package andriy.krupych.voicecommands.matchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;

public class SplitWordsMatcher implements CommandsMatcher {

	@Override
	public Intent findMatchingApp(List<String> suggestions,
			HashMap<String, ApplicationInfo> allApps, Context context) {
		MatchingMap matchingApps = new MatchingMap();
		for (int i = 0; i < suggestions.size(); i++) {
			int importance = suggestions.size() - i;
			String[] suggestionWords = suggestions.get(i).split(" ");
			for (Entry<String, ApplicationInfo> app : allApps.entrySet()) {
				String appLabel = app.getKey();
				for (String word : suggestionWords)
					if (appLabel.toLowerCase().contains(word.toLowerCase()))
						matchingApps.add(appLabel, word.length() * importance);
			}
		}
		matchingApps.sort();
		matchingApps.log();
		for (String appLabel : matchingApps.getAppsList()) {
			String packageName = allApps.get(appLabel).packageName;
			Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
			if (intent != null) return intent;
		}
		return null;
	}

}
