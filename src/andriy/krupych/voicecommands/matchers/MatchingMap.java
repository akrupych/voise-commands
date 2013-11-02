package andriy.krupych.voicecommands.matchers;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class MatchingMap {
	
	class Entry {
		
		public String appName;
		public int matching;
		
		public Entry(String appName, int matching) {
			this.appName = appName;
			this.matching = matching;
		}
		
		@Override
		public String toString() {
			return appName + " " + matching;
		}
	}

	private static final String TAG = MatchingMap.class.getName();
	
	private List<Entry> mList = new ArrayList<MatchingMap.Entry>();
	
	public void add(String appName, int matching) {
		for (Entry entry : mList) {
			if (entry.appName.equalsIgnoreCase(appName)) {
				entry.matching += matching;
				return;
			}
		}
		mList.add(new Entry(appName, matching));
	}

	public void sort() {
		for (int i = 0; i < mList.size() - 1; i++) {
			for (int j = mList.size() - 1; j > i; j--) {
				if (mList.get(j).matching > mList.get(j - 1).matching) {
					Entry swap = mList.get(j);
					mList.set(j, mList.get(j - 1));
					mList.set(j - 1, swap);
				}
			}
		}
	}
	
	public List<String> getAppsList() {
		List<String> result = new ArrayList<String>(mList.size());
		for (Entry entry : mList) result.add(entry.appName);
		return result;
	}

	public void log() {
		for (int i = 0; i < Math.min(mList.size(), 10); i++)
			Log.d(TAG, mList.get(i).toString());
		if (mList.size() > 10) Log.d(TAG, "...");
	}

}
