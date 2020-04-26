package model

import play.api.libs.json.{JsValue, Json}
import java.util.{Calendar, Date}     // Used for Contribution

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
  // https://www.includehelp.com/scala/how-to-check-current-date-and-time-in-scala.aspx
  val created: Date = Calendar.getInstance.getTime     // added as contribution - stores date and time task was added

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "id" -> Json.toJson(id),
      "time" -> Json.toJson(created.toString)
    )
    Json.toJson(taskMap)
  }
}
