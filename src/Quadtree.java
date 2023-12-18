/**
 * A region of n*n Cases. Countain 4 subRegion of a quarter of the Cases.
 * Its place on the Board is defined by an i and j (corresponding to its top left corner).
 * If all Cases Aquise according to the rules, gets a Region COLOR.
 */
public class Quadtree implements Region {
    private Region m_topLeft;
    private Region m_topRight;
    private Region m_bottomRight;
    private Region m_bottomLeft;
    private final int m_iTop;
    private final int m_jLeft;
    private final int m_sizeRegion;
    private boolean m_aquise;
    // color of region if it is aquise A if not aquise
    private COLOR m_COLORaquise;

    /**
     * Constructor, set up the QuadTree with its coordonate on the given Board.
     * 
     * @param board board where the QuadTree will get and modify its Cases from
     * @param i coordonate of the top most cases of the QuadTree on the Board
     * @param j coordonate of the left most cases of the QuadTree on the Board
     * @param nbCaseSide number of Cases on one side of the Quadtree
     */
    public Quadtree(Board board, int i, int j, int nbCaseSide) {
        m_iTop = i;
        m_jLeft = j;
        m_sizeRegion = nbCaseSide;
        m_aquise = false;
        m_COLORaquise = COLOR.A;
        if (nbCaseSide / 2 <= 3) {
            m_topLeft = new SmallRegion(board, i, j);
            m_bottomLeft = new SmallRegion(board, i + nbCaseSide / 2, j);
            m_bottomRight = new SmallRegion(board, i + nbCaseSide / 2, j + nbCaseSide / 2);
            m_topRight = new SmallRegion(board, i, j + nbCaseSide / 2);
        } else {
            m_topLeft = new Quadtree(board, i, j, nbCaseSide / 2);
            m_bottomLeft = new Quadtree(board, i + nbCaseSide / 2, j, nbCaseSide / 2);
            m_bottomRight = new Quadtree(board, i + nbCaseSide / 2, j + nbCaseSide / 2, nbCaseSide / 2);
            m_topRight = new Quadtree(board, i, j + nbCaseSide / 2, nbCaseSide / 2);
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
                if (i + x >= m_iTop && j + y >= m_jLeft && i + x < m_iTop + m_sizeRegion
                        && j + y < m_jLeft + m_sizeRegion) {
                    setCase(i + x, j + y, c, true);
                }
            }
        }
        return allGood;
    }

    /**
     * Implement Region, doc in Region
     */
    public boolean setCase(int i, int j, COLOR c, boolean isNeighbor) {
        boolean allGood = true;
        // check if case in this region
        if (!m_aquise) {
            if (i >= m_iTop && i < m_iTop + m_sizeRegion && j >= m_jLeft && j < m_jLeft + m_sizeRegion) {
                // check if case is in top subregion
                if (i < m_iTop + m_sizeRegion / 2) {
                    // check if case is in left subregion
                    if (j < m_jLeft + m_sizeRegion / 2) {
                        if (m_topLeft.setCase(i, j, c, isNeighbor)) {
                            // if case was succesfuly changed : check if sub region is now aquise : if so,
                            // check if
                            // this region is currently aquise
                            if (m_topLeft.getAquise()) {
                                COLOR regionColorIfAquise = COLOR.A;
                                m_aquise = RemplirRegion(regionColorIfAquise, c);
                            }
                        } else {
                            allGood = false;
                        }
                    } else {
                        if (m_topRight.setCase(i, j, c, isNeighbor)) {
                            // if case was succesfuly changed : check if sub region is now aquise : if so,
                            // check if
                            // this region is currently aquise
                            if (m_topRight.getAquise()) {
                                COLOR regionColorIfAquise = COLOR.A;
                                m_aquise = RemplirRegion(regionColorIfAquise, c);
                            }
                        } else {
                            allGood = false;
                        }
                    }
                } else {
                    // check if case is in left subregion
                    if (j < m_jLeft + m_sizeRegion / 2) {
                        if (m_bottomLeft.setCase(i, j, c, isNeighbor)) {
                            // if case was succesfuly changed : check if sub region is now aquise : if so,
                            // check if
                            // this region is currently aquise
                            if (m_bottomLeft.getAquise()) {
                                COLOR regionColorIfAquise = COLOR.A;
                                m_aquise = RemplirRegion(regionColorIfAquise, c);
                            }
                        } else {
                            allGood = false;
                        }
                    } else {
                        if (m_bottomRight.setCase(i, j, c, isNeighbor)) {
                            // if case was succesfuly changed : check if sub region is now aquise : if so,
                            // check if
                            // this region is currently aquise
                            if (m_bottomRight.getAquise()) {
                                COLOR regionColorIfAquise = COLOR.A;
                                m_aquise = RemplirRegion(regionColorIfAquise, c);
                            }
                        } else {
                            allGood = false;
                        }
                    }
                }
            } else {
                System.out.println("Error out of bound case");
                System.out.println("i : " + i + " m_iTop : " + m_iTop + " j : " + j + " m_jLeft : " + m_jLeft
                        + " sizeregion : " + m_sizeRegion);
                allGood = false;
            }
        }

        return allGood;
    }

    /**
     * Set Quadtree to Aquise according to given rules
     * Remove all branch of the tree past him if Aquise.
     * Set its COLOR according to given rules.
     * 
     * @param regionColorIfAquise get changed to the COLOR of the Region if it get Aquise
     * @param currentPlayerCOLOR COLOR of the current Player red (R) for player, blue (B) for IA
     * @return true if region is Aquise, false if not aquise
     */
    private boolean RemplirRegion(COLOR regionColorIfAquise, COLOR currentPlayerCOLOR) {
        boolean isAquise = true;
        if (m_bottomLeft.getAquise() && m_bottomRight.getAquise() && m_topLeft.getAquise() && m_topRight.getAquise()) {
            int nbRegionBlue = 0;
            if (m_bottomLeft.getCOLORregion() == COLOR.B) {
                nbRegionBlue++;
            }
            if (m_bottomRight.getCOLORregion() == COLOR.B) {
                nbRegionBlue++;
            }
            if (m_topLeft.getCOLORregion() == COLOR.B) {
                nbRegionBlue++;
            }
            if (m_topRight.getCOLORregion() == COLOR.B) {
                nbRegionBlue++;
            }

            if (currentPlayerCOLOR == COLOR.R) {
                if (nbRegionBlue <= 2) {
                    regionColorIfAquise = COLOR.R;
                } else {
                    regionColorIfAquise = COLOR.B;
                }
            } else if (currentPlayerCOLOR == COLOR.B) {
                if (nbRegionBlue < 2) {
                    regionColorIfAquise = COLOR.R;
                } else {
                    regionColorIfAquise = COLOR.B;
                }
            }
            m_COLORaquise = regionColorIfAquise;
            m_bottomLeft = null;
            m_bottomRight = null;
            m_topRight = null;
            m_topLeft = null;
        } else {
            isAquise = false;
        }
        return isAquise;
    }

    /**
     * Implement Region, doc in Region
     */
    public boolean getAquise() {
        return m_aquise;
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
        return m_sizeRegion;
    }

    /**
     * Implement Region, doc in Region
     */
    public void printSelf() {
        if (m_aquise) {
            if (m_iTop > 0) {
                System.out.print("\033[" + m_iTop + "B");
            }
            if (m_jLeft > 0) {
                System.out.print("\033[" + m_jLeft * 3 + "C");
            }
            for (int i = 0; i < m_sizeRegion; i++) {
                for (int j = 0; j < m_sizeRegion; j++) {
                    if (m_COLORaquise == COLOR.A) {
                        System.out.print("A  ");
                    } else if (m_COLORaquise == COLOR.B) {
                        System.out.print("\u001B[34mB\u001B[0m  ");
                    } else if (m_COLORaquise == COLOR.R) {
                        System.out.print("\u001B[31mR\u001B[0m  ");
                    }
                }
                System.out.print("\033[" + 3 * m_sizeRegion + "D\033[1B");
            }
            System.out.print("\033[" + (m_iTop + m_sizeRegion) + "A");
            if (m_jLeft > 0) {
                System.out.print("\033[" + m_jLeft * 3 + "D");
            }
        } else {
            m_topLeft.printSelf();
            m_topRight.printSelf();
            m_bottomLeft.printSelf();
            m_bottomRight.printSelf();
        }
    }

    /**
     * Implement Region, doc in Region
     */
    public int CalculeScore(COLOR c) {
        int totalScore = 0;
        if (m_aquise) {
            if (m_COLORaquise == c) {
                totalScore = m_sizeRegion * m_sizeRegion;
            }
        } else {
            totalScore = m_bottomLeft.CalculeScore(c) + m_bottomRight.CalculeScore(c) + m_topLeft.CalculeScore(c)
                    + m_topRight.CalculeScore(c);
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
        if (!m_aquise) {
            int[] tempo = { 0, 0, -1 };
            tempo = m_topLeft.JouerIATemeraire();
            if (tempo[2] > coordonneAndPriority[2]) {
                coordonneAndPriority[0] = tempo[0];
                coordonneAndPriority[1] = tempo[1];
                coordonneAndPriority[2] = tempo[2];
            }
            tempo = m_topRight.JouerIATemeraire();
            if (tempo[2] > coordonneAndPriority[2]) {
                coordonneAndPriority[0] = tempo[0];
                coordonneAndPriority[1] = tempo[1];
                coordonneAndPriority[2] = tempo[2];
            }
            tempo = m_bottomLeft.JouerIATemeraire();
            if (tempo[2] > coordonneAndPriority[2]) {
                coordonneAndPriority[0] = tempo[0];
                coordonneAndPriority[1] = tempo[1];
                coordonneAndPriority[2] = tempo[2];
            }
            tempo = m_bottomRight.JouerIATemeraire();
            if (tempo[2] > coordonneAndPriority[2]) {
                coordonneAndPriority[0] = tempo[0];
                coordonneAndPriority[1] = tempo[1];
                coordonneAndPriority[2] = tempo[2];
            }
        }

        return coordonneAndPriority;
    }
}
