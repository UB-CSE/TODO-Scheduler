package model

import java.text.SimpleDateFormat
import java.util.Calendar

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

  def apply(title: String, description: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), thisId.toString)
  }


}

class Task(val title: String, val description: String, val id: String) {
  def current(): String = {
    val Date: String = "yyyy-MM-dd HH:mm:ss"
    val calendar: Calendar = Calendar.getInstance
    val sdf: SimpleDateFormat = new SimpleDateFormat(Date)
    sdf.format(calendar.getTime)
  }

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "id" -> Json.toJson(id),
      "date" -> Json.toJson(current())
    )
    Json.toJson(taskMap)
  }

}
