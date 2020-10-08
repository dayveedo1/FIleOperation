package com.david.FileOperation.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@Transactional
public class FileStorageService {

    private Path filesStoragePath;

    private String fileStorageLocation;

    public FileStorageService(@Value("${spring.servlet.multipart.location:temp}") String fileStorageLocation) {

        this.fileStorageLocation = fileStorageLocation;
        filesStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();

        //Creating a directory to store uploaded files on server
        try{
            Files.createDirectories(filesStoragePath);
        } catch (IOException e){
            throw new RuntimeException("Error in creating file directory");
        }

    }

    //To store a file in the directory on server
    public String storeFile(MultipartFile file){

        String fileName = StringUtils.cleanPath((file.getOriginalFilename()));

        Path filePath =  Paths.get(filesStoragePath + "\\" + fileName);

        try {
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error in storing the file", e);
        }
        return fileName;
    }

    //To download a file from server
    public Resource downloadFile(String fileName) {

        Path path = Paths.get(fileStorageLocation).toAbsolutePath().resolve(fileName);

        Resource resource;

        try{
            resource = new UrlResource(path.toUri());
        }catch (MalformedURLException e){
            throw new RuntimeException("Error in reading file", e);
        }

        if (resource.exists()&& resource.isReadable()){
            return resource;
        }else {
            throw new RuntimeException("File doesn't exist or not readable");
        }

    }
}
