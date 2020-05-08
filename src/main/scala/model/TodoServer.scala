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
    val time10: String = (task \ "time10").as[String]
    val time11: String = (task \ "time11").as[String]
    val time12: String = (task \ "time12").as[String]
    val time20: String = (task \ "time20").as[String]
    val time21: String = (task \ "time21").as[String]
    val time22: String = (task \ "time22").as[String]

    server.database.addTask(Task(title, description, time10, time11, time12, time20, time21, time22))
    server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
  }

}


class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
    server.database.completeTask(taskId)
    server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
  }

}


