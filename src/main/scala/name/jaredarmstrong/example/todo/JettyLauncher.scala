package name.jaredarmstrong.example.todo

/**
 * Based on https://github.com/casualjim/cedar
 */

import name.jaredarmstrong.example.todo.models.util.FlywayHelper

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{DefaultServlet, ServletContextHandler}
import org.eclipse.jetty.webapp.WebAppContext

object JettyLauncher {
  def main(args: Array[String]) {
    val port = if(System.getenv("PORT") != null) System.getenv("PORT").toInt else 8080

    // force the update to the db on start
    FlywayHelper.migrate()

    val server = new Server(port)
    val context = new WebAppContext()
    context setContextPath "/"
    context.setResourceBase("src/main/webapp")
    context.addServlet(classOf[CedarScalatraServlet], "/*")
    //context.addServlet(classOf[DefaultServlet], "/")

    server.setHandler(context)

    server.start
    server.join
  }

}
