package tests

import model.Task
import model.database.TestingDatabase
import org.scalatest._

class testClearTasks extends FunSuite{

  test("check addTask functionality and clears"){

    val database1: TestingDatabase = new TestingDatabase

    val task1: Task = new Task("do this", "now", "1", "1")
    val task2: Task = new Task("task 2", "now", "2", "4")
    val task3: Task = new Task("task 3", "now", "3", "3")
    val faketask1: Task = new Task("faketask1", "fake", "4", "q")
    val faketask2: Task = new Task("faketask2", "fake", "4", "#!@$%^&*")
    val faketask3: Task = new Task("faketask3", "fake", "4", "86")
    val faketask4: Task = new Task("faketask4", "fake", "4", "-1")

    database1.addTask(task1)
    database1.addTask(task2)
    database1.addTask(task3)
    database1.addTask(faketask1)

    assert(database1.getTasks.size == 3)

    database1.clearTasks
    assert(database1.getTasks.size == 0)

    database1.addTask(task1)
    database1.addTask(faketask2)
    database1.addTask(faketask3)
    database1.addTask(faketask4)
    assert(database1.getTasks.size == 1)
  }

  test("test2"){
    val database2: TestingDatabase = new TestingDatabase

    val task1: Task = new Task("task 1", "now", "1", "1")
    val task2: Task = new Task("task 2", "now", "2", "4")
    val task3: Task = new Task("task 3", "now", "3", "3")
    val task4: Task = new Task("task 4", "now", "4", "1")
    val task5: Task = new Task("task 5", "now", "5", "2")
    val task6: Task = new Task("task 6", "now", "6", "4")
    val task7: Task = new Task("task 7", "now", "7", "4")
    val faketask1: Task = new Task("faketask1", "fake", "4", "q")
    val faketask2: Task = new Task("faketask2", "fake", "4", "#!@$%^&*")
    val faketask3: Task = new Task("faketask3", "fake", "4", "86")
    val faketask4: Task = new Task("faketask4", "fake", "4", "-1")

    database2.addTask(task1)
    database2.addTask(task2)
    database2.addTask(task3)
    database2.addTask(task4)
    database2.addTask(task5)
    database2.addTask(task6)
    database2.addTask(faketask1)
    database2.addTask(faketask2)

    assert(database2.getTasks.size == 6)

    database2.clearTasks
    database2.addTask(faketask3)
    assert(database2.getTasks.size == 0)

    database2.addTask(task7)
    database2.addTask(faketask4)
    assert(database2.getTasks.size == 1)

    database2.clearTasks
    assert(database2.getTasks.size == 0)
  }

}
