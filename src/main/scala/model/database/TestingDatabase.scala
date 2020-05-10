package model.database

import java.util.Calendar

import model.Task

class TestingDatabase extends DatabaseAPI {

  var data: List[Task] = List()
  var completeTask: List[Task] = List()

  override def addTask(task: Task): Unit = {
    data ::= task
  }


  override def completeTask(taskId: String): Unit = {
    val task = data.filter(_.id == taskId)
    data = data.filter(_.id != taskId)
    task.foreach(_.ending = Calendar.getInstance().getTime.toString)
    completeTask = completeTask ++ task
  }


  override def getTasks: List[Task] = {
    data.reverse
  }

  override def getCompleteTasks: List[Task] = {
    completeTask.reverse
  }

}
