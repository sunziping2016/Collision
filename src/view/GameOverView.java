package view;

/**
 * Created by Sun on 4/21/2016.
 *
 * Game over view.
 */
public class GameOverView extends View implements RadialMenuListener {
    private RadialMenu menu;

    private static final int GAMEOVER_MENU_NEWGAME = 0;
    private static final int GAMEOVER_MENU_EXIT_GAME = 1;
    private static final int GAMEOVER_MENU_LEADERBOARD = 2;

    public GameOverView() {
        menu = new RadialMenu(8, 11.5f, this);
        menu.addItem(new RadialMenuItem(GAMEOVER_MENU_NEWGAME, "New Game", "newGame", 105, 30, RadialMenuItem.ORIENT_TOP));
        menu.addItem(new RadialMenuItem(GAMEOVER_MENU_LEADERBOARD, "High Scores", "leaderboard", 75, 30, RadialMenuItem.ORIENT_TOP));
        menu.addItem(new RadialMenuItem(GAMEOVER_MENU_EXIT_GAME, "Exit Game", "exit", 45, 30, RadialMenuItem.ORIENT_TOP));
    }

    @Override
    public void onMenuSelection(int id) {
    }

    @Override
    public void onPaint(GraphicsWrapper g2) {
    }
}
