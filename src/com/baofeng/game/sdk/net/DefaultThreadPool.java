
/*
 * Copyright 2011 meiyitian
 * Blog  :http://www.cnblogs.com/meiyitian
 * Email :haoqqemail@qq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.baofeng.game.sdk.net;

 
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 线程池 、缓冲队列
 * @author sailor
 *
 */
public class DefaultThreadPool {
	/**
	 * BaseRequest任务队列
	 */
	static ArrayBlockingQueue<Runnable> blockingQueue= new ArrayBlockingQueue<Runnable>(15);
	/**
	 * 线程池
	 */
	public static AbstractExecutorService pool = new ThreadPoolExecutor(10,20,15L,TimeUnit.SECONDS,
			blockingQueue,new ThreadPoolExecutor.DiscardOldestPolicy());
	
	private  static DefaultThreadPool instance = null;
	public  static DefaultThreadPool getInstance(){
		if(instance == null){
			instance = new DefaultThreadPool();
		} 
		return instance;
	}
	
	public void execute(Runnable r){
		if(pool == null){
			if(blockingQueue == null)
				blockingQueue = new ArrayBlockingQueue<Runnable>(15);
			pool = new ThreadPoolExecutor(10,20,15L,TimeUnit.SECONDS,
					blockingQueue,new ThreadPoolExecutor.DiscardOldestPolicy());
		}
		pool.execute(r);
	}
	/**
	 * 关闭，并等待任务执行完成，不接受新任务
	 */
	public static  void shutdown(){
		if(pool!=null){
			pool.shutdown();
		}
	}
	
	/**
	 * 关闭，立即关闭，并挂起所有正在执行的线程，不接受新任务
	 */
	public  static void shutdownRightnow(){
		if(pool!=null){
			//List<Runnable>  tasks =pool.shutdownNow();
			pool.shutdownNow();
			try {
				//设置超时极短，强制关闭所有任务
				pool.awaitTermination(1, TimeUnit.MICROSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
//			for(Runnable   task:tasks){
//				task.
//			}
		}
	}
	
	
	public  static void removeTaskFromQueue(){
		//blockingQueue.contains(o);
	}
}
