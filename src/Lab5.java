class Buffer {
    private int data;
    private boolean inUse = false;

    Buffer(int data) {
        this.data = data;
    }

    synchronized void set(int value) {
        while (inUse) {
            try {
                wait();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        inUse = true;
        data = value;
        inUse = false;
        notifyAll();
    }

    synchronized int get() {
        while (inUse) {
            try {
                wait();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        return data;
    }
}

class Resource {
    private String name;
    private boolean inUse = false;

    Resource(String name) {
        this.name = name;
    }

    synchronized void use(int processID, int processMemory) {
        while (inUse) {
            System.out.println(Thread.currentThread().getName() + ". Состояние: блокирования.");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        inUse = true;
        System.out.println(Thread.currentThread().getName() + ". Состояние: " + name + "...");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        inUse = false;
    }
}

class Process implements Runnable {
    private int processID;
    private String processName;
    private int processMemory;
    private char priority;
    private Thread thread;
    private Resource[] resourceList;

    Process(int processID, String processName, int processMemory, Resource[] resourceList, char priority) {
        this.processID = processID;
        this.processName = processName;
        this.processMemory = processMemory;
        this.resourceList = resourceList;
        this.priority = priority;
        thread = new Thread(this, processName);
    }

    public void run() {
        System.out.println(processName + ". Запуск...");
        try {
            for (Resource resource : resourceList) {
                resource.use(processID, processMemory);
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.err.println(processName + ". Выполнение прервано!");
        }
        System.out.println(processName + ". Завершение.");
    }

    public void start() {
        thread.start();
    }

    public void join() throws InterruptedException {
        thread.join();
    }
}

public class Lab5 {
    static int[] processes = {0, 0, 0};
    static int[] procMem = {43, 24, 53};
    static char[] procPrior = {'m', 'h', 'l'};
    static String[] procNames = {"Процесс №1", "Процесс №2", "Процесс №3"};
    static Buffer freeRam;

    public static void main(String[] args) {
        System.err.println("96 Мб. ОЗП для 3-х процессов");
        System.err.println("1) 43Mb, ресурсы (r1,r2), m");
        System.err.println("2) 24Mb, ресурсы (r1,r2), h");
        System.err.println("3) 53Mb, ресурсы (r2), l");
        freeRam = new Buffer(96);

        Resource r1 = new Resource("Ресурс №1");
        Resource r2 = new Resource("Ресурс №2");
        Resource[] resList1 = {r1, r2};
        Resource[] resList2 = {r2};


        Process proc1 = new Process(0, procNames[0], procMem[0], resList1, procPrior[0]);
        Process proc2 = new Process(1, procNames[1], procMem[1], resList1, procPrior[1]);
        Process proc3 = new Process(2, procNames[2], procMem[2], resList2, procPrior[2]);


        proc1.start();
        proc2.start();
        proc3.start();

        try {
            proc1.join();
            proc2.join();
            proc3.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread was interrupted!");
        }

        System.err.println("Работа программы завершена!");
    }
}
