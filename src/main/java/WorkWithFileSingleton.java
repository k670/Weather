import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class WorkWithFileSingleton {
   private static final WorkWithFileSingleton inst= new WorkWithFileSingleton();

   private WorkWithFileSingleton() {
       super();
   }

   public synchronized void writeToFile(String data, String fileName) throws IOException {
       Files.write(Paths.get(fileName), data.getBytes(), StandardOpenOption.APPEND);

      /*try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName)))
       {
           oos.writeObject(data);
           //System.out.println("File has been written");
       }
       catch(Exception ex){

           System.out.println(ex.getMessage());
       }*/

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

   public static WorkWithFileSingleton getInstance() {
       return inst;
   }

}
