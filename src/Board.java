/**
 * Store the board in a matrice of Case
 * Also store the current number of cases of each COLOR if gamemode Brave.
 */
public class Board {
    private int m_nbCaseSide;
    private Case m_matrice[][];
    
    // score system brave, scoreB = number of blue (B) Cases, scoreR = number of red (R) Cases
    private int m_scoreB;
    private int m_scoreR;
    // potential score system -> Contain 9 Cases : one case of each potential score if clicked by IA (or null if no Case of x score)
    // Help the IA to choose the best Case to play for immediate gain (brave gamemode).
    // each Case if linked to an other of the same score if there is one.
    private Case m_TabOfLinkedListsOfscore[];
    // --

    /**
     * Constructor, create a matrice of nbCaseSide Cases on each side.
     * 
     * @param nbCaseSide number of Cases on each side of the new Board
     * @param gamemode true if brave, false if temeraire
     */
    public Board(int nbCaseSide, boolean gamemode) {
        m_nbCaseSide = nbCaseSide;
        m_scoreB = 0;
        m_scoreR = 0;
        m_matrice = new Case[m_nbCaseSide][m_nbCaseSide]; // Creation of the matrice.

        // Création des cases pour le plateau.
        for (int i = 0; i < m_nbCaseSide; i++) {
            for (int j = 0; j < m_nbCaseSide; j++) {
                m_matrice[i][j] = new Case(i, j);
                m_matrice[i][j].setCOLOR(COLOR.A);
            }
        }

        // set up score system if gamemode is brave
        if (gamemode) {
            m_TabOfLinkedListsOfscore = new Case[9];
        }
    }

    /**
     * Getter of the Case at coordonate (i, j)
     * 
     * @param i coordonate i of needed Case
     * @param j coordonate j of needed Case
     * @return the Case at the coordonate (i, j)
     */
    public Case accesCase(int i, int j) {
        return m_matrice[i][j];
    }

    /**
     * Add a Case at the appropriate emplacement (depending on its score) in the array m_TabOfLinkedListsOfscore.
     * The element in the array beeing the top of the linked list of Cases of the same potential score if clicked.
     * 
     * @param toAdd the Case to be added in the array
     */
    public void addToLinkedListBrave(Case toAdd) {
        int score = toAdd.getPotentialScoreBlue();
        if (score >= 0 && score <= 8) {
            if (m_TabOfLinkedListsOfscore[score] == null) {
                m_TabOfLinkedListsOfscore[score] = toAdd;
            } else {
                m_TabOfLinkedListsOfscore[score].setNextPotentialScoreBlue(toAdd);
                Case tempo = m_TabOfLinkedListsOfscore[score];
                toAdd.setPreviousPotentialScoreBlue(tempo);
                m_TabOfLinkedListsOfscore[score] = toAdd;
            }
        } else {
            System.out.println(
                    "Erreur ajout linked list i : " + toAdd.getI() + " j : " + toAdd.getJ() + " score : " + score);
        }
    }

    /**
     * Remove the top element in the array of Linked List at the emplacement of the given score.
     * If there is a next element in the Case removed, put it in the array.
     * Used after the top Case was used by the IA because this case is no longer white and can't be in the linked list.
     * 
     * @param score score of the Case removed
     */
    public void RemoveTopLinkedListBrave(int score) {
        if (score >= 0 && score <= 8) {
            if (m_TabOfLinkedListsOfscore[score] == null) {
                System.out.println("Erreur Linked list déjà null");
            } else {
                if (m_TabOfLinkedListsOfscore[score].getPreviousPotentialScoreBlue() == null) {
                    m_TabOfLinkedListsOfscore[score] = null;
                } else {
                    m_TabOfLinkedListsOfscore[score] = m_TabOfLinkedListsOfscore[score].getPreviousPotentialScoreBlue();
                    m_TabOfLinkedListsOfscore[score].setNextPotentialScoreBlue(null);
                }
            }
        } else {
            System.out.println("Erreur retirer linked list");
        }
    }

    /**
     * Remove the Case at the given (i, j).
     * Used when Case change Potential score because neighbor changed and need to change of linked list.
     * Or when case is no longer white after beeing played.
     * 
     * @param i coordonate i of Case to be remove from its linked list
     * @param j coordonate j of Case to be remove from its linked list
     */
    public void RemoveFromLinkedListBrave(int i, int j) {
        if (i >= 0 && j >= 0 && i < m_nbCaseSide && j < m_nbCaseSide) {
            Case previous = m_matrice[i][j].getPreviousPotentialScoreBlue();
            Case next = m_matrice[i][j].getNextPotentialScoreBlue();

            // if case[i][j] top of the linked list of its score
            if (m_matrice[i][j].getNextPotentialScoreBlue() == null) {
                if (previous == null) {// previous == null && next == null
                    m_TabOfLinkedListsOfscore[m_matrice[i][j].getPotentialScoreBlue()] = null;
                } else {// previous != null && next == null
                    m_TabOfLinkedListsOfscore[m_matrice[i][j].getPotentialScoreBlue()] = previous;
                    previous.setNextPotentialScoreBlue(null);
                }
            } else {
                if (previous == null) {// previous == null && next != null
                    next.setPreviousPotentialScoreBlue(null);
                } else {// previous != null && next != null
                    next.setPreviousPotentialScoreBlue(previous);
                    previous.setNextPotentialScoreBlue(next);
                }
            }

            m_matrice[i][j].setPreviousPotentialScoreBlue(null);
            m_matrice[i][j].setNextPotentialScoreBlue(null);
        }
    }

