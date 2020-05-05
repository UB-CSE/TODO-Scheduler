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
    statement.execute("CREATE TABLE IF NOT EXISTS tasks (title TEXT, description TEXT, id TEXT, comments TEXT)")
  }


  override def addTask(task: Task): Unit = {
    val statement = connection.prepareStatement("INSERT INTO tasks VALUE (?, ?, ?, ?)")

    statement.setString(1, task.title)
    statement.setString(2, task.description)
    statement.setString(3, task.id)
    statement.setString(4, task.comments)

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
      val id = result.getString("id")
      val comments = result.getString("comments")
      tasks = new Task(title, description, id, comments) :: tasks
    }

    tasks.reverse
  }

  override def addComment(taskId: String, commentToAdd: String): Unit = {
    val allTasks: List[Task] = getTasks
    var comments: String = ""
    for (task <- allTasks) {
      if (task.id == taskId) {
        comments = task.comments
      }
    }
    val newComments: String = comments + "_____" + commentToAdd

    val statement = connection.prepareStatement("UPDATE tasks SET comments=? WHERE id=?")
    statement.setString(1, newComments)
    statement.setString(2, taskId)
    statement.execute

  }

}







