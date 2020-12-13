package model.database

import model.Task

trait DatabaseAPI {
  def addSubTask(id: String, description: String): Unit

  def addTask(task: Task): Unit
  def completeTask(taskId: String): Unit
  def getTasks: List[Task]

}
