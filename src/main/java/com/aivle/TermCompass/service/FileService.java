package com.aivle.TermCompass.service;

import com.aivle.TermCompass.domain.FileEntity;
import com.aivle.TermCompass.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class FileService {
    private final FileRepository fileRepository;

    public FileEntity saveFile(MultipartFile file) throws IOException {
        FileEntity fileEntity = new FileEntity(
                file.getOriginalFilename(),
                file.getContentType(),
                file.getBytes()
        );

        return fileRepository.save(fileEntity);
    }

    public Optional<FileEntity> getFile(Long id) {
        return fileRepository.findById(id);
    }
}
