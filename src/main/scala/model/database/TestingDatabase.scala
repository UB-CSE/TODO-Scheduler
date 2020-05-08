package model.database

import model.Task

class TestingDatabase extends DatabaseAPI {

  var data: List[Task] = List()


  override def addTask(task: Task): Unit = {
    data ::= task
    data = data.sortWith(_.priority.toInt > _.priority.toInt)



  }


  override def completeTask(taskId: String): Unit = {
    data = data.filter(_.id != taskId)
  }


  override def getTasks: List[Task] = {
    data
  }

}
