package model

import com.corundumstudio.socketio.listener.{ConnectListener, DataListener, DisconnectListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import model.database.{Database, DatabaseAPI, TestingDatabase}
import play.api.libs.json.{JsValue, Json}
import com.roundeights.hasher.Implicits._

class TodoServer() {

  val database: DatabaseAPI = if (Configuration.DEV_MODE) {
    new TestingDatabase
  } else {
    new Database
  }

  setNextId()

  var socketsConnected: List[SocketIOClient] = List()
  var usernameToSocket: Map[String, SocketIOClient] = Map()
  var socketToUsername: Map[SocketIOClient, String] = Map()
  var socketToUser: Map[SocketIOClient, User] = Map()

  val config: Configuration = new Configuration {
    setHostname("0.0.0.0")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)

  server.addConnectListener(new ConnectionListener(this))
  server.addDisconnectListener(new DisconnectionListener(this))
  server.addEventListener("add_task", classOf[String], new AddTaskListener(this))
  server.addEventListener("add_personal_task", classOf[String], new AddPTaskListener(this))
  server.addEventListener("complete_task", classOf[String], new CompleteTaskListener(this))
  server.addEventListener("register_user", classOf[String], new RegisterListener(this))
  server.addEventListener("login_user", classOf[String], new LoginListener(this))

  server.start()

  def tasksJSON(): Unit = {
    for (socket <- socketsConnected) {
      var tasks: List[Task] = database.getTasks()
      val user: User = socketToUser.getOrElse(socket, new User("", "", ""))

      if (user.username != "") {
        val pList: List[Task] = database.getPersonalTasks(user)

        for (ptask <- pList) {
          tasks ::= ptask
        }
      }

      val tasksJSON: List[JsValue] = tasks.map((entry: Task) => entry.asJsValue())
      socket.sendEvent("all_tasks", Json.stringify(Json.toJson(tasksJSON)))
    }
  }

  def setNextId(): Unit = {
    val tasks = database.getTasks()
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
    println("Connected: " + socket)
    server.socketsConnected ::= socket
    server.tasksJSON()
  }

}

class DisconnectionListener(server: TodoServer) extends DisconnectListener {

  override def onDisconnect(socket: SocketIOClient): Unit = {
    val username = server.socketToUsername.getOrElse(socket, "")

    if (username != "") {
      server.socketToUser -= socket
      server.socketToUsername -= socket
      server.usernameToSocket -= username
    }

    server.socketsConnected = server.socketsConnected.filterNot(_ == socket)
  }

}


class AddTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
    val task: JsValue = Json.parse(taskJSON)
    val title: String = (task \ "title").as[String]
    val description: String = (task \ "description").as[String]

    server.database.addTask(Task(title, description, 0.toString))
    server.tasksJSON()
  }

}

class AddPTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
    val task: JsValue = Json.parse(taskJSON)
    val title: String = "(P) " + (task \ "title").as[String]
    val description: String = (task \ "description").as[String]
    val username: String = server.socketToUsername.getOrElse(socket, "")
    val user: User = server.database.getUser(username)

    if (username != "") {
      server.database.addTask(Task(title, description, user.id))
      server.tasksJSON()
    }
  }

}


class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
    server.database.completeTask(taskId)
    server.tasksJSON()
  }

}

class RegisterListener(server: TodoServer) extends DataListener[String] {
  override def onData(socket: SocketIOClient, userInfo: String, ackRequest: AckRequest): Unit = {
    val parsed: JsValue = Json.parse(userInfo)
    val username = (parsed \ "username").as[String]
    val password = (parsed \ "password").as[String]

    if (server.database.registerUser(username, password)) {
      server.socketToUsername += (socket -> username)
      server.usernameToSocket += (username -> socket)
      server.socketToUser += (socket -> server.database.getUser(username))

      val userMap: Map[String, String] = Map("username" -> username)

      socket.sendEvent("register_user", Json.stringify(Json.toJson(userMap)))
    } else {
      val userMap: Map[String, String] = Map("username" -> "")

      socket.sendEvent("register_user", Json.stringify(Json.toJson(userMap)))

      server.tasksJSON()
    }
  }
}

class LoginListener(server: TodoServer) extends DataListener[String] {
  override def onData(socket: SocketIOClient, userInfo: String, ackRequest: AckRequest): Unit = {
    val parsed: JsValue = Json.parse(userInfo)
    val username = (parsed \ "username").as[String]
    val password = (parsed \ "password").as[String]

    if (server.database.loginUser(username, password)) {
      server.socketToUsername += (socket -> username)
      server.usernameToSocket += (username -> socket)
      server.socketToUser += (socket -> server.database.getUser(username))

      val userMap: Map[String, String] = Map("username" -> username)

      socket.sendEvent("login_user", Json.stringify(Json.toJson(userMap)))
      server.tasksJSON()
    } else {
      val userMap: Map[String, String] = Map("username" -> "")

      socket.sendEvent("login_user", Json.stringify(Json.toJson(userMap)))
      server.tasksJSON()
    }
  }
}


