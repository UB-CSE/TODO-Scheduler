package model.database

import model.Task

trait DatabaseAPI {

  // Tasks
  def addTask(task: Task): Unit
  def completeTask(taskId: String): Unit
  def getTasks: List[Task]

  // Users
  def registerUser(username: String, password: String): Boolean
  def loginUser(username: String, password: String): Boolean
}

