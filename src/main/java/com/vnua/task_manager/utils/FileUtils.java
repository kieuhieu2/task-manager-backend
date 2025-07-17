package com.vnua.task_manager.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtils {
    public static String saveFileToPath(String directory, MultipartFile file) throws IOException {
        Path uploadPath = Path.of(directory);

        if (!Files.exists(uploadPath)) {
            throw new IOException("Thư mục không tồn tại: " + directory);
        }

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Chuyển tên file thành snake_case
        String fileName = StringCustomUtils.convertToSnakeCaseKeepDot(file.getOriginalFilename());

        // Check if file with this name already exists
        Path filePath = uploadPath.resolve(fileName);
        if (Files.exists(filePath)) {
            fileName = handleFileNameConflict(uploadPath, fileName);
        }

        // Lưu tệp vào thư mục
        filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
    
    /**
     * Handle filename conflicts by appending or incrementing a number
     * @param directory Directory where file is being saved
     * @param fileName Original filename
     * @return Modified filename that doesn't conflict with existing files
     */
    private static String handleFileNameConflict(Path directory, String fileName) {
        String baseName;
        String extension = "";
        
        // Extract base name and extension
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0) {
            baseName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        } else {
            baseName = fileName;
        }
        
        // Check if basename ends with a number
        Pattern pattern = Pattern.compile("(.*?)(\\d+)$");
        Matcher matcher = pattern.matcher(baseName);
        
        if (matcher.matches()) {
            // Filename ends with a number, increment it
            String prefix = matcher.group(1);
            int number = Integer.parseInt(matcher.group(2));
            
            String newFileName;
            do {
                number++;
                newFileName = prefix + number + extension;
            } while (Files.exists(directory.resolve(newFileName)));
            
            return newFileName;
        } else {
            // Filename doesn't end with a number, append "1"
            String newFileName = baseName + "1" + extension;
            
            // If that filename also exists, recursively try to find a unique name
            if (Files.exists(directory.resolve(newFileName))) {
                return handleFileNameConflict(directory, newFileName);
            }
            
            return newFileName;
        }
    }
}
