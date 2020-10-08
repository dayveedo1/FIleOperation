package com.david.FileOperation.controller;

import com.david.FileOperation.dto.FileUploadResponse;
import com.david.FileOperation.service.FileStorageService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class FileStorageController {

    @Autowired
    private FileStorageService fileStorageService;

    @ApiOperation("To upload a single file")
    @PostMapping("/single/upload")
    public FileUploadResponse singleFileUpload (@RequestParam("file")MultipartFile file) throws IOException {

        String fileName = fileStorageService.storeFile(file);

        //To convert the file context path to url
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileName)
                .toUriString();

        String contentType = file.getContentType();

        FileUploadResponse response = new FileUploadResponse(fileName, contentType, url);

        return response;
    }

    @ApiOperation("To download a single uploaded file")
    @GetMapping("/download/{fileName}")
     public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) throws IOException {

        Resource resource = fileStorageService.downloadFile(fileName);

        MediaType contentType = MediaType.IMAGE_JPEG;
        String mimeType;

        //To dynamically detect the file media type
        try{
            mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        }catch (IOException e){
            mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;fileName="+ resource.getFilename())
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName="+ resource.getFilename())
                .body(resource);

        /* attachment: this would not render the file in the browser when we hit the url,
            rather it will download it to our local directory

            inline: you can guess right, this would render the file in the browser.
            you can
         */

    }

    @ApiOperation("To upload multiple files to server")
    @PostMapping("/multiple/upload")
    public List<FileUploadResponse> multipleFileUpload(@RequestParam("files") MultipartFile[] files) {

        if (files.length > 5 ){
            throw new RuntimeException("Too many files...");
        }
        //Creating a new array for multiple files
        List<FileUploadResponse> uploadResponseList = new ArrayList<>();
        Arrays.asList(files)
                .stream()
                .forEach(file -> {

                    String fileName = fileStorageService.storeFile(file);

                    //To convert the file context path to url
                    String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/download/")
                            .path(fileName)
                            .toUriString();

                    String contentType = file.getContentType();

                    FileUploadResponse response = new FileUploadResponse(fileName, contentType, url);
                    uploadResponseList.add(response);
                });
        return uploadResponseList;
    }

    @ApiOperation("To download zip files")
    @GetMapping("/zipDownload")
    public void zipDownload(@RequestParam("fileName") String [] files, HttpServletResponse response) throws IOException {

        //first, create the zip-output for the select files to be downloaded
        try(
            ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())){

                Arrays.asList(files)
                        .stream()
                        .forEach(file -> {
                            Resource resource = fileStorageService.downloadFile(file);

                            ZipEntry zipEntry = new ZipEntry(resource.getFilename());

                            try {
                                zipEntry.setSize(resource.contentLength());
                                zos.putNextEntry(zipEntry);

                                StreamUtils.copy(resource.getInputStream(),zos);
                                zos.closeEntry();

                            } catch (IOException e) {
                                throw new RuntimeException("Error while zipping file");
                            }
                        });
                zos.finish();
            }

            response.setStatus(200);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=zipFile");

    }
}
