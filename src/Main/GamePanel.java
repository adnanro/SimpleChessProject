package Main;

import Piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;
    Board board = new Board();
    Mouse mouse = new Mouse();

    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();

    ArrayList<Piece> promotionPieces = new ArrayList<>();


    Piece activeP;
    Piece chekingP;
    public static Piece castleP;
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameover;
    boolean stalemate;


    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        coppyPieces(pieces, simPieces);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));


        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));

    }

    private void coppyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {

        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }


    @Override
    public void run() {

        double drawInterval = 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }

    }

    private void update() {

        if (promotion) {

            promote();

        }
        else if(gameover == false && stalemate == false) {

            if (mouse.pressed) {
                if (activeP == null) {
                    for (Piece piece : simPieces) {
                        if (piece.color == currentColor &&
                                piece.col == mouse.x / Board.SQUARE_SIZE &&
                                piece.row == mouse.y / Board.SQUARE_SIZE) {

                            activeP = piece;

                        }
                    }
                } else {
                    simulate();
                }
            }

            if (mouse.pressed == false) {
                if (activeP != null) {
                    if (validSquare) {

                        coppyPieces(simPieces, pieces);
                        activeP.updatePosition();

                        if (castleP != null) {
                            castleP.updatePosition();
                        }

                        if (isKingInCheck() && isCheckmate()) {
                          gameover = true;
                        }
                        else if(isStalemate() && isKingInCheck() == false) {

                            stalemate = true;

                        } else {
                            if (canBePromoted()) {
                                promotion = true;
                            } else {
                                changePlayer();
                            }
                        }

                    } else {

                        coppyPieces(pieces, simPieces);
                        activeP.resetPosition();
                        activeP = null;
                    }

                }
            }
        }


    }

    private void simulate() {

        canMove = false;
        validSquare = false;

        coppyPieces(pieces, simPieces);

        if (castleP != null) {
            castleP.col = castleP.preCol;
            castleP.x = castleP.getX(castleP.col);
            castleP = null;
        }

        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        if (activeP.canMove(activeP.col, activeP.row)) {

            canMove = true;

            if (activeP.PHitting != null) {
                simPieces.remove(activeP.PHitting.getIndex());
            }
            checkingForCastle();

            if (illegalMove(activeP) == false && kingChecked() == false) {
                validSquare = true;
            }
        }
    }

    private boolean illegalMove(Piece king) {
        if (king.type == Type.KING) {
            for (Piece piece : simPieces) {
                if (piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean kingChecked() {

        Piece king = getKing(false);

        for(Piece piece : simPieces) {
            if(piece.color != king.color && piece.canMove(king.col, king.row)) {
            return true;
            }
        }
        return false;
    }

    private boolean isKingInCheck() {

        Piece king = getKing(true);

        if (activeP.canMove(king.col, king.row)) {
            chekingP = activeP;
            return true;
        } else {
            chekingP = null;
        }

        return false;
    }

    private Piece getKing(boolean opponent) {
        Piece king = null;

        for (Piece piece : simPieces) {
            if (opponent) {
                if (piece.type == Type.KING && piece.color != currentColor) {
                    king = piece;
                }
            } else {
                if (piece.type == Type.KING && piece.color == currentColor) {
                    king = piece;
                }
            }
        }
        return king;
    }
    private boolean isCheckmate() {

        Piece king = getKing(true);

        if(kingCanMove(king)) {
            return false;
        } else {
            //blocking check with piece

            int colDiff = Math.abs(chekingP.col - king.col);
            int rowDiff = Math.abs(chekingP.row - king.row);


            if(colDiff == 0) {
                //vertically attacking

                if(chekingP.row < king.row) {

                    for(int row = chekingP.row; row < king.row; row++) {
                        for(Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(chekingP.col, row)) {
                                return false;
                            }
                        }
                    }
                }
                if(chekingP.row > king.row) {

                    for(int row = chekingP.row; row > king.row; row--) {
                        for(Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(chekingP.col, row)) {
                                return false;
                            }
                        }
                    }

                }

            }
            else if(rowDiff == 0) {
                //horizontally attacking
                if(chekingP.col < king.col) {

                    for(int col = chekingP.col; col < king.col; col++) {
                        for(Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(col,chekingP.row)) {
                                return false;
                            }
                        }
                    }

                }
                if(chekingP.col > king.col) {

                    for(int col = chekingP.col; col > king.col; col--) {
                        for(Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(col,chekingP.row)) {
                                return false;
                            }
                        }
                    }
                }
            }
            else if(colDiff == rowDiff) {

                //diagonally attacking
                if(chekingP.row < king.row) {

                    if(chekingP.col < king.col) {

                    for(int col = chekingP.col, row = chekingP.row; col < king.col; col++, row++) {
                        for(Piece piece : simPieces) {
                            if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                               return false;
                            }
                        }
                    }

                    }

                    if(chekingP.col > king.col) {

                        for(int col = chekingP.col, row = chekingP.row; col > king.col; col--, row++) {
                            for(Piece piece : simPieces) {
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }


                    }
                }
                if(chekingP.row > king.row) {

                    if(chekingP.col < king.col) {

                        for(int col = chekingP.col, row = chekingP.row; col < king.col; col++, row--) {
                            for(Piece piece : simPieces) {
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }
                    }

                    if(chekingP.col > king.col) {

                        for(int col = chekingP.col, row = chekingP.row; col > king.col; col--, row--) {
                            for(Piece piece : simPieces) {
                                if(piece != king && piece.color != currentColor && piece.canMove(col, row)) {
                                    return false;
                                }
                            }
                        }

                    }
                }

            } else {
                //knight check


            }

        }

    return true;
    }

    private boolean kingCanMove(Piece king) {

        if(isMoveValid(king, -1, -1)) {
            return true;
        }
            if(isMoveValid(king, 0, -1)) {
                return true;
            }

                if(isMoveValid(king, 1, -1)) {
                    return true;
                }
                    if(isMoveValid(king, -1, 0)) {
                        return true;
                    }
                        if(isMoveValid(king, 1, 0)) {
                            return true;
                        }
                            if(isMoveValid(king, -1, 1)) {
                                return true;
                            }
                                if(isMoveValid(king, 0, 1)) {
                                    return true;
                                }
                                    if(isMoveValid(king, 1, 1)) {
                                        return true;
                                    }

        return false;
    }

    private boolean isMoveValid(Piece king, int colPlus, int rowPlus) {

                                        boolean isMoveValid = false;
                                        king.col += colPlus;
                                        king.row += rowPlus;

                                        if(king.canMove(king.col, king.row)) {

                                            if (king.PHitting != null) {
                                                simPieces.remove(king.PHitting.getIndex());

                                            }

                                            if (illegalMove(king) == false) {
                                                isMoveValid = true;
                                            }
                                        }
                                        king.resetPosition();
                                        coppyPieces(pieces, simPieces);


         return isMoveValid;
    }

    private void checkingForCastle() {
        if (castleP != null) {
            if (castleP.col == 0) {
                castleP.col += 3;
            } else if (castleP.col == 7) {
                castleP.col -= 2;
            }
            castleP.x = castleP.getX(castleP.col);
        }
    }

    private boolean isStalemate() {
        int count = 0;
        for(Piece piece : simPieces) {
            if(piece.color != currentColor) {
                count++;
            }
        }

        if(count == 1) {
            if(kingCanMove(getKing(true)) == false) {
                return true;
            }
        }
        return false;
    }

    private void changePlayer() {

        if (currentColor == WHITE) {
            currentColor = BLACK;

            for (Piece piece : pieces) {
                if (piece.color == BLACK) {
                    piece.scnStepp = false;
                }
            }
        } else {
            currentColor = WHITE;

            for (Piece piece : pieces) {
                if (piece.color == WHITE) {
                    piece.scnStepp = false;
                }
            }
        }
        activeP = null;

    }

    private boolean canBePromoted() {

        if (activeP.type == Type.PAWN) {
            if (currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7) {
                promotionPieces.clear();
                promotionPieces.add(new Queen(currentColor, 9, 2));
                promotionPieces.add(new Rook(currentColor, 9, 3));
                promotionPieces.add(new Knight(currentColor, 9, 4));
                promotionPieces.add(new Bishop(currentColor, 9, 5));

                return true;
            }
        }


        return false;
    }

    private void promote() {

        if (mouse.pressed) {
            for (Piece piece : promotionPieces) {
                if (piece.col == mouse.x / Board.SQUARE_SIZE && piece.row == mouse.y / Board.SQUARE_SIZE) {
                    switch (piece.type) {
                        case QUEEN:
                            simPieces.add(new Queen(currentColor, activeP.col, activeP.row));
                            break;
                        case ROOK:
                            simPieces.add(new Rook(currentColor, activeP.col, activeP.row));
                            break;
                        case KNIGHT:
                            simPieces.add(new Knight(currentColor, activeP.col, activeP.row));
                            break;
                        case BISHOP:
                            simPieces.add(new Bishop(currentColor, activeP.col, activeP.row));
                            break;
                        default:
                            break;

                    }

                    simPieces.remove(activeP.getIndex());
                    coppyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }

    }

    public void paintComponent(Graphics g){
                                        super.paintComponent(g);

                                        Graphics2D g2 = (Graphics2D) g;
                                        board.draw(g2);

                                        for (Piece p : simPieces) {
                                            p.draw(g2);
                                        }

                                        if (activeP != null) {
                                            if (canMove) {

                                                if (illegalMove(activeP) || kingChecked()) {
                                                    g2.setColor(Color.red);
                                                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                                                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                                                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                                                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                                                } else {
                                                    g2.setColor(Color.white);
                                                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                                                    g2.fillRect(activeP.col * Board.SQUARE_SIZE, activeP.row * Board.SQUARE_SIZE,
                                                            Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                                                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                                                }

                                            }

                                            activeP.draw(g2);
                                        }


                                        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                                        g2.setFont(new Font("JetBrains Mono", Font.PLAIN, 35));
                                        g2.setColor(Color.WHITE);

                                        if (promotion) {
                                            g2.drawString("Promote to:", 840, 150);
                                            for (Piece piece : promotionPieces) {
                                                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row),
                                                        Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
                                            }
                                        } else {

                                            if (currentColor == WHITE) {
                                                g2.drawString("White's turn", 840, 560);
                                                if (chekingP != null && chekingP.color == BLACK) {
                                                    g2.setColor(Color.red);
                                                    g2.drawString("Check!!!", 840, 650);
                                                }
                                            } else {
                                                g2.drawString("Black's turn", 840, 240);
                                                if (chekingP != null && chekingP.color == WHITE) {
                                                    g2.setColor(Color.red);
                                                    g2.drawString("Check!!!", 840, 100);
                                                }

                                            }

                                        }

                                        if(gameover) {
                                            String s = "";
                                            if(currentColor == WHITE) {
                                                s = "White wins";
                                            } else {
                                                s = "Black wins";
                                            }
                                            g2.setFont(new Font("JetBrains Mono", Font.PLAIN, 90));
                                            g2.setColor(Color.green);
                                            g2.drawString(s, 200, 430);
                                        }
                                        if(stalemate) {
                                            g2.setFont(new Font("JetBrains Mono", Font.PLAIN, 90));
                                            g2.setColor(Color.red);
                                            g2.drawString("!!!It's a stalemate!!!", 200, 430);
                                        }

                                    }
                                }
