package model.database

import model.Task

class TestingDatabase extends DatabaseAPI {

  var data: List[Task] = List()

  override def addTask(task: Task): Unit = {
    if(!task.description.isEmpty && !task.title.isEmpty) {
      data :+= task
    }
  }

  override def completeTask(taskId: String): Unit = {
    var IDs: List[String] = List()
    for(i <- data){
      IDs :+= i.id
    }
//    println("IDs: " + IDs)
    var taskHold: Task = null
    for (i <- data) {
      if (i.id == taskId) {
        data = data.filter(_.id != taskId)
        taskHold = i
        i.id = "COMPLETED" + taskId
      }
    }
    data ::= taskHold
  }

  override def getTasks: List[Task] = {
    data.reverse
  }

  override def deleteTask(taskId: String): Unit = {
    data = data.filter(_.id != taskId)
  }

  override def undoTask(taskId: String): Unit = {
    var taskHold: Task = null
    for(i <- data){
      if(i.id == taskId){
        i.id = i.id.replace("COMPLETED", "")
        taskHold = i
        data = data.filter(_.id != i.id)
      }
    }
    data :+= taskHold
  }
}