package ca.carleton.ccsl.meteorite;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

/**
 * The UAppIDUtils class provides static utility methods for calculating application UAppID's.
 * Provided a binary file or a package name this class is capable of creating a UAppID returned
 * as a string literal.
 * 
 * @author dbarrera
 */
public class UAppIDUtils
{
  private static final String TAG         = UAppIDUtils.class.getCanonicalName();
  private static final String HASH_ALGO   = "SHA1";
  private static final String CERT_FORMAT = "X509";

  /**
   * Computes the UAppID for a given Android package name.
   * @param pkgName the package name of an installed application.
   * @return the calculated UAppID for the application.
   */
  public static String getUAppID(Context context, String pkgName)
  {
    PackageManager  pkgManager = context.getPackageManager();
    StringBuilder   hashConcat = new StringBuilder();
    File            apkFile    = null;
    
    try
    {
      CertificateFactory cf       = CertificateFactory.getInstance(CERT_FORMAT);
      PackageInfo        pkgInfo  = pkgManager.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);   
      Signature[]        sigs     = pkgInfo.signatures;
      ApplicationInfo    appInfo  = pkgInfo.applicationInfo;
      
      apkFile = new File(appInfo.sourceDir);
      
      for (Signature sig : sigs)
      {
        byte[]          cert = sig.toByteArray();
        InputStream     input = new ByteArrayInputStream(cert);
        X509Certificate c = (X509Certificate) cf.generateCertificate(input);
              
        MessageDigest md = MessageDigest.getInstance(HASH_ALGO);
        md.update(c.getEncoded(), 0, c.getEncoded().length);
        byte[] hashBytes = md.digest();
        
        String hash = toHexString(hashBytes);
        Log.v(TAG, "Cert SHA1 Sum: " + hash);
        
        hashConcat.append(hash);
        hashConcat.append(',');
      }
    } catch(PackageManager.NameNotFoundException e) {
      Log.e(TAG, "Unable to find package \""+pkgName +"\"");
      return null;
    } catch(CertificateException e) {
      Log.e(TAG, "Exception attempting to parse x509 certificate(s) for package \""+ pkgName +"\"");
      return null;
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, "Device does not support SHA1 MessageDisgest algorithm");
      return null;
    }
    
    if(!apkFile.exists() || !apkFile.canRead())
    {
      Log.e(TAG, "Unable to read APK file for package \""+ pkgName +
                 "\" at \""+ apkFile.getAbsolutePath() +"\"");
      return null;
    }
    
    //TODO: Bundle up hashConcat.toString(); and the binHash and return together.
    return getBinaryHash(apkFile);
  }

  /**
   * Computes the SHA1 hash of a binary file.
   * @param apkPath the path to a binary file to hash.
   * @return the SHA1 hash of the file provided as an argument.
   */ 
  private static String getBinaryHash(File apk)
  {
    try
    {
      MessageDigest md = MessageDigest.getInstance("SHA1");
      FileInputStream fis = new FileInputStream(apk);
      
      byte[] dataBytes = new byte[1024];
      int nread = 0;
      
      while ((nread = fis.read(dataBytes)) != -1)
        md.update(dataBytes, 0, nread);

      byte[] mdbytes = md.digest();
      String sha1Print = toHexString(mdbytes);
      Log.v(TAG, "APK SHA1 Sum: " + sha1Print);
      
      return sha1Print;
    } catch(IOException e) {
      Log.e(TAG, "Error reading \""+ apk.getAbsolutePath() +"\" to compute SHA1 hash.");
      return null;
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, "Device does not support SHA1 MessageDisgest algorithm");
      return null;
    }
  }
  
  /**
   * Computes the base 16 representation of the byte array argument.
   * @param bytes an array of bytes.
   * @return the bytes represented as a string of hexadecimal digits.
   */
  public static String toHexString(byte[] bytes)
  {
    BigInteger bi = new BigInteger(1, bytes);
    return String.format("%0" + (bytes.length << 1) + "X", bi);
  }
}
