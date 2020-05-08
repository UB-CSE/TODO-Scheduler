package model.database

import model.Task

class TestingDatabase extends DatabaseAPI {

  var data: List[Task] = List()

  def compareTasks(t1: Task, t2: Task): Boolean = { //data sorter, by priority level
    t1.priority.toInt < t2.priority.toInt
  }


  override def addTask(task: Task): Unit = {
    //only uses priority 1-4
    if(task.priority == "1" || task.priority == "2" || task.priority == "3" || task.priority == "4") {
      data ::= task
    }
  }


  override def completeTask(taskId: String): Unit = {
    data = data.filter(_.id != taskId)
  }


  override def getTasks: List[Task] = { //sorts data
    data = data.sortWith(compareTasks)
    data = data.reverse
    data
  }

  override def clearTasks: Unit = { //empties list
    data = List()
  }


}
