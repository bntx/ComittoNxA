package src.comitton.config;

import src.comitton.common.DEF;
import jp.dip.muracoro.comittona.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.view.Window;

public class SetConfigActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceChangeListener {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.config);
		fixPreferenceIntents(getPreferenceScreen());

		// 項目選択
		PreferenceScreen onlineHelp = (PreferenceScreen) findPreference(DEF.KEY_CONFHELP);
		onlineHelp.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				// Activityの遷移
				Resources res = getResources();
				String url = res.getString(R.string.url_config);	// 設定画面
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
				return true;
			}
		});
	}

	/**
	 * PreferenceScreen に定義された Intent のターゲットパッケージを現在のアプリパッケージに合わせる
	 */
	private void fixPreferenceIntents(PreferenceGroup group) {
		if (group == null) {
			return;
		}
		String packageName = getPackageName();
		for (int i = 0; i < group.getPreferenceCount(); i++) {
			Preference pref = group.getPreference(i);
			Intent intent = pref.getIntent();
			if (intent != null && intent.getComponent() != null) {
				intent.setClassName(packageName, intent.getComponent().getClassName());
			}
			if (pref instanceof PreferenceGroup) {
				fixPreferenceIntents((PreferenceGroup) pref);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
		sharedPreferences.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

	}

	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		return;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
        return true;
	}
}
