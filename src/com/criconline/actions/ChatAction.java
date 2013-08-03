package com.criconline.actions;

import com.cricket.common.message.GameEvent;
import com.agneya.util.Base64;


public class ChatAction extends StageAction {

	private String chatString;
        private String _name;
        private String _type;
        private int _gid;


	public ChatAction(int pos, String team, String name, String chatString) {
		super(CHAT, pos, team);
                _name = name;
		this.chatString = chatString;
	}

	public ChatAction(String name, String chatString) {
		super(LOBBY_CHAT, -1, "");
                _name = name;
		this.chatString = chatString;
	}

        public void setGM(GameEvent ge){
          _type = ge.get("type");
          _name = ge.get("player");
          _gid = Integer.parseInt(ge.get("id"));
          chatString = ge.get("message");
        }

	public String getChatString() { return chatString; }

        public String getSendersName() { return _name; }

	public String getChatType() { return _type; }

        public int getGId(){ return _gid; }

	public String toString() {
		StringBuffer s = new StringBuffer();
		s.append("CHAT: ").append(chatString).
			append(" > ").append(_name).append(">");
		return s.toString();
	}

}
