package model

import com.corundumstudio.socketio.listener.{ConnectListener, DataListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import model.database.{Database, DatabaseAPI, TestingDatabase}
import play.api.libs.json.{JsValue, Json}


class TodoServer() {

  val database: DatabaseAPI = if (Configuration.DEV_MODE) {
    new TestingDatabase
  } else {
    new Database
  }

  setNextId()

  var usernameToSocket: Map[String, SocketIOClient] = Map()
  var socketToUsername: Map[SocketIOClient, String] = Map()

  val config: Configuration = new Configuration {
    setHostname("0.0.0.0")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)

  server.addConnectListener(new ConnectionListener(this))
  server.addEventListener("add_task", classOf[String], new AddTaskListener(this))
  server.addEventListener("complete_task", classOf[String], new CompleteTaskListener(this))
  server.addEventListener("reorder", classOf[Array[String]], new ReorderListener(this))

  server.start()

  def tasksJSON(): String = {
    val tasks: List[Task] = database.getTasks
    val tasksJSON: List[JsValue] = tasks.map((entry: Task) => entry.asJsValue())
    Json.stringify(Json.toJson(tasksJSON))
  }

  def setNextId(): Unit = {
    val tasks = database.getTasks
    if (tasks.nonEmpty) {
      Task.nextId = tasks.map(_.id.toInt).max + 1
    }
  }

}

object TodoServer {
  def main(args: Array[String]): Unit = {
    new TodoServer()
  }
}


class ConnectionListener(server: TodoServer) extends ConnectListener {

  override def onConnect(socket: SocketIOClient): Unit = {
    socket.sendEvent("all_tasks", server.tasksJSON())
  }

}


class AddTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
    val task: JsValue = Json.parse(taskJSON)
    val title: String = (task \ "title").as[String]
    val description: String = (task \ "description").as[String]

    server.database.addTask(Task(title, description))
    server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
  }

}


class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
    server.database.completeTask(taskId)
    server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
  }

}

class ReorderListener(server: TodoServer) extends DataListener[Array[String]] {

  //data(0) is the initial position
  //data(1) is the new position

  override def onData(client: SocketIOClient, data: Array[String], ackSender: AckRequest): Unit = {
    val aData: Array[String] = data(0).split(",")
    val origTasks: List[Task] = server.database.getTasks
    var newTasks: List[Task] = List()
    Task.nextId = 0

    //Checks if target position is higher(false) or lower(true)
    var isHigherPriority = false
    if(aData(0) >= aData(1)) {
      isHigherPriority = true
    }

    for(task <- origTasks) {
      if(task.id == aData(0)) {
        newTasks ::= new Task(task.title, task.description, aData(1))
      }
      else if(isHigherPriority) {
        if(aData(1) <= task.id && task.id < aData(0)) {
          newTasks ::= new Task(task.title, task.description, (task.id.toInt + 1).toString)
        }
        else {
          newTasks ::= task
        }
      }
      else {
        if(aData(1) >= task.id && task.id > aData(0)) {
          newTasks ::= new Task(task.title, task.description, (task.id.toInt - 1).toString)
        }
        else {
          newTasks ::= task
        }
      }
      server.server.getBroadcastOperations.sendEvent("clear", task.id)
    }
    newTasks = newTasks.sortBy(_.id.toInt)

    for(task <- newTasks) {
      server.server.getBroadcastOperations.sendEvent("add", task.title + "`" + task.description)
    }
  }
}

