package model.database

import model.{Task, User}

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

  override def registerUser(username: String, password: String): Boolean = { true }
  override def loginUser(username: String, password: String): Boolean = { true }
  def getUser(username: String): User = { new User("", "", "")}
  override def getPersonalTasks(user: User): List[Task] = { data.reverse }

}
