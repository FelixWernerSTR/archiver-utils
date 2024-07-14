package de.fw.devops;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.examples.Archiver;
import org.apache.commons.compress.archivers.examples.Expander;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArchiverUtils {
  
  private static final Logger logger = LogManager.getLogger(ArchiverUtils.class);
  
  public static void archive(Path directory, Path destination) throws IOException, ArchiveException {
    String format = FileNameUtils.getExtension(destination);
    new Archiver().create(format, destination, directory);
  }
  
  public static void extract(Path archive, Path destination) throws IOException, ArchiveException {
    new Expander().expand(archive, destination);
  }
  
  /*
   * We can use the specific implementation class directly if we want exclusive features from each format. For example, instead of using ArchiveOutputStream,
   * we’ll instantiate a ZipArchiveOutputStream so we can set its compression method and level directly:
   */
  public static void zip(Path file, Path destination) throws IOException {
    try (InputStream input = Files.newInputStream(file);
        OutputStream output = Files.newOutputStream(destination);
        ZipArchiveOutputStream archive = new ZipArchiveOutputStream(output)) {
      archive.setMethod(ZipEntry.DEFLATED);
      archive.setLevel(Deflater.BEST_SPEED);
      
      archive.putArchiveEntry(new ZipArchiveEntry(file.getFileName().toString()));
      IOUtils.copy(input, archive);
      archive.closeArchiveEntry();
    }
  }
  
  public static void compressFile(Path file, Path destination) throws IOException, CompressorException {
    String format = FileNameUtils.getExtension(destination);
    
    try (OutputStream out = Files.newOutputStream(destination);
        BufferedOutputStream buffer = new BufferedOutputStream(out);
        CompressorOutputStream compressor = new CompressorStreamFactory().createCompressorOutputStream(format, buffer)) {
      IOUtils.copy(Files.newInputStream(file), compressor);
    }
  }
  
  public static void decompress(Path input, Path destination) throws IOException, CompressorException {
    try (InputStream in = Files.newInputStream(input);
        BufferedInputStream inputBuffer = new BufferedInputStream(in);
        OutputStream out = Files.newOutputStream(destination);
        CompressorInputStream decompressor = new CompressorStreamFactory().createCompressorInputStream(inputBuffer)) {
      IOUtils.copy(decompressor, out);
    }
  }
  
  public static void archiveAndCompress(Path directory, Path destination) throws IOException, CompressorException, ArchiveException {
    String compressionFormat = FileNameUtils.getExtension(destination);
    String archiveFormat = FilenameUtils.getExtension(destination.getFileName().toString().replace("." + compressionFormat, ""));
    
    try (OutputStream archive = Files.newOutputStream(destination);
        BufferedOutputStream archiveBuffer = new BufferedOutputStream(archive);
        CompressorOutputStream compressor = new CompressorStreamFactory().createCompressorOutputStream(compressionFormat, archiveBuffer);
        ArchiveOutputStream<?> archiver = new ArchiveStreamFactory().createArchiveOutputStream(archiveFormat, compressor)) {
      new Archiver().create(archiver, directory);
    }
  }
  
  public static boolean extractOne(Path archivePath, String fileName, Path destinationDirectory) throws IOException, ArchiveException {
    boolean isFoundAndExtracted = false;
    try (InputStream input = Files.newInputStream(archivePath);
        BufferedInputStream buffer = new BufferedInputStream(input);
        ArchiveInputStream<? extends ArchiveEntry> archive = new ArchiveStreamFactory().createArchiveInputStream(buffer)) {
      logger.debug("file: {} to find in archive: {}", fileName, archivePath);
      logger.info("attention! only the first file which will be found, will be extracted!");
      ArchiveEntry entry;
      while ((entry = archive.getNextEntry()) != null) {
        if(entry.getName().endsWith(".zip") || entry.getName().endsWith(".tar") || entry.getName().endsWith(".jar") || entry.getName().endsWith(".war")) {
          // erstmal das interne archiv "extracten" und danach auch darin suchen das file suchen und "extracten"
          writeFile(entry.getName(), destinationDirectory, archive);
          Files.exists(Path.of(destinationDirectory+"/"+entry.getName()));
          isFoundAndExtracted = extractOne(Path.of(destinationDirectory+"/"+entry.getName()),fileName,destinationDirectory);
          Files.delete(Path.of(destinationDirectory+"/"+entry.getName()));
          Files.delete(Path.of(destinationDirectory+"/"+entry.getName()).getParent());
          if(isFoundAndExtracted) {
            return isFoundAndExtracted;
          }
        }
        if (entry.getName().endsWith(fileName)) {
          writeFile(fileName, destinationDirectory, archive);
          logger.info("file found and extracted: {}", fileName);
          isFoundAndExtracted = true;
          break; 
        }
      }
    }
    return isFoundAndExtracted;
  }

  /**
   * @param fileName
   * @param destinationDirectory
   * @param archive
   * @throws IOException
   */
  private static void writeFile(String fileName, Path destinationDirectory, ArchiveInputStream<? extends ArchiveEntry> archive) throws IOException {
    Path outFile = destinationDirectory.resolve(fileName);
    Files.createDirectories(outFile.getParent());
    try (OutputStream os = Files.newOutputStream(outFile)) {
      IOUtils.copy(archive, os);
    }
  }
  
}
