package model.database

import model.Task

class TestingDatabase extends DatabaseAPI {

  var data: List[Task] = List()


  override def addTask(task: Task): Unit = {
    data ::= task
  }


  override def completeTask(taskId: String): Unit = {
    data = data.filter(_.id != taskId)
  }


  override def getTasks: List[Task] = {
    data.reverse
  }

  override def claimTasks(taskId: String, username: String): Unit = {
    for (i <- data) {
      if (i.id == taskId){
        i.doer = username
      }
    }
  }

}
