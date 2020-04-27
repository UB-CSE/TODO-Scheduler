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
    var tasks: List[Task] = database.getTasks
    val comparePriority:(Task, Task) => Boolean = (t1: Task, t2: Task) => { //comparator
      var result: Boolean = true
      if (t1.priority == "low" && (t2.priority == "med" || t2.priority == "high")) {
        result = false
      }
      if (t1.priority == "med" && t2.priority == "high") {
        result = false
      }
      if (t1.priority == t2.priority) { //want newer task below old ones if same priority
        result = false
      }
      if (t1.priority == "none" && t2.priority != "none") { //no priority selected is below low priority
        result = false
      }
      result
    }
    tasks = tasks.sortWith(comparePriority) //sort high -> med -> low -> none
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
    val priority: String = (task \ "priority").as[String] //added priority to task

    server.database.addTask(Task(title, description, priority))
    server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
  }

}


class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
    server.database.completeTask(taskId)
    server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
  }

}


