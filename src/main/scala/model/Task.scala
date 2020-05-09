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

  def apply(title: String, description: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), thisId.toString)
  }

  def apply(title: String, description: String, author: String): Task = {
    val thisId = nextId
    nextId += 1
    new Task(cleanString(title), cleanString(description, 1000), thisId.toString, author)
  }


}

class Task(val title: String, val description: String, val id: String) {

  val timeCreated: TimeInstant = TimeInstant.getNow
  var author: String = ""

  def this(title: String, description: String, id: String, author: String) = {
    this(title, description, id)
    this.addAuthor(author)
  }

  def addAuthor(name: String): Unit = {
    author = name.strip()
  }

  // Return the author's name if one exists otherwise make the author
  // "Anonymous".
  def formatAuthor: String = {
    if(author == null || author.strip() == "") { "Anonymous" }
    else { author }
  }

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "timeCreated" -> Json.toJson(timeCreated.getDateTime(" - Created on ", " at ", "")),
      "author" -> Json.toJson(formatAuthor),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
