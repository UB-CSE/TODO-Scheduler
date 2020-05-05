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

  def apply(title: String, description: String, uId: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), thisId.toString, uId)
  }


}

class Task(val title: String, val description: String, val id: String, val uId: String) {

  def asJsValue(): JsValue ={
    var userId = 0.toString

    if (uId == ""){ userId = 0.toString }
    else { userId = uId }

    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "id" -> Json.toJson(id),
      "userId" -> Json.toJson(userId)
    )
    Json.toJson(taskMap)
  }

}
