package fi.oulu.tol.esde21.ohapclientesde21.ohap;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by Domu on 13-May-15.
 */
public class OutgoingMessage {

    /**
     * The internal buffer. It will be grown if the message do not fit to it.
     */
    private byte[] buffer = new byte[256];

    /**
     * The position where the next byte should be appended. The initial position
     * skips the space reserved for the message length.
     */
    private int position = 2;

    /**
     * Character set used to convert strings.
     */
    private final Charset charset = Charset.forName("UTF-8");
    private final String TAG = "OutgoingMessage";

    /**
     * Ensures that the internal buffer have room for the specified amount of
     * bytes. Grows the buffer when needed by doubling its size.
     *
     * @param appendLength the amount of bytes to be appended
     */
    private void ensureCapacity(int appendLength) {
        if (position + appendLength < buffer.length)
            return;
        int newLength = buffer.length * 2;
        while (position + appendLength >= newLength)
            newLength *= 2;
        buffer = Arrays.copyOf(buffer, newLength);
    }

    //TODO: lisataan ensureCapacity -kutsut jokapaikkaan
    public void writeTo(OutputStream os){

       // http://stackoverflow.com/questions/1936857/convert-integer-into-byte-array-java

        Log.d(TAG, "starting writeTo, position: " + position);

        int length = position -2;
        buffer[0] = (byte) (length >> 8);
        buffer[1] = (byte) length;

        System.out.println("writeTo method, length: " + length);

        try {
            os.write(buffer, 0, length + 2);
        }
        catch(IOException e){
            Log.d(TAG, "caught exception in OutgoingMessage writeTo");
        }
    }

    public OutgoingMessage binary8(boolean b){

        Log.d(TAG, "binary8: " + b);

        Integer integer;
        ensureCapacity(1);

        /*if (b == true)
            integer = new Integer (1);
        else
            integer = new Integer (0);*/
        byte by;
        if (b)
            by = 1;
        else
            by = 0;

        Log.d(TAG, "binary8 byte: " + by);

        buffer[position] = by;

        /*if(b) {
            buffer[position] = (byte) 1;
            System.out.println(buffer[position]);
        }
        else
            buffer[position] = (byte) 0;
*/
        position++;

        return this;
    }

    public OutgoingMessage integer8(int i){

        Log.d(TAG, "integer8: " + i);

        ensureCapacity(1);
        Integer integer = new Integer(i);

        //buffer[position] = integer.byteValue();
        buffer[position] = (byte) i;
        position++;

        return this;
    }

    public OutgoingMessage integer16(int i){

        Log.d(TAG, "integer16: " + i);

        ensureCapacity(2);

        // ekat 8 bittia bufferiin, bittisiirros oikealle
        // (puotetaan intin bittikuvajaisesta 8 vasemmanpuolisinta bittia)
        buffer[position] = (byte) (i >> 8);
        position ++;

        // loput 8 tavua bufferin seuraavaan indeksiin
        buffer[position] = (byte) i;
        position++;


        return this;
    }


    public OutgoingMessage integer32(long i){

        ensureCapacity(4);

        Log.d(TAG, "integer32: " + i);


        //jaetaan sisaantuleva intti neljaan palaan bittishiftauksella

        //siirretaan i:n 25-32 bitit oikealle 24 paikkaa ja tallennetaan bufferiin
        //huom, tehdaan castaus byteksi numerolle myos (tietty kun tallennetaan bytebufferiin)
        buffer[position] = (byte) (i >> 24);
        position ++;

        //siirretaan 24-17 bitit oikealle 16 paikkaa ja tallennetaan bufferiin
        buffer[position] = (byte) (i >> 16);
        position++;

        //16-9 bitit oikealle 8 paikkaa ja laitetaan bufferiin
        buffer[position] = (byte) (i >> 8);
        position++;

        //8-1 bitit bufferiin
        buffer[position] = (byte) i;
        position++;

        return this;
    }

    public OutgoingMessage decimal64(double d){

        Log.d(TAG, "decimal64: " + d);

        ensureCapacity(8);

        long longValue = Double.doubleToLongBits(d);

        // samaan tapaan kuin ylla, joka kierroksella longista otetaan 8 bittia, siirretaan
        // ne bittikuvassa 8 ensimmaiseksi bitiksi taman jalkeen castataan numero byteksi
        // eli otetaan siita 8 ensimmaista bittia (jotka on juuri siihe siirretty), ja nama
        // tallennetaan bufferiin.

        // help for solution gotten from:
        // stackoverflow.com/questions/13071777/convert-double-to-byte-array
        for (int i = 0; i < 8; i++){

            buffer[position] = (byte) (longValue >> ((7-i) * 8) & 0xFF);
            position++;
        }

        return this;
    }

    public OutgoingMessage text(String message) {

        Log.d(TAG, "text: " + message);


        byte[] stringBytes;
        // copy message's byte representation to stringBytes array
        stringBytes = message.getBytes(charset);
        ensureCapacity(stringBytes.length+2);
        Log.d(TAG, "text length: " + stringBytes.length);

        // put string's length into buffer as 2 bytes
        buffer[position] = (byte) (stringBytes.length >> 8);
        position ++;
        buffer[position] = (byte) stringBytes.length;
        position ++;

        // copy the stringBytes to buffer index by index
        for (int i = 0; i < stringBytes.length; i++){

            buffer[position] = stringBytes[i];
            //System.out.println(buffer[position]);
            position++;
        }

        return this;
    }
}
