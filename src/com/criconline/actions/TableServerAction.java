package com.criconline.actions;

public class TableServerAction extends Action {

	public TableServerAction(int id, int pos, String team) {
		super(id, ACTION_TYPE_TABLE_SERVER, pos, team);
	}

	public TableServerAction(int id) {
		this(id, -1, "");
	}

	public void handleAction(ActionVisitor v) {
		v.handleTableServerAction(this);
	}

}
