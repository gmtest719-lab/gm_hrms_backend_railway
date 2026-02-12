package com.gm.hrms.service.impl;

import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.EmployeeDocument;
import com.gm.hrms.repository.EmployeeDocumentRepository;
import com.gm.hrms.service.EmployeeDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeDocumentServiceImpl implements EmployeeDocumentService {

    private final EmployeeDocumentRepository documentRepository;

    @Override
    public void saveDocuments(Employee employee, List<MultipartFile> files) {

        for (MultipartFile file : files) {

            String filePath = "uploads/" + file.getOriginalFilename(); // later replace with storage service

            EmployeeDocument doc = new EmployeeDocument();
            doc.setEmployee(employee);
            doc.setDocumentType(file.getContentType());
            doc.setFilePath(filePath);

            documentRepository.save(doc);
        }
    }
}
