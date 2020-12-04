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

  def sortByPriority(a: Task, b: Task): Boolean = {
    a.priority > b.priority
  }

  override def getTasks: List[Task] = {
    data.sortWith(sortByPriority)
  }

}
