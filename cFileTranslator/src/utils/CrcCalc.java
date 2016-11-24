package utils;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author user
 */
public class CrcCalc {
    
    public static int TransmitBufCrc16(byte [] buf, int length, int offset)
    {
        byte array[] = null;        
        array = new byte[buf.length];
        System.arraycopy(buf, offset, array, 0, length);
        return crc16(array, length);       
    }
    
    public static int TransmitBufCrc8(byte [] buf, int length)
    {
        byte array[] = null;        
        array = new byte[buf.length];
        System.arraycopy(buf, 1, array, 0, length);
        return crc8(array, length);       
    }
    
    public static int crc16(final byte[] buffer, int length) {
        int crc = 0xFFFF;
        int crc_and = 0;

        for (int j = 0; j < length; j++) {
            crc ^= ((buffer[j] & 0xff) << 8) & 0xffff;//byte to int, trunc sign

            for (int i = 0; i < 8; i++) {
                crc_and = crc & 0x8000;
                if (crc_and == 0) {
                    crc = (crc << 1) & 0xffff;
                } else {
                    crc = (crc << 1) & 0xffff;
                    crc = (crc ^ 0x1021) & 0xffff;
                }
            }
        }
        crc &= 0xffff;
        return crc;
    }
    
    public static int crc8(final byte[] buffer, int length) {
        int crc = 0xFF;
        int crc_and = 0;

        for (int j = 0; j < length; j++) {
            crc ^= ((buffer[j] & 0xff)) & 0xffff;//byte to int, trunc sign

            for (int i = 0; i < 8; i++) {
                crc_and = crc & 0x80;
                if (crc_and == 0) {
                    crc = (crc << 1) & 0xff;
                } else {
                    crc = (crc << 1) & 0xff;
                    crc = (crc ^ 0x07) & 0xff;
                }
            }
        }
        crc &= 0xff;
        return crc;
    }
    
}
