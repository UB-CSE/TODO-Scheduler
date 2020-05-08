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

  def apply(title: String, description: String, comment: String, date: Date): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), cleanString(comment, 1000), date, thisId.toString)
  }


}

class Task(val title: String, val description: String, val comment: String, val date: Date, val id: String) {

  val dateFormat = new SimpleDateFormat("MM/dd/yy")
  val showdate = dateFormat.format(date)
  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "comment" -> Json.toJson(comment),
      "date" -> Json.toJson(showdate),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
