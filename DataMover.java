import java.util.ArrayList;
import java.util.List;

public class DataMover{
    private static final int move_time = 123;//but this is default move time
    private static final int[] thread_time = {111, 256, 404};//and this one os the default thread time
    private static List<Integer> data;
    private static List<Thread> movers;
    private static int moveTime;
    private static Object[] locks; //lock objects array

    public static void main(String[] args) {
        initialize(args);
        startThreads();
        WaitForThreads();
        print();
    }

    private static void initialize(String[] args) {
        // Set default or provided move time
        if(args.length > 0)
        {
            moveTime = Integer.parseInt(args[0]);
        }else
        {
            moveTime = move_time;
        }
        // in the code below we will specify the number of threads and their sleep times
        int numThreads;
        if (args.length > 1) {
            numThreads = args.length - 1;
        } else {
            numThreads = thread_time.length;
        }

        data = new ArrayList<>();
        movers = new ArrayList<>();
        locks = new Object[numThreads];//initializing

        for (int i = 0; i < numThreads; i++) {
            data.add(i * 1000);
            locks[i] = new Object();//here we specify that each element get a unique lock object
            int sleepTime;
            if(args.length > 1)
            {
                sleepTime = Integer.parseInt(args[i + 1]);
            }else {
                sleepTime = thread_time[i];
            }
            movers.add(new Thread(new DataMoverThread(i, sleepTime)));
        }
    }

    private static void startThreads() {
        for (Thread t : movers) {
            t.start();
        }
    }

    private static void WaitForThreads() {
        for (Thread t : movers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void print() {
        System.out.println(data);
    }

    private static class DataMoverThread implements Runnable {
        private int index;
        private int sleepTime;

        public DataMoverThread(int index, int sleepTime) {
            this.index = index;
            this.sleepTime = sleepTime;
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(sleepTime);
                    int nextIndex = (index + 1) % data.size();//calculating the nextindex 

                    int firstLock = Math.min(index, nextIndex);// lock the first element
                    int secondLock = Math.max(index, nextIndex);//lock the second element
          
                    synchronized (locks[firstLock]) {
                        synchronized(locks[secondLock]) {
                            // Subtract index from the current element
                         int currentValue = data.get(index);//in data array = 0, 1000,2000 data[0] = 0 , data[1] = 1000, data[2] = 2000
                         data.set(index, currentValue - index);
                         System.out.println("#" + index + ": data " + index + " == " + data.get(index));

                         // Sleep for moveTime
                         Thread.sleep(moveTime);
                         
                         // Add index to the next element
                            int nextValue = data.get(nextIndex);
                            data.set(nextIndex, nextValue + index);
                            System.out.println("#" + index + ": data " + nextIndex + " -> " + data.get(nextIndex));
                        
                        }
                        
                    }
                    
                }


                    
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
