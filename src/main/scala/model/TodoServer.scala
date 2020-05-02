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
  server.addEventListener("update_tasks", classOf[Nothing], new UpdateTaskListener(this))
  server.addEventListener("complete_task", classOf[String], new CompleteTaskListener(this))
  server.addEventListener("register_user", classOf[String], new RegisterUserListener(this))
  server.addEventListener("share", classOf[String], new ShareMessageListener(this))

  server.start()

  def tasksJSON(user: String): String = {
    if (user == "") {
      val tasks: List[Task] = database.getTasks
      val tasksJSON: List[JsValue] = tasks.map((entry: Task) => entry.asJsValue())
      val jsonString = Json.stringify(Json.toJson(tasksJSON))
//      println(jsonString)
      jsonString
    }
    else {
      val tasks: List[Task] = database.getTasks.filter(_.user == user)
      val tasksJSON: List[JsValue] = tasks.map((entry: Task) => entry.asJsValue())
      val jsonString = Json.stringify(Json.toJson(tasksJSON))
//      println(jsonString)
      jsonString
    }

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
//    if (server.usernameToSocket.keys.toList.contains(server.socketToUsername(socket))) {
//      socket.sendEvent("all_tasks", server.tasksJSON(server.socketToUsername(socket)))
//    }
//    else {
      socket.sendEvent("all_tasks", server.tasksJSON(""))
//    }
  }
}


class AddTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
//    println(taskJSON)
    val task: JsValue = Json.parse(taskJSON)
    val title: String = (task \ "title").as[String]
    val description: String = (task \ "description").as[String]
    val username: String = server.socketToUsername(socket)
    server.database.addTask(Task(title, description, username))
    socket.sendEvent("all_tasks", server.tasksJSON(server.socketToUsername(socket)))

  }

}

class UpdateTaskListener(server: TodoServer) extends DataListener[Nothing] {
  override def onData(client: SocketIOClient, data: Nothing, ackSender: AckRequest): Unit = {
    client.sendEvent("all_tasks", server.tasksJSON(server.socketToUsername(client)))
  }
}

class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
    server.database.completeTask(taskId)
    socket.sendEvent("all_tasks", server.tasksJSON(server.socketToUsername(socket)))
  }
}

class RegisterUserListener(server: TodoServer) extends DataListener[String] {
  override def onData(client: SocketIOClient, username: String, ackSender: AckRequest): Unit = {
    if (!server.usernameToSocket.keys.toList.contains(username)) {
      server.usernameToSocket += username -> client
      server.socketToUsername += client -> username
      client.sendEvent("registered", username)
    }
    else {
      client.sendEvent("already_registered", username)
      server.usernameToSocket += username -> client
      server.socketToUsername += client -> username
    }
  }
}

class ShareMessageListener(server: TodoServer) extends DataListener[String] {
  override def onData(socket: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
    val user: SocketIOClient = server.usernameToSocket(data)

    val tasks = server.database.getTasks

    var shared: List[String] = List()

    for (task <- tasks) {
      task.display()
      if (task.user == data) {
        shared = shared :+ task.id
        println(shared)
      }
    }

    for (task <- tasks) {
      task.display()
      if (task.user == server.socketToUsername(socket)) {
        if (!shared.contains(task.id)) {
          val currentTask = new Task(task.title, task.description, task.id, data)
          currentTask.display()
          server.database.addTask(currentTask)
        }
      }
    }

    println("send to: " + data)
    println("from: " + server.socketToUsername(socket))

    user.sendEvent("all_tasks", server.tasksJSON(server.socketToUsername(user)))
  }
}


