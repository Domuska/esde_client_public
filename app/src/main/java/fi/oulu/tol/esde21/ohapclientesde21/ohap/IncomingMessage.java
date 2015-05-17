package fi.oulu.tol.esde21.ohapclientesde21.ohap;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Created by Domu on 13-May-15.
 */
public class IncomingMessage {

    /**
     * The internal buffer. It is reserved in the readExactly() method.
     */
    private byte[] buffer;

    /**
     * The position where the next byte should be taken from.
     */
    private int position;

    /**
     * Character set used to convert strings.
     */
    private final Charset charset = Charset.forName("UTF-8");

    private final static String TAG = "IncomingMessage";


    public boolean readFrom(InputStream is){

        // http://stackoverflow.com/questions/12139755/how-to-convert-byte-array-to-integer


        Log.d(TAG, "Starting readFrom, position is " + position);
        byte[] readResult;
        position = 0;

        try {
            readResult = readExactly(is, 2);

            //get the first and second byte from the result array
            int firstByte = readResult[0] & 0xFF;
            int secondByte = readResult[1] & 0xFF;
            //int lsb = readResult[0] & 0xFF;
            //int msb = readResult[1] & 0xFF;
            //int readLength = (msb << 8) + lsb;

            // first byte, 8 bit shift to left, second byte = length
            int readLength = (firstByte << 8) + secondByte;
            Log.d(TAG, "Length of the incoming message: " + readLength);
            buffer = readExactly(is, readLength);
            return true;

        }
        catch(EOFException e){
            Log.d(TAG, "networking stopped");
            return false;
        }
        catch (IOException ie){
            //TODO: handle the exception in a smart way
            Log.d(TAG, "IO exception in readFrom method! " + ie.toString());
            return false;

        }

    }

    public boolean binary8(){

        int value = buffer[position];

        position++;
        if(value > 0)
            return true;
        else
            return false;

    }

    public byte integer8(){
        Log.d(TAG, "Reading integer8...");
        Byte b = new Byte (buffer[position]);
        position ++;
        return b.byteValue();

    }

    public int integer16(){

        //lue alta miten tämä toimii, sama idea
        int firstByte = getNextByte();
        int secondByte = getNextByte();
        int result = (firstByte << 8) + secondByte;

        return result;

    }


    public int integer32(){

        //haetaan neljään muuttujaan arvot bufferista kyseisistä positioneista
        int firstByte = getNextByte();
        int secondByte = getNextByte();
        int thirdByte = getNextByte();
        int fourthByte = getNextByte();

        /*System.out.println("byte 1: " + firstByte);
        System.out.println("byte 2: " + secondByte);
        System.out.println("byte 3: " + thirdByte);
        System.out.println("byte 4: " + fourthByte);

        String str = Integer.toString(firstByte) + secondByte + thirdByte + fourthByte;
        System.out.println(str);*/

        // yhdistetään 4 muuttujaa (tavua) yheksi numeroksi, firstBytessä on isoimmat numerot
        // joten ekana bittikuvaan ne, niitä siirretään vasemmalle 24 paikkaa, tämän
        // jälkeen seuraavat 8 bittiä laitetaan bittikuvaan ja niitä siirretään 16 paikkaa
        // vasemmalle jne
        return (firstByte << 24) + (secondByte << 16) + (thirdByte << 8) + fourthByte;
    }

    // helper method
    private int getNextByte(){

        int i = buffer[position] & 0xFF;
        position++;

        return i;
    }



    public double decimal64() {

        long result = 0;

        // samaan tapaan kuin yllä, tehdään aina bittikuvalle shiftaus vasemmalle 8 paikkaa
        // ja lisätään 8 seuraavaa bittiä bittikarttaan. Tässä vain luodaan kerralla
        // koko longi näin, eikä pistetä erikseen inttimuuttujiin jotka sitten lopuksi
        // yhdistetään niinkuin yllä
        for(int i = 1; i  <= 8; i++){

            result = (result << 8) + getNextByte();
        }

        //System.out.println(result);
        //System.out.println(Double.longBitsToDouble(result));
        return Double.longBitsToDouble(result);

    }

    public String text() {

        // get the length of the string from the buffer's next 2 bytes
        int textLength = (getNextByte() << 8) + getNextByte();

        // a new array for the string's byte representation
        byte[] stringBytes = new byte[textLength];

        //copy the length amount of bytes from buffer to the array
        for(int i = 0; i < textLength; i++){

            stringBytes[i] = buffer[position];
            position++;
        }

        //System.out.println(new String(stringBytes, charset));

        //return the string as new string with the charset attached
        return new String(stringBytes, charset);
    }

    /**
     * Reads the specified amount of bytes from the given InputStream.
     *
     * @param inputStream the InputStream from which the bytes are read
     * @param length the amount of bytes to be read
     * @return the byte array of which length is the given length
     * @throws IOException when the actual read throws an exception
     */
    private static byte[] readExactly(InputStream inputStream, int length) throws IOException {
        byte[] bytes = new byte[length];
        int offset = 0;
        while (length > 0) {
            int got = inputStream.read(bytes, offset, length);
            if (got == -1) {
                Log.d(TAG, "throwing end of message exception");
                throw new EOFException("End of message input.");
            }
            offset += got;
            length -= got;
        }
        return bytes;
    }

}
