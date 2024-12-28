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

    private final List<String> ints = new ArrayList<>(50);
    private final List<String> floats = new ArrayList<>(50);
    private final List<String> strings = new ArrayList<>(50);

    private boolean intsVirgin = true;
    private boolean floatsVirgin = true;
    private boolean stringsVirgin = true;

    public Main(final String[] args) {
        handleArgs(args);
    }

    private void handleArgs(final String[] args) {
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
        for (String inputPath : inputPaths) {
            try (Scanner scanner = new Scanner(new FileInputStream(inputPath), StandardCharsets.UTF_8)) {
                while (scanner.hasNextLine()) {
                    if (intStatistics.getQuantity() + floatStatistics.getQuantity() +
                            stringStatistics.getQuantity() >= 100) {
                        flush();
                    }
                    String line = scanner.nextLine();
                    try {
                        Integer i = Integer.parseInt(line);
                        ints.add(line);
                        intStatistics.update(i);
                    } catch (NumberFormatException e) {
                        try {
                            Float f = Float.parseFloat(line);
                            floats.add(line);
                            floatStatistics.update(f);
                        } catch (NumberFormatException nestedE) {
                            strings.add(line);
                            stringStatistics.update(line);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.err.println("Error: Input file \"" + inputPath + "\" not found. It was skipped.");
            }
        }
        flush();
    }

    private BufferedWriter produceBufferedWriter(String fileName, boolean append) throws FileNotFoundException {
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

    private void flush() {
        if (!ints.isEmpty()) {
            try (BufferedWriter bufferedWriter = produceBufferedWriter("integers.txt", !intsVirgin)) {
                intsVirgin = false;
                for (String line : ints) {
                    bufferedWriter.write(line + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error: integers output failed! Execution continued.");
            }
        }
        if (!floats.isEmpty()) {
            try (BufferedWriter bufferedWriter = produceBufferedWriter("floats.txt", !floatsVirgin)) {
                floatsVirgin = false;
                for (String line : floats) {
                    bufferedWriter.write(line + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error: floats output failed! Execution continued.");
            }
        }
        if (!strings.isEmpty()) {
            try (BufferedWriter bufferedWriter = produceBufferedWriter("strings.txt", !stringsVirgin)) {
                stringsVirgin = false;
                for (String line : strings) {
                    bufferedWriter.write(line + "\n");
                }
            } catch (IOException e) {
                System.err.println("Error: strings output failed! Execution continued.");
            }
        }
        ints.clear();
        floats.clear();
        strings.clear();
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