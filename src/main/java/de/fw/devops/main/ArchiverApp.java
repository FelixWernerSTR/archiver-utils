package de.fw.devops.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.logging.log4j.Logger;

import de.fw.devops.ArchiverUtils;

import org.apache.logging.log4j.LogManager;

public class ArchiverApp {

	private static final Logger logger = LogManager.getLogger(ArchiverApp.class);

	private Path inputPath;
	private Path destinationPath;
	private Path destinationFileName;
	private boolean extract = false;
	private boolean compress = false;
	private boolean decompress = false;
	private boolean dateStamp = false;
	private boolean oneForSubfolder = false;
	private String includes = null;
	private String extension = "zip";

	private static ArchiverApp instance = new ArchiverApp();

	public static void main(String[] args) throws IOException, ArchiveException, CompressorException {

		if (args.length < 2) {
			logger.info("at least 2 or more arguments expected");
		}

		CommandLine commandLine = CommandLineHandler.parseCommandLineOptions("ArchiverUtilsApp", args);
		if (commandLine == null) {
			return;
		}

		instance.inputPath(Paths.get(commandLine.getOptionValue("d1")));
		instance.destinationPath(Paths.get(commandLine.getOptionValue("d2")));

		if (commandLine.hasOption("f")) {
			instance.destinationFileName(Paths.get(commandLine.getOptionValue("f")));
		}

		if (commandLine.hasOption("e")) {
			instance.extract(true);
		}
		
		if (commandLine.hasOption("c")) {
			instance.compress(true);
		}
		
		if (commandLine.hasOption("d")) {
			instance.decompress(true);
		}
		
		if (commandLine.hasOption("ds")) {
			instance.dateStamp(true);
		}
		
		if (commandLine.hasOption("ofs")) {
			instance.oneForSubfolder(true);
		}
		
		if (commandLine.hasOption("inc")) {
			instance.includes(commandLine.getOptionValue("inc"));
		}
		
		if (commandLine.hasOption("ext")) {
			instance.extension(commandLine.getOptionValue("ext"));
		}
		
		if(commandLine.hasOption("c") && commandLine.hasOption("e") && commandLine.hasOption("d")) {
			logger.warn("you can choose only one option (archive(default), extract, compress, decompress)!");
			return;
		}

		instance.process();

	}

	public ArchiverApp inputPath(Path inputPath) {
		this.inputPath = inputPath;
		return this;
	}

	public ArchiverApp destinationPath(Path destinationPath) {
		this.destinationPath = destinationPath;
		return this;
	}

	public ArchiverApp destinationFileName(Path destinationFileName) {
		this.destinationFileName = destinationFileName;
		return this;
	}

	public ArchiverApp extract(boolean extract) {
		this.extract = extract;
		return this;
	}
	
	public ArchiverApp compress(boolean compress) {
		this.compress = compress;
		return this;
	}
	
	public ArchiverApp decompress(boolean decompress) {
		this.decompress = decompress;
		return this;
	}
	
	public ArchiverApp dateStamp(boolean dateStamp) {
		this.dateStamp = dateStamp;
		return this;
	}
	
	public ArchiverApp oneForSubfolder(boolean oneForSubfolder) {
		this.oneForSubfolder = oneForSubfolder;
		return this;
	}
	
	public ArchiverApp includes(String includes) {
		this.includes = includes;
		return this;
	}
	
	public ArchiverApp extension(String extension) {
		this.extension = extension;
		return this;
	}

	public void process() throws IOException, ArchiveException, CompressorException {
		logger.info("process started with configuration: {}", this);

		if (extract) {
			extract();
		}else if (compress) {
			compress();
		}else if(decompress) {
			decompress();
		}else {
			archive();		
		}
		logger.info("process finished");	
	}

