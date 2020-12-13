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

  override def addSubTask(id: String, description: String): Unit = {
    val temp = data.indexWhere(_.id == id)
    if(temp> -1 && description.length>0){
      data(temp).subs = " "+description :: data(temp).subs
    }
  }
}
