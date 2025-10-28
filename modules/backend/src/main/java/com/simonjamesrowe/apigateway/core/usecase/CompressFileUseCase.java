package com.simonjamesrowe.apigateway.core.usecase;

import io.micrometer.tracing.annotation.NewSpan;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class CompressFileUseCase implements ICompressFileUseCase {

    private final int compressionThreshold;

    public CompressFileUseCase(@Value("${image.compressFilesLargerThanKb}") int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    @NewSpan("compressFile")
    @Override
    public byte[] compress(File file, Integer size) {
        try {
            if (size < 1024 * compressionThreshold) {
                return IOUtils.toByteArray(new FileInputStream(file));
            }

            BufferedImage original = ImageIO.read(file);
            int scaledWidth = (int) (original.getWidth() * 0.5);
            int scaledHeight = (int) (original.getHeight() * 0.5);

            BufferedImage outputImage = new BufferedImage(scaledWidth, scaledHeight, original.getType());

            var g2d = outputImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.drawImage(original, 0, 0, scaledWidth, scaledHeight, null);
            g2d.dispose();

            ByteArrayOutputStream compressedBytes = new ByteArrayOutputStream(1024);
            String extension = file.getName().substring(file.getName().indexOf(".") + 1);
            ImageIO.write(outputImage, extension, compressedBytes);
            return compressedBytes.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress file", e);
        }
    }
}