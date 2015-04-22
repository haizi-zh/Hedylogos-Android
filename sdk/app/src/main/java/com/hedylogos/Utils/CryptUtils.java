package com.hedylogos.Utils;

import com.loopj.android.http.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
/**
 * Created by q on 2015/4/16.
 */
public class CryptUtils {
    public static String encrypt(String data, String key) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keyspec);
            byte[] encrypted = cipher.doFinal(data.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String desEncrypt(String data, String key) throws Exception {
        try {
            byte[] encrypted1 = Base64.decode(data.getBytes(), Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keyspec);
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public  static String getMD5String(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            byte[] btInput = s.getBytes();
            //获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            //使用指定的字节更新摘要
            mdInst.update(btInput);
            //获得密文
            byte[] md = mdInst.digest();
            //把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


//    //密钥对
//    private KeyPair keyPair = null;
//
//    /**
//     * 初始化密钥对
//     */
//    //keyPair = this.generateKeyPair();
//
//
//    /**
//     * 生成密钥对
//     * @return KeyPair
//     * @throws Exception
//     */
//    private KeyPair generateKeyPair() throws Exception {
//        try {
//            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA",new org.bouncycastle.jce.provider.BouncyCastleProvider());
//            //这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低
//            final int KEY_SIZE = 1024;
//            keyPairGen.initialize(KEY_SIZE, new SecureRandom());
//            KeyPair keyPair = keyPairGen.genKeyPair();
//            return keyPair;
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
//
//    }
//
//    /**
//     * 生成公钥
//     * @param modulus
//     * @param publicExponent
//     * @return RSAPublicKey
//     * @throws Exception
//     */
//    private RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent) throws Exception {
//
//        KeyFactory keyFac = null;
//        try {
//            keyFac = KeyFactory.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        } catch (NoSuchAlgorithmException ex) {
//            throw new Exception(ex.getMessage());
//        }
//        RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(new BigInteger(modulus), new BigInteger(publicExponent));
//        try {
//            return (RSAPublicKey) keyFac.generatePublic(pubKeySpec);
//        } catch (InvalidKeySpecException ex) {
//            throw new Exception(ex.getMessage());
//        }
//
//    }
//
//    /**
//     * 生成私钥
//     * @param modulus
//     * @param privateExponent
//     * @return RSAPrivateKey
//     * @throws Exception
//     */
//    private RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] privateExponent) throws Exception {
//        KeyFactory keyFac = null;
//        try {
//            keyFac = KeyFactory.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
//        } catch (NoSuchAlgorithmException ex) {
//            throw new Exception(ex.getMessage());
//        }
//        RSAPrivateKeySpec priKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus), new BigInteger(privateExponent));
//        try {
//            return (RSAPrivateKey) keyFac.generatePrivate(priKeySpec);
//        } catch (InvalidKeySpecException ex) {
//            throw new Exception(ex.getMessage());
//        }
//    }
//
//    /**
//     * 加密
//     * @param key 加密的密钥
//     * @param data 待加密的明文数据
//     * @return 加密后的数据
//     * @throws Exception
//     */
//    public byte[] encrypt(Key key, byte[] data) throws Exception {
//        try {
//            Cipher cipher = Cipher.getInstance("RSA", new org.bouncycastle.jce.provider.BouncyCastleProvider());
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            //获得加密块大小，如:加密前数据为128个byte，而key_size=1024 加密块大小为127 byte,加密后为128个byte;
//            //因此共有2个加密块，第一个127 byte第二个为1个byte
//            int blockSize = cipher.getBlockSize();
//            int outputSize = cipher.getOutputSize(data.length);//获得加密块加密后块大小
//            int leavedSize = data.length % blockSize;
//            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
//            byte[] raw = new byte[outputSize * blocksSize];
//            int i = 0;
//            while (data.length - i * blockSize > 0) {
//                if (data.length - i * blockSize > blockSize)
//                    cipher.doFinal(data, i * blockSize, blockSize, raw, i * outputSize);
//                else
//                    cipher.doFinal(data, i * blockSize, data.length - i * blockSize, raw, i * outputSize);
//                //这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到ByteArrayOutputStream中
//                //，而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了OutputSize所以只好用dofinal方法。
//                i++;
//            }
//            return raw;
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
//    }
//
//    /**
//     * 解密
//     * @param key 解密的密钥
//     * @param raw 已经加密的数据
//     * @return 解密后的明文
//     * @throws Exception
//     */
//    public byte[] decrypt(Key key, byte[] raw) throws Exception {
//        try {
//            Cipher cipher = Cipher.getInstance("RSA", new BouncyCastleProvider());
//            cipher.init(cipher.DECRYPT_MODE, key);
//            int blockSize = cipher.getBlockSize();
//            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
//            int j = 0;
//            while (raw.length - j * blockSize > 0) {
//                bout.write(cipher.doFinal(raw, j * blockSize, blockSize));
//                j++;
//            }
//            return bout.toByteArray();
//        } catch (Exception e) {
//            throw new Exception(e.getMessage());
//        }
//    }
//
//    /**
//     * 返回公钥
//     * @return
//     * @throws Exception
//     */
//    public RSAPublicKey getRSAPublicKey() throws Exception{
//
//        //获取公钥
//        RSAPublicKey pubKey = (RSAPublicKey) keyPair.getPublic();
//        //获取公钥系数(字节数组形式)
//        byte[] pubModBytes = pubKey.getModulus().toByteArray();
//        //返回公钥公用指数(字节数组形式)
//        byte[] pubPubExpBytes = pubKey.getPublicExponent().toByteArray();
//        //生成公钥
//        RSAPublicKey recoveryPubKey = this.generateRSAPublicKey(pubModBytes,pubPubExpBytes);
//        return recoveryPubKey;
//    }
//
//    /**
//     * 获取私钥
//     * @return
//     * @throws Exception
//     */
//    public RSAPrivateKey getRSAPrivateKey() throws Exception{
//
//        //获取私钥
//        RSAPrivateKey priKey = (RSAPrivateKey) keyPair.getPrivate();
//        //返回私钥系数(字节数组形式)
//        byte[] priModBytes = priKey.getModulus().toByteArray();
//        //返回私钥专用指数(字节数组形式)
//        byte[] priPriExpBytes = priKey.getPrivateExponent().toByteArray();
//        //生成私钥
//        RSAPrivateKey recoveryPriKey = this.generateRSAPrivateKey(priModBytes,priPriExpBytes);
//        return recoveryPriKey;
//    }
}
