package common.collections;

public abstract class FutureReturnCargo<CARGO, RETURN> extends FutureReturn<RETURN> {

	public CARGO cargo;

	public FutureReturnCargo(CARGO cargo) {
		this.cargo = cargo;
	}

}
