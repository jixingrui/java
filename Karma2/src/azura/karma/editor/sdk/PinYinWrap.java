package azura.karma.editor.sdk;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;

public class PinYinWrap {
	private static final Logger log = Logger.getLogger(PinYinWrap.class);

	public static String convert(String source) {
		if (isChinese(source) == false)
			return source;

		String py;
		try {
			py = PinyinHelper.convertToPinyinString(source, "_", PinyinFormat.WITHOUT_TONE);
			while (true) {
				String pyShort = py.replace("__", "_");
				if (py.length() == pyShort.length())
					break;

				py = pyShort;
			}
			String[] chars = py.split("_");
			py=chars[0];
			for (int i=1;i<chars.length;i++){
				String one=chars[i];
				one=one.substring(0, 1).toUpperCase() + one.substring(1);
				py+=one;
			}
		} catch (PinyinException e) {
			log.warn("Pinyin Convert failed: " + source);
			return source;
		}
		return py;
	}

	public static boolean isChinese(String str) {
		boolean isCh = false;
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			isCh = true;
		}
		return isCh;
	}
}
