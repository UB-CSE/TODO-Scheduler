package model

class DaysOfTheWeek {
  var state: State = new Schedular(this)
  def Sunday(): Unit ={
    this.state.Sunday
  }
  def Monday(): Unit = {
    this.state.Monday
  }
  def Tuesday(): Unit = {
    this.state.Tuesday
  }
  def Wednesday(): Unit = {
    this.state.Wednesday
  }
  def Thursday(): Unit = {
    this.state.Thursday
  }
  def Friday(): Unit = {
    this.state.Friday
  }
  def Saturday(): Unit = {
    this.state.Saturday
  }
}
