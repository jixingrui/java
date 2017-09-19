package common.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class IpGetter {
//	public static void main(String[] args) {
//		System.out.println(IpGetter.fromSina());
//	}

	public static String fromSina() {
		try {
			URL url = new URL("http://counter.sina.com.cn/ip/");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String s = "";
			StringBuffer sb = new StringBuffer("");
			String webContent = "";
			while ((s = br.readLine()) != null) {
				sb.append(s + "\r\n");
			}
			br.close();
			webContent = sb.toString();
			int start = nthIndexOf(webContent, "\"", 1) + 1;
			int end = nthIndexOf(webContent, "\"", 2);
			webContent = webContent.substring(start, end);
			return webContent;
		} catch (Exception e) {
			e.printStackTrace();
			return "error open url";
		}
	}

	public static int nthIndexOf(String source, String sought, int n) {
		int index = source.indexOf(sought);
		if (index == -1)
			return -1;

		for (int i = 1; i < n; i++) {
			index = source.indexOf(sought, index + 1);
			if (index == -1)
				return -1;
		}
		return index;
	}

	public static String fromIp138() {
		try {
			URL url = new URL("http://iframe.ip138.com/ic.asp");
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String s = "";
			StringBuffer sb = new StringBuffer("");
			String webContent = "";
			while ((s = br.readLine()) != null) {
				sb.append(s + "\r\n");
			}
			br.close();
			webContent = sb.toString();
			int start = webContent.indexOf("[") + 1;
			int end = webContent.indexOf("]");
			webContent = webContent.substring(start, end);
			return webContent;
		} catch (Exception e) {
			e.printStackTrace();
			return "error open url";
		}
	}
}