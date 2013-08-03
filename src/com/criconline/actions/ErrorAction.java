package com.criconline.actions;

public class ErrorAction extends Action {

	public ErrorAction(int id, int pos, String team) {
		super(id, ACTION_TYPE_ERROR, pos, team);
	}

	public ErrorAction(int id) {
		this(id, -1, "");
	}

	public void handleAction(ActionVisitor v) {
		v.handleErrorAction(this);
	}

}
