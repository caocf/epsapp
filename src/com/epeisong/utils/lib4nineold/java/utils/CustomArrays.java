package com.epeisong.utils.lib4nineold.java.utils;

import java.lang.reflect.Array;

public class CustomArrays {

    public static <T> T[] copyOf(T[] original, int newLength) {
        if (original == null) {
            throw new NullPointerException("original == null");
        }
        if (newLength < 0) {
            throw new NegativeArraySizeException(Integer.toString(newLength));
        }
        return copyOfRange(original, 0, newLength);
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] copyOfRange(T[] original, int start, int end) {
        int originalLength = original.length; // For exception priority
                                              // compatibility.
        if (start > end) {
            throw new IllegalArgumentException();
        }
        if (start < 0 || start > originalLength) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int resultLength = end - start;
        int copyLength = Math.min(resultLength, originalLength - start);
        T[] result = (T[]) Array.newInstance(original.getClass().getComponentType(), resultLength);
        System.arraycopy(original, start, result, 0, copyLength);
        return result;
    }
}
