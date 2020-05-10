package model

import play.api.libs.json.{JsValue, Json}


object Task {

  var nextId: Int = 0

  def apply(
             title: String,
             description: String,
             startTime: String,
             deadline: String,
             tag: String,
             priority: String,
             isPrivate: Boolean,
             username: String
           ): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title),
      cleanString(description, 1000),
      cleanString(startTime),
      cleanString(deadline),
      cleanString(tag),
      cleanString(priority),
      isPrivate,
      cleanString(username),
      thisId.toString)
  }

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

}

class Task(
            val title: String,
            val description: String,
            val startTime: String,
            val deadline: String,
            val tag: String,
            val priority: String,
            val isPrivate: Boolean,
            val username: String,
            val id: String
          ) {

  def asJsValue(): JsValue = {
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "start" -> Json.toJson(startTime),
      "deadline" -> Json.toJson(deadline),
      "tag" -> Json.toJson(tag),
      "priority" -> Json.toJson(priority),
      "isPrivate" -> Json.toJson(isPrivate),
      "username" -> Json.toJson(username),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
