/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package intCount.utility;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author 
 */
public class DirectoryTreeCopy extends SimpleFileVisitor<Path> {
    
    private final Path sourcePath;
    private final Path destinationPath;
    private final CopyOption[] copyOptions;
    
    public DirectoryTreeCopy(final String source, final String destination) {
        sourcePath = Paths.get(source);
        destinationPath = Paths.get(destination);
        
        copyOptions = new CopyOption[] {LinkOption.NOFOLLOW_LINKS, 
            StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES};
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        final Path targetPath = destinationPath.resolve(sourcePath.relativize(dir));
        if(!Files.exists(targetPath)){
            Files.createDirectory(targetPath);
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
         Files.copy(file, destinationPath.resolve(sourcePath.relativize(file)),
                 copyOptions);
         return FileVisitResult.CONTINUE;
    }
    
}
