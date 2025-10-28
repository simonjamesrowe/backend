package com.simonjamesrowe.apigateway.core.usecase;

import io.micrometer.tracing.annotation.NewSpan;

public interface IResumeUseCase {
    byte[] getResume();

    @NewSpan("regenerateResume")
    void regenerateResume();
}