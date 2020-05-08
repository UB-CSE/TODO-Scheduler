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

  def apply(title: String, description: String, nickname:String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), thisId.toString,cleanString(nickname))
  }


}

class Task(val title: String, val description: String, val id: String, val nickname:String) {

  //used calendar and simpleDateFormat inputs to get the current time
  val current = Calendar.getInstance().getTime()
  val TimeFormat = new SimpleDateFormat("hh:mm:a")
  val currentHourMin = TimeFormat.format(current)


  def asJsValue(): JsValue = {
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "id" -> Json.toJson(id),
      "currentTime" -> Json.toJson(currentHourMin),
      "nickname" ->Json.toJson(nickname)
    )
    Json.toJson(taskMap)
  }
}

