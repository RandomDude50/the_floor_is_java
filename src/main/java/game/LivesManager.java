package game;

public class LivesManager {

    private int lives;

    public LivesManager(int initialLives) { this.lives = initialLives; }

    public void    loseLife()    { lives--; }
    public boolean isGameOver()  { return lives <= 0; }
    public int     getLives()    { return lives; }
}