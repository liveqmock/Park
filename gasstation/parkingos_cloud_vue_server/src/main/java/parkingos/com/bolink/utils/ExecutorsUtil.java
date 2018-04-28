package parkingos.com.bolink.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorsUtil {

	private static ExecutorService threadpool=Executors.newCachedThreadPool();
	
	public static ExecutorService getExecutorPool(){
		return threadpool;
	}
}
