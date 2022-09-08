package be.hi10.realnutrition.apis.amazon;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class NecessaryFunctions {
    /*function to generate signature*/
    static byte[] HmacSHA256(String data, byte[] key) throws Exception {
        String algorithm="HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF-8"));
    }

    static byte[] getSignatureKey(String key, String dateStamp, String regionName, String serviceName) throws Exception {
        byte[] kSecret = ("AWS4" + key).getBytes("UTF-8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        return kSigning;
    }
    
    static String bytesToHex(byte[] bytes) {
    	 final char[] hexArray = "0123456789ABCDEF".toCharArray();
    	 
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars).toLowerCase();
    }
    
    /*get dateTimeStamp*/
    static public String getTimeStamp(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
		String formatDateTime = dateTime.format(formatter);
		return formatDateTime;
	}

	/* get dateStamp */
	static public String getDate(LocalDateTime dateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String formatDateTime = dateTime.format(formatter);
		return formatDateTime;
	}
	
	/*download file from url*/
	  public void downloadFile(URL url, String outputFileName) throws IOException{
	    	{
	            try (InputStream in = url.openStream();
	                ReadableByteChannel rbc = Channels.newChannel(in);
	                FileOutputStream fos = new FileOutputStream(outputFileName)) {
	                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	            }
	        }
	    }
	    

}
