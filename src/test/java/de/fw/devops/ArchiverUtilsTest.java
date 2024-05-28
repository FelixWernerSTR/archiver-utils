package de.fw.devops;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.compressors.CompressorException;
import org.junit.jupiter.api.Test;

public class ArchiverUtilsTest {
	
	@Test
	void testArchive() throws IOException, ArchiveException {
	    Path directory = Paths.get("src/test/resources/snippets");
	    Path targetFile = Paths.get("target/snippets.zip");

	    ArchiverUtils.archive(directory, targetFile);

	    assertTrue(Files.isRegularFile(Paths.get("target/snippets.zip")));
	}
	
	@Test
	void givenFile_whenCompressing_thenCompressed() throws IOException, CompressorException {
	    Path destination = Paths.get("target/jason-content.gz");

	    ArchiverUtils.compressFile(Paths.get("src/test/resources/snippets/jason-content.txt"), destination);

	    assertTrue(Files.isRegularFile(destination));
	}
	
	@Test
	void givenCompressedArchive_whenDecompressing_thenArchiveAvailable() throws IOException, CompressorException {
	    Path destination = Paths.get("target/jason-content2.gz");

	    ArchiverUtils.compressFile(Paths.get("src/test/resources/snippets/jason-content.txt"), destination);

		
		destination = Paths.get("target/jason-content.txt");

		ArchiverUtils.decompress(Paths.get("target/jason-content2.gz"), destination);

	    assertTrue(Files.isRegularFile(destination));
	}
	
	@Test
	void testStringIncludes() {
		String includes = "testA,testB,TestC";
		assertTrue(!List.of(includes.split(",")).contains("test"));
		assertTrue(List.of(includes.split(",")).contains("testA"));
	}

}
