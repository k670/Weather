import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {

        String fileName = ".\\src\\main\\resources\\testForkJoin.txt";
        String fileNameExe = ".\\src\\main\\resources\\testExecutor.txt";
        WeatherTry4 weatherTry4 = new WeatherTry4();
        long begin = System.nanoTime();

        boolean res = weatherTry4.WeatherReadToFileForkJoin(fileName, 160,1000);
        long end = System.nanoTime();
        System.out.println("WeatherReadToFileForkJoin\n"+(end-begin)/1000000000 +"s");
        System.out.println(res);


        begin = System.nanoTime();
         res = weatherTry4.WeatherReadToFileExecutor(fileNameExe, 160,1000);
         end = System.nanoTime();
        System.out.println("WeatherReadToFileExecutor\n"+(end-begin)/1000000000+"s");
        System.out.println(res);
    }
}
