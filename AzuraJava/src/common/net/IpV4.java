package common.net;

public class IpV4 {
	public String ip;
	public int port;
	public long ipFull;

	public IpV4() {
	}

	public IpV4(String ip, int port) {
		serialize(ip, port);
	}

	public IpV4(long ipFull) {
		deserialize(ipFull);
	}

	public void serialize(String ip, int port) {
		this.ip = ip;
		this.port = port;
		ipFull = (long) ipToInt(ip) << 32;
		ipFull |= port;
	}

	public void deserialize(long ipFull) {
		this.ipFull = ipFull;
		int ipPart = (int) (ipFull >> 32);
		ip = intToIp(ipPart);
		port = (int) (ipFull);
	}

	@Override
	public String toString() {
		return ip + ":" + port;
	}

	public static String intToIp(int i) {
		return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
				+ ((i >> 8) & 0xFF) + "." + (i & 0xFF);
	}

	private static int ipToInt(final String addr) {
		final String[] addressBytes = addr.split("\\.");
		if (addressBytes.length != 4) {
			return 0;
		}
		int ip = 0;
		for (int i = 0; i < 4; i++) {
			int chunk = 0;
			try {
				chunk = Integer.parseInt(addressBytes[i]);
			} catch (Exception e) {
				return 0;
			}
			ip <<= 8;
			ip |= chunk;
		}
		return ip;
	}
}
