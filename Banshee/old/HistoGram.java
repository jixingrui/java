package azura.banshee.nagaOld;

public class HistoGram {
	
	public int shadowDx;
	public int shadowWidth;
	
	private int[] xLine;
	private int max;

	public HistoGram(int width) {
		this.xLine=new int[width];
	}

	public void add(int i) {
		xLine[i]++;
		max=Math.max(max,xLine[i]);
	}

	public void make() {
		int left;
		for(left=0;left<xLine.length;left++){
			if(xLine[left]>max/2){
				break;
			}
		}
		int right;
		for(right=xLine.length-1;right>=0;right--){
			if(xLine[right]>max/2){
				break;
			}
		}
		if(left>=right){
			shadowDx=0;
			shadowWidth=0;
		}else{
			shadowDx=(left+right)/2-xLine.length/2;
			shadowWidth=right-left;
		}
	}

}
