/**
 * A Region can be a smallRegion or a bigRegion (a Quadtree)
 */
public interface Region {
    /**
     * Set Case in (i, j), with COLOR c.
     * If isNeighbor is true, COLOR is changed only if current COLOR of Case(i, j) is NOT white (A).
     * If isNeighbor is false, COLOR is changed only if current COLOR of Case(i, j) is white (A).
     * 
     * @param i coordonate i of Case to be changed
     * @param j coordonate j of Case to be changed
     * @param c color to be changed
     * @param isNeighbor true if Case is the neighbor of the Case choosen by the player or the ia to be changed, false if it is the actual Case choosen by the player or the ia
     * @return true if case was successfuly changed, false if Case not changed
     */
    public boolean setCase(int i, int j, COLOR c, boolean isNeighbor);

    /**
     * Getter, is Case in an Aquise Region ?
     * 
     * @return true if Case in an Aquise Region, false otherwise
     */
    public boolean getAquise();

    /**
     * Getter of the COLOR of the Region if Region Aquise, white (A) if Region is not Aquise.
     * 
     * @return the COLOR of the Region
     */
    public COLOR getCOLORregion();

    /**
     * Getter of the size of the Region.
     * 
     * @return the number of Cases on a side of the Region
     */
    public int getSize();

    /**
     * Print each Case of the Quadtree.
     */
    public void printSelf();

    /**
     * Set a Case and its neighbour according to rules and set Regions to Aquise when needed.
     * 
     * @param i coordonate i of Case to be changed
     * @param j coordonate j of Case to be changed
     * @param c color to be changed
     * @return true if case was successfuly changed, false if Case not changed
     */
    public boolean chooseCase(int i, int j, COLOR c);

    /**
     * Go through the Quadtree and count each cases of COLOR c.
     * 
     * @param c the Color of the Cases that needs to be counted
     * @return the number of Cases of COLOR c
     */
    public int CalculeScore(COLOR c);

    /**
     * Choose the best Case according to the optimal srategie (not greedy), can only loose if start first or if board has 9*9 Cases.
     * Is recursivly called to find the best Cases. Best Case is choosen according to priority : priority = -1, error no Case can be played, 
     * 0 : Case can be played, 1 : Case can be played and is better that 0, 2 : Case can be played and is better than 1, 
     * priority is used to choose best Case on recursive call of the fonction.
     * 
     * @return an array of 3 integer : i, j, and priority
     */
    public int[] JouerIATemeraire();
}
