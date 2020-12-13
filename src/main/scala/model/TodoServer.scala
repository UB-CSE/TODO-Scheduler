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

  var socketToTasks: Map[SocketIOClient, List[Int]] = Map()//maps socket to a list of task ids created by said socket

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
  server.addEventListener("import_task", classOf[String], new importTaskListener(this))
  server.addEventListener("export_task", classOf[Nothing], new exportTaskListener(this))

  server.start()

  def tasksJSON(socket: SocketIOClient): String = {
    val tasks: List[Task] = database.getTasks
    val pTasks: List[Task] = tasks.filter((value: Task) => socketToTasks.getOrElse(socket,List()).contains(value.id.toInt))
    val tasksJSON: List[JsValue] = pTasks.map((entry: Task) => entry.asJsValue())
    Json.stringify(Json.toJson(tasksJSON))
  }

  /*def tasksJSONALL(): String = {
    val tasks: List[Task] = database.getTasks
    val tasksJSON: List[JsValue] = tasks.map((entry: Task) => entry.asJsValue())
    Json.stringify(Json.toJson(tasksJSON))
  }*/

  def setNextId(): Unit = {
    val tasks = database.getTasks
    if (tasks.nonEmpty) {
      Task.nextId = tasks.map(_.id.toInt).max + 1
    }
  }

  def importTasks(taskJson: String, socket: SocketIOClient): Unit = {
    val tasks: List[JsValue] = Json.parse(taskJson).as[List[JsValue]]
    for(task <- tasks){
      database.addTask(makeTask(task))
      socketToTasks += socket -> (database.getLastTaskId() :: socketToTasks.getOrElse(socket,List()))
    }
  }

  def makeTask(task: JsValue): Task = {
    val title: String = (task \ "title").as[String]
    val description: String = (task \ "description").as[String]
    Task(title, description)
  }

}

object TodoServer {
  def main(args: Array[String]): Unit = {
    new TodoServer()
  }
}


class ConnectionListener(server: TodoServer) extends ConnectListener {

  override def onConnect(socket: SocketIOClient): Unit = {
    socket.sendEvent("all_tasks", server.tasksJSON(socket))
  }

}

class importTaskListener(server: TodoServer) extends DataListener[String]{//expects the client to emit a signal of "import_task" with a value of JSON string, will add the tasks into the current database
  override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
    server.importTasks(taskJSON,socket)
    socket.sendEvent("all_tasks", server.tasksJSON(socket))
  }
}

class exportTaskListener(server: TodoServer) extends DataListener[Nothing]{//expects the client to emit a signal of "export_task", will convert all in the database into a JSON string and send it to the socket requesting
  override def onData(socket: SocketIOClient, nothing: Nothing, ackRequest: AckRequest): Unit ={
    socket.sendEvent("exported_task", server.tasksJSON(socket))
  }
}

class AddTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
    server.database.addTask(server.makeTask(Json.parse(taskJSON)))
    server.socketToTasks += socket -> (server.database.getLastTaskId() :: server.socketToTasks.getOrElse(socket,List()))
    //server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSONALL())
    socket.sendEvent("all_tasks", server.tasksJSON(socket))

  }

}


class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {

    server.database.completeTask(taskId)
    server.socketToTasks += socket -> server.socketToTasks(socket).filter(_!=taskId.toInt)
    socket.sendEvent("all_tasks", server.tasksJSON(socket))

  }

}


