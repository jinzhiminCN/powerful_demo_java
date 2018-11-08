package basicDemo;

public class ThreadLocalDemo {

    public static void testThreadLocalSetGet() {
        Son p = new Son();
        System.out.println(p);
        Thread t = new Thread(new Runnable() {
            public void run() {
                ThreadLocal<Son> threadLocal = new ThreadLocal<>();
                System.out.println(threadLocal);
                threadLocal.set(p);
                System.out.println(threadLocal.get());
                threadLocal.remove();
                try {
                    threadLocal.set((Son) p.clone());
                    System.out.println(threadLocal.get());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                System.out.println(threadLocal);
            }
        });
        t.start();
    }

    public static void testMultThreadLocal(){
        Thread t = new Thread(new Runnable(){
            public void run(){
                ThreadLocal<Son> threadLocal1 = new ThreadLocal<>();
                threadLocal1.set(new Son());
                System.out.println(threadLocal1.get());
                ThreadLocal<Son> threadLocal2 = new ThreadLocal<>();
                threadLocal2.set(new Son());
                System.out.println(threadLocal2.get());
            }});
        t.start();
    }

    public static void main(String[] args) {
//        testThreadLocalSetGet();
        testMultThreadLocal();
    }
}

class Son implements Cloneable {
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}