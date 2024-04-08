package Piece;

import Main.GamePanel;
import Main.Type;

public class King extends Piece{
    public King(int color, int col, int row) {
        super(color, col, row);

        type = Type.KING;

        if(color == GamePanel.WHITE) {
            image = getImage("/piece/w-king");
        } else {
            image = getImage("/piece/b-king");
        }
    }
    public boolean canMove(int targetCol, int targetRow) {

        if(isWithinBoard(targetCol, targetRow)) {

            if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 ||
            Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1) {
                if(isSquareValid(targetCol, targetRow)) {
                    return true;
                }

            }

            // castle


            if(moved == false) {

                // desno

                if(targetCol == preCol+2 && targetRow == preRow && pieceSameLine(targetCol, targetRow) == false) {
                    for(Piece piece : GamePanel.simPieces) {
                        if(piece.col == preCol+3 && piece.row == preRow && piece.moved == false) {
                            GamePanel.castleP = piece;
                            return true;
                        }
                    }
                }

                // lijevo

                if(targetCol == preCol-2 && targetRow == preRow && pieceSameLine(targetCol, targetRow) == false) {
                    Piece p[] = new Piece[2];
                    for(Piece piece : GamePanel.simPieces) {
                        if(piece.col == preCol-3 && piece.row == targetRow) {

                            p[0] = piece;

                        }

                        if(piece.col == preCol-4 && piece.row == targetRow) {
                            p[1] = piece;
                        }
                        if(p[0] == null && p[1] != null && p[1].moved == false) {
                            GamePanel.castleP = piece;
                            return true;
                        }
                    }
                }

            }

        }
    return false;
    }
}


