package com.simonjamesrowe.apigateway.entrypoints.restcontroller;

import com.simonjamesrowe.apigateway.core.usecase.IResumeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequiredArgsConstructor
public class ResumeController {

    private final IResumeUseCase resumeUseCase;

    @GetMapping("/resume")
    public ResponseEntity<byte[]> resume() {
        byte[] bytes = resumeUseCase.getResume();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Simon_Rowe_Resume.pdf\"")
                .body(bytes);
    }
}