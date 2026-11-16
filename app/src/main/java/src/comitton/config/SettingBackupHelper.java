package src.comitton.config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import src.comitton.common.DEF;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences の設定値をストリームへエクスポート／インポートする共通ヘルパー。
 */
public class SettingBackupHelper {

	/**
	 * SharedPreferences のエクスポート対象設定を UTF-8 テキストとしてストリームへ書き出す。
	 */
	public static void exportToStream(SharedPreferences sp, OutputStream os) throws IOException {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"), 8192);
		Map<String, ?> keyMap = sp.getAll();
		if (keyMap != null) {
			Set<String> keys = new TreeSet<String>(keyMap.keySet());
			for (String key : keys) {
				if (!DEF.checkExportKey(key)) {
					continue;
				}
				Object value = keyMap.get(key);
				if (value == null) {
					continue;
				}
				// NxT専用キーチェック
				boolean ex = DEF.checkTonlyExportKey(key);
				String line;
				String className = value.getClass().getName();
				if (className.equals("java.lang.String")) {
					line = (ex ? "s:" : "S:") + key + "=" + value.toString();
				}
				else if (className.equals("java.lang.Boolean")) {
					line = (ex ? "b:" : "B:") + key + "=" + value.toString();
				}
				else if (className.equals("java.lang.Float")) {
					line = (ex ? "f:" : "F:") + key + "=" + value.toString();
				}
				else if (className.equals("java.lang.Integer")) {
					line = (ex ? "i:" : "I:") + key + "=" + value.toString();
				}
				else if (className.equals("java.lang.Long")) {
					line = (ex ? "l:" : "L:") + key + "=" + value.toString();
				}
				else {
					continue;
				}
				bw.write(line);
				bw.newLine();
			}
		}
		bw.flush();
	}

	/**
	 * ストリームから UTF-8 テキストの設定を読み込み、SharedPreferences に適用する。
	 * 有効な設定が1件以上読み込めた場合のみ既存設定を置き換えて commit する。
	 *
	 * @return 読み込んだ設定項目数
	 */
	public static int importFromStream(SharedPreferences sp, InputStream is) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8192);
		Editor ed = sp.edit();

		// 既存の設定を削除（commit() するまでは反映されない）
		Map<String, ?> keys = sp.getAll();
		if (keys != null) {
			for (String key : keys.keySet()) {
				if (DEF.checkExportKey(key)) {
					ed.remove(key);
				}
			}
		}

		int count = 0;
		String line;
		while ((line = br.readLine()) != null) {
			if (line.length() < 3 || line.charAt(1) != ':') {
				continue;
			}
			char valuetype = line.charAt(0);
			int index = line.indexOf('=', 2);
			if (index <= 2) {
				continue;
			}
			String key = line.substring(2, index);
			String value = line.substring(index + 1);
			switch (valuetype) {
				case 'S':
				case 's': // NxT専用キー
					ed.putString(key, value);
					count++;
					break;
				case 'B':
				case 'b': // NxT専用キー
					ed.putBoolean(key, Boolean.parseBoolean(value));
					count++;
					break;
				case 'F':
				case 'f': // NxT専用キー
					ed.putFloat(key, Float.parseFloat(value));
					count++;
					break;
				case 'I':
				case 'i': // NxT専用キー
					ed.putInt(key, Integer.parseInt(value));
					count++;
					break;
				case 'L':
				case 'l': // NxT専用キー
					ed.putLong(key, Long.parseLong(value));
					count++;
					break;
			}
		}

		if (count > 0) {
			ed.commit();
		}
		return count;
	}
}
