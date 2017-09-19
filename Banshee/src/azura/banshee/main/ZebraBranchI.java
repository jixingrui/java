package azura.banshee.main;

import java.awt.Rectangle;

import azura.gallerid.GalPackI;
import common.collections.buffer.i.BytesI;

public interface ZebraBranchI extends BytesI, GalPackI {

	Rectangle getBoundingBox();
//	int getWidth();
//
//	int getHeight();
}
