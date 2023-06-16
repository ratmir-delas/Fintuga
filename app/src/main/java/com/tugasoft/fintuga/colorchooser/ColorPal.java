package com.tugasoft.fintuga.colorchooser;

public class ColorPal {
    private boolean check;
    private int color;

    public ColorPal(int i, boolean z) {
        this.color = i;
        this.check = z;
    }

    public boolean equals(Object obj) {
        return (obj instanceof ColorPal) && ((ColorPal) obj).color == this.color;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int i) {
        this.color = i;
    }

    public boolean isCheck() {
        return this.check;
    }

    public void setCheck(boolean z) {
        this.check = z;
    }
}
