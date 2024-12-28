package ru.subbotin;

import java.util.Comparator;

public class NumericStatistics<T extends Number> {

    private int quantity = 0;

    private T minValue = null;
    private T maxValue = null;
    private Double sum = null;

    private final Comparator<T> comparator;

    public NumericStatistics(Comparator<T> comparator) {
        this.comparator = comparator;
    }

    public int getQuantity() {
        return quantity;
    }

    public void update(T value) {
        quantity++;
        if (minValue == null) {
            minValue = value;
        } else if (comparator.compare(value, minValue) < 0) {
            minValue = value;
        }
        if (maxValue == null) {
            maxValue = value;
        } else if (comparator.compare(value, minValue) > 0) {
            maxValue = value;
        }
        if (sum == null) {
            sum = value.doubleValue();
        } else {
            sum = sum + value.doubleValue();
        }
    }

    public void printShortStatistics() {
        System.out.println("Quantity: " + quantity);
    }

    public void printFullStatistics() {
        if (quantity == 0) {
            System.out.println("Quantity: " + 0);
            System.out.println("Min value: no data");
            System.out.println("Max value: no data");
            System.out.println("Sum: no data");
            System.out.println("Average: no data");
        } else {
            System.out.println("Quantity: " + quantity);
            System.out.println("Min value: " + minValue);
            System.out.println("Max value: " + maxValue);
            System.out.println("Sum: " + sum);
            System.out.println("Average: " + sum / quantity);
        }
    }
}
