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
    statement.execute("CREATE TABLE IF NOT EXISTS tasks (title TEXT, description TEXT, deadline DATE, taskAdded DATE, priority NUMBER, estimated TEXT, id TEXT)")
  }


  override def addTask(task: Task): Unit = {
    val statement = connection.prepareStatement("INSERT INTO tasks VALUE (?, ?, ?, ?, ?, ?)")

    statement.setString(1, task.title)
    statement.setString(2, task.description)
    statement.setString(3, task.deadline)
    statement.setString(4, task.taskAdded.toString())
    statement.setString(5, task.priority.toString())
    statement.setString(6, task.estimated)
    statement.setString(7, task.id)

    statement.execute()
  }


  override def completeTask(taskId: String): Unit = {
    val statement = connection.prepareStatement("DELETE FROM tasks WHERE id=?")

    statement.setString(1, taskId)

    statement.execute()
  }


  override def getTasks: List[Task] = {
    val statement = connection.prepareStatement("SELECT * FROM tasks ORDER BY priority;")

    val result: ResultSet = statement.executeQuery()
    print(result)
    var tasks: List[Task] = List()

    while (result.next()) {
      val title = result.getString("title")
      val description = result.getString("description")
      val deadline = result.getString("deadline")
      val taskAdded = result.getDate("taskAdded")
      val priority = result.getInt("priority")
      val estimated = result.getString("estimated")
      val id = result.getString("id")
      tasks = new Task(title, description, deadline, taskAdded, estimated, priority, id) :: tasks
    }

    tasks.reverse
  }

}







