package com.gm.hrms.service;

import com.gm.hrms.entity.Employee;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeDocumentService {

    void saveDocuments(Employee employee, List<MultipartFile> files);
}
