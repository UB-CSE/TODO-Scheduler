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

  def apply(title: String, description: String, time10: String, time11: String, time12: String, time20: String, time21: String, time22: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), cleanString(time10), cleanString(time11), cleanString(time12), cleanString(time20), cleanString(time21), cleanString(time22), thisId.toString)
  }


}

class Task(val title: String, val description: String, val time10: String, val time11: String, val time12: String, val time20: String, val time21: String, val time22: String, val id: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "time10" -> Json.toJson(time10),
      "time11" -> Json.toJson(time11),
      "time12" -> Json.toJson(time12),
      "time20" -> Json.toJson(time20),
      "time21" -> Json.toJson(time21),
      "time22" -> Json.toJson(time22),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