    /**
     * Play a case for the IA. Go through the array of Cases of different potential score (9 Cases) from best to worst
     * and choose the first Case that is not null. Remove it from the array after beeing play and put the next case
     * of the same score in there is one instead as head of the linked list and as the Case in the array at that score.
     * Also, actualise the potential score of every white (A) case around the Cases that was were modified.
     * 
     * @return true if game must be stop, false if game can be continued
     */
    public boolean JouerGloutonBrave() {
        int score = 8;
        boolean arrayLoop = true;
        boolean erreur = true;

        while (arrayLoop) {
            if (score == 0) {
                arrayLoop = false;
            }

            if (m_TabOfLinkedListsOfscore[score] != null) {
                System.out.println("L'ia glouton joue  i : " + m_TabOfLinkedListsOfscore[score].getI() + ", j : "
                        + m_TabOfLinkedListsOfscore[score].getJ() + " !");
                setCaseColor(m_TabOfLinkedListsOfscore[score].getI(), m_TabOfLinkedListsOfscore[score].getJ(), COLOR.B);
                m_TabOfLinkedListsOfscore[score].setPotentialScoreBlue(-1);
                setNeighbour(m_TabOfLinkedListsOfscore[score].getI(), m_TabOfLinkedListsOfscore[score].getJ(), false);
                RemoveTopLinkedListBrave(score);
                erreur = false;
                arrayLoop = false;
            }

            score--;
        }

        return !erreur;
    }

