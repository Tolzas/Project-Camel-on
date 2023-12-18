import java.io.File;
import java.util.Scanner;

/**
 * Class UI : User Interface, provide a user interface for the user to interact with.
 * Allow the user to type on the terminal according to the displayed instruction to setup and play the game.
 * Print the nessessary informations to play.
 */
public class UI {
    private Scanner sc = new Scanner(System.in);

    /**
     * Ask the user to chose between brave and temeraire,
     * the user must enter "brave" or "temeraire".
     * Spaces before and after are allowed.
     * The question is ask until the input is valid.
     * 
     * @return true for brave, false for temeraire
     */
    public boolean setGameMode() {
        boolean answerOK = false;
        String answerString = new String();
        while (!answerOK) {
            System.out.println("Selection du mode de jeux.");
            System.out.println("Selectionez en tapant : \"brave\" ou \"temeraire\".");
            answerString = sc.nextLine();
            if (answerString.matches("\\s*brave\\s*") || answerString.matches("\\s*temeraire\\s*")) {
                answerOK = true;
            }
        }
        return answerString.matches("\\s*brave\\s*");
    }

    /**
     * Ask the user to chose between an empty board and a board set from a file,
     * the user must enter "vide" or "preremplis".
     * Spaces before and after are allowed.
     * The question is ask until the input is valid.
     * 
     * @return true for empty, false for board from file
     */
    public boolean setBoard() {
        boolean answerOK = false;
        String answerString = new String();
        while (!answerOK) {
            System.out.println("Selection du plateau de jeux.");
            System.out.println("Selectionez en tapant : \"vide\" ou \"preremplis\".");
            answerString = sc.nextLine();
            if (answerString.matches("\\s*vide\\s*") || answerString.matches("\\s*preremplis\\s*")) {
                answerOK = true;
            }
        }
        return answerString.matches("\\s*vide\\s*");
    }

    /**
     * Ask the user to chose between the strategie intelligente and the greedy one,
     * the user must enter "intelligente" or "gloutone".
     * Spaces before and after are allowed.
     * The question is ask until the input is valid.
     * 
     * @return true for intelligente, false for greedy
     */
    public boolean setStrategieTemeraire() {
        boolean answerOK = false;
        String answerString = new String();
        while (!answerOK) {
            System.out.println("Selection de la strategie de l'ia jeux.");
            System.out.println("Selectionez en tapant : \"intelligente\" ou \"gloutone\" (non implementée, redirige vers strategie intelligente).");
            answerString = sc.nextLine();
            if (answerString.matches("\\s*intelligente\\s*") || answerString.matches("\\s*gloutone\\s*")) {
                answerOK = true;
            }
        }
        return answerString.matches("\\s*intelligente\\s*");
    }

    /**
     * Ask the user to chose n,
     * the user must enter an integer >= 0.
     * Spaces before and after are NOT allowed.
     * The question is ask until the input is valid (valid is considered >= 0).
     * 
     * @return number of cases of one side of the board
     */
    public int setN() {
        boolean answerOK = false;
        int answerInt = 0;

        while (!answerOK) {
            System.out.println("Selection du nombre n pour la création des région du tableau.");
            System.out.println("Tapez le nombre n.");
            answerInt = sc.nextInt();
            if (answerInt >= 0) {
                answerOK = true;
            }
        }

        return (int) (3 * Math.pow(2, answerInt));
    }

    /**
     * Create and fill a board from a path/file.
     * Format of the file given in desciption of the projet.
     * Ask the user the name of the file.
     * The question is only asked once, if reponse invalid or file in wrong format
     * throw exeption and stop game.
     * Path and file must contain no space or accent.
     * 
     * @param isPlaying set to false if board can't be setup for any reason
     * @param gamemode true if gamemode is brave, false if gamemode is temeraire
     * @return a board loaded from the file
     */
    public Board RemplirPlateau(boolean isPlaying, boolean gamemode) {
        String fileName = new String();
        Board board = null;
        System.out.println("Selection du fichier contenant le plateau de jeux.");
        System.out.println("Tapez l'adresse absolue du fichier (ne doit pas contenir d'espace ou d'accent).");
        fileName = sc.nextLine();

        try {
            File plateau = new File(fileName);
            Scanner scan = new Scanner(plateau);
            int nbCase = scan.nextInt();
            board = new Board(nbCase, gamemode);
            for (int i = 0; i < nbCase; i++) {
                String str = scan.next();
                for (int j = 0; j < nbCase; j++) {
                    char ch = str.charAt(j);
                    while (Character.isWhitespace(ch)) {
                        ch = scan.next().charAt(0);
                    }
                    switch (ch) {
                        case 'A':
                            break;
                        case 'B':
                            board.setCaseColor(i, j, COLOR.B);
                            break;
                        case 'R':
                            board.setCaseColor(i, j, COLOR.R);
                            break;

                        default:
                            System.out.println("Erreur contenu fichier");
                            isPlaying = false;
                            break;
                    }
                }
            }

            // initialisation brave score system
            if (gamemode) {
                board.setCaseBlueScore();
            }

            scan.close();
        } catch (Exception e) {
            isPlaying = false;
            System.out.println("Probleme lecture fichier.");
        }

        return board;
    }

