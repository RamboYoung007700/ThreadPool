package thread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPool {
	public List<Runnable> tasks = new LinkedList<>();
	private int PoolSize = 3;
	private Lock lock = new ReentrantLock();
	private Condition condition = lock.newCondition();
	//构造器
	public ThreadPool() {
		for (int i = 0; i < PoolSize; i++) {
			log("启动线程"+i);
			new Worker("线程"+i).start();
		}
	}
	//创建日志方法
	private static void log(String msg) {
		System.out.printf("%s,%s,%s,%n",now(),Thread.currentThread().getName(),msg);
	}

	private static String now() {
		// TODO Auto-generated method stub
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	// 定义内部类，开线程用。
	private class Worker extends Thread {
		Runnable task;

		public Worker(String name) {
			super(name);
		}

		public void run() {
			while (true) {
				try {
					lock.lock();
					log("加锁完成");
					while (tasks.isEmpty()) {
						log("任务容器空，进入等待");
						condition.await();
						log("正在等待");
						}
					log("被唤醒");
					task = tasks.remove(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					lock.unlock();
					log("解锁完成");
				}
				log("执行任务");
				task.run();
			}
		}
	}
	
	//添加任务
	public void addtask(Runnable r) {
		try {
			lock.lock();
			log("加锁完成，添加任务中");
			tasks.add(r);
			log("任务添加完毕");
			condition.signalAll();
			log("唤醒等待中的线程们");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			log("任务添加完成，解锁！");
			lock.unlock();
		}
	}

}
