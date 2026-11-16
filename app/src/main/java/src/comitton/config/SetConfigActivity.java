package src.comitton.config;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

public class SetConfigActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener, OnPreferenceChangeListener {
	private static final int REQ_EXPORT_SAF = 1001;
	private static final int REQ_IMPORT_SAF = 1002;
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

		Preference exportSaf = findPreference(DEF.KEY_EXPORTSETTING_SAF);
		if (exportSaf != null) {
			exportSaf.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					String date = new SimpleDateFormat("yyyyMMdd", Locale.US).format(new Date());
					Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("application/octet-stream");
					intent.putExtra(Intent.EXTRA_TITLE, "comittona_" + date + DEF.EXTENSION_SETTING);
					startActivityForResult(intent, REQ_EXPORT_SAF);
					return true;
				}
			});
		}

		Preference importSaf = findPreference(DEF.KEY_IMPORTSETTING_SAF);
		if (importSaf != null) {
			importSaf.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("*/*");
					startActivityForResult(intent, REQ_IMPORT_SAF);
					return true;
				}
			});
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != RESULT_OK || data == null || data.getData() == null) {
			return;
		}
		Uri uri = data.getData();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		if (requestCode == REQ_EXPORT_SAF) {
			try (OutputStream os = getContentResolver().openOutputStream(uri)) {
				if (os == null) {
					Toast.makeText(this, R.string.export_saf_error, Toast.LENGTH_SHORT).show();
					return;
				}
				SettingBackupHelper.exportToStream(sp, os);
				Toast.makeText(this, R.string.export_saf_success, Toast.LENGTH_SHORT).show();
			}
			catch (Exception e) {
				Log.e("ExportSettingsSAF", "export failed", e);
				Toast.makeText(this, R.string.export_saf_error, Toast.LENGTH_SHORT).show();
			}
		}
		else if (requestCode == REQ_IMPORT_SAF) {
			try (InputStream is = getContentResolver().openInputStream(uri)) {
				if (is == null) {
					Toast.makeText(this, R.string.import_saf_error, Toast.LENGTH_SHORT).show();
					return;
				}
				int count = SettingBackupHelper.importFromStream(sp, is);
				if (count > 0) {
					Toast.makeText(this, R.string.import_saf_success, Toast.LENGTH_SHORT).show();
				}
				else {
					Toast.makeText(this, R.string.import_saf_empty, Toast.LENGTH_SHORT).show();
				}
			}
			catch (Exception e) {
				Log.e("ImportSettingsSAF", "import failed", e);
				Toast.makeText(this, R.string.import_saf_error, Toast.LENGTH_SHORT).show();
			}
		}
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
