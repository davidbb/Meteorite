/**
 * 
 */
package ca.carleton.ccsl.meteorite;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import android.util.Log;
/**
 * @author dbarrera
 *
 */
public class UAppIDUtils {
	private static final String TAG = "Meteorite-UAppIDUtils";

	public static String getUAppID(String pkgName) {
		Process proc;
		String apkPath, sha1Print;
		String temp;

		try {
			// create process to find package storage location
			proc = Runtime.getRuntime().exec("pm path " + pkgName);
			DataInputStream in = new DataInputStream(proc.getInputStream());
			apkPath = in.readLine().substring(8); // line starts with "package:"
			Log.v(TAG, "File path:"+apkPath);
			// ensure package is in /data/app (otherwise is a system app)
			if (!apkPath.startsWith("/data/app")) {
				Log.d(TAG, pkgName + " is system app.  Returning...");
				return null; // package is not uninstallable (system package)
			}
		
			JarFile jarf = new JarFile(apkPath);
			Enumeration<JarEntry> entries = jarf.entries();
			String filename = null;
			while(entries.hasMoreElements()) { //Get the last RSA file in the list
				temp = entries.nextElement().getName();
				//Log.v(TAG, temp + ": " + temp.startsWith("META-INF/") + ", " + temp.endsWith(".RSA"));
				if(temp.startsWith("META-INF/") && temp.endsWith(".RSA")) {
					Log.v(TAG, "Found RSA file");
					Log.v(TAG, "RSA file:"+temp);
					filename = temp;
				}
			}
			Log.v(TAG, filename != null ? filename : "null filename");
			if(filename == null) return null;
			/*reading the sha1fingerprint of the cert*/
			CertificateFactory cf = null;
			Collection<? extends Certificate> certs = null;
			ZipEntry zipEntry = jarf.getEntry(filename);
			InputStream inStream = jarf.getInputStream(zipEntry);
			cf =  CertificateFactory.getInstance("X.509");
			certs = cf.generateCertificates(inStream);
			for(Certificate cert : certs){
				byte[] encCertInfo = cert.getEncoded();
				MessageDigest md = MessageDigest.getInstance("SHA1");
			    md.update(encCertInfo, 0, encCertInfo.length);
			    byte[] mdbytes = md.digest();
			    sha1Print = toHexString(mdbytes);
	            Log.v(TAG, "Cert SHA1 Sum: "+sha1Print);
			}
			
			//missing:calculate the uappid and return it
            
			proc.destroy();
		} catch (Exception e) {
			Log.d(TAG, "Exception in UAppIDUtils");
			e.printStackTrace();
			return null;
		}
		String binaryHash = getBinaryHash(apkPath);
		return binaryHash;
	}
	
	private static String getBinaryHash(String apkPath) {
		/*This gets the SHA1 sum of the full apk*/
		String sha1Print=null;
		try{
		MessageDigest md = MessageDigest.getInstance("SHA1");
		FileInputStream fis = new FileInputStream(apkPath);
	    byte[] dataBytes = new byte[1024];
	    int nread = 0; 
	    while ((nread = fis.read(dataBytes)) != -1) {
	      md.update(dataBytes, 0, nread);
	    };
	    byte[] mdbytes = md.digest();
	    sha1Print = toHexString(mdbytes);
		//byte[] digest = md.digest(encCertInfo);
        //sha1Print = toHexString(digest);
        Log.v(TAG, "APK SHA1 Sum: "+sha1Print);
		}
		catch(Exception e){
			Log.e(TAG, "Exception while calculating hash of the binary at "+apkPath);
		}
        return sha1Print;
	}

	private static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++)
            byte2hex(block[i], buf);
        return buf.toString();
    }
	
	private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                            '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }
}
