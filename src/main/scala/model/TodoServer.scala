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
  server.addEventListener("closest_deadline", classOf[Nothing], new DeadlineTaskListener(this))


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
    val deadline: String = (task \ "deadline").as[String]
    if(title.length > 0 || description.length > 0 || deadline.length > 0) {
      server.database.addTask(Task(title, description, deadline))
      server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
    }
  }

}

class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
    server.database.completeTask(taskId)
    server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
  }

}

class DeadlineTaskListener(server: TodoServer) extends DataListener[Nothing] {

  override def onData(socket: SocketIOClient, taskId: Nothing, ackRequest: AckRequest): Unit = {
    val everyTask: List[Task] = server.database.getTasks
    var closeDeadline = "99/99"
    var titleDeadline = "N/A"
    for(task <- everyTask){
      if(task.deadline.slice(0,2).toInt < closeDeadline.slice(0,2).toInt){
        closeDeadline = task.deadline
        titleDeadline = task.title
      }else if(task.deadline.slice(0,2).toInt == closeDeadline.slice(0,2).toInt && task.deadline.slice(3,5).toInt < closeDeadline.slice(3,5).toInt){
        closeDeadline = task.deadline
        titleDeadline = task.title
      }
    }
    socket.sendEvent("closest", closeDeadline,titleDeadline)
  }

}



