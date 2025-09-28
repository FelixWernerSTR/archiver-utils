import java.nio.file.*;

import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.*;

import java.io.IOException;

public class CopyDirectoryPlainJava {
	private static final Logger logger = Logger.getLogger(CopyDirectoryPlainJava.class.getName());
	
	Path source;
	Path target;
	
	CopyDirectoryPlainJava source(String s) {
		source = Paths.get(s);
		return this;
	}
	
	CopyDirectoryPlainJava target(String t) {
		target = Paths.get(t);
		return this;
	}
	
	public static void main(String[] args) {
		
		if (args.length < 2) {
			logger.info("at least 2 or more arguments expected");
			return;
		}		
		new CopyDirectoryPlainJava().source(args[0]).target(args[1]).process();
		logger.info("copy finished");
	
	}
	
	void process() {
		logger.info(String.format("start copy source: %s to target: %s", source, target));
		CopyDirectoryPlainJava.copy(source, target);
		logger.info(String.format("copy finished"));
	}
	
	static LocalDateTime start;
	static LocalDateTime end;
	
    protected static void copy(Path source, Path target)  {
    	
        try {
			Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
			    @Override
			    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs){
			        try {
			            start = LocalDateTime.now();
			        	//logger.info(String.format("process files: %s in dir: %s", Files.list(dir).count(), dir.getFileName().toAbsolutePath()));
			            logger.info(String.format("process in dir: %s", dir.getFileName().toAbsolutePath()));
			        	Path targetDir = target.resolve(source.relativize(dir));
						Files.createDirectories(targetDir);
					} catch (IOException e) {
						logger.info(String.format("error at dir %s", dir.getFileName()));
						e.printStackTrace();
					}
			        return FileVisitResult.CONTINUE;
			    }
			    
			    @Override
			    public FileVisitResult postVisitDirectory(Path dir, IOException exc){

			    	end = LocalDateTime.now();
				    logger.info(String.format("finished process dir: %s in %s seconds", dir.getFileName(), Duration.between(start, end).getSeconds()));
				    if(exc!=null) {
				    	exc.printStackTrace();
				    }
			        return FileVisitResult.CONTINUE;
			    }
			    
			    @Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			        try {
						if(!Files.exists(target.resolve(source.relativize(file)))){
							Files.copy(file, target.resolve(source.relativize(file)), REPLACE_EXISTING);
						}else {
							logger.info(String.format("file allready exists in target %s", file.getFileName()));
						}
					} catch (IOException e) {
						logger.info(String.format("error at file: %s", file.getFileName()));
						e.printStackTrace();
					}
			        return FileVisitResult.CONTINUE;
			    }
			});
		} catch (IOException e) {
			logger.info(String.format("error copy source %s to target %s", source, target));
			e.printStackTrace();
		}
    }
}