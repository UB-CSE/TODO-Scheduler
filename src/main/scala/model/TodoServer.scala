package model

import com.corundumstudio.socketio.listener.{ConnectListener, DataListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
import model.database.{Database, DatabaseAPI, TestingDatabase}
import play.api.libs.json.{JsValue, Json}
import java.util.Calendar


class TodoServer() {

  val database: DatabaseAPI = if (Configuration.DEV_MODE) {
    new TestingDatabase
  } else {
    new Database
  }

  setNextId()

  var usernameToSocket: Map[String, SocketIOClient] = Map()
  var socketToUsername: Map[SocketIOClient, String] = Map()
  var finishedTasks: List[Task] = List()

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

  def getTime: String = {
    val today = Calendar.getInstance()
    val currentDate: String = today.get(Calendar.DATE) + " " + getMonth(today.get(Calendar.MONTH)) + " " + today.get(Calendar.YEAR)
    var currentTime: String = ""

    val times: List[Int] = List(today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE), today.get(Calendar.SECOND))
    var count: Int = 0
    for (i <- times) {
      if (i < 10) {
        currentTime += "0" + i
      }
      else {
        currentTime += i
      }
      count += 1
      if (count < 3) {
        currentTime += ":"
      }
    }

    currentDate + " " + currentTime
  }

  def getMonth (theMonth: Int): String = {
    val allMonths: Map[Int, String] =
      Map(0 -> "Jan",
        1 -> "Feb",
        2 -> "Mar",
        3 -> "Apr",
        4 -> "May",
        5 -> "Jun",
        6 -> "Jul",
        7 -> "Aug",
        8 -> "Sep",
        9 -> "Oct",
        10 -> "Nov",
        11 -> "Dec")
    allMonths.getOrElse(theMonth, "no")
  }

}

object TodoServer {
  def main(args: Array[String]): Unit = {
    new TodoServer()
  }
}


class ConnectionListener(server: TodoServer) extends ConnectListener {

  override def onConnect(socket: SocketIOClient): Unit = {
    val tasksJSON: List[JsValue] = server.finishedTasks.map((entry: Task) => entry.asJsValue())
    socket.sendEvent("complete", Json.stringify(Json.toJson(tasksJSON)))
    socket.sendEvent("all_tasks", server.tasksJSON())
  }

}


class AddTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
    val task: JsValue = Json.parse(taskJSON)
    val title: String = (task \ "title").as[String]
    val description: String = (task \ "description").as[String]

    server.database.addTask(Task(title, description, server.getTime))
    server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
  }

}


class CompleteTaskListener(server: TodoServer) extends DataListener[String] {

  override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
    for (task <- server.database.getTasks) {
      if (task.id == taskId) {
        server.finishedTasks = new Task(task.title, task.description, "-" + task.id, server.getTime) :: server.finishedTasks
      }
    }

    val tasksJSON: List[JsValue] = server.finishedTasks.map((entry: Task) => entry.asJsValue())

    server.database.completeTask(taskId)
    server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
    server.server.getBroadcastOperations.sendEvent("complete", Json.stringify(Json.toJson(tasksJSON)))
  }

}

