package Piece;

import Main.GamePanel;
import Main.Type;

public class Knight extends Piece{
    public Knight(int color, int col, int row) {
        super(color, col, row);

        type = Type.KNIGHT;

        if(color == GamePanel.WHITE) {
            image = getImage("/piece/w-knight");
        } else {
            image = getImage("/piece/b-knight");
        }
    }


    public boolean canMove(int targetCol, int targetRow) {

        if(isWithinBoard(targetCol, targetRow)) {
            // 1:2 / 2:1
            if(Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 2) {
            if(isSquareValid(targetCol, targetRow)) {
                return true;
            }
            }
        }
        return false;
    }
    }