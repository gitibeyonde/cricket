package com.cricket.mmog;

public class GameStateEvent {

  private GameStateEvent(int val) {
    intVal = val;
  }

  public static final GameStateEvent GAME_OVER = new GameStateEvent(0);

  public static final GameStateEvent GAME_BEGIN = new GameStateEvent(1);

  public static final GameStateEvent PRE_FLOP = new GameStateEvent(2);

  public static final GameStateEvent FLOP = new GameStateEvent(3);

  public static final GameStateEvent TURN = new GameStateEvent(4);

  public static final GameStateEvent RIVER = new GameStateEvent(5);

  public static final GameStateEvent MTT_OVER = new GameStateEvent(10);

  public static final GameStateEvent UNKNOWN = new GameStateEvent(99);

  public int intValue() {
    return intVal;
  }

  public int hashCode() {
    return intVal;
  }

  public boolean equals(Object o) {
    return ( (o.getClass() == this.getClass()) &&
            ( (GameStateEvent) o).intVal == this.intVal);
  }

  public static String stringVal(GameStateEvent event) {
    switch (event.intVal) {
      case 0:
        return "GAME_OVER";
      case 1:
        return "GAME_BEGIN";
      case 2:
        return "FLOP";
      case 3:
        return "TURN";
      case 4:
        return "RIVER";
      case 5:
        return "SITNGO_OVER";

      default:
        return "UNKNOWN";
    }
  }

  int intVal;
}
