
package edu.fhm.cs.spaceracing.net.udp;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import edu.fhm.cs.spaceracing.net.udp.internal.UDPBufferPool;

/**
 * Wrapper around ByteBuffers <br>
 * Transparently wraps several ByteBuffers as if you would only access one. <br>
 * When this object is destructed the ByteBuffers will be returned to the
 * UDPBufferPool <br>
 * 
 * You should call destroy in order to return all ByteBuffers. Otherwise they
 * will be held till finalize is invoked. Calling destroy invalidates this
 * Object!!
 * 
 * @author bernhard
 * 
 */
public class UDPUnserializer
{
	/**
	 * Initialize for one ByteBuffer
	 * 
	 * @param arg
	 */
	public UDPUnserializer(ByteBuffer arg)
	{
		pool = UDPBufferPool.getInstance();

		data = new ByteBuffer[1];
		data[0] = arg;
		index = 0;
	}

	/**
	 * Initialize for multiple ByteBuffers
	 * 
	 * @param max_buffers
	 */
	public UDPUnserializer(int max_buffers)
	{
		pool = UDPBufferPool.getInstance();

		data = new ByteBuffer[max_buffers];
		for (int i = 0; i < max_buffers; ++i)
		{
			data[i] = null;
		}
		index = 0;
	}

	/**
	 * Return all ByteBuffers to the UDPBufferPool and invalidate this object
	 * 
	 */
	public void destroy()
	{
		for (int i = 0; i < data.length; ++i)
		{
			if (data[i] != null)
			{
				pool.put(data[i]);
				data[i] = null;
			}
		}
	}

	/**
	 * Set a ByteBuffer at a specified position. If there is already a
	 * ByteBuffer set it will be returned to the pool.
	 * 
	 * @param index
	 *            Position should be smaller then the initial size. Starting at
	 *            0
	 * @param b
	 *            ByteBuffer which should be added and managed!
	 * @exception IndexOutOfBoundsException
	 *                Thrown if index >= max_buffers
	 */
	public void setBufferAt(int index, ByteBuffer b)
	{
		if (index >= data.length)
		{
			throw new IndexOutOfBoundsException("Illegal Index!");
		}
		if (data[index] != null)
		{
			pool.put(data[index]);
		}
		data[index] = b;
	}

	/**
	 * Checks if every index has a ByteBuffer set
	 * 
	 * @return true if yes otherwise false
	 */
	public boolean checkAllSet()
	{
		for (int i = 0; i < data.length; ++i)
		{
			if (data[i] == null)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if requested bytes are remaining in the ByteBuffer. If yes it will
	 * do nothing otherwise it will increment the internal index to use the next
	 * ByteBuffer <br>
	 * BufferUnderFlowException is thrown if following condition is true: <br> -
	 * No ByteBuffers are remaining <br> - If some bytes are left in the current
	 * ByteBuffer but ByteBuffer.remaing() < size <br>
	 * 
	 * @param size
	 *            Requested Bytes
	 */
	private void ensureBuffer(int size)
	{
		if (!data[index].hasRemaining())
		{ // Hat der derzeitige Puffer noch Daten?
			++index;
			if (data.length <= index)
			{
				throw new BufferUnderflowException();
			}
		}

		if (data[index].remaining() < size)
		{ // Wir erlauben keine unserialisierung über ByteBuffer Grenzen!
			throw new BufferUnderflowException();
		}
	}

	public boolean hasRemaining()
	{
		if (index >= data.length)
		{
			return false;
		}
		if (data[index].hasRemaining())
		{
			return true;
		}
		else
		{
			++index;
			return hasRemaining();
		}
	}

	public byte get()
	{
		ensureBuffer(1); // Byte.SIZE / Byte.SIZE
		return data[index].get();
	}

	public short getShort()
	{
		ensureBuffer(Short.SIZE / Byte.SIZE);
		return data[index].getShort();
	}

	public char getChar()
	{
		ensureBuffer(Character.SIZE / Byte.SIZE);
		return data[index].getChar();
	}

	public int getInt()
	{
		ensureBuffer(Integer.SIZE / Byte.SIZE);
		return data[index].getInt();
	}

	public long getLong()
	{
		ensureBuffer(Long.SIZE / Byte.SIZE);
		return data[index].getLong();
	}
	
	public double getDouble()
	{
		ensureBuffer(Double.SIZE / Byte.SIZE);
		return data[index].getDouble();
	}
	
	public void printCurrentByteBuffer () {
		if (data == null) {
			// TODO bernhard: bessere Exception (FindBugs fix by stf)
			throw new RuntimeException("No ByteBuffer!");
		}
		if (index >= data.length || data[index] == null) {
			// TODO bernhard: bessere Exception (FindBugs fix by stf)
			throw new RuntimeException("No Valid ByteBuffer at current position! Pos.:" + index + " Max.: " + data.length);
		}
		final int positionSave = data[index].position();
		
		System.out.println ("ByteBuffer Content:");
		System.out.println ("Current position: " + positionSave);
		data[index].position(0);
		
		for (int i = 1; data[index].hasRemaining(); ++i) {
			String val = Integer.toHexString((0xFF & data[index].get()));
			if (val.length() == 1) {
				System.out.print('0');
			}
			System.out.print(val);
			if (data[index].position() == positionSave) {
				System.out.println("%");
			}
			System.out.print(' ');
			if (i % 16 == 0) {
				System.out.println();
			}
		}
		System.out.println();
		
		data[index].position(positionSave);
	}

	@Override
	protected void finalize() throws Throwable
	{
		destroy(); // Free resources if they haven't!
		super.finalize();
	}

	private final UDPBufferPool pool;

	private ByteBuffer data[];

	private int index;
}
