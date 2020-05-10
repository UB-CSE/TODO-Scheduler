package model

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
    val setup = Calendar.getInstance()
    val currentTime = setup.getTime
    new Task(cleanString(title), cleanString(description, 1000), thisId.toString, currentTime.toString,"")
  }


}

class Task(val title: String, val description: String, val id: String, val started: String, var ending: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "id" -> Json.toJson(id),
      "time" -> Json.toJson(started),
      "endTime" -> Json.toJson(ending)
    )
    Json.toJson(taskMap)
  }

}
