package edu.fhm.cs.spaceracing.client.view.models.milkshapeloader;

import java.io.DataInput;
import java.io.IOException;


/**
 * Utility-Klasse zum Dekodieren von MilkShape3D-Daten.
 * @author Thomas "HUFFMAN" Brunner
 */
class MS3DDecoder
{
	/**
	 * Liest einen mit 0 terminierten String ein.  
	 * @param input die Datenquelle.
	 * @param maximumLength die Stringlänge, die komplett eingelesen wird.
	 * @return den String.
	 * @throws IOException der String konnte nicht gelesen werden.
	 */
    static String decodeZeroTerminatedString(DataInput input, int maximumLength) throws IOException 
    {
        boolean zeroEncountered = false;
        StringBuffer stringBuffer = new StringBuffer();
        for (int c = 0; c < maximumLength; c++) 
        {
            int readByte = input.readUnsignedByte();
            
            // Auch nach der 0 weiterlesen, um den Stream vorzuspulen
            if (!zeroEncountered && readByte != 0)
                stringBuffer.append((char)readByte);
            else 
                zeroEncountered = true;
        }

        return stringBuffer.toString();
    }
}
