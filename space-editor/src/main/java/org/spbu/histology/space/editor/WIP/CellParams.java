package org.spbu.histology.space.editor.WIP;

public class CellParams {

    private int baseX = 25;
    private int baseZ = 25;
    private int baseY = 25;

    private int scaleWidth = 1;
    private int scaleHeight = 1;
    private int scaleLength = 1;

    private int shiftX = 0;
    private int shiftY = 0;
    private int shiftZ = 0;

    public CellParams(int baseX, int baseZ, int baseY, int scaleWidth, int scaleHeight, int scaleLength, int shiftX, int shiftY, int shiftZ) {
        this.baseX = baseX;
        this.baseZ = baseZ;
        this.baseY = baseY;
        this.scaleWidth = scaleWidth;
        this.scaleHeight = scaleHeight;
        this.scaleLength = scaleLength;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.shiftZ = shiftZ;
    }

    public CellParams() {
    }


    public CellParams(int baseX, int baseY) {
        this.baseX = baseX;
        this.baseY = baseY;
    }

    public CellParams defaultValues() {
        return this;
    }

    public int getBaseX() {
        return baseX;
    }

    public int getBaseZ() {
        return baseZ;
    }

    public int getBaseY() {
        return baseY;
    }

    public int getScaleWidth() {
        return scaleWidth;
    }

    public int getScaleHeight() {
        return scaleHeight;
    }

    public int getScaleLength() {
        return scaleLength;
    }

    public int getShiftX() {
        return shiftX;
    }

    public int getShiftY() {
        return shiftY;
    }

    public int getShiftZ() {
        return shiftZ;
    }

    public static class Builder {

        private int baseX = 25;
        private int baseZ = 25;
        private int baseY = 25;

        private int scaleWidth = 1;
        private int scaleHeight = 1;
        private int scaleLength = 1;

        private int shiftX = 0;
        private int shiftY = 0;
        private int shiftZ = 0;

        Builder() {
        }

        public Builder setBaseX(int baseX) {
            this.baseX = baseX;
            return this;
        }

        public Builder setBaseZ(int baseZ) {
            this.baseZ = baseZ;
            return this;
        }

        public Builder setBaseY(int baseY) {
            this.baseY = baseY;
            return this;
        }

        public Builder setScaleWidth(int scaleWidth) {
            this.scaleWidth = scaleWidth;
            return this;
        }

        public Builder setScaleHeight(int scaleHeight) {
            this.scaleHeight = scaleHeight;
            return this;
        }

        public Builder setScaleLength(int scaleLength) {
            this.scaleLength = scaleLength;
            return this;
        }

        public Builder setShiftX(int shiftX) {
            this.shiftX = shiftX;
            return this;
        }

        public Builder setShiftY(int shiftY) {
            this.shiftY = shiftY;
            return this;
        }

        public Builder setShiftZ(int shiftZ) {
            this.shiftZ = shiftZ;
            return this;
        }

        public CellParams build() {
            return new CellParams(baseX, baseZ, baseY, scaleWidth, scaleHeight, scaleLength, shiftX, shiftY, shiftZ);
        }
    }
}
