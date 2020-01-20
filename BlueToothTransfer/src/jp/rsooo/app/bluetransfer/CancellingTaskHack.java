package jp.rsooo.app.bluetransfer;

import static jp.rsooo.app.bluetransfer.Constants.TAG;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import android.util.Log;



public class CancellingTaskHack implements Cancelable{

	private final ExecutorService mExec;
	private final Cancelable mTask;
	private final long mTimeout;
	private final TimeUnit mUnit;

	public CancellingTaskHack(ExecutorService exec, Cancelable task) {
		this(exec, task, 0, null);
	}

	/**
	 * キャンセル可能なタスクを管理するクラス
	 * @param exec タスクの実行を管理するクラス
	 * @param task 実行するタスク
	 * @param timeout タイムアウトまでの時間
	 * @param unit タイムアウトの時間の単位
	 */
	public CancellingTaskHack(ExecutorService exec, Cancelable task, long timeout,
			TimeUnit unit) {
		mExec = exec;
		mTask = task;
		mTimeout = timeout;
		mUnit = unit;
	}
	
	@Override
	public void cancel() {
		mTask.cancel();		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Future<?> future = null;
		try{
			Log.i("debug", "future task start");
			future = mExec.submit(mTask);
			waitForCompletionOrTimeout(future);
			Log.i("debug", "future task ");

		}catch(InterruptedException e){
			mTask.cancel();
		}catch(ExecutionException e){
			Log.e(TAG, "task failed", e);
			mTask.cancel();
		}
	}

	private void waitForCompletionOrTimeout(Future<?> future)
			throws InterruptedException, ExecutionException {
		if (mTimeout <= 0) {
			future.get();
			return;
		}
		try {
			future.get(mTimeout, mUnit);
		} catch (TimeoutException e) {
			mTask.cancel();
		}
	}

}
