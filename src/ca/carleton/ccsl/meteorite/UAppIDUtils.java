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
  private static final String TAG                   = UAppIDUtils.class.getCanonicalName();
  private static final String HASH_ALGO             = "SHA1";
  private static final String CERT_FORMAT           = "X509";
  private static final char   SIGNATURE_DELIMINATOR = ' ';

  /**
   * Creates a UAppRecord object for a given Android package name.
   * In the event of error a message will be logged to the error console and
   * a null value will be returned.
   * 
   * @param pkgName the package name of an installed application.
   * @return a UAppRecord object for the application.
   */
  public static UAppRecord getUApp(Context context, String pkgName)
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
        byte[]          certData = sig.toByteArray();
        InputStream     input    = new ByteArrayInputStream(certData);
        X509Certificate certOb   = (X509Certificate) cf.generateCertificate(input);             
        String          hash     = hashBytes(certOb.getEncoded());
        
        Log.v(TAG, "Cert SHA1 Fingerprint: " + hash);
        hashConcat.append(hash);
        hashConcat.append(SIGNATURE_DELIMINATOR);
      }
    } catch(PackageManager.NameNotFoundException e) {
      Log.e(TAG, "Unable to find package \""+pkgName +"\"");
      return null;
    } catch(CertificateException e) {
      Log.e(TAG, "Exception attempting to parse x509 certificate(s) for package \""+ pkgName +"\"");
      return null;
    } 
    
    if(!apkFile.exists() || !apkFile.canRead())
    {
      Log.e(TAG, "Unable to read APK file for package \""+ pkgName +
                 "\" at \""+ apkFile.getAbsolutePath() +"\"");
      return null;
    }
    
    String     uAppIDString = pkgName + SIGNATURE_DELIMINATOR + hashConcat.toString();
    String     uAppID       = hashBytes(uAppIDString.getBytes());
    UAppRecord uAppRec      = new UAppRecord(pkgName);

    uAppRec.setApkFile(apkFile);
    uAppRec.setBinHash(getBinaryHash(apkFile));
    uAppRec.setUAppID(uAppID);
   
    return uAppRec;
  }
  
  /**
   * Computes a hash of the provided byte array, returning it in hex string format.
   * @param input an array of bytes to hash.
   * @return a base 16 string of hexadecimal digits.
   */
  private static String hashBytes(byte[] input)
  {    
    try
    {
      MessageDigest md        = MessageDigest.getInstance(HASH_ALGO);
      byte[]        hashBytes = md.digest(input);     
      String        hash      = toHexString(hashBytes);
      
      md.reset();
      return hash;
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, "Device does not support SHA1 MessageDisgest algorithm");
      return null;
    }
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
      //Note: didn't use sha1Hash() here as we're blocking 1024 bytes at a time
      //into the MessageDigest rather than feeding it a stable byte array
      MessageDigest md = MessageDigest.getInstance("SHA1");
      FileInputStream fis = new FileInputStream(apk);
      
      byte[] dataBytes = new byte[1024];
      int nread = 0;
      
      while ((nread = fis.read(dataBytes)) != -1)
        md.update(dataBytes, 0, nread);

      byte[] mdbytes = md.digest();
      return toHexString(mdbytes);
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
