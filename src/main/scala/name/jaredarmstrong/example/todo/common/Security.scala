package name.jaredarmstrong.example.todo
package common

/**
 * Created with IntelliJ IDEA.
 * User: jarmstrong
 * Date: 6/9/12
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */

import _root_.java.io.{InputStream, ByteArrayOutputStream, ByteArrayInputStream, Reader, File, FileInputStream, BufferedReader, InputStreamReader}
import _root_.java.security.{SecureRandom, MessageDigest}
import _root_.javax.crypto._
import _root_.javax.crypto.spec._

object Security {

  val salt = "64ry4t478reftiw4t04tnefy7h3w37h8w78n3r7r#@$78uefwfywe7fyewbfwufew78f"

  /** create a SHA hash from a Byte array */
  def hash(in : String) : Array[Byte] = {
    (MessageDigest.getInstance("SHA")).digest( (in+this.salt).getBytes )
  }


}
