package model.database

import java.sql.{Connection, DriverManager, ResultSet}

import model.Task


class Database extends DatabaseAPI{

  val url = "jdbc:mysql://localhost:3306/mysql"//"jdbc:mysql://mysql/todo?autoReconnect=true"
  val username: String = "root" //sys.env("DB_USERNAME")
  val password: String = null//sys.env(""DB_PASSWORD")

  var connection: Connection = DriverManager.getConnection(url, username, password)
  setupTable()

  var tempList: List[Map[String, String]] = List()
  var tempIdList: List[String] = List()


  def setupTable(): Unit = {
    val statement = connection.createStatement()
    statement.execute("CREATE TABLE IF NOT EXISTS tasks (title TEXT, description TEXT, id TEXT)")
  }


  override def addTask(task: Task): Unit = {
    val statement = connection.prepareStatement("INSERT INTO tasks VALUE (?, ?, ?)")

    statement.setString(1, task.title)
    statement.setString(2, task.description)
    statement.setString(3, task.id)

    statement.execute()
  }


  override def addTemp(task: Task): Unit = {
    tempList = tempList :+ Map("title" -> task.title, "description" -> task.description, "id" -> task.id)
    tempIdList = tempIdList :+ task.id
  }


  override def completeTask(taskId: String): Unit = {
    if(tempIdList.contains(taskId)){
      var finished: Int = 0
      var newList: List[Map[String, String]] = List()
      var tempIteration: Int = 0
      var position: Int = 0
      for(item <- tempList){
        if(item.apply("id") == taskId){
          position = tempIteration
        }
        tempIteration += 1
      }
      while(finished != tempIteration){
        if(finished != position){
          newList = newList :+ tempList(finished)
        }
        finished += 1
      }
      tempList = newList
    }
    else {
      val statement = connection.prepareStatement("DELETE FROM tasks WHERE id=?")

      statement.setString(1, taskId)

      statement.execute()
    }
  }


  override def getTasks: List[Task] = {
    var priorityTask: Task = null
    val statement = connection.prepareStatement("SELECT * FROM tasks")
    val result: ResultSet = statement.executeQuery()

    var tasks: List[Task] = List()

    if(tempIdList != List()){
      for(item <- tempList){
        val iid = item.apply("id")
        val idesc = item.apply("description")
        val ititle = item.apply("title")
        tasks = new Task(ititle, idesc, iid) :: tasks
      }
    }

    while (result.next()) {
      val title = result.getString("title")

      var index: Int = 0
      var front: String = ""
      if(title.length >2) {
        for (char <- title) {
          if (index < 3){
            front += char
            index +=1
          }
        }
      }

      val description = result.getString("description")
      val id = result.getString("id")

      if( front == "P: ") {
        priorityTask = new Task(title, description, id)
      }
      else {
        tasks = new Task(title, description, id) :: tasks
      }
    }



    tasks = tasks.reverse
    if(priorityTask != null) {
      tasks = priorityTask :: tasks
    }
    tasks
  }

}







