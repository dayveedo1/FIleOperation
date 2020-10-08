package com.david.FileOperation.repository;

import com.david.FileOperation.model.FileSystem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FIleSystemRepository extends JpaRepository<FileSystem, Long> {


    FileSystem findByFileName(String fileName);
}
