package ru.subbotin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Storage {

    public final static int INITIAL_CAPACITY = 10;

    private int linesTotalNum = 0;

    private List<String> intLines = new ArrayList<>(INITIAL_CAPACITY);
    private List<String> floatLines = new ArrayList<>(INITIAL_CAPACITY);
    private List<String> stringLines = new ArrayList<>(INITIAL_CAPACITY);

    private boolean intLinesVirgin = true;
    private boolean floatLinesVirgin = true;
    private boolean stringLinesVirgin = true;

    public int getLinesTotalNum() {
        return linesTotalNum;
    }

    public void addInt(String line) {
        intLines.add(line);
        linesTotalNum++;
    }

    public void addFloat(String line) {
        floatLines.add(line);
        linesTotalNum++;
    }

    public void addString(String line) {
        stringLines.add(line);
        linesTotalNum++;
    }

    public Iterator<String> getIntLinesIterator() {
        return intLines.iterator();
    }

    public Iterator<String> getFloatLinesIterator() {
        return floatLines.iterator();
    }

    public Iterator<String> getStringLinesIterator() {
        return stringLines.iterator();
    }

    public boolean getIntLinesVirgin() {
        return intLinesVirgin;
    }

    public boolean getFloatLinesVirgin() {
        return floatLinesVirgin;
    }

    public boolean getStringLinesVirgin() {
        return stringLinesVirgin;
    }

    public void setIntLinesVirgin(boolean intLinesVirgin) {
        this.intLinesVirgin = intLinesVirgin;
    }

    public void setFloatLinesVirgin(boolean floatLinesVirgin) {
        this.floatLinesVirgin = floatLinesVirgin;
    }

    public void setStringLinesVirgin(boolean stringLinesVirgin) {
        this.stringLinesVirgin = stringLinesVirgin;
    }

    public void reset() {
        intLines.clear();
        floatLines.clear();
        stringLines.clear();
        linesTotalNum = 0;
    }

}
