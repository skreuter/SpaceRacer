package edu.fhm.cs.spaceracing.client.view.models.milkshapeloader;

import java.io.DataInput;
import java.io.IOException;

/**
 * Ein Milkshape3D Header.
 * @author Thomas "HUFFMAN" Brunner
 */
class MS3DHeader 
{
    private static final String MAGIC_NUMBER = "MS3D000000";
    private final int version;


    MS3DHeader(DataInput input) throws IOException
    {
        String header = MS3DDecoder.decodeZeroTerminatedString(input, 10);
        if (!MAGIC_NUMBER.equals(header))
            throw new IOException("Kein MilkShape3D model!");

        version = input.readInt();
    }
    
    int getVersion() 
    {
        return version;
    }
}
