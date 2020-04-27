package model

import com.corundumstudio.socketio.listener.{ConnectListener, DataListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import model.database.{Database, DatabaseAPI, TestingDatabase}
import play.api.libs.json.{JsValue, Json}

import scala.collection.mutable.ListBuffer


class TodoServer() {

  val database: DatabaseAPI = if (Configuration.DEV_MODE) {
    new TestingDatabase
  } else {
    new Database
  }
  var username = ""
  setNextId(username)

  var usernameToTask: scala.collection.mutable.Map[String, ListBuffer[Task]] = scala.collection.mutable.Map()
  var completeTaskList: List[Task] = List()
  var socketToUsername: Map[SocketIOClient, String] = Map()
  var first = true
  val config: Configuration = new Configuration {
    setHostname("0.0.0.0")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)
  server.addEventListener("startTask", classOf[String], new StartListener())
  server.addEventListener("add_task", classOf[String], new AddTaskListener(this))
  server.addEventListener("complete_task", classOf[String], new CompleteTaskListener(this))

  server.start()
  class StartListener() extends DataListener[String] {
    override def onData(socket: SocketIOClient, data: String, ackRequest: AckRequest): Unit = {
      socketToUsername+=(socket->data)
      if(first) {
        usernameToTask+=(data->ListBuffer())
        first = false
      }
      else if(!usernameToTask.keys.toList.contains(data)) {
        usernameToTask += (data -> ListBuffer())

      }

      socket.sendEvent("start_tasks", tasksJSON(data))
    }
  }
  def tasksJSON(data: String): String = {
    val tasks: ListBuffer[Task] = usernameToTask(data)
    username = data
    val tasksJSON: List[JsValue] = tasks.toList.map((entry: Task) => entry.asJsValue())
    Json.stringify(Json.toJson(tasksJSON))
  }

  def setNextId(username: String): Unit = {
    if(username!=""){
      val tasks = usernameToTask(username)
      if (tasks.nonEmpty) {
        Task.nextId = tasks.map(_.id.toInt).max + 1
      }
    }
  }

}

object TodoServer {
  def main(args: Array[String]): Unit = {
    new TodoServer()
  }
}



class AddTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
    val check = server.socketToUsername.values.toList.distinct
    val indexOf = check.indexOf(server.socketToUsername(socket))
    var indexOf2 = server.socketToUsername.values.toList.indexOf(server.socketToUsername(socket))
    var first = true
    for(sockets<-server.socketToUsername.keys){
      if(server.socketToUsername.values.toList.indexOf(server.socketToUsername(sockets))==indexOf2&&sockets!=socket){
        indexOf2 = server.socketToUsername.values.toList.lastIndexOf(server.socketToUsername(sockets))
      }
    }
    for(sockets<-server.socketToUsername.keys) {
      if(indexOf==indexOf2&&sockets==socket){
        val task: JsValue = Json.parse(taskJSON)
        val title: String = (task \ "title").as[String]
        val description: String = (task \ "description").as[String]
        val username: String = server.socketToUsername(socket)
        val x = Task(title,description)
        server.completeTaskList = server.completeTaskList:+x
        server.usernameToTask(username)=(server.usernameToTask(username):+x)
        socket.sendEvent("all_tasks", server.tasksJSON(username))
      }
      else if (server.socketToUsername(sockets) == server.socketToUsername(socket)) {
        val task: JsValue = Json.parse(taskJSON)
        val title: String = (task \ "title").as[String]
        val description: String = (task \ "description").as[String]
        val username: String = server.socketToUsername(sockets)
        val x = Task(title,description)
        server.completeTaskList = server.completeTaskList:+x
        if(first) {
          server.usernameToTask(username) = ((server.usernameToTask(username) :+ x))
          first = false
        }
        sockets.sendEvent("all_tasks", server.tasksJSON(username))
        socket.sendEvent("all_tasks", server.tasksJSON(username))
      }



    }

  }


}


class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
    for(sockets<-server.socketToUsername.keys) {
      if(server.socketToUsername(sockets) == server.socketToUsername(socket)&&sockets!=socket) {
        val username: String = server.socketToUsername(sockets)
        val correctList = server.completeTaskList(taskId.toInt)
        val getRidOfTask = correctList
        server.usernameToTask+=(username->(server.usernameToTask(username).filter(_ != getRidOfTask)))
        sockets.sendEvent("all_tasks", server.tasksJSON(username))
        socket.sendEvent("all_tasks", server.tasksJSON(username))
      }
      else if(sockets==socket){
        val username: String = server.socketToUsername(sockets)
        val correctList = server.completeTaskList(taskId.toInt)
        val getRidOfTask = correctList
        server.usernameToTask+=(username->(server.usernameToTask(username).filter(_ != getRidOfTask)))
        sockets.sendEvent("all_tasks", server.tasksJSON(username))
      }
    }
  }

}


