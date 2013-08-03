package com.criconline.actions;

public class StageAction extends Action {
  Object _o;

	public StageAction(int id, int pos, String team) {
		super(id, ACTION_TYPE_STAGE, pos, team);
	}

	public StageAction(int id) {
		this(id, -1, "");
	}

        public Object getObject(){
          return _o;
        }

        public void setObject(Object o){
          _o=o;
        }

	public void handleAction(ActionVisitor v) {
		v.handleStageAction(this);
	}

}
