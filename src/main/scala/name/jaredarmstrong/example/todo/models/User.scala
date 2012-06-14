package name.jaredarmstrong.example.todo
package models

/**
 * Created with IntelliJ IDEA.
 * User: jarmstrong
 * Date: 6/9/12
 * Time: 7:51 PM
 * To change this template use File | Settings | File Templates.
 */

import anorm._
import anorm.SqlParser._

import java.util.Date
import java.security.SecureRandom

import common.{Loggable, Security, AppDb}

case class User(
                 id: Pk[Int],
                 first_name: String,
                 last_name: String,
                 email: String,
                 password: String,
                 rememberMe_token: Option[String]

                 ) {

  def userIdAsString = this.id.toString()

  def idAsInt: Int = userIdAsString.toInt

  def rememberMe = {
    val token = User.saveRememberMeToken(id.asInstanceOf[Int],"")

    this.copy(rememberMe_token = Some(token))

    token
  }

  def forgetMe() {
    User.forgetMe(id.asInstanceOf[Int])
  }
}

object User extends Loggable {
  val tableName = "users"

  ///implicit val connection =  ACdb.connection

  val simple = {
    get[Pk[Int]]("users.id") ~
      get[String]("users.first_name") ~
      get[String]("users.last_name") ~
      get[String]("users.email") ~
      get[String]("users.password") ~
      { get[Option[String]]("users.rememberMe_token") ? } map {
      case id  ~ first_name ~ last_name ~ email ~ password ~ rememberMe_token => User(
        id, first_name, last_name, email, password, rememberMe_token.get
      )
     /* case id  ~ first_name ~ last_name ~ email => User(
        id, first_name, last_name, email, "",""
      )*/
    }
  }

  def create(u: User): Int = {
    var ret: Int = -1

    AppDb.withConnection {
      implicit connection =>

        try {
          ret = SQL("INSERT INTO users(first_name,last_name,email,password,rememberMe_token) values({first_name},{last_name},{email},{password},{rememberMe_token})").on(
          'first_name -> u.first_name,
            'last_name -> u.last_name,
            'email -> u.email,
            'password -> Security.hash(u.password.trim()),
            'rememberMe_token -> ""
          ).executeInsert().get.toInt
        }catch{
          case e => logger.info("Invalid user id returned")
        }
    }

    ret
  }

  def update(){
    // TODO
  }

  def generateToken(s: String): String = {
    val random = SecureRandom.getInstance("SHA1PRNG")
    val str = new Array[Byte](16)
    random.nextBytes(str)
    //return
    str.toString
  }


  def validateRememberToken(token: String): Option[User] = {

    AppDb.withConnection {
      implicit connection =>

        SQL("SELECT users.* FROM users WHERE rememberMe_token={token}").on(
          "token" -> token
        ).as(User.simple.singleOpt)

    }

  }

  def login(u: String, p: String): Option[User] = {
    AppDb.withConnection {
      implicit connection =>

        SQL("SELECT users.* FROM users WHERE email = {email} AND password = {password}").on(
          'email -> u,
          'password -> Security.hash(p)
        ).as(User.simple.singleOpt)
    }
  }


  def find(id: String): Option[User] = {
    this.findById(id)
  }

  def findById(id: String) = {
    AppDb.withConnection {
      implicit connection =>

        SQL("SELECT users.* FROM users WHERE id = {id}").on(
          'id -> id
        ).as(User.simple.singleOpt)
    }

  }

  def findByLogin(login: String) = {
    AppDb.withConnection {
      implicit connection =>

        val u = SQL("SELECT TOP 1 users.* FROM users WHERE email = {login}").on(
          'login -> login
        ).as(User.simple.singleOpt)

        u
    }
  }

  def findExists(login: String) = {
    AppDb.withConnection {
      implicit connection =>
        try {

          val u: Long =SQL({
            "SELECT COUNT(*) as count FROM " + tableName + " WHERE email = {login}"
          }).on(
            'login -> login
          ).as(scalar[Long].single)

          if(u >= 1L ){
            true
          }else{
            false
          }

        } catch {
          case e: Exception => {
            false
          } // refactor to singleOpt
        }

    }
  }

  def findAll() = {
    AppDb.withConnection {
      implicit connection =>
        SQL("SELECT users.* FROM users").as(User.simple *)

    }
  }

  def forgetMe(id: Int) {
    AppDb.withConnection {
      implicit connection =>

      val upd = SQL("UPDATE users SET rememberMe='' WHERE id={id}").on("id" -> id).executeUpdate()

    }
  }

  def rememberMe(): String = {
    ""
  }

  def saveRememberMeToken(id: Int, t: String):String = {
    var nT = t
    if (t == "" || t == null) {
      val dt = new Date()
      nT = Security.hash( (dt.toString + id.toString) ).toString
    }

    AppDb.withTransaction {
      implicit connection =>
        SQL("UPDATE users SET rememberMe_token={token} WHERE id={id}").on(
          "id" -> id,
          "{token}" -> nT
        ).executeUpdate()
    }

    nT

  }

}
