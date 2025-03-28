package com.vnua.task_manager.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

        // Lưu tệp vào thư mục
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }
}
