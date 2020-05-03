package model

import com.corundumstudio.socketio.listener.{ConnectListener, DataListener, DisconnectListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import model.database.{Database, DatabaseAPI, TestingDatabase}
import play.api.libs.json.{JsValue, Json}
import java.util.Date

class TodoServer() {

  val database: DatabaseAPI = if (Configuration.DEV_MODE) {
    new TestingDatabase
  } else {
    new Database
  }

  setNextId()

  var usernameToSocket: Map[String, SocketIOClient] = Map()
  var socketToUsername: Map[SocketIOClient, String] = Map()
  var socketToSortBy: Map[SocketIOClient, String] = Map()

  val config: Configuration = new Configuration {
    setHostname("0.0.0.0")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)

  server.addConnectListener(new ConnectionListener(this))
  server.addEventListener("add_task", classOf[String], new AddTaskListener(this))
  server.addEventListener("complete_task", classOf[String], new CompleteTaskListener(this))
  server.addEventListener("sort_tasks", classOf[String], new SortTasksListener(this))
  server.addDisconnectListener(new DisconnectionListener(this))
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

  def tasksJSON(sortBy: String): String = {
    val sortedTasks: List[Task] = sortBy match {
      case "newest" =>
        database.getTasks.sortWith((t1: Task, t2: Task) => t1.createdAt >= t2.createdAt)
      case "oldest" =>
        database.getTasks.sortWith((t1: Task, t2: Task) => t1.createdAt < t2.createdAt)
      case "deadline" =>
        database.getTasks.sortWith((t1: Task, t2: Task) => t1.deadline < t2.deadline)
    }
    val sortedJson = sortedTasks.map((entry: Task) => entry.asJsValue())
    Json.stringify(Json.toJson(sortedJson))
  }

  def updateTasksWithSort(): Unit = {
    socketToSortBy.keys.foreach((socket: SocketIOClient) => {
      socket.sendEvent("all_tasks", tasksJSON(socketToSortBy(socket)))
    })
  }

}

object TodoServer {
  def main(args: Array[String]): Unit = {
    new TodoServer()
  }
}


class ConnectionListener(server: TodoServer) extends ConnectListener {

  override def onConnect(socket: SocketIOClient): Unit = {
    server.socketToSortBy += socket -> "oldest"
    socket.sendEvent("all_tasks", server.tasksJSON())
  }

}


class AddTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
    val task: JsValue = Json.parse(taskJSON)
    val title: String = (task \ "title").as[String]
    val description: String = (task \ "description").as[String]
    val deadline: Long = (task \ "deadline").as[Long]
    val createdAt: Long = new Date().getTime

    server.database.addTask(Task(title, description, deadline, createdAt))
    server.updateTasksWithSort()
  }

}


class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
    server.database.completeTask(taskId)
    server.updateTasksWithSort()
  }

}

class SortTasksListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, sortBy: String, ackRequest: AckRequest): Unit = {
    server.socketToSortBy += socket -> sortBy
    socket.sendEvent("all_tasks", server.tasksJSON(sortBy))
  }

}

class DisconnectionListener(server: TodoServer) extends DisconnectListener {
  override def onDisconnect(socket: SocketIOClient): Unit = {
    server.socketToSortBy -= socket
  }
}
