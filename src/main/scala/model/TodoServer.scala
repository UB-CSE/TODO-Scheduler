package model

import java.text.{ParseException, SimpleDateFormat}
import java.util.Calendar

import com.corundumstudio.socketio.listener.{ConnectListener, DataListener}
import com.corundumstudio.socketio.{AckRequest, Configuration, SocketIOClient, SocketIOServer}
//import model.database.{Database, DatabaseAPI, TestingDatabase}
import model.database.{DatabaseAPI, TestingDatabase}
import play.api.libs.json.{JsValue, Json}


class TodoServer() {

/*  val database: DatabaseAPI = if (Configuration.DEV_MODE) {
    new TestingDatabase
  } else {
    new Database
  }*/

  val database: DatabaseAPI = new TestingDatabase

  setNextId()
  val config: Configuration = new Configuration {
    setHostname("0.0.0.0")
    setPort(8080)
  }

  // Use if socket is set to time_wait to avoid address in use
  if(System.getProperty("os.name") == "Linux"){
    import com.corundumstudio.socketio.SocketConfig
    config.setSocketConfig(new SocketConfig{setReuseAddress(true)})
  }

  val server: SocketIOServer = new SocketIOServer(config)
  var usernameToSocket: Map[String, SocketIOClient] = Map()
  var socketToUsername: Map[SocketIOClient, String] = Map()

  server.addConnectListener(new ConnectionListener(this))
  server.addEventListener("add_task", classOf[String], new AddTaskListener(this))
  server.addEventListener("complete_task", classOf[String], new CompleteTaskListener(this))
  server.addEventListener("complete_private_task", classOf[String], new CompletePrivateTaskListener(this))
  server.addEventListener("sort_message", classOf[String], new SortMessageListener(this))

  server.start()

  def tasksJSON(sortingKey: String = "None"): String = {
    val tasks: List[Task] = database.getTasks
    // pass through sort
    val sortTask = sort(tasks, sortingKey)
    val tasksJSON: List[JsValue] = for (entry <- sortTask; if !entry.isPrivate) yield {
      entry.asJsValue()
    }
    Json.stringify(Json.toJson(tasksJSON))
  }

  def sort(taskList: List[Task], sortingKey: String): List[Task] = {
    sortingKey match {
      case "Tag" => taskList.sortWith(_.tag > _.tag)
      case "Priority" => {
        val comparisionMap = Map("None" -> 0, "Low" -> 1, "Medium" -> 2, "High" -> 3)
        taskList.map(task => {(task, comparisionMap.get(task.priority).head)}).sortWith(_._2 > _._2).map(_._1)
      }
      case "Date" => {
        val comparator:(Task, Task) => Boolean = (task1:Task, task2:Task) => {
          val timeFormat: SimpleDateFormat = new SimpleDateFormat("kk:mm MM/dd/yyyy")
          var result:Int = 0
          if(task1.deadline.nonEmpty){
            result = timeFormat.parse(task1.deadline).compareTo(timeFormat.parse(task2.deadline))
          }
          if(result == 1) true else false
        }
        taskList.sortWith(comparator)
      }
      case _ => taskList
    }
  }

  def privateTasksJSON(user: String, sortingKey: String = "none"): String = {
    val tasks: List[Task] = database.getTasks
    // pass through sort
    val sortTask = sort(tasks, sortingKey)
    val tasksJSON: List[JsValue] = for (entry <- sortTask; if entry.isPrivate && entry.username == user) yield {
      entry.asJsValue()
    }
    Json.stringify(Json.toJson(tasksJSON))
  }

  def setNextId(): Unit = {
    val tasks = database.getTasks
    if (tasks.nonEmpty) {
      Task.nextId = tasks.map(_.id.toInt).max + 1
    }
  }


  class ConnectionListener(server: TodoServer) extends ConnectListener {
    override def onConnect(socket: SocketIOClient): Unit = {
      socket.sendEvent("all_tasks", server.tasksJSON())
      server.usernameToSocket.keys.foreach(user =>
        usernameToSocket.get(user).head.sendEvent("all_private_tasks", server.privateTasksJSON(user))
      )
    }

  }


  class AddTaskListener(server: TodoServer) extends DataListener[String] {
    override def onData(socket: SocketIOClient, taskJSON: String, ackRequest: AckRequest): Unit = {
      val task: JsValue = Json.parse(taskJSON)
      val title: String = (task \ "title").as[String]
      val description: String = (task \ "description").as[String]

      val deadline: String = (task \ "deadline").as[String]
      val tag: String = (task \ "tag").as[String]
      val priority: String = (task \ "priority").as[String]
      val isPrivate: Boolean = (task \ "private").as[Boolean]
      val username: String = (task \ "user").as[String]

      // Generate created time date
      val timeFormat: SimpleDateFormat = new SimpleDateFormat("kk:mm MM/dd/yyyy")
      val CreatedDate: String = timeFormat.format(Calendar.getInstance().getTime)

      if (username.nonEmpty &&
        !server.socketToUsername.keySet.contains(socket) &&
        !server.usernameToSocket.contains(username)) {
        server.usernameToSocket += (username -> socket)
        server.socketToUsername += (socket -> username)
      }

      def isValidFormJson(element: String, isValid: Boolean): String = {
        Json.stringify(Json.toJson(Map(
          "element" -> Json.toJson(element),
          "isValid" -> Json.toJson(isValid)
        )))
      }

      // Check if valid time by trying to parse to SimpleDateFormat
      def isValidTime: Boolean = {
        try {
          timeFormat.setLenient(false)
          if (deadline.nonEmpty) timeFormat.parse(deadline)
          socket.sendEvent("isValid_form", isValidFormJson("deadline", true))
          true
        } catch {
          case _: ParseException => {
            socket.sendEvent("isValid_form", isValidFormJson("deadline", false))
            false
          }
        }
      }

      if (isValidTime && title.nonEmpty) {
        socket.sendEvent("isValid_form", isValidFormJson("title", true))
        server.database.addTask(Task(title, description, CreatedDate, deadline, tag, priority, isPrivate, username))
        if (isPrivate && server.usernameToSocket.contains(username)) {
          server.usernameToSocket.get(username).head.sendEvent("all_private_tasks", server.privateTasksJSON(username))
        } else {
          server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
        }
      } else {
        socket.sendEvent("isValid_form", isValidFormJson("title", false))
      }

    }

  }

  class SortMessageListener(server: TodoServer) extends DataListener[String] {
    override def onData(socket: SocketIOClient, sortingKey: String, ackRequest: AckRequest): Unit = {
      socket.sendEvent("all_tasks", server.tasksJSON(sortingKey))
      socket.sendEvent("all_private_tasks", server.privateTasksJSON(sortingKey))
    }

  }


  class CompleteTaskListener(server: TodoServer) extends DataListener[String] {
    override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
      server.database.completeTask(taskId)
      server.server.getBroadcastOperations.sendEvent("all_tasks", server.tasksJSON())
    }

  }

  class CompletePrivateTaskListener(server: TodoServer) extends DataListener[String] {
    override def onData(socket: SocketIOClient, taskId: String, ackRequest: AckRequest): Unit = {
      server.database.completeTask(taskId)
      socket.sendEvent("all_private_tasks", server.privateTasksJSON(server.socketToUsername.get(socket).head))
    }

  }

}

object TodoServer {
  def main(args: Array[String]): Unit = {
    new TodoServer()
  }
}


