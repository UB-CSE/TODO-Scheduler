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
    statement.execute("CREATE TABLE IF NOT EXISTS tasks (title TEXT, description TEXT, id TEXT, priority TEXT)")
  }


  def compareTasks(t1: Task, t2: Task): Boolean = {    //sorts tasks by priority
    t1.priority.toInt < t2.priority.toInt
  }


  override def addTask(task: Task): Unit = {
    val statement = connection.prepareStatement("INSERT INTO tasks VALUE (?, ?, ?, ?)")
    //only adds priorities 1-4
    if(task.priority == "1" || task.priority == "2" || task.priority == "3" || task.priority == "4") {
      statement.setString(1, task.title)
      statement.setString(2, task.description)
      statement.setString(3, task.id)
      statement.setString(4, task.priority.toString)

      statement.execute()
    }
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
      val priority = result.getString("priority")   //priority added
      tasks = new Task(title, description, id, priority) :: tasks
    }

    tasks.sortWith(compareTasks) //sorted tasks
  }

  override def clearTasks: Unit = {
    val statement = connection.prepareStatement("DELETE FROM tasks") //clears all tasks from database
    statement.execute()
  }

}







