package model

import java.text.SimpleDateFormat
import java.util.Date

import play.api.libs.json.{JsValue, Json}


object Task {

  var nextId: Int = 0

  def cleanString(input: String, maxLength: Int = 100): String = {
    var output = input
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
    if (output.length > maxLength) {
      output = output.slice(0, maxLength) + "..."
    }
    output
  }

  def apply(title: String, description: String, deadline: String, taskAdded: Date, priority: Int, estimated: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), deadline, taskAdded, estimated, priority, thisId.toString)
  }


}

class Task(val title: String, val description: String, val deadline: String, val taskAdded: Date, val estimated: String, val priority: Int, val id: String) {

  val formatted = new SimpleDateFormat("dd / MM / yy")
  val formattedDate = formatted.format(taskAdded)
  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "deadline" -> Json.toJson(deadline),
      "taskAdded" -> Json.toJson(formattedDate),
      "priority" -> Json.toJson(priority.toString()),
      "estimated" -> Json.toJson(estimated),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
