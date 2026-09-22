
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Concurrent {
    
    public static void main(String[] args) {
        
        Runnable task = () -> {
            String name = Thread.currentThread().getName();
            System.out.println("Executing"+name);
        };

        Thread thread1 = new Thread(task);
        thread1.start();
        Thread thread2 = new Thread(task);
        thread2.start();


        // Concurency API

        ExecutorService executor = Executors.newFixedThreadPool(10);

        // for(int i=1; i<=10;i++){
        //     executor.submit(()->{
        //         String name = Thread.currentThread().getName();
        //         System.out.println("Executing: "+name);
        //     });
        // }


        // With Callable 

        for(int i=1; i<=2;i++){

            Future<String> future = executor.submit(()->{
                String name = Thread.currentThread().getName();
                System.out.println("Executing: "+name);
                return name;
            });

            System.out.println("is Done: "+future.isDone());
            
            try {
                String result = future.get();
                System.out.println("Result: " + result);
                System.out.println("is Done: "+future.isDone()+"\n");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            

        }

        executor.shutdown();

    }
}
