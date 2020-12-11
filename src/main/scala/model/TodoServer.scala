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
  var usernameAndPassword: Map[String, String] = Map()

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
    val messageMap: Map[String, JsValue] = Map("message" -> Json.toJson("Welcome to the to do list. Please sign up or login"))
    val sending: String = Json.stringify(Json.toJson(messageMap))
    socket.sendEvent("welcome", sending)
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

class SignUpListener(server: TodoServer) extends DataListener[String]{
  override def onData(socket: SocketIOClient, data: String, ackRequest: AckRequest): Unit={
    val jsonValue = Json.parse(data)
    val username = (jsonValue \ "username").toString
    val password = (jsonValue \ "password").toString
    server.socketToUsername +=  (socket -> username)
    server.usernameToSocket += (username -> socket)
    server.usernameAndPassword += (username -> password)

    val messageMap: Map[String, JsValue] = Map("message" -> Json.toJson("You have signed up for our To Do list as " + data.head))
    val sending: String = Json.stringify(Json.toJson(messageMap))

    socket.sendEvent("confirmation", sending)

  }
}



class LoginListener(server: TodoServer) extends DataListener[String]{
  override def onData(socket: SocketIOClient, data: String, ackRequest: AckRequest): Unit={

    val jsonValue = Json.parse(data)
    val username = (jsonValue \ "username").toString
    val password = (jsonValue \ "password").toString

    if(server.usernameAndPassword.contains(username)){

      if(server.usernameAndPassword(username) == password){
        socket.sendEvent("all_tasks", server.tasksJSON())
      }else{
        val messageMap: Map[String, JsValue] = Map("message" -> Json.toJson("Sorry, you input the wrong username or password. Please try again or sign up!"))
        val sending: String = Json.stringify(Json.toJson(messageMap))
        socket.sendEvent("login", sending)
      }

    }else{
      val messageMap: Map[String, JsValue] = Map("message" -> Json.toJson("The username "+ username +"does not exist. Please sign up!"))
      val sending: String = Json.stringify(Json.toJson(messageMap))
      socket.sendEvent("login", sending)
    }
  }
}


