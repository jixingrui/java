package common.algorithm.geohash;


import common.util.ByteUtil;

public class GeoUtil {
	private static long next(long original, int significantBits) {
		return original + (1L << (64 - significantBits));
	}

	public static byte[] toExclusive(GeoHash gh) {
		long toLong = GeoUtil.next(gh.longValue(), gh.significantBits());
		byte[] to = ByteUtil.long2Byte(toLong);
		return to;
	}

	public static byte[] byteValue(GeoHash gh) {
		return ByteUtil.long2Byte(gh.longValue());
	}
}
