package com.simonjamesrowe.apigateway.core.usecase;

import com.simonjamesrowe.apigateway.core.repository.ResumeRepository;
import io.micrometer.tracing.annotation.NewSpan;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class ResumeUseCase implements IResumeUseCase {

    private final ResumeRepository resumeRepository;
    private final AtomicReference<byte[]> resumeCache = new AtomicReference<>();

    @Value("${resume.cache-on-init:false}")
    private boolean cacheResumeOnInit;

    @PostConstruct
    public void init() {
        if (cacheResumeOnInit) {
            regenerateResume();
        }
    }

    @Override
    public byte[] getResume() {
        byte[] cachedResume = resumeCache.get();
        if (cachedResume != null) {
            return cachedResume;
        }
        // If not cached, regenerate and return
        regenerateResume();
        return resumeCache.get();
    }

    @NewSpan("regenerateResume")
    @Override
    public void regenerateResume() {
        var resumeData = resumeRepository.getResumeData();
        byte[] resume = ResumeGenerator.generate(resumeData);
        resumeCache.set(resume);
    }
}