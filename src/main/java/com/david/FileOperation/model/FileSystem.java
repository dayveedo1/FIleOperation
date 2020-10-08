package com.david.FileOperation.model;

import javax.persistence.*;
import java.util.Arrays;

@Entity
@Table(name = "file_system")
public class FileSystem {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Lob
    @Column(name = "DOC_FILE")
    private byte[] docFile;

    public FileSystem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getDocFile() {
        return docFile;
    }

    public void setDocFile(byte[] docFile) {
        this.docFile = docFile;
    }

    @Override
    public String toString() {
        return "FileSystem{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", docFile=" + Arrays.toString(docFile) +
                '}';
    }
}
