package model.database

import model.Task

class TestingDatabase extends DatabaseAPI {

  val tskTest: Task = new Task("Test", "testing to see if estimation shows up", "1", "3 hours")

  var data: List[Task] = List(tskTest)


  override def addTask(task: Task): Unit = {
    data ::= task
  }


  override def completeTask(taskId: String): Unit = {
    data = data.filter(_.id != taskId)
  }


  override def getTasks: List[Task] = {
    data.reverse
  }



}
