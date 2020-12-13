package model.database

import model.Task

class TestingDatabase extends DatabaseAPI {

  var data: List[Task] = List()


  override def addTask(task: Task): Unit = {
    data ::= task
  }

  override def getLastTaskId(): Int = {
    var id = 0
    for(task <- data){
      if(task.id.toInt > id){
        id = task.id.toInt
      }
    }
    id
  }


  override def completeTask(taskId: String): Unit = {
    data = data.filter(_.id != taskId)
  }


  override def getTasks: List[Task] = {
    data.reverse
  }

}
