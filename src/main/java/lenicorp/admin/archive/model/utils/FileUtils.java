package lenicorp.admin.archive.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for file operations
 */
public class FileUtils {

    private static final Map<String, String> MIME_TO_EXTENSION = new HashMap<>();

    static {
        // PDF
        MIME_TO_EXTENSION.put("application/pdf", "pdf");

        // Word
        MIME_TO_EXTENSION.put("application/msword", "doc");
        MIME_TO_EXTENSION.put("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx");

        // Excel
        MIME_TO_EXTENSION.put("application/vnd.ms-excel", "xls");
        MIME_TO_EXTENSION.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");

        // PowerPoint
        MIME_TO_EXTENSION.put("application/vnd.ms-powerpoint", "ppt");
        MIME_TO_EXTENSION.put("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx");

        // Images
        MIME_TO_EXTENSION.put("image/jpeg", "jpg");
        MIME_TO_EXTENSION.put("image/png", "png");
        MIME_TO_EXTENSION.put("image/gif", "gif");
        MIME_TO_EXTENSION.put("image/bmp", "bmp");
        MIME_TO_EXTENSION.put("image/webp", "webp");

        // Vidéos
        MIME_TO_EXTENSION.put("video/mp4", "mp4");
        MIME_TO_EXTENSION.put("video/x-msvideo", "avi");
        MIME_TO_EXTENSION.put("video/x-matroska", "mkv");
        MIME_TO_EXTENSION.put("video/quicktime", "mov");
    }

    public static String getExtensionFromInputStream(InputStream file) throws IOException
    {
        InputStreamDetails mimeTypeResult = getInputStreamDetails(file);
        return getExtensionFromMimeType(mimeTypeResult.getMimeType());
    }

    public static String getExtensionFromMimeType(String mimeType)
    {
        if (mimeType == null) return "";
        return MIME_TO_EXTENSION.getOrDefault(mimeType.toLowerCase(), "");
    }
    public static String getExtensionFromFilename(String filename)
    {
        if (filename == null) {
            return "";
        }
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }

    /**
     * Result class for getMimeType method that contains both the MIME type and a new InputStream
     */
    @Getter
    @AllArgsConstructor
    public static class InputStreamDetails
    {
        private final String mimeType;
        private final String extension;
        private final long size;
        private final InputStream inputStream;
    }

    /**
     * Result class for areFilesIdenticalByContent method that contains both the comparison result and a new InputStream
     */
    @Getter
    @AllArgsConstructor
    public static class ComparisonResult
    {
        private final boolean identical;
        private final InputStream inputStream;
    }

    /**
     * Gets the MIME type of an InputStream and returns a new InputStream that can be used for further processing
     * @param file the input stream to get the MIME type of
     * @return a MimeTypeResult containing the MIME type and a new InputStream
     * @throws IOException if an I/O error occurs
     */
    public static InputStreamDetails getInputStreamDetails(InputStream file) throws IOException
    {
        // Read the entire input stream into a byte array output stream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192]; // 8KB buffer for efficiency
        int bytesRead;

        while ((bytesRead = file.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        // Get the byte array and create two new input streams from it
        byte[] bytes = baos.toByteArray();

        // One for detecting the MIME type
        InputStream mimeTypeStream = new ByteArrayInputStream(bytes);
        //String mimeType = new Tika().detect(mimeTypeStream);
        //String extension = getExtensionFromMimeType(mimeType);
        long size = bytes.length;
        // One for returning to the caller
        InputStream newInputStream = new ByteArrayInputStream(bytes);
        return null;//new InputStreamDetails(mimeType, extension, size, newInputStream);
    }

    /**
     * Compares the content of an InputStream with a file on disk
     * @param file the input stream to compare
     * @param localFilePath the path to the file on disk
     * @return a ComparisonResult containing the comparison result and a new InputStream
     */
    public static ComparisonResult areFilesIdenticalByContent(InputStream file, String localFilePath) {
        try {
            File localFile = new File(localFilePath);
            InputStreamDetails streamDetails = getInputStreamDetails(file);
            // Compare file sizes
            long fileSize = streamDetails.getSize();
            long localFileSize = localFile.length();
            if (fileSize != localFileSize) {
                return new ComparisonResult(false, streamDetails.getInputStream());
            }

            // Create a copy of the input stream for comparison
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192]; // 8KB buffer for efficiency
            int bytesRead;
            InputStream comparisonStream = streamDetails.getInputStream();

            while ((bytesRead = comparisonStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            byte[] fileBytes = baos.toByteArray();
            byte[] localFileBytes = Files.readAllBytes(localFile.toPath());

            boolean identical = Arrays.equals(fileBytes, localFileBytes);

            // Create a new input stream for the caller to use
            InputStream newInputStream = new ByteArrayInputStream(fileBytes);

            return new ComparisonResult(identical, newInputStream);
        } catch (IOException e) {
            e.printStackTrace();
            // In case of error, return false and a null input stream
            return new ComparisonResult(false, null);
        }
    }
}
