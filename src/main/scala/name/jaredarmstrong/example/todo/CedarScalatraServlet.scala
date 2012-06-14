package name.jaredarmstrong.example.todo

import org.scalatra._
import scalate.ScalateSupport
import anorm.Id

import name.jaredarmstrong.example.todo.common.{Loggable, Security, AppDb}
//import name.jaredarmstrong.example.todo.AuthenticationSupport
import name.jaredarmstrong.example.todo.models._



class CedarScalatraServlet extends ScalatraServlet
with ScalateSupport
with AuthenticationSupport
with FlashMapSupport
{

  private val oneWeek = 7 * 24 * 3600


  before() {
    if (!isAuthenticated) {
      scentry.authenticate('RememberMe)
    }
  }


  get("/") {
     <html>
       <body>
       Please <a href="/login">login</a>.
       </body>
     </html>
  }

  get("/login") {
    redirectIfAuthenticated

    contentType = "text/html"

    <html>
      <body>
        <h1>Login</h1>
        <div style="color: #F00;">{flash.getOrElse("error", "")}</div>
        <form method="post" action="/login">
          <div><label>Email:</label><input type="text" name="userName" value="test@test.com" /></div>
          <div><label>Password:</label><input type="password" name="password" /></div>
          <div><label>Remember Me:</label><input type="checkbox" name="rememberMe" value="true" /></div>
          <div><input type="submit" value="submit" /></div>
        </form>
      </body>
    </html>
  }


  post("/login") {
    redirectIfAuthenticated


    scentry.authenticate('UserPassword)

    var email: String = cookies.get("todos.email") match {
      case Some(v) => v.toString
      case None => ""
    }

    if (isAuthenticated) {
      logger.info("Success **")

      email = params.getOrElse("userName","").toString()
      session("email") = email

      response.addCookie(Cookie("todos.email", email)(CookieOptions(secure = false, maxAge = oneWeek, httpOnly = false)).toServletCookie)

      redirect(scentryConfig.returnTo)
    } else {
      logger.info("Failed Login **")
    }

    flash += ("error" -> "Unable to authenticate. Please try again.")

    redirect("/login")

  }

  get("/loggedin") {
    redirectIfNotAuthenticated

    contentType = "text/html"

    <html>
      <body>
        <h1>Hello, world!</h1>
        Welcome {user.email} you are logged in <a href="/todo">todos</a>. <a href="/logout">Logout</a>
      </body>
    </html>
  }


  get("/logout/?") {
    logOut

    redirect("/logout_step")
  }

  // this step is used to verify the cookies are erased
  get("/logout_step/?") {
    redirect("/login")
  }

  get("/register") {
    contentType = "text/html"

    <html>
      <body>
        <h1>Register</h1>
        <form method="post" action="/register">
          <div><label>First Name</label><input name="first_name" value="John" /></div>
          <div><label>Last Name</label><input name="last_name" value="Smith" /></div>
          <div><label>Email</label><input name="email" value="test@test.com" /></div>
          <div><label>Password</label><input name="password" /></div>
          <div><input type="submit" value="submit" /></div>
        </form>
      </body>
    </html>
  }

  post("/register") {
    val u = new User(new Id(0),params("first_name"),params("last_name"),params("email"),params("password"),None)


    val id = User.create(u)

    if(id <= 0 ){
      flash += ("error" -> "Unable to create your account. Please try again.")
      redirect("/register")
    }

    flash += ("success" -> "Account created. Please log in.")
    redirect("/login")
  }

  notFound {
    // Try to render a ScalateTemplate if no route matched
    findTemplate(requestPath) map { path =>
      contentType = "text/html"
      layoutTemplate(path)
    } orElse serveStaticResource() getOrElse resourceNotFound() 
  }
}
