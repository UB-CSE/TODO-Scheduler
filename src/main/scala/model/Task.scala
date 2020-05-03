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

  def apply(title: String, description: String, deadline: Long, createdAt: Long): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), deadline, createdAt, thisId.toString)
  }


}

class Task(val title: String, val description: String, val deadline: Long, val createdAt: Long, val id: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "deadline" -> Json.toJson(deadline),
      "createdAt" -> Json.toJson(createdAt),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
