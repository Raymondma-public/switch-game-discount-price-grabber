package managers;

import java.io.IOException;

@FunctionalInterface
public interface FileWriterUseInstance<T>{
    void accept(T fileWriter) throws IOException, InterruptedException;
}