    /**
     * Set the neighbour of Case (i, j) for the brave gamemode.
     * Case in : (i + [-1, 1], j + [-1, 1]) where if i == 0, j != 0.
     * Set previously defined Case if it is currently blue (B) only if player = true
     * Set previously defined Case if it is currently red (R) only if player = false
     * 
     * @param i coordinate i of Case whose neighbour will be changed
     * @param j coordinate j of Case whose neighbour will be changed
     * @param player true if Player is player red (R), false if player is IA : blue (B)
     */
    public void setNeighbour(int i, int j, boolean player) {
        // color neighbour
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (((i + x) >= 0) && ((j + y) >= 0) && ((i + x) < m_nbCaseSide) && ((j + y) < m_nbCaseSide)) {
                    if (m_matrice[i + x][j + y].getCOLOR() == COLOR.B && player) {
                        setCaseColor(i + x, j + y, COLOR.R);

                        // change neighbor of neighbour blue score if it is white (neighbour is (i+x,
                        // j+y))
                        for (int w = -1; w < 2; w++) {
                            for (int z = -1; z < 2; z++) {
                                if ((w != 0 || z != 0) && (i + x + w) >= 0 && (j + y + z) >= 0
                                        && (i + x + w) < m_nbCaseSide && (j + y + z) < m_nbCaseSide) {
                                    if (m_matrice[i + x + w][j + y + z].getCOLOR() == COLOR.A) {
                                        RemoveFromLinkedListBrave(i + x + w, j + y + z);
                                        m_matrice[i + x + w][j + y + z].incrementPotentialScoreBlue();
                                        addToLinkedListBrave(m_matrice[i + x + w][j + y + z]);
                                    }
                                }
                            }
                        }
                    } else if (m_matrice[i + x][j + y].getCOLOR() == COLOR.R && !player) {
                        setCaseColor(i + x, j + y, COLOR.B);

                        // change neighbor of neighbor red score if it is white (neighbor is (i+x, j+y))
                        for (int w = -1; w < 2; w++) {
                            for (int z = -1; z < 2; z++) {
                                if ((w != 0 || z != 0) && (i + x + w) >= 0 && (j + y + z) >= 0
                                        && (i + x + w) < m_nbCaseSide && (j + y + z) < m_nbCaseSide) {
                                    if (m_matrice[i + x + w][j + y + z].getCOLOR() == COLOR.A) {
                                        RemoveFromLinkedListBrave(i + x + w, j + y + z);
                                        m_matrice[i + x + w][j + y + z].decrementPotentialScoreBlue();
                                        addToLinkedListBrave(m_matrice[i + x + w][j + y + z]);
                                    }
                                }
                            }
                        }
                    } else if (m_matrice[i + x][j + y].getCOLOR() == COLOR.A) {
                        if (x != 0 || y != 0) {
                            if (player) {
                                System.out.println((i + x) + " " + (j + y));
                                RemoveFromLinkedListBrave(i + x, j + y);
                                m_matrice[i + x][j + y].incrementPotentialScoreBlue();
                                addToLinkedListBrave(m_matrice[i + x][j + y]);
                            } else {
                                RemoveFromLinkedListBrave(i + x, j + y);
                                m_matrice[i + x][j + y].decrementPotentialScoreBlue();
                                addToLinkedListBrave(m_matrice[i + x][j + y]);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Check score, if score of blue cases and score of red cases equal
     * the number of Cases on Board, game is over.
     * Print the winner with the best score, or equality.
     * 
     * @return true if game can be continue, false if game is over
     */
    public boolean continueGame() {
        boolean continueGame = true;
        if (getScoreB() + getScoreR() == m_nbCaseSide * m_nbCaseSide) {
            System.out.println("Partie terminé !");
            if (getScoreB() == getScoreR()) {
                System.out.println("Egalité !!");
            } else if (getScoreB() > getScoreR()) {
                System.out.println("Les \u001B[34mbleus\u001B[0m gagnent !!");
            } else if (getScoreB() < getScoreR()) {
                System.out.println("Les \u001B[31mrouges\u001B[0m gagnent !!");
            }
            continueGame = false;
        }
        return continueGame;
    }

    /**
     * Set up of the game for brave gamemode IA. 
     * Set each white (A) Cases to their correct potential score, depending on the number of neigbour the got
     * then place them in the correct linked list.
     */
    public void setCaseBlueScore() {
        for (int i = 0; i < m_nbCaseSide; i++) {
            for (int j = 0; j < m_nbCaseSide; j++) {
                m_matrice[i][j].setPotentialScoreBlue(0);
                // if case white, look how much score can be gained in brave gamemode if clicked
                if (m_matrice[i][j].getCOLOR() == COLOR.A) {
                    // each neightbor of each white cases
                    for (int x = -1; x < 2; x++) {
                        for (int y = -1; y < 2; y++) {
                            // if neighbor exist in board
                            if ((x != 0 || y != 0) && (i + x) >= 0 && (j + y) >= 0 && (i + x) < m_nbCaseSide
                                    && (j + y) < m_nbCaseSide) {
                                if (m_matrice[i + x][j + y].getCOLOR() == COLOR.R) {
                                    // if neighbor is of opposite color, score +1 if i, j case taken
                                    m_matrice[i][j].incrementPotentialScoreBlue();
                                }
                            }
                        }
                    }
                    // put cases in the appropriate linked list depending on blue neighbor score
                    addToLinkedListBrave(m_matrice[i][j]);
                } else {
                    accesCase(i, j).setPotentialScoreBlue(-1);
                }
            }
        }
    }

    /**
     * Set Case in (i, j) to the COLOR c and change score accordingly.
     * 
     * @param i coordonate i of the Case to be changed
     * @param j coordonate j of the Case to be changed
     * @param c COLOR to be changed at Case (i, j)
     */
    public void setCaseColor(int i, int j, COLOR c) {
        if (i >= 0 && i < m_nbCaseSide && j >= 0 && j < m_nbCaseSide) {
            if (m_matrice[i][j].getCOLOR() == COLOR.A) {
                if (c == COLOR.R) {
                    incrementScoreR();
                    m_matrice[i][j].setCOLOR(c);
                } else if (c == COLOR.B) {
                    incrementScoreB();
                    m_matrice[i][j].setCOLOR(c);
                }
            } else if (m_matrice[i][j].getCOLOR() == COLOR.B) {
                if (c == COLOR.R) {
                    incrementScoreR();
                    decrementScoreB();
                    m_matrice[i][j].setCOLOR(c);
                }
            } else if (m_matrice[i][j].getCOLOR() == COLOR.R) {
                if (c == COLOR.B) {
                    incrementScoreB();
                    decrementScoreR();
                    m_matrice[i][j].setCOLOR(c);
                }
            }
        }
    }

    /**
     * Add one to the score of the blue (B) the IA.
     */
    public void incrementScoreB() {
        m_scoreB++;
    }

    /**
     * Add one to the score of the red (R) the player.
     */
    public void incrementScoreR() {
        m_scoreR++;
    }
    /**
     * Remove one to the score of the blue (B) the IA.
     */
    public void decrementScoreB() {
        m_scoreB--;
    }
    /**
     * Remove one to the score of the red (R) the player.
     */
    public void decrementScoreR() {
        m_scoreR--;
    }

    /**
     * Getter of the number of Cases on one side of the Board.
     * 
     * @return number of Cases on one side of the Board
     */
    public int getSize() {
        return m_nbCaseSide;
    }

    /**
     * getter of the score of the blue (B) the IA.
     * 
     * @return the score of the blue (B) the IA
     */
    public int getScoreB() {
        return m_scoreB;
    }

    /**
     * getter of the score of the red (R) the player.
     * 
     * @return the score of the red (R) the player
     */
    public int getScoreR() {
        return m_scoreR;
    }
}
