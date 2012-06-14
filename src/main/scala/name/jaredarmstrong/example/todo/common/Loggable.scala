package name.jaredarmstrong.example.todo
package common

/**
 * Created with IntelliJ IDEA.
 * User: jarmstrong
 * Date: 6/9/12
 * Time: 7:39 PM
 * To change this template use File | Settings | File Templates.
 */


import org.slf4j.{LoggerFactory}

trait Loggable {

  /**
   * The Logger instance. Created when we need it.
   */
  lazy val logger = LoggerFactory.getLogger(getClass)

  /**
   * Log a debug level message.
   *
   * @param msg Message to log, only evaluated if debug logging is
   *            enabled.
   * @param t Throwable, optional.
   */
  protected def debug(msg: => AnyRef, t: => Throwable = null): Unit = {
    if (logger.isDebugEnabled) {
      if (t != null) {
        logger.debug(msg.toString, t);
      } else {
        logger.debug(msg.toString)
      }
    }
  }
}