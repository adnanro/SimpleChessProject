package Piece;

import Main.GamePanel;
import Main.Type;

public class Queen extends Piece{
    public Queen(int color, int col, int row) {
        super(color, col, row);

        type = Type.QUEEN;

        if(color == GamePanel.WHITE) {
            image = getImage("/piece/w-queen");
        } else {
            image = getImage("/piece/b-queen");
        }
    }

    public boolean canMove(int targetCol, int targetRow) {

        if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false){

            if (targetCol == preCol || targetRow == preRow) {
                if (isSquareValid(targetCol, targetRow) && pieceSameLine(targetCol, targetRow) == false) {
                    return true;
                }
            }

            if (Math.abs(targetCol - preCol) == Math.abs(targetRow - preRow)) {
                if (isSquareValid(targetCol, targetRow) && pieceLineDiagonal(targetCol, targetRow) == false) {
                    return true;
                }
            }
        }
        return false;
    }

}
