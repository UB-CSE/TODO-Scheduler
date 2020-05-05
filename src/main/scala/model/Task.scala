package model

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

  def apply(title: String, description: String, priority: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), thisId.toString, cleanString(priority)) //Include the priority when creating a new task
  }


}

class Task(val title: String, val description: String, val id: String, val priority: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "id" -> Json.toJson(id),
      "priority" -> Json.toJson(priority)
    )
    Json.toJson(taskMap)
  }

}
