package tools;

import java.util.concurrent.Callable;

public class Task<V> implements Callable<V> {
	protected int n = 0;
	protected int co = 0;
	public Task(int n, int co) {
		this.n = n;
		this.co = co;
	}
	@Override
    public V call() throws Exception {
       return null;
    }
}
