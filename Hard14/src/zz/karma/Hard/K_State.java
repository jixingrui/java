package zz.karma.Hard;

import azura.karma.run.Karma;
import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;
import zz.karma.Hard.K_Item;

/**
*@note Server and Client each has one copy
*/
public class K_State extends KarmaReaderA {
	public static final int type = 18389688;

	public K_State(KarmaSpace space) {
		super(space, type , 18912897);
	}

	@Override
	public void fromKarma(Karma karma) {
		selectedIdx = karma.getInt(0);
		selected_up_down = karma.getBoolean(1);
		pageSize = karma.getInt(4);
		heldItem.fromKarma(karma.getKarma(5));
		heldItemMom.fromKarma(karma.getKarma(6));
		isTree = karma.getBoolean(7);
		selectedItem.fromKarma(karma.getKarma(8));
	}

	@Override
	public Karma toKarma() {
		karma.setInt(0, selectedIdx);
		karma.setBoolean(1, selected_up_down);
		karma.setInt(4, pageSize);
		if(heldItem != null)
			karma.setKarma(5, heldItem.toKarma());
		if(heldItemMom != null)
			karma.setKarma(6, heldItemMom.toKarma());
		karma.setBoolean(7, isTree);
		if(selectedItem != null)
			karma.setKarma(8, selectedItem.toKarma());
		return karma;
	}

	/**
	*@type INT
	*@note -1=unselected
	*/
	public int selectedIdx;
	/**
	*@type BOOLEAN
	*@note empty
	*/
	public boolean selected_up_down;
	/**
	*@type LIST
	*<p>[Item] empty
	*@note empty
	*/
	public java.util.List<Karma> upList=karma.getList(2);
	/**
	*@type LIST
	*<p>[Item] empty
	*@note empty
	*/
	public java.util.List<Karma> downList=karma.getList(3);
	/**
	*@type INT
	*@note record from askMore
	*/
	public int pageSize;
	/**
	*@type KARMA
	*<p>[Item] empty
	*@note empty
	*/
	public K_Item heldItem=new K_Item(space);
	/**
	*@type KARMA
	*<p>[Item] empty
	*@note empty
	*/
	public K_Item heldItemMom=new K_Item(space);
	/**
	*@type BOOLEAN
	*@note empty
	*/
	public boolean isTree;
	/**
	*@type KARMA
	*<p>[Item] empty
	*@note 根据State.selected_up_down和State.selectedIdx找到Item放在这里
	*/
	public K_Item selectedItem=new K_Item(space);

	public static final int T_Item = 18389685;
}