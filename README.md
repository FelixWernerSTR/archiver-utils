# archiver-utils
create .zip/.tar./7z-archives with java. With some extra features like archives for subfolders in a directory. Adding Datestamp. Compress/Decompress files... Exports a standalone-Jar.

#Usage:
usage: ArchiverUtilsApp

	-c,--compress                compress file
	-d,--decompress              decompress file
	-d1,--dirOrFilePath <arg>    directory or file path
	-d2,--destDir <arg>          directory or file path
	-do,--date suffix            delete older archives
	-ds,--datestamp              datestamp suffix for archive
	-e,--extract                 extract archive
	-ext,--extension <arg>       extension for example zip(default),tar,7z
	-f,--fileArchiveName <arg>   optional file archive name, default is:dirOrFilePath.zip
	-inc,--includes <arg>        comma separated subfolder to archive
	-ofs,--one_for_subfolder     one archive for subfolder

#Example:
 
%JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target