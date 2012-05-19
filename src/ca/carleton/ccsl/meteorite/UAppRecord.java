package ca.carleton.ccsl.meteorite;

import java.io.File;
import java.io.Serializable;

/**
 * The UAppRecord class is a Java Bean for storing UApp related details.
 * 
 * Each UAppRecord corresponds to a package name/APK file installed on the users
 * phone. The UAppRecord contains the package name, the path to the APK file, 
 * a hash of the APK file and the UAppID for the record. 
 * 
 * @author dmccarney
 */
public class UAppRecord implements Serializable
{
  //Auto-generated serial version UID string.
  private static final long serialVersionUID = -1792659753578304905L;
  
  private String UAppID;
  private String binHash;
  private String pkgName;
  private File   apkFile;
  
  /**
   * Creates an empty UAppRecord.
   * All fields are left with null values until each explicit set() method is called.
   */
  public UAppRecord()
  {
    //NOP -- default constructor. Leaves everything at null until set()
  }
  
  /**
   * Creates a UAppRecord for the given package name.
   * All fields other than getPkgName() will return null until explicitly set().
   * @param packageName the package name this UAppRecord corresponds to.
   */
  public UAppRecord(String packageName)
  {
    this.pkgName = packageName;
  }

  /**
   * Get the application's UAppID string.
   * This value is a hash of:
   *   pkgName certHash1 certHash2 ... certHashN
   * 
   * Each 'field' of the UAppID string is delimited by a space character.
   * Depending on the app there may be 1 or more certificate hashes corresponding
   * to the number of certificates used to sign the app.
   * 
   * @return the uAppID
   */
  public String getUAppID()
  {
    return UAppID;
  }

  /**
   * Sets the UAppID for this UAppRecord.
   * See {@link getUAppID} for more information on the format of the UAppID
   * string.
   * 
   * @param uAppID the UAppID string for the UAppRecord.
   * @see getUAppID the UAppID string for this UAppRecord.
   */
  public void setUAppID(String uAppID)
  {
    UAppID = uAppID;
  }

  /**
   * Gets a hash of the APK binary corresponding to this UAppRecord.
   * @return the hash of the APK binary corresponding to this UAppRecord.
   */
  public String getBinHash()
  {
    return binHash;
  }

  /**
   * Set the hash of the APK binary corresponding to this UAppRecord.
   * @param binHash a hash of the bytes read from the APK binary.
   */
  public void setBinHash(String binHash)
  {
    this.binHash = binHash;
  }

  /**
   * Gets the package name corresponding to this UAppRecord.
   * @return the package name of the app corresponding to this UAppRecord.
   */
  public String getPkgName()
  {
    return pkgName;
  }

  /**
   * Set the package name of the app corresponding to this UAppRecord.
   * @param pkgName an application's package name.
   */
  public void setPkgName(String pkgName)
  {
    this.pkgName = pkgName;
  }

  /**
   * Gets a file object pointing to the APK associated with this UAppRecord.
   * @return a file object for the APK associated with this UAppRecord.
   */
  public File getApkFile()
  {
    return apkFile;
  }

  /**
   * Sets the file object for the APK associated with this UAppRecord.
   * @param apkFile a file object for the APK associated with this UAppRecord.
   */
  public void setApkFile(File apkFile)
  {
    this.apkFile = apkFile;
  }
}
