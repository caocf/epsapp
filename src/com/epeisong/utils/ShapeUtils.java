package com.epeisong.utils;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.graphics.drawable.StateListDrawable;

/**
 * 
 * @author poet
 *
 */
public class ShapeUtils {

    public static StateListDrawable getMainBtnBg(int cornerInDp) {
        int corner = DimensionUtls.getPixelFromDpInt(cornerInDp);
        int[] states = { -android.R.attr.state_pressed, android.R.attr.state_pressed };
        ShapeParams[] params = {
                new ShapeParams().setStrokeWidth(0).setBgColor(Color.argb(0xff, 0x00, 0x9c, 0xff)).setCorner(corner),
                new ShapeParams().setStrokeWidth(0).setBgColor(Color.argb(0xff, 0x00, 0x7c, 0xca)).setCorner(corner) };
        return getStateListDrawable(states, params);
    }

    public static StateListDrawable getWhiteGrayBg(int cornerInDp, int strokeColor) {
        int corner = DimensionUtls.getPixelFromDpInt(cornerInDp);
        int[] states = { -android.R.attr.state_pressed, android.R.attr.state_pressed };
        ShapeParams[] params = {
                new ShapeParams().setStrokeWidth(1).setStrokeColor(strokeColor).setBgColor(Color.WHITE)
                        .setCorner(corner),
                new ShapeParams().setStrokeWidth(1).setStrokeColor(strokeColor).setBgColor(Color.GRAY)
                        .setCorner(corner) };
        return getStateListDrawable(states, params);
    }

    public static StateListDrawable getStateListDrawable(int[] states, ShapeParams[] params) {
        if (states == null || params == null || states.length <= 0 || states.length != params.length) {
            return null;
        }
        StateListDrawable d = new StateListDrawable();
        int len = states.length;
        for (int i = 0; i < len; i++) {
            d.addState(new int[] { states[i] }, getShape(params[i]));
        }
        return d;
    }

    public static Drawable getShape(ShapeParams params) {
        if (params == null) {
            params = new ShapeParams();
        }
        GradientDrawable d = null;

        if (params.orientation != null && params.bgColors != null) {
            d = new GradientDrawable(params.orientation, params.bgColors);
        } else {
            d = new GradientDrawable();
            d.setColor(params.bgColor);
        }
        if (params.shape > 0) {
            d.setShape(params.shape);
        }
        d.setStroke(params.strokeWidth, params.strokeColor);
        d.setCornerRadii(params.corners);
        return d;
    }

    public static class ShapeParams {
        int shape;
        int strokeWidth; // 边框宽度
        int strokeColor; // 边框颜色
        Orientation orientation;
        int bgColor; // 背景色
        int[] bgColors; // 背景渐变色
        float[] corners; // 拐角半径

        public ShapeParams() {
            strokeWidth = 1;
            strokeColor = Color.GRAY;
            bgColor = Color.TRANSPARENT;
            corners = new float[8];
        }

        /**
         * @param shape @see {GradientDrawable.LINE}
         * @return
         */
        public ShapeParams setShape(int shape) {
            this.shape = shape;
            return this;
        }

        public ShapeParams setStrokeWidth(int w) {
            strokeWidth = w;
            return this;
        }

        public ShapeParams setStrokeColor(int color) {
            strokeColor = color;
            return this;
        }

        public ShapeParams setBgColor(int color) {
            bgColor = color;
            return this;
        }

        public ShapeParams setBgColors(Orientation ori, int... colors) {
            if (this.bgColors == null) {
                this.bgColors = new int[3];
            }
            orientation = ori;
            if (colors != null) {
                int len = Math.min(3, colors.length);
                for (int i = 0; i < len; i++) {
                    bgColors[i] = colors[i];
                }
            }
            return this;
        }

        public ShapeParams setCorner(float corner) {
            for (int i = 0; i < 8; i++) {
                corners[i] = corner;
            }
            return this;
        }

        public ShapeParams setCorners(float... corners) {
            if (corners != null) {
                int len = Math.min(4, corners.length);
                for (int i = 0; i < len; i++) {
                    switch (i) {
                    case 0:
                        this.corners[0] = corners[i];
                        this.corners[1] = corners[i];
                        break;
                    case 1:
                        this.corners[2] = corners[i];
                        this.corners[3] = corners[i];
                        break;
                    case 2:
                        this.corners[4] = corners[i];
                        this.corners[5] = corners[i];
                        break;
                    case 3:
                        this.corners[6] = corners[i];
                        this.corners[7] = corners[i];
                        break;
                    }
                }
            }
            return this;
        }
    }
}
