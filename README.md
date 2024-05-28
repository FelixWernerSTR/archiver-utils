# archiver-utils
create .zip/.tar./7z-archives with java. With some extar features like rchives for subfolders in directory. Adding Datestamp. Compress/Decompress files... Exports a standalone-Jar.

Usage:
rem %JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target -ds
rem %JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target -ds -ofs
rem %JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target -ds -ofs -inc subfolder,subfolder3
rem %JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target -ds -ofs -inc subfolder,subfolder3 -ext 7z

%JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target