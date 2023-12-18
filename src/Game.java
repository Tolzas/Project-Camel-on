/**
 * Set up a game of Cameleon, interact with the user thanks to an UI user interface.
 * Play the game according to rules given in pdf.
 */
public class Game {
    // true if game is playing, if false, game is off
    private boolean m_playing;
    // define wich gamemode is on, brave or temeraire. True = brave, false =
    // temeraire
    private boolean m_gamemode;
    // true : intelligente, false : gloutone
    private boolean m_startTemeraire;
    // number of cases on side of board, given by user and >0
    private int m_nbCaseSide = 0;
    // user text interface
    private UI m_ui;

    private Region quadtree;

    private Board m_board;

    /**
     * Constructor.
     * Set up the Board and, if gamemode is temeraire ; setup the Quadtree.
     */
    public Game() {
        m_playing = true;
        // text interface setUp
        m_ui = new UI();
        m_gamemode = m_ui.setGameMode();
        if (!m_gamemode) {
            m_startTemeraire = m_ui.setStrategieTemeraire();
        }
        if (m_ui.setBoard()) {
            m_nbCaseSide = m_ui.setN();
            m_board = new Board(m_nbCaseSide, m_gamemode);
            if (!m_gamemode) {
                if (m_nbCaseSide > 3) {
                    quadtree = new Quadtree(m_board, 0, 0, m_nbCaseSide);
                } else {
                    quadtree = new SmallRegion(m_board, 0, 0);
                }
            } else {
                m_board.setCaseBlueScore();
            }
        } else {
            m_board = m_ui.RemplirPlateau(m_playing, m_gamemode);
            m_nbCaseSide = m_board.getSize();
            if (!m_gamemode) {
                if (m_nbCaseSide > 3) {
                    quadtree = new Quadtree(m_board, 0, 0, m_nbCaseSide);
                } else {
                    quadtree = new SmallRegion(m_board, 0, 0);
                }
            }
        }

        if (m_playing) {
            System.out.println("Jeux initialisé, c'est partis!.");
        }
    }

    /**
     * Call the necessary method to run a game of Cameleon depending on the gamemode chosen during setup.
     */
    public void runGame() {
        if (m_gamemode) {
            m_ui.printBoard(m_board);
        } else {
            m_ui.printQuadtree(quadtree);
        }

        while (m_playing) {

            if (m_gamemode) {// BRAVE
                m_playing = m_ui.playerChoseIJ(m_board, m_gamemode, null);

                if (m_playing) {
                    m_ui.printBoard(m_board);
                    m_playing = m_board.continueGame();
                }

                if (m_playing) {
                    m_playing = m_board.JouerGloutonBrave();
                    m_ui.printBoard(m_board);
                    m_playing = m_board.continueGame();
                }
            } else {// TEMERAIRE
                m_playing = m_ui.playerChoseIJ(m_board, m_gamemode, quadtree);
                if (m_playing) {
                    m_playing = m_ui.printQuadtree(quadtree);
                }
                if (m_playing) {
                    int[] toPlay = quadtree.JouerIATemeraire();
                    if (toPlay[2] > -1) {
                        System.out.println("L'ia joue ; i : " + toPlay[0] + " j : " + toPlay[1]);
                        quadtree.chooseCase(toPlay[0], toPlay[1], COLOR.B);
                    } else {
                        m_playing = false;
                    }
                }
                if (m_playing) {
                    m_playing = m_ui.printQuadtree(quadtree);
                }
            }
        }
    }
}