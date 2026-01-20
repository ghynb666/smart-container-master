package cn.fuguang.communicate.utils;

/**
 * CRC16校验工具类
 */
public class CRC16Util {
    
    /**
     * 计算CRC16校验值
     * @param bytes 待校验的字节数组
     * @return CRC16校验值
     */
    public static short calculateCRC16(byte[] bytes) {
        int crc = 0xFFFF;
        for (byte b : bytes) {
            crc ^= (int) b & 0xFF;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1;
                    crc ^= 0xA001;
                } else {
                    crc >>= 1;
                }
            }
        }
        return (short) crc;
    }
    
    /**
     * 验证CRC16校验值
     * @param bytes 待校验的字节数组
     * @param checksum 预期的CRC16校验值
     * @return true-校验通过，false-校验失败
     */
    public static boolean verifyCRC16(byte[] bytes, short checksum) {
        short calculatedChecksum = calculateCRC16(bytes);
        return calculatedChecksum == checksum;
    }
}