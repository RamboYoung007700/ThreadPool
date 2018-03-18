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
	//������
	public ThreadPool() {
		for (int i = 0; i < PoolSize; i++) {
			log("���߳�"+i);
			new Worker("�߳�"+i).start();
		}
	}
	//������־����
	private static void log(String msg) {
		System.out.printf("%s,%s,%s,%n",now(),Thread.currentThread().getName(),msg);
	}

	private static String now() {
		// TODO Auto-generated method stub
		return new SimpleDateFormat("HH:mm:ss").format(new Date());
	}

	// �����ڲ��࣬���߳��á�
	private class Worker extends Thread {
		Runnable task;

		public Worker(String name) {
			super(name);
		}

		public void run() {
			while (true) {
				try {
					lock.lock();
					log("�������");
					while (tasks.isEmpty()) {
						log("���������գ�����ȴ�");
						condition.await();
						log("���ڵȴ�");
						}
					log("������");
					task = tasks.remove(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					lock.unlock();
					log("�������");
				}
				log("ִ������");
				task.run();
			}
	}
	
	//�������
	public void addtask(Runnable r) {
		try {
			lock.lock();
			log("������ɣ����������");
			tasks.add(r);
			log("����������");
			condition.signalAll();
			log("���ѵȴ��е��߳���");
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			log("���������ɣ�������");
			lock.unlock();
		}
	}

}
