package com.simonjamesrowe.apigateway.core.usecase;

import java.io.File;

public interface ICompressFileUseCase {
    byte[] compress(File file, Integer size);
}