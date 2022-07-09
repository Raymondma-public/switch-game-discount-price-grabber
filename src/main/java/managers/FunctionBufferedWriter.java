package managers;


import java.io.*;
import java.nio.charset.StandardCharsets;

public class FunctionBufferedWriter {

    public static void use(String fileName, FileWriterUseInstance<BufferedWriter> writerInstance){
        BufferedWriter fileWriter=null;
        try{
             fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), StandardCharsets.UTF_8));
            writerInstance.accept(fileWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            if(fileWriter!=null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
