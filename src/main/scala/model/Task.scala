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

  def apply(title: String, deadline: String, description: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(deadline,1000), cleanString(description, 1000), thisId.toString)
  }


}

class Task(val title: String, val deadline: String, val description: String, val id: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "deadline" -> Json.toJson(deadline),
      "description" -> Json.toJson(description),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
