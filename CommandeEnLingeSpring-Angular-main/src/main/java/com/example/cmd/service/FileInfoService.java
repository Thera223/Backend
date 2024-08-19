package com.example.cmd.service;

import com.example.cmd.model.FileInfo;
import com.example.cmd.repository.FileInfoRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileInfoService {
    private FileInfoRepository fileInfoRepository;

    @Transactional(Transactional.TxType.REQUIRED)
    public FileInfo creer(FileInfo fileInfo) {
        return this.fileInfoRepository.save(fileInfo);
    }
}
