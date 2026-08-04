package ru.klimachkov.workplaces.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Утилитный класс для выполнения операций хеширования данных с использованием алгоритма SHA-256.
 *
 * @see MessageDigest
 */
public class SHA {

    /**
     * Вычисляет SHA-256 хеш переданных данных.
     *
     * <p>Входные данные преобразуются в последовательность байтов
     * с использованием кодировки UTF-8, после чего результат хеширования
     * преобразуется в шестнадцатеричное представление.</p>
     *
     * @param data данные для хеширования
     * @return SHA-256 хеш в шестнадцатеричном формате
     * @throws RuntimeException если алгоритм SHA-256 недоступен
     */
    public static String hashSha256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Cannot find SHA-256 algorithm", ex);
        }
    }

    /**
     * Преобразует массив байтов в шестнадцатеричную строку.
     *
     * @param hash массив байтов хеша
     * @return шестнадцатеричное представление массива байтов
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
