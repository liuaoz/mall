package com.macro.mall.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 安全工具类，主要用于各种加密解密，信息摘要<br>
 * <B>常见的加密模式：</B><br>
 * ECB（电码本模式），CBC（加密块链模式），OFB（输出反馈模式），CFB（加密反馈模式）<br>
 * 一般的加密通常都是块加密，如果要加密超过块大小的数据，就需要涉及填充和链加密模式，文中提到的ECB和CBC等就是指链加密模式。<br>
 * AES支持支持几种填充：NoPadding，PKCS5Padding，ISO10126Padding，PaddingMode.Zeros;PaddingMode.PKCS7<br>
 * <B>填充模式：</B><br>
 * PKCS7 就是数据少几个就填充几个；<br>
 * 秘钥长度，秘钥，IV值，加解密模式，PADDING方式<br>
 *
 * <B>算法提供者：</B><br>
 * new com.sun.crypto.provider.SunJCE() <br>
 * new org.bouncycastle.jce.provider.BouncyCastleProvider()<br>
 *
 * <B>信息摘要算法的实现</B><br>
 * MessageDigest、DigestInputStream、DigestOutputStream、Mac
 *
 * @author CHENXX
 * @since 2017-05-25
 */
public final class SafeUtil {

    private static final Logger log = LoggerFactory.getLogger(SafeUtil.class);

    private static final char[] HEX_CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public final static String ALGORITHM_MD5 = "MD5";

    /**
     * Secure Hash Algorithm，安全散列算法
     */
    public final static String ALGORITHM_SHA = "SHA";

    /**
     * MAC算法可选以下多种算法
     *
     * <pre>
     *
     * HmacMD5
     * HmacSHA1
     * HmacSHA256
     * HmacSHA384
     * HmacSHA512
     * </pre>
     */
    public static final String ALGORITHM_MAC = "HmacMD5";

    /**
     * key size must be equal to 56<br>
     * AES algorithm allows 128, 192 or 256 bit key length. which is 16, 24 or
     * 32 byte. your keys length should be 16 , 24 or 32 bytes.
     */
    public final static String ALGORITHM_DES = "DES";
    /**
     * key size must be equal to 112 or 168
     */
    public final static String ALGORITHM_3DES = "DESede";
    /**
     * key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be
     * available(it needs to download local_policy.jar and US_export_policy.jar.
     * directory:\jdk1.8.0_60\jre\lib\security)<br>
     * AES algorithm allows 128, 192 or 256 bit key length. which is 16, 24 or
     * 32 byte. your keys length should be 16 , 24 or 32 bytes.
     */
    public final static String ALGORITHM_AES = "AES";

    /**
     * key size must be multiple of 8, and can only range from 32 to 448
     * (inclusive)
     */
    public final static String ALGORITHM_BLOWFISH = "Blowfish";

    /**
     * key size must be between 40 and 1024 bits
     */
    public final static String ALGORITHM_RC2 = "RC2";
    /**
     * (ARCFOUR) key size must be between 40 and 1024 bits
     */
    public final static String ALGORITHM_RC4 = "RC4";

    /**
     * RSA非对称加密内容长度有限制，512位key最多只能加密64位数据，1024位key的最多只能加密127位数据，否则就会报错
     */
    public final static String ALGORITHM_RSA = "RSA";

    /**
     * 数字签名 签名/验证算法 MD5withRSA
     */
    public static final String SIGNATURE_MD5WITHRSA = "MD5withRSA";

    /**
     * 数字签名 签名/验证算法 SHA1withRSA
     */
    public static final String SIGNATURE_SHA1WITHRSA = "SHA1withRSA";

    /**
     * 用于保存在map中的key
     */
    public static final String PUBLIC_KEY = "RSAPublicKey";
    public static final String PRIVATE_KEY = "RSAPrivateKey";

