set JAVA_HOME=D:/devel/java/jdk-11.0.18
rem Examples:
rem %JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target -ds
rem %JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target -ds -ofs
rem %JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target -ds -ofs -inc subfolder,subfolder3
rem %JAVA_HOME%/bin/java -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target -ds -ofs -inc subfolder,subfolder3 -ext 7z

%JAVA_HOME%/bin/java -Xms8g -Xmx8g -jar archiver-utils.jar -d1 D:\devel\projects\archiver-utils\src\test\resources\snippets -d2 target

