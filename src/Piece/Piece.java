package Piece;

import Main.Board;
import Main.GamePanel;
import Main.Type;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Piece {

    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece PHitting;
    public boolean moved;
    public boolean scnStepp;

    public Piece(int color, int col, int row) {
        this.color = color;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try{
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        }catch(IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    public int getX(int col) {
        return col * Board.SQUARE_SIZE;
    }

    public int getY(int row) {
        return row * Board.SQUARE_SIZE;
    }

    public int getCol(int x) {
        return (x + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }

    public int getRow(int y) {
        return (y + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }

    public int getIndex() {
        for(int index = 0; index < GamePanel.simPieces.size(); index++) {
        if(GamePanel.simPieces.get(index) == this) {
            return index;
        }
        }
        return 0;
    }
    public void updatePosition() {


        if(type == Type.PAWN) {
            if(Math.abs(row - preRow) == 2) {
                scnStepp = true;
            }
        }


        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }

    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    public void resetPosition() {
       col = preCol;
       row = preRow;
       x = getX(col);
       y = getY(row);
    }
    public boolean isWithinBoard(int targetCol, int targetRow) {
        if(targetCol >= 0 && targetRow <= 7 && targetRow >= 0 && targetCol <= 7) {
            return true;
        }
        return false;
    }

    public boolean isSameSquare(int targetCol, int targetRow) {

        if(targetCol == preCol && targetRow == preRow) {
            return true;
        }
        return false;
    }
    public Piece getPHitting(int targetCol, int targetRow) {
        for(Piece piece : GamePanel.simPieces) {
            if(piece.col == targetCol && piece.row == targetRow && piece != this) {
                return piece;
            }
        }
        return null;
    }

    public boolean isSquareValid(int targetCol, int targetRow) {
        PHitting = getPHitting(targetCol, targetRow);
            if(PHitting == null) {
                return true;
            } else {
                if(PHitting.color != this.color) {
                    return true;
                } else {
                    PHitting = null;
                }
            }

        return false;
    }

    public boolean pieceSameLine(int targetCol, int targetRow) {

        //left
        for(int c = preCol-1; c > targetCol; c--) {
            for(Piece piece : GamePanel.simPieces) {
                if(piece.col == c && piece.row == targetRow) {
                    PHitting = piece;
                    return true;
                }
            }
        }
         //right
        for(int c = preCol+1; c < targetCol; c++) {
            for(Piece piece : GamePanel.simPieces) {
                if(piece.col == c && piece.row == targetRow) {
                    PHitting = piece;
                    return true;
                }
            }
        }

        //up
        for(int r = preRow-1; r > targetRow; r--) {
            for(Piece piece : GamePanel.simPieces) {
                if(piece.col == targetCol && piece.row == r) {
                    PHitting = piece;
                    return true;
                }
            }
        }

        //down
        for(int r = preRow+1; r < targetRow; r++) {
            for(Piece piece : GamePanel.simPieces) {
                if(piece.col == targetCol && piece.row == r) {
                    PHitting = piece;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean pieceLineDiagonal(int targetCol, int targetRow) {

        if(targetRow < preRow) {

            for(int c = preCol-1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces) {
                    if(piece.col == c && piece.row == preRow - diff) {
                        PHitting = piece;
                                return true;
                    }
                }
            }

            for(int c = preCol+1; c < targetCol; c++) {
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces) {
                    if(piece.col == c && piece.row == preRow - diff) {
                        PHitting = piece;
                        return true;
                    }
                }
            }
        }


        if(targetRow > preRow) {

            for(int c = preCol-1; c > targetCol; c--) {
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces) {
                    if(piece.col == c && piece.row == preRow + diff) {
                        PHitting = piece;
                        return true;
                    }
                }
            }

            for(int c = preCol+1; c < targetCol; c++) {
                int diff = Math.abs(c - preCol);
                for(Piece piece : GamePanel.simPieces) {
                    if(piece.col == c && piece.row == preRow + diff) {
                        PHitting = piece;
                        return true;
                    }
                }
            }

        }
      return false;
    }
    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}
