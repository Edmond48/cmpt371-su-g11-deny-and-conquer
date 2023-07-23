package com.vng_eleven.deny_and_conquer.client;

import com.vng_eleven.deny_and_conquer.server.TokenMessage;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

// a few parts of the game UI are based on Deny and Conquer: https://github.com/scc23/deny-and-conquer/
// the logic for networking belongs entirely to us (group 11)
class Cell {
    Canvas canvas;
    GraphicsContext gc;
    Board parent;

    int row;
    int col;
    boolean isLocked;

    Cell(Board parent, int row, int col) {
        this.parent = parent;
        this.row = row;
        this.col = col;
        this.isLocked = false;
        this.canvas = new Canvas(50, 50);
        gc = this.canvas.getGraphicsContext2D();

        drawBorder();

        gc.setLineWidth(5);
        gc.setStroke(Color.RED);

        setUpEventHandlers();
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    /////////////////////////////////////////////////////////////////////
    // set up event handlers
    private void setUpEventHandlers() {
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (this.isLocked) {
                return;
            }
            gc.setStroke(getPenColor());
            gc.setLineWidth(5);

            gc.beginPath();
            gc.moveTo(event.getX(), event.getY());
            gc.stroke();

            parent.sendMessage(newMessage(TokenMessage.Token.ATTEMPT));
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (this.isLocked) {
                return;
            }
            gc.lineTo(event.getX(), event.getY());
            gc.stroke();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            if (this.isLocked) {
                return;
            }
            WritableImage snap = canvas.snapshot(null, null);
            double fillPercent = computeFillPercentage(snap, getPenColor());

            if (fillPercent > 50.0) {
                fillCell(getPenColor());
                isLocked = true;
                parent.sendMessage(newMessage(TokenMessage.Token.OCCUPY));
            }
            else {
                clearCell();
                parent.sendMessage(newMessage(TokenMessage.Token.RELEASE));
            }
        });
    }
    private double computeFillPercentage(WritableImage snap, Color penColor) {
        // obtains PixelReader from the snap
        PixelReader pixelReader = snap.getPixelReader();

        double h = snap.getHeight();
        double w = snap.getWidth();
        double coloredPixels = 0;
        double totalPixels = (h * w);

        // computes the number of colored pixels
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color color = pixelReader.getColor(x, y);

                // checks if a pixel is colored with PenColor
                if (penColor.equals(color)) {
                    coloredPixels += 1;
                }
            }
        }
        // computes colored area percentage
        return (coloredPixels / totalPixels) * 100.0;
    }

    /////////////////////////////////////////////////////////////////////
    // operations
    public void attempt(int color) {
        if (color == getIntPenColor()) {
            return;
        }
        drawCross(Board.intToColor(color));
        isLocked = true;
    }

    public void occupy(int color) {
        if (color == getIntPenColor()) {
            return;
        }
        fillCell(Board.intToColor(color));
        isLocked = true;
    }

    public void release() {
        clearCell();
        isLocked = false;
    }


    /////////////////////////////////////////////////////////////////////
    // helper methods
    private void drawBorder() {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        gc.strokeRect(0, 0, 50, 50);
    }

    private void fillCell(Color color) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.setFill(color);
        gc.fillRect(0, 0, w, h);
        drawBorder();
    }
    private void clearCell() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawBorder();
    }

    private void drawCross(Color color) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        gc.setStroke(color);
        gc.setLineWidth(3);
        gc.strokeLine(0, 0, w, h);
        gc.strokeLine(w, 0, 0, h);
        drawBorder();
    }

    private Color getPenColor() {
        return parent.getPenColor();
    }

    private int getIntPenColor() {
        return parent.getIntPenColor();
    }

    private TokenMessage newMessage(TokenMessage.Token token) {
        return new TokenMessage(token, getIntPenColor(), row, col);
    }
}