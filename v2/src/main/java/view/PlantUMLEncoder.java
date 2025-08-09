package view;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;

public class PlantUMLEncoder {
    private static final String CHARSET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_";

    public static String encode(String text) {
        byte[] data = text.getBytes(StandardCharsets.UTF_8);

        Deflater deflater = new Deflater(8, true);
        deflater.setInput(data);
        deflater.finish();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }

        deflater.end();
        byte[] deflatedData = outputStream.toByteArray();
        return encode64(deflatedData);
    }

    private static String encode64(byte[] data) {
        StringBuilder encoded = new StringBuilder();
        int i = 0;
        while (i < data.length) {
            int b1 = data[i++] & 0xFF;
            if (i == data.length) {
                encoded.append(CHARSET.charAt(b1 >> 2));
                encoded.append(CHARSET.charAt((b1 & 0x3) << 4));
                break;
            }
            int b2 = data[i++] & 0xFF;
            if (i == data.length) {
                encoded.append(CHARSET.charAt(b1 >> 2));
                encoded.append(CHARSET.charAt(((b1 & 0x3) << 4) | (b2 >> 4)));
                encoded.append(CHARSET.charAt((b2 & 0xF) << 2));
                break;
            }
            int b3 = data[i++] & 0xFF;
            encoded.append(CHARSET.charAt(b1 >> 2));
            encoded.append(CHARSET.charAt(((b1 & 0x3) << 4) | (b2 >> 4)));
            encoded.append(CHARSET.charAt(((b2 & 0xF) << 2) | (b3 >> 6)));
            encoded.append(CHARSET.charAt(b3 & 0x3F));
        }
        return encoded.toString();
    }
}
