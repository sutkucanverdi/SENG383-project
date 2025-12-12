package kidtask.persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Generic file storage utility for reading and writing delimited text files.
 * Handles header validation, parsing, and escape/delimiter issues.
 */
public class FileStorage {
    private static final String DELIMITER = "|";
    private static final String ENCODING = "UTF-8";
    private static final String DATA_DIR = "data";

    private final Path filePath;

    /**
     * Creates a FileStorage instance for the specified file.
     *
     * @param fileName Name of the file (will be stored in data/ directory)
     */
    public FileStorage(String fileName) {
        Path dataDir = Paths.get(DATA_DIR);
        this.filePath = dataDir.resolve(fileName);
    }

    /**
     * Reads all lines from the file, validates header, and returns data rows.
     *
     * @param expectedHeader Expected header line (pipe-delimited)
     * @return List of data rows, each row is a list of field values
     * @throws StorageException if file operations fail or header is invalid
     */
    public List<List<String>> readAll(String expectedHeader) throws StorageException {
        ensureFileExists(expectedHeader);
        List<List<String>> rows = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return rows; // Empty file, return empty list
            }

            // Validate header
            validateHeader(headerLine, expectedHeader);

            // Read data rows
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue; // Skip empty lines
                }
                List<String> fields = parseLine(line);
                rows.add(fields);
            }

            return rows;
        } catch (IOException e) {
            throw new StorageException("Failed to read file: " + filePath, e);
        }
    }

    /**
     * Writes all rows to the file with the specified header.
     *
     * @param header Header line (pipe-delimited)
     * @param rows   List of data rows, each row is a list of field values
     * @throws StorageException if file operations fail
     */
    public void writeAll(String header, List<List<String>> rows) throws StorageException {
        ensureDirectoryExists();

        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            // Write header
            writer.write(header);
            writer.newLine();

            // Write data rows
            for (List<String> row : rows) {
                String line = formatLine(row);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new StorageException("Failed to write file: " + filePath, e);
        }
    }

    /**
     * Validates that the file header matches the expected header.
     *
     * @param actualHeader   Actual header from file
     * @param expectedHeader Expected header format
     * @throws StorageException if headers don't match
     */
    private void validateHeader(String actualHeader, String expectedHeader) throws StorageException {
        if (actualHeader == null || actualHeader.trim().isEmpty()) {
            throw new StorageException("File header is missing: " + filePath);
        }

        String[] actualFields = actualHeader.split("\\" + DELIMITER, -1);
        String[] expectedFields = expectedHeader.split("\\" + DELIMITER, -1);

        if (actualFields.length != expectedFields.length) {
            throw new StorageException(
                    String.format("Header field count mismatch in %s. Expected %d fields, found %d",
                            filePath, expectedFields.length, actualFields.length));
        }

        // Normalize and compare field names (case-insensitive, trim whitespace)
        for (int i = 0; i < expectedFields.length; i++) {
            String expected = expectedFields[i].trim().toLowerCase();
            String actual = actualFields[i].trim().toLowerCase();
            if (!expected.equals(actual)) {
                throw new StorageException(
                        String.format("Header field mismatch at position %d in %s. Expected '%s', found '%s'",
                                i + 1, filePath, expectedFields[i], actualFields[i]));
            }
        }
    }

    /**
     * Parses a line into fields, handling delimiter and escape issues.
     * Simple approach: split by delimiter, trim whitespace.
     * Empty values between delimiters are preserved as empty strings.
     *
     * @param line Line to parse
     * @return List of field values
     */
    private List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        String[] parts = line.split("\\" + DELIMITER, -1); // -1 preserves trailing empty fields

        for (String part : parts) {
            // Trim whitespace and handle escape sequences
            String field = part.trim();
            // Simple unescape: replace common escape sequences
            field = field.replace("\\|", "|"); // Unescape pipe if escaped
            field = field.replace("\\n", "\n"); // Unescape newline
            field = field.replace("\\r", "\r"); // Unescape carriage return
            fields.add(field);
        }

        return fields;
    }

    /**
     * Formats a row of fields into a line string.
     * Handles escape sequences for delimiter and special characters.
     *
     * @param fields List of field values
     * @return Formatted line string
     */
    private String formatLine(List<String> fields) {
        List<String> escapedFields = new ArrayList<>();

        for (String field : fields) {
            if (field == null) {
                escapedFields.add("");
            } else {
                // Simple escape: replace delimiter and special characters
                String escaped = field.replace("\\", "\\\\"); // Escape backslash first
                escaped = escaped.replace("|", "\\|"); // Escape delimiter
                escaped = escaped.replace("\n", "\\n"); // Escape newline
                escaped = escaped.replace("\r", "\\r"); // Escape carriage return
                escapedFields.add(escaped);
            }
        }

        return String.join(DELIMITER, escapedFields);
    }

    /**
     * Ensures the data directory exists, creates it if necessary.
     */
    private void ensureDirectoryExists() throws StorageException {
        try {
            Path dataDir = filePath.getParent();
            if (dataDir != null && !Files.exists(dataDir)) {
                Files.createDirectories(dataDir);
            }
        } catch (IOException e) {
            throw new StorageException("Failed to create data directory: " + filePath.getParent(), e);
        }
    }

    /**
     * Ensures the file exists with the correct header, creates it if necessary.
     *
     * @param header Header line to write if file doesn't exist
     */
    private void ensureFileExists(String header) throws StorageException {
        try {
            ensureDirectoryExists();

            if (!Files.exists(filePath)) {
                // Create file with header
                try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                    writer.write(header);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new StorageException("Failed to initialize file: " + filePath, e);
        }
    }

    /**
     * Gets the file path.
     *
     * @return Path to the storage file
     */
    public Path getFilePath() {
        return filePath;
    }
}

