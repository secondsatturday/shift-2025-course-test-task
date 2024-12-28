package ru.subbotin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class Main {

    public static final String DEFAULT_PREFIX = "";
    public static final String DEFAULT_OUTPUT_PATH = "./";
    public static final boolean DEFAULT_APPEND = false;
    public static final StatisticsType DEFAULT_STATISTICS_TYPE = StatisticsType.MISSING;

    private String prefix = DEFAULT_PREFIX;
    private String outputPath = DEFAULT_OUTPUT_PATH;
    private boolean append = DEFAULT_APPEND;
    private StatisticsType statisticsType = DEFAULT_STATISTICS_TYPE;

    private final Set<String> inputPaths = new HashSet<>();;

    private final NumericStatistics<Integer> intStatistics = new NumericStatistics<>(Integer::compareTo);
    private final NumericStatistics<Float> floatStatistics = new NumericStatistics<>(Float::compareTo);
    private final StringStatistics stringStatistics = new StringStatistics();

    public Main(final String[] args) {
        handleArgs(args);
    }

    private void handleArgs(final String[] args) {
        for (String arg : args) {
            System.out.println(arg);
        }
        try {
            for (int i = 0; i < args.length; i++) {
                if (isOption(args[i]) && !inputPaths.isEmpty()) {
                    throw new IllegalArgumentException("Error. Options must come before input file names.");
                }
                switch (args[i]) {
                    case "-o" -> {
                        outputPath = args[i + 1];
                        i++;
                    }
                    case "-p" -> {
                        prefix = args[i + 1];
                        i++;
                    }
                    case "-a" -> {
                        append = true;
                    }
                    case "-s" -> {
                        statisticsType = StatisticsType.SHORT;
                    }
                    case "-f" -> {
                        statisticsType = StatisticsType.FULL;
                    }
                    default -> inputPaths.add(args[i]);
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Error. An argument for the option was expected.");
        }
    }

    private boolean isOption(String arg) {
        return arg.equals("-o") || arg.equals("-p") || arg.equals("-a") || arg.equals("-s") || arg.equals("-f");
    }

    private void filter() {

        Storage storage = new Storage();

        for (String inputPath : inputPaths) {
            try (Scanner scanner = new Scanner(new FileInputStream(inputPath), StandardCharsets.UTF_8)) {
                while (scanner.hasNextLine()) {
                    if (storage.getLinesTotalNum() >= 2) {
                        flush(storage);
                    }
                    String line = scanner.nextLine();
                    try {
//                        char[] chars = line.toCharArray();
//                        for (int i = 0; i < chars.length; i++) {
//                            if (Character.getType(chars[i]) != Character.DECIMAL_DIGIT_NUMBER) {
//                                if (i != 0 || chars[i] != '-') {
//                                    throw new NumberFormatException();
//                                }
//                            }
//                        }
                        Integer i = Integer.parseInt(line);
                        storage.addInt(line);
                        intStatistics.update(i);
                    } catch (NumberFormatException e) {
                        try {
                            Float f = Float.parseFloat(line);
                            storage.addFloat(line);
                            floatStatistics.update(f);
                        } catch (NumberFormatException nestedE) {
                            storage.addString(line);
                            stringStatistics.update(line);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Error: Input file \"" + inputPath + "\" not found. It was skipped.");
            }
        }
        flush(storage);
    }

    private BufferedWriter newBufferedWriter(String fileName, boolean append) throws FileNotFoundException {
        if (this.append) {
            append = true;
        }
        Path path = Path.of(outputPath, prefix + fileName);
        try {
            return new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(path.toFile(), append),
                    StandardCharsets.UTF_8
            ));
        } catch (FileNotFoundException e) {
            Path defaultPath = Path.of(DEFAULT_OUTPUT_PATH, DEFAULT_PREFIX + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(defaultPath.toFile(), append),
                    StandardCharsets.UTF_8
            ));
            System.err.println("Warning: Writing to path \"" + path + "\" failed. It was set to default properties: \"" +
                    defaultPath + "\".");
            return bufferedWriter;
        }

    }

    private void flush(Storage storage) {
        Iterator<String> iterator = storage.getIntLinesIterator();
        if (iterator.hasNext()) {
            try (BufferedWriter bufferedWriter = newBufferedWriter("integers.txt", !storage.getIntLinesVirgin())) {
                storage.setIntLinesVirgin(false);
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    bufferedWriter.write(line + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error: integers output failed! Execution continued.");
            }
        }
        iterator = storage.getFloatLinesIterator();
        if (iterator.hasNext()) {
            try (BufferedWriter bufferedWriter = newBufferedWriter("floats.txt", !storage.getFloatLinesVirgin())) {
                storage.setFloatLinesVirgin(false);
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    bufferedWriter.write(line + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error: floats output failed! Execution continued.");
            }
        }
        iterator = storage.getStringLinesIterator();
        if (iterator.hasNext()) {
            try (BufferedWriter bufferedWriter = newBufferedWriter("strings.txt", !storage.getStringLinesVirgin())) {
                storage.setStringLinesVirgin(false);
                while (iterator.hasNext()) {
                    String line = iterator.next();
                    bufferedWriter.write(line + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error: strings output failed! Execution continued.");
            }
        }
        storage.reset();
    }

    public void printStatistics() {
        if (statisticsType == StatisticsType.SHORT) {
            System.out.println(" --- Integers output statistics --- ");
            intStatistics.printShortStatistics();
            System.out.println(" --- Floats output statistics --- ");
            floatStatistics.printShortStatistics();
            System.out.println(" --- Strings output statistics --- ");
            stringStatistics.printShortStatistics();
        } else if (statisticsType == StatisticsType.FULL) {
            System.out.println(" --- Integers output statistics --- ");
            intStatistics.printFullStatistics();
            System.out.println(" --- Floats output statistics --- ");
            floatStatistics.printFullStatistics();
            System.out.println(" --- Strings output statistics --- ");
            stringStatistics.printFullStatistics();
        }
    }

    public static void main(String[] args) {
        Main app = new Main(args);
        app.filter();
        app.printStatistics();
    }
}