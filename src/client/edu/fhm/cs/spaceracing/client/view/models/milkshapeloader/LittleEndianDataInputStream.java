package edu.fhm.cs.spaceracing.client.view.models.milkshapeloader;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wrappt einen InputStream auf Little-Endian-Format.
 * Milkshape3D-Models sind in Little-Endian gespeichert.
 * @author Thomas "HUFFMAN" Brunner
 */
public class LittleEndianDataInputStream extends FilterInputStream implements DataInput 
{
    private DataInputStream dataInputStream;

    /**
     * Erzeugt einen neuen LittleEndianDataInputStream.
     * @param inputStream der zu wrappende InputStream.
     */
    public LittleEndianDataInputStream(InputStream inputStream) 
    {
        super(inputStream);
        dataInputStream = new DataInputStream(inputStream);
    }

    
    /**
     * Liest ein Byte aus dem Stream.
     * @return das gelesene Byte.
     * @throws IOException das Byte konnte nicht gelesen werden.
     */
    @Override
	public int read() throws IOException 
    {
        return super.read();
    }

    
    /**
     * Liest ein Unsigned Byte aus dem Stream.
     * @return das gelesene Unsigned Byte.
     * @throws IOException das Byte konnte nicht gelesen werden.
     */
    public int readUnsignedByte() throws IOException 
    {
        return (read() & 0xFF);
    }

    
    /**
     * Liest ein Short aus dem Stream.
     * @return das gelesene Short.
     * @throws IOException das Short konnte nicht gelesen werden.
     */
    public short readShort() throws IOException 
    {
        int byte1 = (read() & 0xFF);
        int byte2 = (read() & 0xFF) << 8;
        return ((short) (byte1 | byte2));
    }

    
    /**
     * Liest ein Unsigned Short aus dem Stream.
     * @return das gelesene Unsigned Short.
     * @throws IOException das Short konnte nicht gelesen werden.
     */
    public int readUnsignedShort() throws IOException 
    {
        int byte1 = (read() & 0xFF);
        int byte2 = (read() & 0xFF) << 8;
        return (byte1 | byte2);
    }

    
    /**
     * Liest ein Int aus dem Stream.
     * @return das gelesene Int.
     * @throws IOException das Int konnte nicht gelesen werden.
     */
    public int readInt() throws IOException 
    {
        return (read() & 0xFF) |
               (read() & 0xFF) << 8 |
               (read() & 0xFF) << 16 |
               (read() & 0xFF) << 24;
    }

    
    /**
     * Liest ein Float aus dem Stream.
     * @return das gelesene Float.
     * @throws IOException das Float konnte nicht gelesen werden.
     */
    public float readFloat() throws IOException 
    {
        return Float.intBitsToFloat(readInt());
    }

    
    /**
     * Liest ein Long aus dem Stream.
     * @return das gelesene Long.
     * @throws IOException das Long konnte nicht gelesen werden.
     */
    public long readLong() throws IOException 
    {
        return (
                ((read() & 0xff)) |
                ((long) (read() & 0xff) << 8) |
                ((long) (read() & 0xff) << 16) |
                ((long) (read() & 0xff) << 24) |
                ((long) (read() & 0xff) << 32) |
                ((long) (read() & 0xff) << 40) |
                ((long) (read() & 0xff) << 48) |
                ((long) (read() & 0xff) << 56)
                );
    }

    
    /**
     * Liest ein Double aus dem Stream.
     * @return das gelesene Double.
     * @throws IOException das Double konnte nicht gelesen werden.
     */
    public double readDouble() throws IOException 
    {
        return Double.longBitsToDouble(readLong());
    }

    
    /**
     * Liest ein Array von Bytes aus dem Stream. 
     * Die Anzahl der gelesenen Bytes entspricht der Länge von b. 
     * @param b das Array, in welches die Bytes geschrieben werden.
     * @throws IOException die Bytes konnten nicht gelesen werden.
     */
    public void readFully(byte[] b) throws IOException 
    {
        readFully(b, 0, b.length);
    }

    
    /**
     * Liest len Bytes aus dem Stream.
     * @param b das Array, in welches die Bytes geschrieben werden.
     * @param off ein Offset in die Daten.
     * @param len die Anzahl der zu lesenden Bytes.
     * @throws IOException die Bytes konnten nicht gelesen werden. 
     */
    public void readFully(byte[] b, int off, int len) throws IOException 
    {
        while (len > 0) 
        {
            int bytesRead = read(b, off, len);
            if(bytesRead == -1) 
                throw new EOFException("Unexpected end of file");
            else 
            {
                len -= bytesRead;
                off += bytesRead;
            }
        }
    }

    /**
     * Versucht n Bytes im Stream zu überspringen.
     * @param n die Anzahl der zu überspringenden Bytes.
     * @return die Anzahl der tatsächlich übersprungenen Bytes.
     * @throws IOException die Bytes konnten nicht gelesen werden.
     */
    public int skipBytes(int n) throws IOException 
    {
        int bytesRead = 0;
        while(bytesRead < n) 
        {
            int read = read();
            if(read == -1) 
                break;
            else 
                bytesRead++;            
        }
        return bytesRead;
    }

    
    /**
     * Liest ein Boolean aus dem Stream.
     * @return das gelesene Boolean.
     * @throws IOException das Boolean konnte nicht gelesen werden.
     */
    public boolean readBoolean() throws IOException 
    {
        return readInt() != 0;
    }

    
    /**
     * Liest ein Byte aus dem Stream.
     * @return das gelesene Byte.
     * @throws IOException das Byte konnte nicht gelesen werden.
     */
    public byte readByte() throws IOException 
    {
        return (byte) read();
    }

    
    /**
     * Liest einen Char aus dem Stream.
     * @return der gelesene Char.
     * @throws IOException der Char konnte nicht gelesen werden.
     */
    public char readChar() throws IOException 
    {
        return (char) ((read() << 8) | (read() & 0xff));
    }

    /**
     * Liest eine Zeile aus dem Stream.
     * @deprecated Es sollte ein BufferedReader verwendet werden.
     * @return die gelesene Zeile.
     * @throws IOException die Zeile konnte nicht gelesen werden.
     */
    @Deprecated
    public String readLine() throws IOException 
    {
        return dataInputStream.readLine();
    }

    /**
     * Liest einen UTF-8-String aus dem Stream.
     * @return der gelesene String.
     * @throws IOException der String konnte nicht gelesen werden.
     */
    public String readUTF() throws IOException 
    {
        return dataInputStream.readUTF();
    }

    /**
     * Markiert die momentane Position im Stream.
     * Achtung: mark und reset werden nicht unterstützt!
     * @param readlimit (nicht benutzt)
     */
    @Override
	public synchronized void mark(int readlimit) 
    {
    }

    /**
     * Gibt zurück ob mark und reset unterstützt werden.
     * @return false, da mark und reset nicht unterstützt werden.
     */
    @Override
	public boolean markSupported() 
    {
        return false;
    }

    /**
     * Setzt den Stream auf die letzte Markierung zurück.
     * Achtung: mark und reset werden nicht unterstützt!
     * @throws IOException da reset nicht unterstützt wird.
     */
    @Override
	public synchronized void reset() throws IOException 
    {
        throw new IOException();
    }
}
