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

  def apply(title: String, description: String,dateMade:String,date_due:String,priority:String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000),cleanString(dateMade), cleanString(date_due),cleanString(priority),thisId.toString)
  }


}

class Task(val title: String, val description: String,val dateMade:String ,val date_due:String,val priority:String, val id: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "dateMade"->Json.toJson(dateMade),
      "date_due"->Json.toJson(date_due),
      "priority"->Json.toJson(priority),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
