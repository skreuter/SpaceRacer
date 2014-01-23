
package edu.fhm.cs.spaceracing.net.udp.internal;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * Goal is to minimize the allocation and deallocation of ByteBuffer-objects as those may be allocated using
 * allocateDirect. If the pool is empty additional buffers will be allocated. 
 * If a ByteBuffer is taken from the pool it should returned later. 
 * Using the BufferPool is safe across different threads.<br>
 * The caller must ensure that a ByteBuffer is only returned once otherwise undefined behaviour will occur! <br>
 * You can also add ByteBuffers, which were allocated outside of the pool. The put-method will check if 
 * the buffer is big enough otherwise it is discarded.
 *  
 * @author bernhard
 * 
 */
public class UDPBufferPool
{

	private final static int BUFFER_SIZE = 1400;
	
	private static  UDPBufferPool udpBufferpool = new UDPBufferPool(BUFFER_SIZE);

	public static UDPBufferPool getInstance()
	{
		return udpBufferpool;
	}
	
	public UDPBufferPool (int initialbuffers)
	{
		pool = new LinkedList<ByteBuffer>();
		for (int i = 0; i < initialbuffers; ++i)
		{
			pool.add(ByteBuffer.allocateDirect(BUFFER_SIZE));
		}
	}

	/**
	 * Returns a ByteBuffer from the pool or creates a new one if the pool is
	 * empty
	 * 
	 * @return
	 */
	public synchronized ByteBuffer get()
	{
		if (!pool.isEmpty())
		{
			return pool.remove();
		}
		else
		{
			System.out.println("ByteBufferPool: Allocating additional buffer!");

			return ByteBuffer.allocateDirect(BUFFER_SIZE);
		}
	}

	/**
	 * Returns a ByteBuffer to the pool the caller must ensure that the object
	 * isn't added twice!
	 * 
	 * @param arg
	 */
	public synchronized void put(ByteBuffer arg)
	{
		if (arg == null || arg.capacity() != BUFFER_SIZE)
		{
			return;
		}
		arg.clear();
		pool.add(arg);
	}

	private final LinkedList<ByteBuffer> pool;

}
