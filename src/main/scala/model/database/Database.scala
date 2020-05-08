package model.database

import java.sql.{Connection, DriverManager, ResultSet}

import model.Task


class Database extends DatabaseAPI{

  val url = "jdbc:mysql://mysql/todo?autoReconnect=true"
  val username: String = sys.env("DB_USERNAME")
  val password: String = sys.env("DB_PASSWORD")

  var connection: Connection = DriverManager.getConnection(url, username, password)
  setupTable()


  def setupTable(): Unit = {
    val statement = connection.createStatement()
    statement.execute("CREATE TABLE IF NOT EXISTS tasks (title TEXT, description TEXT, id TEXT)")
  }


  override def addTask(task: Task): Unit = {
    val statement = connection.prepareStatement("INSERT INTO tasks VALUE (?, ?, ?, ?, ?, ?, ?, ?, ?)")

    statement.setString(1, task.title)
    statement.setString(2, task.description)
    statement.setString(3, task.time10)
    statement.setString(4, task.time11)
    statement.setString(5, task.time12)
    statement.setString(6, task.time20)
    statement.setString(7, task.time21)
    statement.setString(8, task.time22)
    statement.setString(9, task.id)

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
      val time10 = result.getString("time10")
      val time11 = result.getString("time11")
      val time12 = result.getString("time12")
      val time20 = result.getString("time20")
      val time21 = result.getString("time21")
      val time22 = result.getString("time22")
      val id = result.getString("id")
      tasks = new Task(title, description, time10, time11, time12, time20, time21, time22, id) :: tasks
    }

    tasks.reverse
  }

}







