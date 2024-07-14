package de.fw.devops.main;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CommandLineHandler {
  private static final Logger logger = LogManager.getLogger(CommandLineHandler.class);

  private static Options getCommandLineOptions() {
    Options options = new Options();
    
    Option dirPath = new Option("d1", "dirOrFilePath", true,"directory or file path");
    dirPath.setRequired(true);
    options.addOption(dirPath);
    
    Option destination = new Option("d2", "destDir", true, "directory or file path");
    destination.setRequired(true);
    options.addOption(destination);
    
    Option fileArchiveName = new Option("f", "fileArchiveName", true,"optional file archive name, default is: dirOrFilePath.zip");
    fileArchiveName.setRequired(false);
    options.addOption(fileArchiveName);
    
    Option dateSuffix = new Option("ds", "date suffix", false, "add date suffix");
    dateSuffix.setRequired(false);
    options.addOption(dateSuffix);
    
    Option deleteOlder = new Option("do", "date suffix", false, "delete older archives");
    deleteOlder.setRequired(false);
    options.addOption(deleteOlder);
    
    Option extract = new Option("e", "extract", false, "extract archive");
    extract.setRequired(false);
    options.addOption(extract);
    
    Option extractOne = new Option("eo", "extractone", true, "extract a file from archive");
    extractOne.setRequired(false);
    options.addOption(extractOne);
    
    Option compress = new Option("c", "compress", false, "compress file");
    compress.setRequired(false);
    options.addOption(compress);
    
    Option decompress = new Option("d", "decompress", false, "decompress file");
    decompress.setRequired(false);
    options.addOption(decompress);
    
    Option timestamp_suffix = new Option("ds", "datestamp", false, "datestamp suffix for archive");
    timestamp_suffix.setRequired(false);
    options.addOption(timestamp_suffix);
    
    Option one_for_subfolder = new Option("ofs", "one_for_subfolder", false, "one archive for subfolder");
    one_for_subfolder.setRequired(false);
    options.addOption(one_for_subfolder);
    
    Option includes = new Option("inc", "includes", true, "comma separated subfolder to archive");
    includes.setRequired(false);
    options.addOption(includes);
    
    Option extension = new Option("ext", "extension", true, "extension for example zip(default),tar,7z");
    extension.setRequired(false);
    options.addOption(extension);
    
    return options;
  }
  
  static final String HELP_OPTION = "help";
  
  static void printHelp(String programName, Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(programName, options);
  }
  
  static CommandLine parseCommandLineOptions(String programName, String[] commandLineArguments) {
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine line = parser.parse(getCommandLineOptions(), commandLineArguments);
      if (line.hasOption(HELP_OPTION)) {
        printHelp(programName, getCommandLineOptions());
        return null;
      }
      return line;
    } catch (ParseException e) {
      logger.error(e);
      printHelp(programName, getCommandLineOptions());
      return null;
    }
  }
  
}
