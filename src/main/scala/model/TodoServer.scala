package model

import com.corundumstudio.socketio.listener.{ConnectListener, DataListener, DisconnectListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File
import model.database.{Database, DatabaseAPI, TestingDatabase}
import play.api.libs.json.{JsValue, Json}

import scala.io.Source
import scala.reflect.io
import scala.reflect.io.File


class TodoServer() {

  val database: DatabaseAPI = if (Configuration.DEV_MODE) {
    new TestingDatabase
  } else {
    new Database
  }

  setNextId()

  var total_time: Double = (Json.parse(Source.fromFile("stats.json").mkString)\"total_time").as[Double]
  var visits: Int = (Json.parse(Source.fromFile("stats.json").mkString)\"visits").as[Int]
  var avg_time: Double = (Json.parse(Source.fromFile("stats.json").mkString)\"avg_time").as[Double]
  var time_spent: Map[SocketIOClient,Double] = Map()

  var usernameToSocket: Map[String, SocketIOClient] = Map()
  var socketToUsername: Map[SocketIOClient, String] = Map()

  val config: Configuration = new Configuration {
    setHostname("0.0.0.0")
    setPort(8080)
  }

  val server: SocketIOServer = new SocketIOServer(config)

  server.addConnectListener(new ConnectionListener(this))
  server.addDisconnectListener(new Disconnection(this))
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
    server.visits += 1
    server.time_spent += (socket -> System.nanoTime()/1000000000)


    var stats_payload: JsValue = Json.obj("visits" -> server.visits,
    "total_time" -> server.total_time,
    "avg_time" -> server.avg_time)
    socket.sendEvent("stats",Json.stringify(stats_payload))
  }

}

class Disconnection(server: TodoServer) extends DisconnectListener {
  override def onDisconnect(socketIOClient: SocketIOClient): Unit = {
    server.total_time += (System.nanoTime() / 1000000000) - server.time_spent(socketIOClient)
    server.avg_time = server.total_time/server.visits

    var stats_payload: JsValue = Json.obj("visits" -> server.visits,
      "total_time" -> server.total_time,
      "avg_time" -> server.avg_time)
    io.File("stats.json").writeAll(Json.stringify(stats_payload))
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


