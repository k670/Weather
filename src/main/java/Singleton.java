import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Singleton {
   private static final Singleton inst= new Singleton();

   private Singleton() {
       super();
   }

   public synchronized void writeToFile(String data, String fileName) throws IOException {
       Files.write(Paths.get(fileName), data.getBytes(), StandardOpenOption.APPEND);
   }

    public  void deleteAndCreateFile(String fileName) throws IOException {
        try
        {
            Files.deleteIfExists(Paths.get(fileName));
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }finally {
            Files.createFile(Paths.get(fileName));
        }
    }

   public static Singleton getInstance() {
       return inst;
   }

}
