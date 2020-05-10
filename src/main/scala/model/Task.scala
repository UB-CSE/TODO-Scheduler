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

  def apply(title: String, description: String, dueDate: String, comment: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), dueDate,
             cleanString(comment, 5000), thisId.toString)
  }


}

class Task(val title: String, val description: String, val dueDate: String, val comment: String, val id: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "comment" -> Json.toJson(comment),
      "dueDate" -> Json.toJson(dueDate),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
