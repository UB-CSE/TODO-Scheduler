package model.database

import java.sql.{Connection, DriverManager, ResultSet}

import com.roundeights.hasher.Implicits._

import scala.language.postfixOps
import model.{Task, User}


class Database extends DatabaseAPI{

  val url = "jdbc:mysql://mysql/todo?autoReconnect=true"
  val username: String = sys.env("DB_USERNAME")
  val password: String = sys.env("DB_PASSWORD")


  //local test DB
  //val url = "jdbc:mysql://localhost/mysql?serverTimezone=UTC"
  //val username: String = "root"
  //val password: String = "123456789"

  var connection: Connection = DriverManager.getConnection(url, username, password)
  setupTable()


  def setupTable(): Unit = {
    val statement = connection.createStatement()

    //Use in tests
    //statement.execute("DROP TABLE IF EXISTS tasks")
    //statement.execute("DROP TABLE IF EXISTS users")
    //statement.execute("CREATE TABLE IF NOT EXISTS tasks (title TEXT, description TEXT, id TEXT, userId TEXT)")
    //statement.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, username TEXT, password TEXT)")


    // Use in production
    statement.execute("CREATE TABLE IF NOT EXISTS tasks (title TEXT, description TEXT, id TEXT, userId TEXT)")
    statement.execute("CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY AUTO_INCREMENT, username TEXT, password TEXT)")
  }


  override def addTask(task: Task): Unit = {
    val statement = connection.prepareStatement("INSERT INTO tasks VALUE (?, ?, ?, ?)")

    statement.setString(1, task.title)
    statement.setString(2, task.description)
    statement.setString(3, task.id)
    statement.setString(4, task.uId)

    statement.execute()
  }


  override def completeTask(taskId: String): Unit = {
    val statement = connection.prepareStatement("DELETE FROM tasks WHERE id=?")

    statement.setString(1, taskId)

    statement.execute()
  }


  override def getTasks(): List[Task] = {
    val statement = connection.prepareStatement("SELECT * FROM tasks WHERE userId=(?)")
    statement.setString(1, 0.toString)
    val result: ResultSet = statement.executeQuery()

    var tasks: List[Task] = List()

    while (result.next()) {
      val title = result.getString("title")
      val description = result.getString("description")
      val id = result.getString("id")
      val userId = result.getString("userId")
      tasks = new Task(title, description, id, userId) :: tasks
    }
    tasks.reverse
  }

  def getPersonalTasks(user: User): List[Task] = {
    val statement = connection.prepareStatement("SELECT * FROM tasks WHERE userId=(?)")
    statement.setString(1, user.id)
    val result: ResultSet = statement.executeQuery()

    var tasks: List[Task] = List()

    while (result.next()) {
      val title = result.getString("title")
      val description = result.getString("description")
      val id = result.getString("id")
      val userId = result.getString("userId")
      tasks = new Task(title, description, id, userId) :: tasks
    }

    tasks.reverse
  }

  override def registerUser(username: String, password: String): Boolean = {
    password.salt("cse116IsTheBest")
    password.bcrypt

    val statement = connection.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)")
    val user: User = getUser(username)

    statement.setString(1, username)
    statement.setString(2, password)

    if (user.username == "" && user.password == "" && user.id == ""){
      statement.execute()
      true
    } else {
      false
    }

  }

  override def loginUser(username: String, password: String): Boolean = {
    val user: User = getUser(username)

    if (user.username == "" && user.password == "" && user.id == ""){
      false
    } else {
      true
    }
  }

  override def getUser(username: String): User = {
    val statement = connection.prepareStatement("SELECT * FROM users WHERE username=(?) LIMIT 1")

    statement.setString(1, username)

    val result: ResultSet = statement.executeQuery()

    var user: User = new User("", "", "")

    while (result.next()) {
      val username = result.getString("username")
      val passwordHash = result.getString("password")
      val id = result.getString("id")
      user = new User(username, passwordHash, id)
    }

    user
  }

}







