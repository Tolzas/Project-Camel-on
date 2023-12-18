/**
 * Define a single Case of the board.
 * Each case possess a coordonate set i and j, a color.
 * 
 *          ----------------------BRAVE----------------------
 * In case the gamemode is brave, each case possess a score system
 * depending on how much score can be gain by the ia (Blue) if played.
 * Each case is linked to two other case with the same score if such a case exist
 * in a linked list manner (previous case and next case). 
 * This score needs to be utdated each time the neighbor of the case is changed.
 * Also each time the neighbor of the case is changed
 * it needs to be removed from the linked list it is in and added in the one
 * corresponding to its new score.
 */
public class Case {

    private final int m_i;
    private final int m_j;

    private COLOR m_c;

    // Potential score if case selected (-1 if case not white)
    // (from 0 to 8 depending on how many neightbor case of opposite color can be
    // changed)
    // help to find best case (greedy strategy)
    private int m_potentialScoreBlue;
    // previous link in linked list of cases of same @m_scoreBlue
    private Case m_previousPotentialScoreBlue;
    // next link in linked list of cases of same @m_scoreBlue
    private Case m_nextPotentialScoreBlue;

    /**
     * Constructor of Case set up a White (A) Case.
     * 
     * @param i coordonate i of Case
     * @param j coordonate j of Case
     */
    public Case(int i, int j) {
        m_potentialScoreBlue = 0;
        m_c = COLOR.A;
        m_i = i;
        m_j = j;
    }

    /**
     * Setter of Case COLOR
     * 
     * @param c new Case COLOR
     * @return true if Case COLOR changed, false if not changed
     */
    public boolean setCOLOR(COLOR c) {
        boolean hasChanged = false;
        if (m_c != c) {
            m_c = c;
            hasChanged = true;
        }
        return hasChanged;
    }

    /**
     * Getter of Case COLOR.
     * 
     * @return color of Case
     */
    public COLOR getCOLOR() {
        return m_c;
    }

    /**
     * Getter of Case coordonate i.
     * 
     * @return coordonate i
     */
    public int getI() {
        return m_i;
    }

    /**
     * Getter of Case coordonate j.
     * 
     * @return coordonate j
     */
    public int getJ() {
        return m_j;
    }

    //          ----------------------BRAVE CASE SCORE SYSTEM----------------------

    /**
     * Setter of PotentialScoreBlue.
     * 
     * @param score new PotentialScoreBlue, integer E[-1, 9]
     */
    public void setPotentialScoreBlue(int score) {
        if (score >= -1 && score < 9) {
            m_potentialScoreBlue = score;
        }
    }

    /**
     * Incrementer of PotentialScoreBlue.
     */
    public void incrementPotentialScoreBlue() {
        if (m_potentialScoreBlue < 8) {
            m_potentialScoreBlue++;
        }
    }

    /**
     * Decrementer of PotentialScoreBlue.
     */
    public void decrementPotentialScoreBlue() {
        if (m_potentialScoreBlue > 0) {
            m_potentialScoreBlue--;
        }
    }

    /**
     * Getter of PotentialScoreBlue.
     * 
     * @return PotentialScoreBlue
     */
    public int getPotentialScoreBlue() {
        return m_potentialScoreBlue;
    }

    /**
     * Setter of PreviousPotentialScoreBlue.
     * 
     * @param previousB new value of PreviousPotentialScoreBlue
     */
    public void setPreviousPotentialScoreBlue(Case previousB) {
        m_previousPotentialScoreBlue = previousB;
    }

    /**
     * Setter of NextPotentialScoreBlue.
     * 
     * @param nextB new value of NextPotentialScoreBlue
     */
    public void setNextPotentialScoreBlue(Case nextB) {
        m_nextPotentialScoreBlue = nextB;
    }
    
    /**
     * Getter of PreviousPotentialScoreBlue.
     */
    public Case getPreviousPotentialScoreBlue() {
        return m_previousPotentialScoreBlue;
    }

    /**
     * Getter of NextPreviousPotentialScoreBlue.
     */
    public Case getNextPotentialScoreBlue() {
        return m_nextPotentialScoreBlue;
    }
}