    /**
     * 秘钥长度，单位bit；对应的字节为除于8
     */
    public static final int KEY_SIZE_56 = 56;
    public static final int KEY_SIZE_112 = 112;
    public static final int KEY_SIZE_128 = 128;
    public static final int KEY_SIZE_168 = 168;
    public static final int KEY_SIZE_192 = 192;
    public static final int KEY_SIZE_256 = 256;
    public static final int KEY_SIZE_512 = 512;
    public static final int KEY_SIZE_1024 = 1024;

    /**
     * 加密/解密算法 / 工作模式 / 填充方式 <br>
     * Java 6支持PKCS5Padding填充方式; Bouncy Castle支持PKCS7Padding填充方式
     */
    public final static String DES_CBC_PKCS5PADDING = "DES/CBC/PKCS5Padding";
    public final static String DESede_CBC_PKCS5PADDING = "DESede/CBC/PKCS5Padding";
    public final static String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";// 不推荐使用

    public final static String AES_CBC_NO_PADDING = "AES/CBC/NoPadding";
    public final static String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";// 推荐使用

    public final static String AES_CBC_PKCS7PADDING = "AES/CBC/PKCS7Padding";
    public final static String AES_ECB_NOPADDING = "AES/ECB/NoPadding";

    public static void main(String[] args) throws Exception {
        String content = "这是一段重要的内容，不能泄露！";
        String key = "1234567812345678";

        // testSign(content.getBytes(), key.getBytes(), SIGNATURE_SHA1WITHRSA,
        // SafeUtil.KEY_SIZE_512);
        // testMD5(content);
        // testBase64(content);
        testAesCbc(content, key);
        // testDes(content, key);
        // testRsa(content, key);

    }

    /**
     * BASE64解码
     */
    public static byte[] decryptBASE64(String data) {
        return Base64.getDecoder().decode(data);
    }

    /**
     * BASE64编码
     */
    public static String encryptBASE64(byte[] dataBytes) {
        return Base64.getEncoder().encodeToString(dataBytes);
    }

    public static String md5(String src) {
        try {
            MessageDigest instance = MessageDigest.getInstance(ALGORITHM_MD5);
            byte[] digest = instance.digest(src.getBytes(StandardCharsets.UTF_8));
            return new String(encodeHex(digest));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(ALGORITHM_MD5 + "算法不存在");
        }
    }

