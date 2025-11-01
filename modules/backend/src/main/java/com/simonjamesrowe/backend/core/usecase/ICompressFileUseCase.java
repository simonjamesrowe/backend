package com.simonjamesrowe.backend.core.usecase;

import java.io.File;

public interface ICompressFileUseCase {
    byte[] compress(File file, Integer size);
}