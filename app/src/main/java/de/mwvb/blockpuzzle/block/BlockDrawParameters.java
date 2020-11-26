package de.mwvb.blockpuzzle.block;

import android.graphics.Canvas;

public class BlockDrawParameters {
    private Canvas canvas;
    private int br;
    private float p;
    private float f;
    /** true: big block display, false: small block display */
    private boolean dragMode;

    public Canvas getCanvas() {
        return canvas;
    }

    public float getP() {
        return p;
    }

    public int getBr() {
        return br;
    }

    public float getF() {
        return f;
    }

    public boolean isDragMode() {
        return dragMode;
    }

    public void setBr(int br) {
        this.br = br;
        this.p = br * 0.05f; // old: 0.1f
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public void setDragMode(boolean dragMode) {
        this.dragMode = dragMode;
    }

    public void setF(float f) {
        this.f = f;
    }
}
