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

  /** DEFAULT TASKS: CHORES
   * adding this here so that as people change the task params (timestamp, time to complete, etc
   * these can be quickly updated at the same time, without searching through other parts of the code
   * I realize I can do Task(title, description) instead of using apply, but it helps me understand
   * apply better since I'm still a little unclear about scala's apply functionality
   * ALSO: anyone who changes the params for Task, use default values so this doesn't all break
   *  or need to be updated everytime
   */
  val dailyChores: List[Task] = List(
    Task.apply("Dishes", "Go wash the dishes in the sink"),
    Task.apply("Make Bed", "This is just basic"),
    Task.apply("Tidy up", "Tidy up your work area, pick your clothes off the floor"),
    Task.apply("Take out Trash", "Pick up all the trash and take it to the dumpster"),
    Task.apply("Shower", "Everyday. And change your clothes"),
    Task.apply("Drink Water", "Coffee is not water. Soda is not water."))

  val weeklyChores: List[Task] = List(
    Task.apply("Laundry", "Wash AND fold"),
    Task.apply("Clean Bathroom", "Before you need a professional hazmat team"))

  /** version with added time_to_complete (in hours) param, per swisstackle's contribution */
  /**   val chores: List[Task] = List(
    Task.apply("Dishes", "Go wash the dishes in the sink", 0.25),
    Task.apply("Make Bed", "This is just basic", 0.10),
    Task.apply("Tidy up", "Tidy up your work area, pick your clothes off the floor", 0.50),
    Task.apply("Take out Trash", "Pick up all the trash and take it to the dumpster", 0.18),
    Task.apply("Shower", "Everyday. And change your clothes", 0.33),
    Task.apply("Drink Water", "Coffee is not water. Soda is not water.", 0.016))*/
}

class Task(val title: String, val description: String, val id: String) {

  def asJsValue(): JsValue ={
    val taskMap: Map[String, JsValue] = Map(
      "title" -> Json.toJson(title),
      "description" -> Json.toJson(description),
      "id" -> Json.toJson(id)
    )
    Json.toJson(taskMap)
  }

}
