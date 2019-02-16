package ru.javawebinar.topjava.util;

public class ComparisonUtil {
    public static <T extends Comparable<T>> boolean isBetween(T comparedObject, T startBound, T endBound) {
        return comparedObject.compareTo(startBound) >= 0 && comparedObject.compareTo(endBound) <= 0;
    }
}
