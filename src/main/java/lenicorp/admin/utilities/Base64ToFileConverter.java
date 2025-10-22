package lenicorp.admin.utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

/**
 * Utility class for converting between Base64 strings and files
 */
public class Base64ToFileConverter {

    /**
     * Convert a Base64 URL string to a file
     * @param base64UrlString the Base64 URL string
     * @param extension the file extension (e.g., ".pdf")
     * @return the file
     * @throws IOException if an I/O error occurs
     */
    public static File convertToFile(String base64UrlString, String extension) throws IOException {
        // Replace URL-safe characters with Base64 standard characters
        String base64String = base64UrlString.replace('-', '+').replace('_', '/');
        
        // Decode the Base64 string
        byte[] fileBytes = Base64.getDecoder().decode(base64String);
        
        // Create a temporary file
        File tempFile = File.createTempFile("temp", extension);
        tempFile.deleteOnExit();


        
        // Write the bytes to the file
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(fileBytes);
        }
        
        return tempFile;
    }

    /**
     * Convert a file to a Base64 URL string
     * @param file the file
     * @return the Base64 URL string
     * @throws IOException if an I/O error occurs
     */
    public static String convertToBase64String(File file) throws IOException {
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        return convertBytesToBase64String(fileBytes);
    }
    
    /**
     * Convert bytes to a Base64 URL string
     * @param fileBytes the file bytes
     * @return the Base64 URL string
     */
    public static String convertBytesToBase64String(byte[] fileBytes) {
        // Encode the bytes to Base64
        String base64String = Base64.getEncoder().encodeToString(fileBytes);

        return base64String;
    }
}