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

  def apply(title: String, description: String, eta: Int, priority: Int): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), eta, priority, thisId.toString)
  }


}

class Task(val title: String, val description: String, val eta: Int, val priority: Int, val id: String) {

  def asJsValue(): JsValue = {
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "eta" -> Json.toJson(eta),
      "priority" -> Json.toJson(priority),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}

