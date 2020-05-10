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

  def apply(title: String, description: String, due: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), cleanString(due), thisId.toString)
  }


}

// added due by to task class and asJsValue method
// new "due" part is also added to parsing in JS and has an element for it in HTML

class Task(val title: String, val description: String, val due: String, val id: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "due by" -> Json.toJson(due),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
