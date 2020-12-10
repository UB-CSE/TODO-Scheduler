package model

import model.Task.cleanString
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

  def apply(title: String, description: String, deadline: String, createdDate: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), thisId.toString,
      cleanString(deadline), cleanString(createdDate))
  }


}

class Task(val title: String, val description: String, val id: String, val deadline: String,
           val createdDate: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "id" -> Json.toJson(id),
      "deadline" -> Json.toJson(deadline),
      "createdDate" -> Json.toJson(createdDate)
    )
    Json.toJson(taskMap)
  }

}
