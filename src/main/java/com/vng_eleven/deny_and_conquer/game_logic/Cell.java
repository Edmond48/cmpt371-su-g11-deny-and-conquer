package com.vng_eleven.deny_and_conquer.game_logic;

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

    Cell(Board parent) {
        this.parent = parent;
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
            gc.setStroke(getPenColor());
            gc.setLineWidth(5);

            gc.beginPath();
            gc.moveTo(event.getX(), event.getY());
            gc.stroke();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            gc.lineTo(event.getX(), event.getY());
            gc.stroke();
        });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            WritableImage snap = canvas.snapshot(null, null);
            double fillPercent = computeFillPercentage(snap, getPenColor());

            if (fillPercent > 50.0) {
                fillCell(getPenColor());
            }
            else {
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                drawBorder();
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

    private Color getPenColor() {
        return parent.getPenColor();
    }
}