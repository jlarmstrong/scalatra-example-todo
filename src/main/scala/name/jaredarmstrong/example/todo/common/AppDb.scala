package name.jaredarmstrong.example.todo
package common

/**
 * Created with IntelliJ IDEA.
 * User: jarmstrong
 * Date: 6/9/12
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */



import java.sql._
import javax.sql._

import com.jolbox.bonecp.BoneCP
import com.jolbox.bonecp.BoneCPConfig
import com.jolbox.bonecp.Statistics
import com.jolbox.bonecp._
import com.jolbox.bonecp.hooks._

import net.liftweb.json._
import net.liftweb.json.Serialization.{read, write}

trait AppDb extends Loggable {

  private var db:BoneCPDataSource = null
  private val boneSettings = new BoneCPConfig()

  /**
   *
   *
   * @param conf
   * @param name
   */
  def boneSetup( name: String ="default") {
    db.setJdbcUrl("jdbc:h2:mem:todos")
    db.setUsername("sa")



    /*
    val autocommit = true
    val isolation = Connection.TRANSACTION_NONE
    val readOnly = false


    db.setConnectionHook(new AbstractConnectionHook {

      override def onCheckIn(connection: ConnectionHandle) {
        if (logger.isTraceEnabled) {
          logger.trace("Check in connection [%s leased]".format(db.getTotalLeased))
        }
      }

      override def onCheckOut(connection: ConnectionHandle) {
        connection.setAutoCommit(true)
        connection.setTransactionIsolation(isolation)
        connection.setReadOnly(readOnly)
        logger.info("Check out connection [%s leased]".format(db.getTotalLeased))
      }

    })

    */

    // Pool configuration
    db.setPartitionCount(2)
    db.setMaxConnectionsPerPartition(30)
    db.setMinConnectionsPerPartition(5)
    db.setAcquireIncrement(2)
    db.setAcquireRetryAttempts(10)
    db.setAcquireRetryDelayInMs(1000)
    db.setConnectionTimeoutInMs(1000)
    db.setIdleMaxAge((1000 * 60 * 10), java.util.concurrent.TimeUnit.MILLISECONDS)
    db.setMaxConnectionAge((1000 * 60 * 60), java.util.concurrent.TimeUnit.MILLISECONDS)
    db.setDisableJMX(true)
    db.setIdleConnectionTestPeriod((1000 * 60), java.util.concurrent.TimeUnit.MILLISECONDS)

    db.setLogStatementsEnabled(true)


  }

  def setupConnection() {
    if(db == null){
      Class.forName("org.h2.Driver")


      db = new BoneCPDataSource()
      this.boneSetup()

    }

  }

  def shutdownPool(ds: DataSource) = {
    ds match {
      case ds: BoneCPDataSource => ds.close()
      //case _ => error(" - could not recognize DataSource, therefore unable to shutdown this pool")
    }
  }

  // TODO: Implement named connections
  def getConnection(name: String, autocommit: Boolean = true) = {
    this.setupConnection()

    val conn = db.getConnection()
    conn.setAutoCommit(autocommit)

    conn
  }

  def withConnection[A](name: String)(block: Connection => A): A = {
    val connection = getConnection(name) // new AutoCleanConnection(getConnection(name))
    try {
      //stats()
      block(connection)

    } finally {
      connection.close()
    }

  }

  def withTransaction[A](name: String)(block: Connection => A): A = {
    withConnection(name) { connection =>
      try {
        connection.setAutoCommit(false)
        val r = block(connection)
        connection.commit()
        r
      } catch {
        case e => connection.rollback(); throw e
      }
    }
  }

  def stats() = {
    /*val stats: Statistics = new Statistics(db);
    println("--------  Connection: --------");
    println("Tot Conn Created:   " + stats.getTotalCreatedConnections());
    println("Tot Free Conn:      " + stats.getTotalFree());
    println("Tot Leased Conn:    " + stats.getTotalLeased());*/
  }

}

object AppDb extends AppDb {

  /** The exception we are throwing. */
  private def error = throw new Exception("DB plugin is not registered.")

  def getDataSource = {
    this.setupConnection()

    this.db
  }

  def withConnection[A](block: Connection => A): A = {
    this.withConnection("default")(block)
  }


  def withTransaction[A](block: Connection => A): A = {
    this.withTransaction("default")(block)
  }

}





/**
 * A helper that will JSON serialize the Anorm Pk[id] fields
 */
import anorm._
object PkSerializer extends Serializer[anorm.Pk[Int]] {
  private val Class = classOf[anorm.Pk[Int]]


  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), anorm.Pk[Int]] = {
    case (TypeInfo(Class, _), json) => json match {
      case JInt(iv) => Id(iv.toInt)
      case JDouble(dv) => Id(dv.toInt)
      case value => throw new MappingException("Can't convert " + value + " to " + Class)
    }
  }

  def serialize(implicit format: Formats): PartialFunction[Any, JValue] = {
    case Id(d) => { JInt(d.toString.toInt) }

  }
}