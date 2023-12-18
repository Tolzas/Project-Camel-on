/**
 * A region of 3*3 Cases, gets it's Cases from a reference of the Board.
 * Its place on the Board is defined by an i and j (corresponding to its top left corner).
 * If all Cases Aquise according to the rules, gets a Region COLOR.
 * Count the number of white Cases left to know when it gets Aquise.
 */
public class SmallRegion implements Region {
    // reference of the board
    private final Board m_matrice;
    // coordonate of the left most case of the region
    private final int m_iTop;
    // coordonate of the top most case of the region
    private final int m_jLeft;
    // true if region is exclusivly one non A (white) COLOR
    private boolean m_aquise;
    // number of white cases left
    private int m_nbWhiteCase;
    // color of region if it is aquise A if not aquise
    private COLOR m_COLORaquise;

    /**
     * Constructor, set up the SmallRegion with its coordonate on the given Board.
     * 
     * @param board board where the SmallRegion will get and modify its Cases from
     * @param i coordonate of the top most cases of the SmallRegion on the Board
     * @param j coordonate of the left most cases of the SmallRegion on the Board
     */
    public SmallRegion(Board board, int i, int j) {
        m_COLORaquise = COLOR.A;
        m_matrice = board;
        m_iTop = i;
        m_jLeft = j;
        m_nbWhiteCase = 9;
        m_aquise = true;

        COLOR firstCase = m_matrice.accesCase(m_iTop, m_jLeft).getCOLOR();
        // check number of white cases left
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (firstCase != m_matrice.accesCase(m_iTop + x, m_jLeft + y).getCOLOR()) {
                    m_aquise = false;
                }
                if (m_matrice.accesCase(m_iTop + x, m_jLeft + y).getCOLOR() != COLOR.A) {
                    m_nbWhiteCase--;
                } else {
                    m_aquise = false;
                }
            }
        }
        if (m_aquise) {
            m_COLORaquise = firstCase;
        }
    }

    /**
     * Implement Region, doc in Region
     */
    public boolean setCase(int i, int j, COLOR c, boolean isNeighbor) {
        boolean allGood = true;
        if (!m_aquise) {
            if (i >= m_iTop && i < m_iTop + 3 && j >= m_jLeft && j < m_jLeft + 3) {
                if (isNeighbor) {
                    if (m_matrice.accesCase(i, j).getCOLOR() != COLOR.A) {
                        m_matrice.setCaseColor(i, j, c);
                    } else {
                        allGood = false;
                    }
                } else {
                    if (m_matrice.accesCase(i, j).getCOLOR() == COLOR.A) {
                        m_matrice.setCaseColor(i, j, c);
                        // if last white case filled, all cases of the region are set to the current
                        // COLOR
                        // and region is "aquise"
                        m_nbWhiteCase--;
                        if (m_nbWhiteCase <= 0) {
                            m_aquise = true;
                            m_COLORaquise = c;
                        }
                    } else {
                        System.out.println("Erreur case non blanche");
                        allGood = false;
                    }
                }
            } else {
                System.out.println("erreur case introuvable");
                allGood = false;
            }
        }

        return allGood;
    }

    /**
     * Implement Region, doc in Region
     */
    public boolean getAquise() {
        return m_aquise || m_nbWhiteCase == 0;
    }

    /**
     * Implement Region, doc in Region
     */
    public COLOR getCOLORregion() {
        return m_COLORaquise;
    }

    /**
     * Implement Region, doc in Region
     */
    public int getSize() {
        return 3;
    }

    /**
     * Implement Region, doc in Region
     */
    public void printSelf() {
        if (m_iTop > 0) {
            System.out.print("\033[" + m_iTop + "B");
        }
        if (m_jLeft > 0) {
            System.out.print("\033[" + m_jLeft * 3 + "C");
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                COLOR c;
                if (m_aquise) {
                    c = m_COLORaquise;
                } else {
                    c = m_matrice.accesCase(m_iTop + i, m_jLeft + j).getCOLOR();
                }
                if (c == COLOR.A) {
                    System.out.print("A  ");
                } else if (c == COLOR.B) {
                    System.out.print("\u001B[34mB\u001B[0m  ");
                } else if (c == COLOR.R) {
                    System.out.print("\u001B[31mR\u001B[0m  ");
                }
            }
            System.out.print("\033[" + 9 + "D\033[1B");
        }
        System.out.print("\033[" + (m_iTop + 3) + "A");
        if (m_jLeft > 0) {
            System.out.print("\033[" + m_jLeft * 3 + "D");
        }
    }

    /**
     * Implement Region, doc in Region
     */
    public boolean chooseCase(int i, int j, COLOR c) {
        boolean allGood = true;
        allGood = setCase(i, j, c, false);
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (i + x >= m_iTop && j + y >= m_jLeft && i + x < m_iTop + 3 && j + y < m_jLeft + 3) {
                    setCase(i + x, j + y, c, true);
                }
            }
        }
        return allGood;
    }

    /**
     * Implement Region, doc in Region
     */
    public int CalculeScore(COLOR c) {
        int totalScore = 0;
        if (m_aquise) {
            if (m_COLORaquise == c) {
                totalScore = 9;
            }
        } else {
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    if (m_matrice.accesCase(m_iTop + x, m_jLeft + y).getCOLOR() == c) {
                        totalScore++;
                    }
                }
            }
        }
        return totalScore;
    }

    /**
     * Implement Region, doc in Region
     */
    public int[] JouerIATemeraire() {
        // First two int are i and j, third is priority -> -1 : unusable, 0 : worst, 1 :
        // ok, 2 : best
        int[] coordonneAndPriority = { 0, 0, -1 };
        if (!m_aquise && m_nbWhiteCase != 0) {
            if (m_nbWhiteCase == 1) {
                coordonneAndPriority[2] = 2;
            } else if (m_nbWhiteCase == 2) {
                coordonneAndPriority[2] = 0;
            } else if (m_nbWhiteCase > 2) {
                coordonneAndPriority[2] = 1;
            }
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    if (m_matrice.accesCase(m_iTop + x, m_jLeft + y).getCOLOR() == COLOR.A) {
                        coordonneAndPriority[0] = m_iTop + x;
                        coordonneAndPriority[1] = m_jLeft + y;
                    }
                }
            }
        }

        return coordonneAndPriority;
    }
}
