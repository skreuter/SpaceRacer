
package edu.fhm.cs.spaceracing.net.udp;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

import edu.fhm.cs.spaceracing.net.udp.internal.UDPBufferPool;
import edu.fhm.cs.spaceracing.net.udp.internal.UDPFeatureManager;

/**
 * Encapsulates several ByteBuffers if neccessary, so it is responsible for fragmentating packets.
 * The method-names should almost be the same as used in ByteBuffer
 * 
 * @author bernhard
 *
 */
public class UDPSerializer
{

	/**
	 * Iterator for iterating over the current ByteBuffers
	 * 
	 * @author bernhard
	 * 
	 */
	public class Iterator
	{

		Iterator()
		{
			iterIndex = 0;
		}

		public int maxNumberOfBuffers()
		{
			return index + 1;
		}

		public boolean hasNext()
		{
			return iterIndex <= index;
		}

		public ByteBuffer next()
		{
			return data[iterIndex++];
		}

		private int iterIndex;
	}

	/**
	 * Initializes the Serializer with one ByteBuffer, which shoulld be enough
	 * for most cases
	 * 
	 */
	public UDPSerializer()
	{
		pool = UDPBufferPool.getInstance();

		data = new ByteBuffer[1];
		data[0] = pool.get();
		index = 0;

		initBuffer();
	}

	/**
	 * Returns the managed ByteBuffers to the UDPBufferPool
	 * 
	 */
	public void destroy()
	{
		for (int i = 0; i <= index; ++i)
		{
			if (data[i] != null)
			{
				pool.put(data[i]);
				data[i] = null;
			}
		}
		data = null;
		index = 0;
	}
	
	public boolean isValid ()
	{
		return data != null;
	}
	
	/**
	 * Aus Java 1.6 geklaut :)
	 * 
     * Copies the specified array, truncating or padding with nulls (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>null</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     * The resulting array is of the class <tt>newType</tt>.
     *
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @param newType the class of the copy to be returned
     * @return a copy of the original array, truncated or padded with nulls
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @throws ArrayStoreException if an element copied from
     *     <tt>original</tt> is not of a runtime type that can be stored in
     *     an array of class <tt>newType</tt>
     * @since 1.6
     */
	@SuppressWarnings("unchecked")
	private <T> T[] copyOf(T[] original, int newLength)
	{
		Class newType = original.getClass();
        T[] copy = (newType == Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }


	/**
	 * Ensures the requested number of bytes can be writen to the current
	 * ByteBuffer
	 * 
	 * @param size
	 */
	private void ensureSpace(int size)
	{
		if (size > data[index].remaining())
		{
			if (data.length == index + 1)
			{
				data = copyOf(data, data.length > 1 ? data.length * 3 / 2
						: data.length * 2);
			}
			++index;
			data[index] = pool.get();
			initBuffer();
		}
	}

	/**
	 * Skips enough Bytes for the header but doesn't write any data for it!
	 * 
	 */
	private void initBuffer()
	{
		// Der Header wird dann im Manager geschrieben
		data[index].position(UDPFeatureManager.HEADER_SIZE); 
	}

	public Iterator getBufferIterator()
	{
		return new Iterator();
	}

	public void put(byte arg)
	{
		ensureSpace(1); // Byte.SIZE / Byte.SIZE
		data[index].put(arg);
	}

	public void putShort(short arg)
	{
		ensureSpace(Short.SIZE / Byte.SIZE);
		data[index].putShort(arg);
	}

	public void putChar(char arg)
	{
		ensureSpace(Character.SIZE / Byte.SIZE);
		data[index].putChar(arg);
	}

	public void putInt(int arg)
	{
		ensureSpace(Integer.SIZE / Byte.SIZE);
		data[index].putInt(arg);
	}

	public void putLong(long arg)
	{
		ensureSpace(Long.SIZE / Byte.SIZE);
		data[index].putLong(arg);
	}
	
	public void putDouble(double arg)
	{
		ensureSpace(Double.SIZE / Byte.SIZE);
		data[index].putDouble(arg);
	}

	@Override
	protected void finalize() throws Throwable
	{
		destroy();
		super.finalize();
	}

	private ByteBuffer data[];

	private int index;

	private final UDPBufferPool pool;
}