    /**
     * MD5加密
     *
     * @param data
     * @return
     */
    public static byte[] encryptMD5(byte[] data) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(ALGORITHM_MD5);
            md5.update(data);
            return md5.digest();
        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }
        return null;
    }

    /**
     * SHA加密
     *
     * @param dataByte
     * @return
     */
    public static byte[] encryptSHA(byte[] dataByte) {
        try {
            MessageDigest sha = MessageDigest.getInstance(ALGORITHM_SHA);
            sha.update(dataByte);
            return sha.digest();
        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }
        return null;
    }

    /**
     * HMAC加密
     *
     * @param dataByte
     * @param keyByte  base64encode后得到的字符串
     * @return
     */
    public static byte[] encryptHMAC(byte[] dataByte, byte[] keyByte) {

        SecretKey secretKey = new SecretKeySpec(keyByte, ALGORITHM_MAC);
        try {
            Mac mac = Mac.getInstance(secretKey.getAlgorithm());
            mac.init(secretKey);
            return mac.doFinal(dataByte);
        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }
        return null;
    }

    // 对称加密：DES,3DES,AES,PES

    /**
     * 加密<br>
     * 算法：DES 模式：CBC 填充：PKCS5Padding
     *
     * @param dataBytes
     * @param keyBytes
     * @return
     */
    public static byte[] encryptWithDesCbc(byte[] dataBytes, byte[] keyBytes) {

        byte[] result = null;

        try {
            SecretKey key = getKeyByFactory(keyBytes, ALGORITHM_DES);

            Cipher cipher = Cipher.getInstance(DES_CBC_PKCS5PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec((new DESKeySpec(keyBytes)).getKey()));
            result = cipher.doFinal(dataBytes);

        } catch (Exception e) {
            e.printStackTrace();
            log.error("exception:" + e.toString());
        }

        return result;

    }

    /**
     * 解密<br>
     * 算法：DES 模式：CBC 填充：PKCS5Padding
     *
     * @param dataBytes
     * @param keyBytes
     * @return
     */
    public static byte[] decryptWithDesCbc(byte[] dataBytes, byte[] keyBytes) {

        byte[] result = null;

        try {
            SecretKey key = getKeyByFactory(keyBytes, ALGORITHM_DES);
            Cipher cipher = Cipher.getInstance(DES_CBC_PKCS5PADDING);
            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec((new DESKeySpec(keyBytes)).getKey()));
            result = cipher.doFinal(dataBytes);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("exception:" + e.toString());
        }
        return result;
    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[] 解密数据
     * @throws Exception
     */
    public static byte[] decryptWithAesEcb(byte[] data, byte[] key, Integer keySize) {
        try {
            Key k = getKeyForAes(key, keySize);
            Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
            // 初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, k);
            // 执行操作
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密<br>
     * 注意：因为美国出口政策原因，如果要使用192，256长度的，需要额外下载两个jar包，local_policy.jar和US_export_policy.jar
     *
     * @param data    待加密数据
     * @param key     密钥
     * @param keySize 生成的秘钥长度(默认128，如果要使用192、256长度的，需要将jre\lib\security
     *                local_policy.jar和US_export_policy.jar替换掉，文件从oracle上下载)
     * @return byte[] 加密数据
     * @throws Exception
     */
    public static byte[] encryptWithAesEcb(byte[] data, byte[] key, Integer keySize) {
        try {
            Key k = getKeyForAes(key, keySize);
            Cipher cipher = Cipher.getInstance(AES_ECB_PKCS5PADDING);
            // 初始化，设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, k);
            // 执行操作
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decryptWithAesCbc5Padding(byte[] data, byte[] key, byte[] iv) {
        return decryptWithAesCbc(data, key, iv, AES_CBC_PKCS5PADDING);
    }

    /**
     * java不支持PKCS7Padding
     * 使用第三方工具bouncycastle
     * 注意：使用前需注册提供商,即:Security.addProvider(new BouncyCastleProvider());
     */
    public static byte[] decryptWithAesCbc7Padding(byte[] data, byte[] key, byte[] iv) {
        Key keySpec = getKeySpecBC(key, KEY_SIZE_128);
        try {
            Cipher cipher = getCipherBC();
            assert cipher != null;
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv));
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES 解密
     *
     * @param data                 待解密数据
     * @param key                  密钥
     * @param iv                   向量
     * @param algorithmModePadding 算法-模式-填充
     * @return byte[] 解密数据
     */
    public static byte[] decryptWithAesCbc(byte[] data, byte[] key, byte[] iv, String algorithmModePadding) {
        try {
            Key k = new SecretKeySpec(key, ALGORITHM_AES);// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance(algorithmModePadding);
            IvParameterSpec ipc = new IvParameterSpec(iv);
            // 初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, k, ipc);
            // 执行操作
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] encryptWithAesCbc5Padding(byte[] data, byte[] key, byte[] iv) {
        return encryptWithAes(data, key, iv, AES_CBC_PKCS5PADDING);
    }

    /**
     * java不支持PKCS7Padding
     * 使用第三方工具bouncycastle
     * 注意：使用前需注册提供商,即:Security.addProvider(new BouncyCastleProvider());
     */
    public static byte[] encryptWithAesCbc7Padding(byte[] data, byte[] key, byte[] iv) {
        Key keySpec = getKeySpecBC(key, KEY_SIZE_128);
        try {
            Cipher cipher = getCipherBC();
            assert cipher != null;
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv));
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * AES 加密<br>
     * 注意：因为美国出口政策原因，如果要使用192，256长度的，需要额外下载两个jar包，local_policy.jar和US_export_policy.jar
     *
     * @param data                 待加密数据
     * @param key                  密钥
     * @param iv                   向量
     *                             生成的秘钥长度(默认128，如果要使用192、256长度的，需要将jre\lib\security
     *                             local_policy.jar和US_export_policy.jar替换掉，文件从oracle上下载)
     * @param algorithmModePadding 算法-模式-填充
     * @return byte[] 加密数据
     */
    public static byte[] encryptWithAes(byte[] data, byte[] key, byte[] iv, String algorithmModePadding) {
        try {
            Key k = new SecretKeySpec(key, ALGORITHM_AES);// 转换为AES专用密钥
            Cipher cipher = Cipher.getInstance(algorithmModePadding);
            IvParameterSpec ipc = new IvParameterSpec(iv);
            // 初始化，设置为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, k, ipc);
            // 执行操作
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Cipher getCipherBC() {
        try {
            // 初始化cipher
            return Cipher.getInstance(AES_CBC_PKCS7PADDING, "BC");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static Key getKeySpecBC(byte[] key, int keySize) {
        int base = keySize / 8;
        if (key.length % base != 0) {
            int groups = key.length / base + (key.length % base != 0 ? 1 : 0);
            byte[] temp = new byte[groups * base];
            Arrays.fill(temp, (byte) 0);
            System.arraycopy(key, 0, temp, 0, key.length);
            key = temp;
        }
        // 转化成JAVA的密钥格式
        return new SecretKeySpec(key, ALGORITHM_AES);
    }


    public static void init(byte[] key, int keySize) {

        getKeySpecBC(key, keySize);
        getCipherBC();
    }


    // 非对称加密：RSA

    /**
     * 解密<br>
     * 用私钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String key) {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }
        return null;
    }

    /**
     * 解密<br>
     * 用公钥解密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String key) {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);

            // 对数据解密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }
        return null;

    }

    /**
     * 加密<br>
     * 用公钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) {
        // 对公钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key publicKey = keyFactory.generatePublic(x509KeySpec);

            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }

        return null;

    }

    /**
     * 加密<br>
     * 用私钥加密
     *
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String key) {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);

        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
            Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

            // 对数据加密
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }
        return null;
    }

    // 生成秘钥的方法

    /**
     * 根据用户密码，使用Factory生成key<br>
     * 稍微加工了一下，加了一些置换操作
     *
     * @param keyBytes  key
     * @param algorithm 算法
     * @return
     */
    public static SecretKey getKeyByFactory(byte[] keyBytes, String algorithm) {
        SecretKey key = null;
        try {
            DESKeySpec keySpec = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(algorithm);
            key = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }
        return key;
    }

    /**
     * 使用Generator生成key。可以根据实际需要，配置生成秘钥的长度，提高安全系数<br>
     * 可用于：1.初始化HMAC密钥 2.des 3... <br>
     * 注意算法要和秘钥长度匹配;AES使用getKeyBySecretKeySpec;<br>
     * 若要根据算法获取默认长度的秘钥，则keySize=0即可<br>
     * seed:建议使用用户自定义密码
     *
     * @param seed
     * @param algorithm 算法
     * @param keySize   秘钥长度
     * @return
     */
    public static SecretKey getKeyByGenerator(byte[] seed, String algorithm, Integer keySize) {
        SecretKey key = null;
        try {
            // 创建algorithm的Key生产者
            KeyGenerator keyGenerator = KeyGenerator.getInstance(algorithm);
            // key为8个字节，实际用了56位； 后面随机数用key作为种子seed生成
            keyGenerator.init(keySize, new SecureRandom(seed));

            key = keyGenerator.generateKey();

            System.out.println("【密钥长度】" + key.getEncoded().length + "【秘钥值】" + byteToHexString((key.getEncoded())));

        } catch (Exception e) {
            log.error("exception:" + e);
        }
        return key;
    }

    /**
     * 使用SecretKeySpec生成key<br>
     * key是原始的，没有加工过。在使用时注意秘钥长度要为8的整数倍<br>
     * AES、Blowfish 算法生成秘钥用该方法，不要用getKeyByFactory
     *
     * @param keyBytes
     * @param algorithm 算法
     * @return
     */
    public static SecretKey getKeyBySecretKeySpec(byte[] keyBytes, String algorithm) {
        SecretKey key = null;
        try {
            key = new SecretKeySpec(keyBytes, algorithm);// SecretKeySpec类同时实现了Key和KeySpec接口
        } catch (Exception e) {
            log.error("exception:" + e);
        }
        return key;
    }

    /**
     * 随机生成秘钥(需要将秘钥存储起来，方便解密时候使用)<br>
     * 算法：AES
     */
    public static SecretKey getKeyByRandon(int keySize) {

        SecretKey sk = null;
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_AES);
            kg.init(keySize);
            // 要生成多少位，只需要修改这里即可128, 192或256
            sk = kg.generateKey();
        } catch (NoSuchAlgorithmException e) {
            log.error("exception:" + e);
        }
        return sk;
    }

    /**
     * 使用指定的字符串生成秘钥<br>
     * 算法：AES
     */
    public static SecretKey getKeyByPass(int keySize, String password) {

        SecretKey sk = null;
        try {
            KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_AES);
            // kg.init(128);//要生成多少位，只需要修改这里即可128, 192或256
            // SecureRandom是生成安全随机数序列，password.getBytes()是种子，只要种子相同，序列就一样，所以生成的秘钥就一样。
            kg.init(keySize, new SecureRandom(password.getBytes()));
            sk = kg.generateKey();
            // byte[] b = sk.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            log.error("exception:" + e.toString());
        }
        return sk;
    }

    /**
     * @param seed
     * @param keySize
     * @return
     */
    public static SecretKeySpec getKeyForAes(byte[] seed, Integer keySize) {
        SecretKeySpec key = null;

        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_AES);// 创建AES的Key生产者
            // key为8个字节，实际用了56位； 后面随机数用key作为种子seed生成;生成安全随机数序列,只要种子相同,序列就一样
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            secureRandom.setSeed(seed);
            keyGenerator.init(keySize, secureRandom);
            SecretKey secretKey = keyGenerator.generateKey(); // 根据用户密码，生成一个密钥
            byte[] enCodeFormat = secretKey.getEncoded();
            key = new SecretKeySpec(enCodeFormat, ALGORITHM_AES);// 转换为AES专用密钥

        } catch (Exception e) {
            log.error("exception:" + e);
        }
        return key;
    }

    /**
     * 根据用户密码，获取3des算法的秘钥对象
     *
     * @return
     */
    public static Key getKeyFor3Des() {

        Key key = null;
        try {
            // 1.初始化key秘钥
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM_3DES);
            keyGenerator.init(new SecureRandom());
            SecretKey secretKey = keyGenerator.generateKey();
            // 转换key秘钥
            DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(secretKey.getEncoded());
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM_3DES);
            key = secretKeyFactory.generateSecret(deSedeKeySpec);

        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }
        return key;
    }

    /**
     * 根据指定的秘钥长度，初始化秘钥对（非对称加密RSA）
     *
     * @param keySize
     * @return
     * @throws Exception
     */
    public static Map<String, Key> getPairKey(Integer keySize) throws Exception {

        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM_RSA);

        keyPairGen.initialize(keySize);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Key> keyMap = new HashMap<String, Key>(2);
        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static byte[] getPrivateKey(Map<String, Key> keyMap) throws Exception {
        Key key = keyMap.get(PRIVATE_KEY);
        return key.getEncoded();
    }

    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static byte[] getPublicKey(Map<String, Key> keyMap) throws Exception {
        Key key = keyMap.get(PUBLIC_KEY);
        return key.getEncoded();
    }

    /**
     * 使用【私钥】对信息生成数字签名<br>
     * PKCS8EncodedKeySpec;RSA
     *
     * @param data               加密数据
     * @param keyByte            用户密码
     * @param signatureAlgorithm 签名/验证算法
     * @return
     */
    public static byte[] sign(byte[] data, byte[] keyByte, String signatureAlgorithm) {

        byte[] sign = null;

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);

            // 构造PKCS8EncodedKeySpec对象
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyByte);

            // 取私钥匙对象
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);

            // 用私钥对信息生成数字签名
            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initSign(priKey);
            signature.update(data);
            sign = signature.sign();

        } catch (Exception e) {
            e.printStackTrace();
            log.error("exception:" + e.toString());
        }

        return sign;
    }

    /**
     * 使用【公钥】对数字签名进行验签
     *
     * @param data               加密数据
     * @param publicKey          公钥
     * @param sign               数字签名字节码
     * @param signatureAlgorithm 签名/验证算法
     * @return 校验成功返回true 失败返回false
     */
    public static boolean verify(byte[] data, byte[] publicKey, byte[] sign, String signatureAlgorithm) {

        boolean result = false;

        // 构造X509EncodedKeySpec对象
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);

        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);

            // 取公钥匙对象
            PublicKey pubKey = keyFactory.generatePublic(keySpec);

            Signature signature = Signature.getInstance(signatureAlgorithm);
            signature.initVerify(pubKey);
            signature.update(data);
            result = signature.verify(sign);
        } catch (Exception e) {
            log.error("exception:" + e.toString());
        }
        // 验证签名是否正常
        return result;
    }

    // 其他工具方法

    /**
     * 字节数组转十六进制字符串(用于加密之后以十六进制字符串显示)
     *
     * @param bytes
     * @return
     */
    public static String byteToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;
        for (int i = 0; i < bytes.length; i++) {
            sTemp = Integer.toHexString(0xFF & bytes[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp);
        }
        return sb.toString();
    }

    /**
     * 字节数组转base64字符串(用于加密之后以base64字符串显示)
     *
     * @param bytes
     * @return
     */
    public static String byteToBase64String(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将16进制转换为二进制
     *
     * @param hexStr
     * @return
     */
    public static byte[] hexStringToByte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
            int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    private static char[] encodeHex(byte[] bytes) {
        char[] chars = new char[32];
        for (int i = 0; i < chars.length; i += 2) {
            byte b = bytes[i / 2];
            chars[i] = HEX_CHARS[b >>> 4 & 15];
            chars[i + 1] = HEX_CHARS[b & 15];
        }
        return chars;
    }

    /**
     * 10进制数组转换16进制数组
     *
     * @param tip
     * @param b
     * @return
     */
    public static String printbytes(String tip, byte[] b) {
        String ret = "";
        String str;
        for (int i = 0; i < b.length; i++) {

            str = Integer.toHexString((int) (b[i] & 0xff));

            if (str.length() == 1)
                str = "0" + str;
            ret = ret + str + " ";
        }
        // 02 00 07 00 00 60 70 01 17 35 03 C8
        return ret;
    }

    public static byte[] tmpDecrypt(byte[] data, byte[] keyBytes) {

        byte[] ret = null;
        try {

            SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance(AES_ECB_NOPADDING);// 创建密码器
            // 初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, key);
            // 执行操作
            ret = cipher.doFinal(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * 读取密钥
     *
     * @param keyPath
     * @return
     * @throws Exception
     */
    public static SecretKey readKey(Path keyPath) throws Exception {
        byte[] keyBytes = Files.readAllBytes(keyPath);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM_AES);
        return keySpec;
    }

    /**
     * 保存密钥
     *
     * @param sk
     * @return
     * @throws Exception
     */
    public static void saveKey(SecretKey sk) throws Exception {
        // 把上面的密钥存起来
        Path keyPath = Paths.get("D:/aes.key");
        Files.write(keyPath, sk.getEncoded());
    }

    public static void testMD5(String content) {
        System.out.println("-----MD5  开始--------");
        System.out.println("加密前：" + content);
        String after = byteToHexString((encryptMD5(content.getBytes())));
        System.out.println("加密后：" + after);
        System.out.println("-----MD5 结束--------");
    }

    public static void testBase64(String content) {
        System.out.println("-----Base64  开始--------");
        System.out.println("加密前：" + content);
        String after = encryptBASE64(content.getBytes());
        System.out.println("加密后：" + after);
        System.out.println("解密后：" + new String(decryptBASE64(after)));
        System.out.println("-----Base64 结束--------");
    }

    public static void testAesEcb(String content, String key, Integer keySize) {
        System.out.println("-----AES 加解密 开始--------");
        System.out.println("加密前：" + content);
        String after = encryptBASE64(encryptWithAesEcb(content.getBytes(), key.getBytes(), keySize));
        System.out.println("加密后(AES)：" + after);
        System.out.println("解密后：" + new String(decryptWithAesEcb(decryptBASE64(after), key.getBytes(), keySize)));
        System.out.println("-----AES 加解密 结束--------");
    }

    public static void testAesCbc(String content, String key) {
        System.out.println("-----AES 加解密 开始--------");
        System.out.println("加密前：" + content);
        String after = encryptBASE64(encryptWithAesCbc5Padding(content.getBytes(), key.getBytes(), key.getBytes()));
        System.out.println("加密后(AES)：" + after);
        System.out
                .println("解密后：" + new String(decryptWithAesCbc5Padding(decryptBASE64(after), key.getBytes(), key.getBytes())));
        System.out.println("-----AES 加解密 结束--------");
    }

    public static void testDes(String content, String key) {
        System.out.println("-----DES 加解密 开始--------");
        System.out.println("加密前：" + content);
        String after = encryptBASE64(encryptWithDesCbc(content.getBytes(), key.getBytes()));
        System.out.println("加密后(DES)：" + after);
        System.out.println("解密后：" + new String(decryptWithDesCbc(decryptBASE64(after), key.getBytes())));
        System.out.println("-----DES 加解密 结束--------");
    }

    public static void testSign(byte[] data, byte[] keyByte, String signatureAlgorithm, Integer keySize)
            throws Exception {
        System.out.println("-----生成sign  开始--------");
        System.out.println("数据内容：" + new String(data) + "; 用户密码：" + new String(keyByte));
        // 先生成秘钥对
        Map<String, Key> map = getPairKey(keySize);
        byte[] sign = sign(data, getPrivateKey(map), signatureAlgorithm);
        System.out.println("生成签名：" + encryptBASE64(sign));
        System.out.println("验签结果：" + verify(data, getPublicKey(map), sign, signatureAlgorithm));
        System.out.println("-----验签sign  结束--------");
    }

    public static void testRsa(String content, String key) throws Exception {
        Map<String, Key> map = getPairKey(512);
        System.out.println("private:" + encryptBASE64(map.get(PRIVATE_KEY).getEncoded()));
        System.out.println("public:" + encryptBASE64(map.get(PUBLIC_KEY).getEncoded()));

        System.out.println("-----RSA 加解密 开始--------");
        System.out.println("加密前：" + content);
        byte[] afterByte = encryptByPublicKey(content.getBytes(), encryptBASE64(map.get(PUBLIC_KEY).getEncoded()));
        String after = encryptBASE64(afterByte);
        System.out.println("加密后(RSA)：" + after);
        System.out.println(
                "解密后：" + new String(decryptByPrivateKey(afterByte, encryptBASE64(map.get(PRIVATE_KEY).getEncoded()))));
    }

}
