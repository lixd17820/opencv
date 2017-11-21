package com.my.opencv.cv;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestScheduledThreadPoolExecutor {

	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static void main(String[] args) {
		// ScheduledExecutorService exec=Executors.newScheduledThreadPool(1);
		ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
		/**
		 * ÿ��һ��ʱ���ӡϵͳʱ�䣬����Ӱ���<br/>
		 * ������ִ��һ���ڸ��ʼ�ӳٺ��״����õĶ��ڲ���������������и�����ڣ�<br/>
		 * Ҳ���ǽ��� initialDelay ��ʼִ�У�Ȼ����initialDelay+period ��ִ�У�<br/>
		 * ������ initialDelay + 2 * period ��ִ�У��������ơ�
		 */
		exec.scheduleAtFixedRate(new Runnable() {
			public void run() {
				System.out.println(format.format(new Date()));
			}
		}, 1000, 5000, TimeUnit.MILLISECONDS);

		// ��ʼִ�к�ʹ����쳣,next���ڽ���������
		exec.scheduleAtFixedRate(new Runnable() {
			public void run() {
				System.out
						.println("RuntimeException no catch,next time can't run");
				throw new RuntimeException();
			}
		}, 1000, 5000, TimeUnit.MILLISECONDS);

		// ��Ȼ�׳��������쳣,����������,next���ڼ�������
		exec.scheduleAtFixedRate(new Runnable() {
			public void run() {
				try {
					throw new RuntimeException();
				} catch (Exception e) {
					System.out.println("RuntimeException catched,can run next");
				}
			}
		}, 1000, 5000, TimeUnit.MILLISECONDS);

		/**
		 * ������ִ��һ���ڸ��ʼ�ӳٺ��״����õĶ��ڲ�����<br/>
		 * �����ÿһ��ִ����ֹ����һ��ִ�п�ʼ֮�䶼���ڸ���ӳ١�
		 */
		exec.scheduleWithFixedDelay(new Runnable() {
			public void run() {
				System.out.println("scheduleWithFixedDelay:begin,"
						+ format.format(new Date()));
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("scheduleWithFixedDelay:end,"
						+ format.format(new Date()));
			}
		}, 1000, 5000, TimeUnit.MILLISECONDS);

		/**
		 * ������ִ���ڸ��ӳٺ����õ�һ���Բ�����
		 */
		exec.schedule(new Runnable() {
			public void run() {
				System.out.println("The thread can only run once!");
			}
		}, 5000, TimeUnit.MILLISECONDS);
	}

}
