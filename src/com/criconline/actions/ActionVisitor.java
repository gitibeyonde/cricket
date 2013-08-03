package com.criconline.actions;


public interface ActionVisitor {


	void handleDefaultAction     (Action action);

        void handleLastMoveAction     (LastMoveAction action);

	void handleMoveRequestAction     (MoveRequestAction action);

	void handleSimpleAction      (SimpleAction action);

	void handleStageAction       (StageAction action);

	void handleTableServerAction (TableServerAction action);

	void handleErrorAction       (ErrorAction action);
}
