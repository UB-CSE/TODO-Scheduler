package model.database

import model.Task

trait DatabaseAPI {

  def addTask(task: Task): Unit
  def completeTask(taskId: String): Unit
  def getTasks: List[Task]
  def addComment(taskId: String, commentToAdd: String): Unit

}
