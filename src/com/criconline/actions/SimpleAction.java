package com.criconline.actions;

public class SimpleAction extends Action {

	public SimpleAction(int id) {
		super(id, ACTION_TYPE_SIMPLE);
	}

	public void handleAction(ActionVisitor v) {
		v.handleSimpleAction(this);
	}

        public String toString(){
          return "SimpleAction " + super.toString();
        }

}
