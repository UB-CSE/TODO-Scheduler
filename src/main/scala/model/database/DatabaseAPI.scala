package model.database

import model.Task

trait DatabaseAPI {

  def addTask(task: Task): Unit
  def completeTask(taskId: String): Unit
  def getTasks: List[Task]
  def switchView(s: String): List[Task]

}
