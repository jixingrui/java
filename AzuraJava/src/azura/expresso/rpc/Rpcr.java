package azura.expresso.rpc;

import azura.expresso.Datum;

public abstract class Rpcr<CARGO> extends Rpc {

	protected CARGO cargo;
	public int ruler;

	public Rpcr() {
		super();
	}

	public Rpcr(CARGO cargo) {
		super();
		this.cargo = cargo;
	}

	public abstract void returnHandler(Datum ret);
}
