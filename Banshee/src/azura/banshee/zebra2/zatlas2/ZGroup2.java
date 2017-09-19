package azura.banshee.zebra2.zatlas2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZGroup2 {
	private Zatlas2 atlas;
	public List<Zframe2> frameList = Collections.synchronizedList(new ArrayList<>());

	ZGroup2(Zatlas2 atlas) {
		this.atlas = atlas;
	}

	public Zframe2 newFrame() {
		Zframe2 frame = atlas.newFrame();
		frameList.add(frame);
		return frame;
	}

	public void seal() {

		boolean allEmpty = true;
		for (Zframe2 frame : frameList) {
			if (frame.blank_key_delta > 0) {
				allEmpty = false;
				break;
			}
		}

		if (allEmpty)
			return;

		Zsheet3 sheet = atlas.newSheet();
		for (Zframe2 frame : frameList) {
			if (frame.blank_key_delta == 0)
				continue;

//			if(frame.mc5Original.toString().equals("099118b65ce9f4616a7ce6458ab4beb2298e91aff6")){
//				boolean stop=true;
//			}
		
			boolean success = sheet.tryAdd(frame);
			if (!success) {
				sheet = atlas.newSheet();
				success = sheet.tryAdd(frame);
				if (!success)
					throw new Error();
			} else {
//				if (frame.rectOnSheet.x + frame.rectOnSheet.width > sheet.width
//						|| frame.rectOnSheet.y + frame.rectOnSheet.height > sheet.height) {
//					Logger.getLogger(ZGroup2.class).error("frame exceed sheet size");
//					throw new Error();  
//				}
			}
		}
	}

	public Zframe2[] listToHalf(Zframe2[] sourceList) {

		boolean allBlank = true;
		for (int i = 0; i < sourceList.length; i++) {
			if (sourceList[i].isSmall() == false) {
				allBlank = false;
			}
		}
		if (allBlank)
			return null;

		Zframe2[] halfList = new Zframe2[sourceList.length];
		ArrayList<Integer> iterator = new ArrayList<Integer>();
		for (int i = 0; i < sourceList.length; i++) {
			iterator.add(i);
		}
		// iterator.parallelStream().forEach(i -> {
		iterator.forEach(i -> {
			halfList[i] = newFrame();
			halfList[i].loadHalf(sourceList[i]);
		});

		return halfList;
	}

}
