package model.database

import model.Task

trait DatabaseAPI {

  def addTask(task: Task): Unit
  def completeTask(taskId: String): Unit
  def getTasks: List[Task]
  def sortTasks(sortType:String): Unit
  def singleTask(taskId: String): Task //  return a Task according to taskID

}
