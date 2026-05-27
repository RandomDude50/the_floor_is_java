package game;

public class InputManager {

    private final boolean[] keys = new boolean[256];

    public void    keyPressed(int code)  { if (inRange(code)) keys[code] = true; }
    public void    keyReleased(int code) { if (inRange(code)) keys[code] = false; }
    public boolean isPressed(int code)   { return inRange(code) && keys[code]; }

    private boolean inRange(int code)    { return code >= 0 && code < keys.length; }
}