    /**
     * Ask player to choose and play an i, and a j. Player can also choose to quit the game or to show the board and score
     * Play the choosen i and j in the Board if gamemode is brave or in the Region if gamemode is temeraire.
     * Ask for a i and j until it is an i and a j on the board and on a white case, or until "quitter" or "afficher" is entered.
     * "quitter" end the game, "afficher" print the board.
     * Format for i and j is : 2 integers separeted by a space with no space before and after.
     * 
     * @param board the structure used if gamemode is brave, get changed by i and j
     * @param gamemode true if gamemode is brave, false if gamemode is temeraire
     * @param quadtree the structure used if gamemode is temeraire, get changed by i and j
     * @return true
     */
    public boolean playerChoseIJ(Board board, boolean gamemode, Region quadtree) {
        boolean validCoordonne = false;
        boolean error = false;
        boolean printBoard = false;
        int i = 0;
        int j = 0;
        String str;

        while (!validCoordonne && !error && !printBoard) {
            System.out.println("A vous de jouer");
            System.out.println(
                    "Selectionner deux cases i et j du plateau ou taper \"quitter\" pour quitter\nou encore \"afficher\" pour afficher le plateau et les scores\nVous êtes les \u001B[31mrouges\u001B[0m.");
            str = "";
            while (str.equals("")) {
                str = sc.nextLine();
            }

            if (str.matches("\\s*quitter\\s*")) {
                error = true;
            } else if (str.matches("[0-9]+\\s+[0-9]+")) {
                String[] ij = str.split("\\s+");
                i = Integer.parseInt(ij[0]);
                j = Integer.parseInt(ij[1]);
                if (i >= 0 && i < board.getSize() && j >= 0 && j < board.getSize()) {
                    System.out.println("i : " + i);
                    System.out.println("j : " + j);

                    if (board.accesCase(i, j).getCOLOR() == COLOR.A) {
                        if (gamemode) {
                            board.setCaseColor(i, j, COLOR.R);
                            board.RemoveFromLinkedListBrave(i, j);
                            board.accesCase(i, j).setPotentialScoreBlue(-1);
                        } else {
                            quadtree.chooseCase(i, j, COLOR.R);
                        }
                        validCoordonne = true;
                    } else {
                        System.out.println("La case doit-etre blanche");
                    }
                } else {
                    System.out.println("La case n'est pas dans les limites du plateau");
                }
            } else if (str.matches("\\s*afficher\\s*")) {
                printBoard = true;
                printBoard(board);
            }
        }

        if (validCoordonne) {
            if (gamemode) {
                board.setNeighbour(i, j, true);
            }
        }

        return !error;
    }

    /**
     * Print the board from the Board structure.
     * 
     * @param board the Board to be printed
     */
    public void printBoard(Board board) {
        COLOR c;
        System.out.print("X  ");
        for (int x = 0; x < board.getSize(); ++x) {
            if (x >= 10 && x < 100) {
                System.out.print(x + " ");
            } else if (x >= 100) {
                System.out.print(x);
            } else {
                System.out.print(x + "  ");
            }
        }
        System.out.print('\n');
        for (int i = 0; i < board.getSize(); ++i) {
            if (i >= 10 && i < 100) {
                System.out.print(i + " ");
            } else if (i >= 100) {
                System.out.print(i);
            } else {
                System.out.print(i + "  ");
            }
            for (int j = 0; j < board.getSize(); ++j) {
                c = board.accesCase(i, j).getCOLOR();
                if (c == COLOR.A) {
                    System.out.print("A  ");
                } else if (c == COLOR.B) {
                    System.out.print("\u001B[34mB\u001B[0m  ");
                } else if (c == COLOR.R) {
                    System.out.print("\u001B[31mR\u001B[0m  ");
                }
            }
            System.out.print('\n');
        }
        System.out.println("Score \u001B[34mBleu\u001B[0m (l'ia) : " + board.getScoreB());
        System.out.println("Score \u001B[31mRouge\u001B[0m (vous) : " + board.getScoreR());
    }

    /**
     * Print the board from the Region structure (Quadtree if number of side case > 3, SmallRegion otherwise).
     * Print the score for Red and Blue, stop the game if game is over and show the winner.
     * 
     * @param region the Region to be printed
     * @return false if the game is over (no more white case), true otherwise
     */
    public boolean printQuadtree(Region region) {
        boolean continueGame = true;
        System.out.print("X  ");
        for (int x = 0; x < region.getSize(); ++x) {
            if (x >= 10 && x < 100) {
                System.out.print(x + " ");
            } else if (x >= 100) {
                System.out.print(x);
            } else {
                System.out.print(x + "  ");
            }
        }
        for (int i = 0; i < region.getSize(); ++i) {
            System.out.print('\n');
            if (i >= 10 && i < 100) {
                System.out.print(i + " ");
            } else if (i >= 100) {
                System.out.print(i);
            } else {
                System.out.print(i + "  ");
            }
        }
        int scoreB = region.CalculeScore(COLOR.B);
        int scoreR = region.CalculeScore(COLOR.R);
        System.out.print("\n\033[3C");
        System.out.print("\033[" + region.getSize() + "A");
        region.printSelf();
        System.out.print("\033[" + region.getSize() + "B");
        System.out.println("\033[3DScore \u001B[34mBleu\u001B[0m (l'ia) : " + scoreB);
        System.out.println("Score \u001B[31mRouge\u001B[0m (vous) : " + scoreR);
        if (continueGame) {
            if (scoreB + scoreR == region.getSize() * region.getSize()) {
                System.out.println("Partie terminé !");
                if (scoreB == scoreR) {
                    System.out.println("Egalité !!");
                } else if (scoreB > scoreR) {
                    System.out.println("Les \u001B[34mbleus\u001B[0m gagnent !!");
                } else if (scoreB < scoreR) {
                    System.out.println("Les \u001B[31mrouges\u001B[0m gagnent !!");
                }
                continueGame = false;
            }
        }
        return continueGame;
    }
}
