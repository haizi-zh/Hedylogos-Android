//package com.hedylogos.net;
//
//import android.util.Base64;
//
//import com.qiniu.api.auth.AuthException;
//import com.qiniu.api.config.Config;
//import com.qiniu.api.net.CallRet;
//import com.qiniu.api.rs.GetPolicy;
//import com.qiniu.api.rs.PutPolicy;
//
//import org.apache.http.Header;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.StatusLine;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.client.methods.HttpRequestBase;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
//
//import java.io.ByteArrayOutputStream;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URI;
//import java.security.InvalidKeyException;
//import java.security.NoSuchAlgorithmException;
//import java.util.zip.CRC32;
//import java.util.zip.CheckedInputStream;
//
//import javax.crypto.Mac;
//import javax.crypto.spec.SecretKeySpec;
//
///**
// * Created by q on 2015/4/14.
// */
//public class TestUtils {
//
//
//
//        private static Mac mac = null;
//        /**
//         * 云存储空间名
//         */
//        private static String bucketName = "";
//
//        /**
//         * 云存储域名
//         */
//        private static String domain = "";
//
//        static {
//            Config.ACCESS_KEY = KmsConfig.getPropConfigValue(FileSystemConstant.QiniuConfig.AK);
//            Config.SECRET_KEY = KmsConfig.getPropConfigValue(FileSystemConstant.QiniuConfig.CK);
//            bucketName = KmsConfig.getPropConfigValue(FileSystemConstant.QiniuConfig.BUCKETNAME);
//            domain = KmsConfig.getPropConfigValue(FileSystemConstant.QiniuConfig.DOMAIN);
//            mac = new Mac(Config.ACCESS_KEY, Config.SECRET_KEY);
//        }
//
//        /**
//         *
//         * @Title: getUptoken
//         * @Description: 获取上传token验证
//         * @param @return
//         * @return String
//         * @throws
//         */
//        private String getUptoken() {
//            PutPolicy putPolicy = new PutPolicy(bucketName);
//            String uptoken = null;
//            try {
//                uptoken = putPolicy.token(mac);
//            } catch (AuthException e) {
//                LOG.error(e.getMessage());
//            } catch (JSONException e) {
//                LOG.error(e.getMessage());
//            }
//            return uptoken;
//        }
//
//        /**
//         *
//         * @Title: upload
//         * @Description: 本地文件上传，参数为本地文件路径
//         * @param @param path
//         * @param @param id
//         * @param @return
//         * @param @throws Exception
//         * @return PutRet
//         * @throws
//         */
//        public PutRet upload(String path,String id) throws Exception {
//            FilterParamUtil.paramBlankCheckHasException(path, "文件路径");
//            FilterParamUtil.paramBlankCheckHasException(id, "文件id");
//            LOG.info("七牛云存储开始上传---");
//            String uptoken = getUptoken();
//            if(null==uptoken) {
//                LOG.error("七牛云存储上传token获取失败");
//                throw new Exception("七牛云存储上传token获取失败");
//            }
//            PutExtra extra = new PutExtra();
//            extra.mimeType = Tools.getExtension(path);;
//            PutRet ret = null;
//            try {
//                ret = IoApi.putFile(uptoken, id, path, extra);
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                throw new Exception("七牛云存储上传文件失败");
//            }
//            LOG.info("七牛云存储结束上传---");
//            return ret;
//        }
//
//        /**
//         *
//         * @Title: upload
//         * @Description: 通过io流实现向七牛上传文件
//         * @param @param io
//         * @param @param path
//         * @param @param id
//         * @param @return
//         * @param @throws Exception
//         * @return PutRet
//         * @throws
//         */
//        public PutRet upload(InputStream io, String path, String id) throws Exception {
//            FilterParamUtil.paramBlankCheckHasException(path, "文件路径");
//            FilterParamUtil.paramBlankCheckHasException(id, "文件id");
//            LOG.info("七牛云存储上传---开始");
//            String uptoken = getUptoken();
//            if(null==uptoken) {
//                LOG.error("七牛云存储上传token获取失败");
//                throw new Exception("七牛云存储上传token获取失败");
//            }
//            PutExtra extra = new PutExtra();
//            extra.mimeType = Tools.getExtension(path);
//            PutRet ret = null;
//            try {
//                ret = IoApi.Put(uptoken, id, io, extra);
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                throw new Exception("七牛云存储上传文件失败");
//            }
//            LOG.info("七牛云存储上传---结束");
//            return ret;
//        }
//
//        /**
//         *
//         * @Title: scanFile
//         * @Description: 通过文件id获取文件基本信息
//         * @param @param id
//         * @param @return
//         * @param @throws Exception
//         * @return Entry
//         * @throws
//         */
//        public Entry scanFile(String id) throws Exception {
//            FilterParamUtil.paramBlankCheckHasException(id, "文件id");
//            RSClient client = new RSClient(mac);
//            Entry statRet = null;
//            try {
//                statRet = client.stat(bucketName, id);
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                throw new Exception("七牛云存储获取文件信息失败");
//            }
//            return statRet;
//        }
//
//        public String getDownUrl(String id) throws Exception {
//            FilterParamUtil.paramBlankCheckHasException(id, "文件id");
//            String baseUrl = URLUtils.makeBaseUrl(domain, id);
//            GetPolicy getPolicy = new GetPolicy();
//            String downloadUrl = "";
//            try {
//                downloadUrl = getPolicy.makeRequest(baseUrl, mac);
//            } catch (Exception e) {
//                LOG.error(e.getMessage());
//                throw new Exception("七牛云存储获取文件文件下载路径失败");
//            }
//            return downloadUrl;
//        }
//
//        /**
//         *
//         * @Title: downFile
//         * @Description: 通过id下载文件
//         * @param @throws Exception
//         * @return void
//         * @throws
//         */
//        public void downFile(String id) throws Exception {
//
//            String baseUrl;
//
//            baseUrl = URLUtils.makeBaseUrl(domain, txtKey);
//            GetPolicy getPolicy = new GetPolicy();
//            String downloadUrl = getPolicy.makeRequest(baseUrl, mac);
//            System.out.println(downloadUrl);
//            download(downloadUrl);
//        }
//
//        private void download(String url) throws ClientProtocolException,
//                IOException {
//            // url = "http://liubin.u.qiniudn.com/eztv.jpg";
//            // url = "http://liubin.u.qiniudn.com/index.html";
//            HttpGet httpget9 = new HttpGet(url);
//            HttpClient client9 = Http.getClient();
//            HttpResponse res = client9.execute(httpget9);
//            Header[] hs = res.getAllHeaders();
//
//            for (Header h : hs) {
//                System.out.println(h);
//                System.out.println(new String(h.getValue().getBytes("iso8859-1"),
//                        "utf-8"));
//            }
//            HttpEntity entity = res.getEntity();
//            System.out.println(entity.getContentType());
//            System.out.println(entity.getContentLength());
//            System.out.println(entity.getContentEncoding());
//
//            if (entity.isStreaming()) {
//                InputStream is = entity.getContent();
//                FileOutputStream fo = new FileOutputStream("D:\\test2");
//                byte[] bs = new byte[1024 * 4];
//                int len = -1;
//                while ((len = is.read(bs)) != -1) {
//                    fo.write(bs, 0, len);
//                }
//            }
//
//        }
//
//        public void putFile(String uptoken, String key, FileInputStream io,
//                            PutExtra extra) throws Exception {
//            if (extra.checkCrc == 1) {
//                extra.crc32 = getCRC32(io);
//            }
//        }
//
//        private long getCRC32(FileInputStream io) throws Exception {
//            CRC32 crc32 = new CRC32();
//            FileInputStream in = null;
//            CheckedInputStream checkedInputStream = null;
//            long crc = 0;
//            try {
//                in = io;
//                checkedInputStream = new CheckedInputStream(in, crc32);
//                while (checkedInputStream.read() != -1) {
//                }
//                crc = crc32.getValue();
//            } finally {
//                if (in != null) {
//                    in.close();
//                    in = null;
//                }
//                if (checkedInputStream != null) {
//                    checkedInputStream.close();
//                    checkedInputStream = null;
//                }
//            }
//            return crc;
//        }
//
//        public CallRet fetch(String from, String bucket, String key)
//                throws Exception {
//            String to = bucket + ":" + key;
//            String encodeFrom = encodeBase64URLSafeString(from);
//            String encodeTo = encodeBase64URLSafeString(to);
//            String url = "http://iovip.qbox.me/fetch/" + encodeFrom + "/to/"
//                    + encodeTo;
//            HttpClient client = Http.getClient();
//            HttpPost post = new HttpPost(url);
//            String accessToken = signRequest(post, Config.SECRET_KEY,
//                    Config.ACCESS_KEY);
//            post.setHeader("User-Agent", Config.USER_AGENT);
//            post.setHeader("Authorization", "QBox " + accessToken);
//            HttpResponse res = client.execute(post);
//            CallRet ret = handleResult(res);
//            return ret;
//        }
//
//        public static String CHARSET = "utf-8";
//
//        public static String encodeBase64URLSafeString(String p) {
//            return encodeBase64URLSafeString(toByte(p));
//        }
//
//        public static String encodeBase64URLSafeString(byte[] binaryData) {
//            byte[] b = encodeBase64URLSafe(binaryData);
//            return toString(b);
//        }
//
//        /** 保留尾部的“=” */
//        public static byte[] encodeBase64URLSafe(byte[] binaryData) {
//            byte[] b = Base64.encodeBase64URLSafe(binaryData);
//            int mod = b.length % 4;
//            if (mod == 0) {
//                return b;
//            } else {
//                int pad = 4 - mod;
//                byte[] b2 = new byte[b.length + pad];
//                System.arraycopy(b, 0, b2, 0, b.length);
//                b2[b.length] = '=';
//                if (pad > 1) {
//                    b2[b.length + 1] = '=';
//                }
//                return b2;
//            }
//        }
//
//        public static byte[] toByte(String s) {
//            try {
//                return s.getBytes(CHARSET);
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        public static String toString(byte[] bs) {
//            try {
//                return new String(bs, CHARSET);
//            } catch (UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        public static String signRequest(HttpRequestBase request, String secretKey,
//                                         String accessKey) throws NoSuchAlgorithmException,
//                InvalidKeyException, IOException {
//            URI uri = request.getURI();
//            String path = uri.getRawPath();
//            String query = uri.getRawQuery();
//            byte[] sk = toByte(secretKey);
//            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA1");
//            SecretKeySpec keySpec = new SecretKeySpec(sk, "HmacSHA1");
//            mac.init(keySpec);
//            mac.update(toByte(path));
//            if (query != null && query.length() != 0) {
//                mac.update((byte) ('?'));
//                mac.update(toByte(query));
//            }
//            mac.update((byte) '\n');
//            signEntity(request, mac);
//            byte[] digest = mac.doFinal();
//            byte[] digestBase64 = encodeBase64URLSafe(digest);
//            StringBuffer b = new StringBuffer();
//            b.append(accessKey);
//            b.append(':');
//            b.append(toString(digestBase64));
//            return b.toString();
//        }
//
//        private static void signEntity(HttpRequestBase request, javax.crypto.Mac mac)
//                throws IOException {
//            HttpEntity entity = getEntity(request);
//            if (entity != null) {
//                if (needSignEntity(entity, request)) {
//                    ByteArrayOutputStream w = new ByteArrayOutputStream();
//                    entity.writeTo(w);
//                    mac.update(w.toByteArray());
//                }
//            }
//        }
//
//        private static HttpEntity getEntity(HttpRequestBase request) {
//            try {
//                HttpPost post = (HttpPost) request;
//                if (post != null) {
//                    return post.getEntity();
//                }
//            } catch (Exception e) {
//            }
//            return null;
//        }
//
//        private static boolean needSignEntity(HttpEntity entity,
//                                              HttpRequestBase request) {
//            String contentType = "application/x-www-form-urlencoded";
//            Header ect = entity.getContentType();
//            if (ect != null && contentType.equals(ect.getValue())) {
//                return true;
//            }
//            Header[] cts = request.getHeaders("Content-Type");
//            for (Header ct : cts) {
//                if (contentType.equals(ct.getValue())) {
//                    return true;
//                }
//            }
//            return false;
//        }
//
//        public static CallRet handleResult(HttpResponse response) {
//            try {
//                StatusLine status = response.getStatusLine();
//                int statusCode = status.getStatusCode();
//                String responseBody = EntityUtils.toString(response.getEntity(),
//                        CHARSET);
//                return new CallRet(statusCode, responseBody);
//            } catch (Exception e) {
//                return new CallRet(400, e);
//            }
//        }
//
//        public static void main(String[] args) throws Exception, JSONException {
//            QiniuFileSystemUtil uf = new QiniuFileSystemUtil();
//            // uf.upload();
//            // uf.scanFile();
////		uf.downFile();
//        }
//    }
//
//}
