package utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.InputMismatchException;

/**
 * User: iiotep9huy
 * Date: 6/30/13
 * Time: 4:36 PM
 * Project: Coloring
 */
public class InputReader {

    private InputStream stream;
    private byte[] buf = new byte[1024];
    private int curChar;
    private int numChars;

    public InputReader(InputStream stream) {
        this.stream = stream;
    }

    public int read() {
        if (numChars == -1)
            throw new InputMismatchException();
        if (curChar >= numChars) {
            curChar = 0;
            try {
                numChars = stream.read(buf);
            } catch (IOException e) {
                throw new InputMismatchException();
            }
            if (numChars <= 0)
                return -1;
        }
        return buf[curChar++];
    }

    public BigInteger readLong() {
        StringBuilder sb = new StringBuilder();
        while (true) {
            int i = read();
            if (i >= '0' && i <= '9') {
                sb.append(i - '0');
            }
            else
                break;
        }
        BigInteger result = new BigInteger(sb.toString());
        return result;
    }

    public int readInt() {
        int c = read();
        while (isSpaceChar(c))
            c = read();
        int sgn = 1;
        if (c == '-') {
            sgn = -1;
            c = read();
        }
        int res = 0;
        do {
            if (c < '0' || c > '9')
                throw new InputMismatchException();
            res *= 10;
            res += c - '0';
            c = read();
        } while (!isSpaceChar(c));
        return res * sgn;
    }

	public double readDouble() {
		int c = read();
		while (isSpaceChar(c))
			c = read();

		StringBuilder stringBuilder = new StringBuilder();
		do {
			stringBuilder.append((char) c);
			c = read();
		} while (!isSpaceChar(c));

		return Double.parseDouble(stringBuilder.toString());
	}

    public static boolean isSpaceChar(int c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t' || c == -1;
    }
}