	private void archive() throws IOException, ArchiveException {
		Instant start = Instant.now();
		logger.info("archive started with configuration: {}", this);	
		if(!Files.exists(destinationPath)) {
			Files.createDirectories(destinationPath);
			logger.info("created non existent destinationPath: {}", destinationPath);
		}
		if (destinationFileName != null) {
			ArchiverUtils.archive(inputPath,Paths.get(destinationPath + File.separator + instance.destinationFileName));
		} else {
			if(oneForSubfolder) {
			
			Files.list(inputPath).filter(s->Files.isDirectory(s)).filter(s->isIncludes(s)).forEach(s->{
				try {
					archiveSubPath(s);
				} catch (IOException | ArchiveException e) {
					e.printStackTrace();
				}
			});

			}else{
				String zipFileName = inputPath.getFileName() + "."+extension;
				if(dateStamp) {
					zipFileName = inputPath.getFileName() + "_v"+getTodaySuffix()+"."+extension;
				}
				ArchiverUtils.archive(inputPath, Paths.get(destinationPath + File.separator + zipFileName));				
			}

		}
		logFinish(start,"archive");

	}
	
	private boolean isIncludes(Path s) {
		if(includes==null) {
		 return true;	
		}else {
		 return List.of(includes.split(",")).contains(s.getFileName().toString());
		}
	}

	private void archiveSubPath(Path s) throws IOException, ArchiveException {
      new ArchiverApp().inputPath(s).destinationPath(destinationPath).dateStamp(dateStamp).archive();
	}

	private String getTodaySuffix() {
		return LocalDate.now().getDayOfMonth()+"-"+LocalDate.now().getMonthValue()+"-"+LocalDate.now().getYear();
	}

	private void decompress() throws IOException, CompressorException {
		Instant start = Instant.now();
		logger.info("decompress with configuration: {}", this);
		if (!Files.isRegularFile(inputPath)) {
			logger.error("must be a regular file: {}", inputPath);
		}
		if (!Files.isRegularFile(destinationPath)) {
			logger.error("must be a file: {}", destinationPath);
		}
		ArchiverUtils.decompress(inputPath, destinationPath);
		logFinish(start,"decompress");
	}

	private void compress() throws IOException, CompressorException {
		Instant start = Instant.now();
		logger.info("compress with configuration: {}", this);
		if (!Files.isRegularFile(inputPath)) {
			logger.error("must be a regular file: {}", inputPath);
		}
		if (!Files.isRegularFile(destinationPath)) {
			logger.error("must be a file: {}", destinationPath);
		}
		ArchiverUtils.compressFile(inputPath, destinationPath);
		logFinish(start,"compress");
	}

	private void extract() throws IOException, ArchiveException {
		Instant start = Instant.now();
		logger.info("extract with configuration: {}", this);
		if(!Files.exists(destinationPath)) {
			Files.createDirectories(destinationPath);
			logger.info("created non existent destinationPath: {}", destinationPath);
		}
		if (!Files.isRegularFile(inputPath)) {
			logger.error("must be a regular file: {}", inputPath);
		}
		if (!Files.isDirectory(destinationPath)) {
			logger.error("must be a directory: {}", destinationPath);
		}
		ArchiverUtils.extract(inputPath, destinationPath);
		logFinish(start,"extract");
	}

	private void logFinish(Instant start, String whoFinished) {
		Duration now = Duration.between(start, Instant.now());
		int totalNanos = now.getNano();
		long totalSeconds = now.getNano();
		logger.info(whoFinished+" finished with configuration: {}, took {} minutes, {} seconds {} millies", this, totalSeconds/60, totalSeconds%60, totalNanos%1000);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ArchiverApp [inputPath=");
		builder.append(inputPath);
		builder.append(", destinationPath=");
		builder.append(destinationPath);
		builder.append(", destinationFileName=");
		builder.append(destinationFileName);
		builder.append(", extract=");
		builder.append(extract);
		builder.append(", compress=");
		builder.append(compress);
		builder.append(", decompress=");
		builder.append(decompress);
		builder.append(", dateStamp=");
		builder.append(dateStamp);
		builder.append(", oneForSubfolder=");
		builder.append(oneForSubfolder);
		builder.append(", includes=");
		builder.append(includes);
		builder.append(", extension=");
		builder.append(extension);
		builder.append("]");
		return builder.toString();
	}


}
