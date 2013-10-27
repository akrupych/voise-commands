package andriy.krupych.voicecommands;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Performs actions in response to voice commands.
 * @author Andriy Krupych
 */
public class CommandsProcessor {
	
	///////////////////////////////////// STATIC PART ////////////////////////////////////////////
	
	/**
	 * Singleton instance
	 */
	private static CommandsProcessor mInstance;
	
	/**
	 * Creates new processor instance
	 * @param context - required for getting and launching apps
	 */
	public static void create(Context context) {
		if (mInstance == null)
			mInstance = new CommandsProcessor(context);
	}
	
	/**
	 * Returns singleton instance
	 */
	public static CommandsProcessor get() {
		return mInstance;
	}
	
	///////////////////////////////////// INSTANCE PART /////////////////////////////////////////
	
	/**
	 * Required for activity starting
	 */
	private Context mContext;
	/**
	 * All the apps labels for command comparison
	 */
	private HashMap<String, ApplicationInfo> mAllApps;
	/**
	 * When loading all apps info, this should be false.
	 * In this state no processing can be performed.
	 */
	private boolean mIsDataLoaded = false;
	/**
	 * Stores last suggestions for processing, while all apps info is loaded.
	 * This data will be processed after loading has ended.
	 */
	private List<String> mSavedRequest = null;

	/**
	 * Simple constructor
	 */
	private CommandsProcessor(Context context) {
		mContext = context;
		loadAllApps();
	}
	
	/**
	 * Loading app labels can be a long process
	 * (on my machine it's taking up to 7 seconds),
	 * so we better start it at the very beginning.
	 */
	private void loadAllApps() {
		new AllAppsLoaderTask().execute();
	}

	/**
	 * Processes recognition results
	 * @param suggestions - list with recognized variants
	 */
	@SuppressWarnings("unchecked")
	protected void process(List<String> suggestions) {
		toast(suggestions.get(0));
		if (mIsDataLoaded) new CommandsProcessingTask().execute(suggestions);
		else mSavedRequest = suggestions;
	}
	
	/**
	 * This task loads all applications info on a worker thread.
	 * When this process ended, it checks for a saved request
	 * and launches process method with that request.
	 * @author Andriy Krupych
	 *
	 */
	private class AllAppsLoaderTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			PackageManager packageManager = mContext.getPackageManager();
			List<ApplicationInfo> packages = packageManager.getInstalledApplications(
					PackageManager.GET_META_DATA);
	    	mAllApps = new HashMap<String, ApplicationInfo>(packages.size());
	    	for (ApplicationInfo info : packages)
	    		mAllApps.put(info.loadLabel(packageManager).toString(), info);
	    	log("loaded " + mAllApps.size() + " app labels");
	    	return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
	    	mIsDataLoaded = true;
			if (mSavedRequest != null) {
				process(mSavedRequest);
				mSavedRequest = null;
			}
		}
		
	}
	
	/**
	 * This task performs actions on a worker thread:
	 * - parsing of recognition results;
	 * - comparing with all-applications list;
	 * - selecting the best matching app;
	 * - launching this app as a new task.
	 * @author Andriy Krupych
	 */
	private class CommandsProcessingTask extends AsyncTask<List<String>, Void, Intent> {

		@SuppressLint("DefaultLocale") @Override
		protected Intent doInBackground(List<String>... params) {
			// regexp: "first word in string"
			Pattern pattern = Pattern.compile("^\\w+");
			// for each suggestion
			for (String suggestion : params[0]) {
				// get starting word (it may be a whole string)
				String firstWord = suggestion.toLowerCase();
				Matcher matcher = pattern.matcher(suggestion);
				// if word is found, that's good;
				// in other case leave suggestion as-is
				if (matcher.find())
					firstWord = matcher.group().toLowerCase();
				// for each app
				for (Entry<String, ApplicationInfo> app : mAllApps.entrySet()) {
					// if app name contains parsed word (ignore case)
					if (app.getKey().toLowerCase().contains(firstWord)) {
						// get launcher intent for this app
						String packageName = app.getValue().packageName;
						Intent intent = mContext.getPackageManager()
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
		
		@Override
		protected void onPostExecute(Intent result) {
			if (result != null) mContext.startActivity(result);
			else toast("Please try again");
		}
		
	}

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
		Log.d(CommandsProcessor.class.getName(), string);
	}

}
