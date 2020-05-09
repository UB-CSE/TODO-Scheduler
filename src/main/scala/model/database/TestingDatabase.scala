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

  override def sortTasks(sortType:String):Unit={
    if(sortType=="Priority"){
      data = data.sortWith(_.priority<_.priority) //Sort the data with priority
    }
    else if (sortType=="Date"){
      data= data.sortWith(_.dueDate>_.dueDate) //Sort the data with due date
    }
  }

  override def singleTask(taskId: String): Task = {
    data.filter(_.id == taskId).head //Return the task according to taskId
  }
}
