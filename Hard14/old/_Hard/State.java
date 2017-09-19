package zzz.karma._Hard;

import azura.karma.run.KarmaReaderA;
import azura.karma.def.KarmaSpace;

/**
*@note Server and Client each has one copy
*/
public abstract class State extends KarmaReaderA {
	public static final int type = 18389688;
	public static final int version = 18912897;

	public State(KarmaSpace space) {
		super(space, type , version);
	}

	/**
	*@type INT
	*@note -1=unselected
	*/
	public static final int F_selectedIdx = 0;
	/**
	*@type BOOLEAN
	*@note empty
	*/
	public static final int F_selected_up_down = 1;
	/**
	*@type LIST
	*<p>[Item] empty
	*@note empty
	*/
	public static final int F_upList = 2;
	/**
	*@type LIST
	*<p>[Item] empty
	*@note empty
	*/
	public static final int F_downList = 3;
	/**
	*@type INT
	*@note record from askMore
	*/
	public static final int F_pageSize = 4;
	/**
	*@type KARMA
	*<p>[Item] empty
	*@note empty
	*/
	public static final int F_heldItem = 5;
	/**
	*@type KARMA
	*<p>[Item] empty
	*@note empty
	*/
	public static final int F_heldItemMom = 6;
	/**
	*@type BOOLEAN
	*@note empty
	*/
	public static final int F_isTree = 7;
	/**
	*@type KARMA
	*<p>[Item] empty
	*@note 根据State.selected_up_down和State.selectedIdx找到Item放在这里
	*/
	public static final int F_selectedItem = 8;

	public static final int T_Item = 18389685;
}