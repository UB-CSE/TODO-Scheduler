package model.database

import java.sql.{Connection, DriverManager, ResultSet}

import model.Task


class Database extends DatabaseAPI{

  val url = "jdbc:mysql://localhost/mysql?serverTimezone=UTC"

  val username: String = "root"

//    sys.env("DB_USERNAME")
  val password: String = "ayushira"
//    sys.env("DB_PASSWORD")

  var connection: Connection = DriverManager.getConnection(url, username, password)
  setupTable()


  def setupTable(): Unit = {
    val statement = connection.createStatement()

    statement.execute("CREATE TABLE IF NOT EXISTS tasks (title TEXT, description TEXT, deadline TEXT, id TEXT)")
  }


  override def addTask(task: Task): Unit = {
    val statement = connection.prepareStatement("INSERT INTO tasks VALUE (?, ?, ?, ?)")

    statement.setString(1, task.title)
    statement.setString(2, task.description)
    statement.setString(4, task.id)
    statement.setString(3, task.deadline)


    statement.execute()
  }


  override def completeTask(taskId: String): Unit = {
    val statement = connection.prepareStatement("DELETE FROM tasks WHERE id=?")

    statement.setString(1, taskId)

    statement.execute()
  }


  override def getTasks: List[Task] = {
    val statement = connection.prepareStatement("SELECT * FROM tasks")
    val result: ResultSet = statement.executeQuery()

    var tasks: List[Task] = List()

    while (result.next()) {
      val title = result.getString("title")
      val description = result.getString("description")
      val deadline = result.getString("deadline")
      val id = result.getString("id")
      tasks = new Task(title, description, id , deadline) :: tasks
    }


    tasks.reverse
  }

}







