package ru.subbotin.statistics;

public class StringStatistics {
    private int quantity = 0;

    Integer minLength = null;
    Integer maxLength = null;

    public int getQuantity() {
        return quantity;
    }

    public void update(String value) {
        quantity++;
        int length = value.length();
        if (minLength == null) {
            minLength = length;
        } else if (length < minLength) {
            minLength = length;
        }
        if (maxLength == null) {
            maxLength = length;
        } else if (length > maxLength) {
            maxLength = length;
        }
    }

    public void printShortStatistics() {
        System.out.println("Quantity: " + quantity);
    }

    public void printFullStatistics() {
        if (quantity == 0) {
            System.out.println("Quantity: 0");
            System.out.println("Min string length: no data");
            System.out.println("Max string length: no data");
        } else {
            System.out.println("Quantity: " + quantity);
            System.out.println("Min string length: " + minLength);
            System.out.println("Max string length: " + maxLength);
        }
    }
}
