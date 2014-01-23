
package edu.fhm.cs.spaceracing.net.tcp;

import java.util.LinkedList;

/**
 * Synchronized Fifo
 * 
 * @author bernhard
 * 
 * @param <T>
 */
public class SynFIFO<T>
{

	public SynFIFO()
	{
		que = new LinkedList<T>();
	}

	public synchronized void add(T obj)
	{
		que.add(obj);
	}

	/**
	 * Gets the first element on the que
	 * 
	 * @return Element or null if empty
	 */
	public synchronized T take()
	{
		if (que.isEmpty()) {
			return null;
		} else {
			return que.remove();
		}
	}

	private final LinkedList<T> que;
}
