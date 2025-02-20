package com.example.GestionNote.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ModuleGradesUploadDTO {
    MultipartFile excelFile;
    Integer moduleId;
    String academicYear;
    String session;
}
