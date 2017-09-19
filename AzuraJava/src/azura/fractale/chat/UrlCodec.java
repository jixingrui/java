package azura.fractale.chat;

import common.algorithm.A32;
import common.algorithm.Base62;

public class UrlCodec {

	public static String encode(byte[] data) {
		byte[] mix = A32.encode(data);
		return Base62.encode(mix);
	}

	public static byte[] decode(String url) {
		byte[] mix = Base62.decode(url);
		if (mix == null)
			return null;
		else
			return A32.decode(mix);
	}

}
