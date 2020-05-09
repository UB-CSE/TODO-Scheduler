package model

import org.joda.time.DateTime

// An immutable class that stores a single point in time.
class TimeInstant(
                 val year: Int,
                 val month: Int,
                 val day: Int,
                 val hour: Int,
                 val minute: Int,
                 val second: Int
                 ) {

  // Get the date stored in the TimeInstant as a String.
  def getDate: String = {
    TimeInstant.formatDate(year, month, day)
  }

  // Get the time stored in the TimeInstant as a string.
  def getTime: String = {
    TimeInstant.formatTime(hour, minute, second)
  }

  // Get the date and time stored in the TimeInstant as a string.
  def getDateTime: String = {
    getDate + " " + getTime
  }

  // Get the date and time stored in the TimeInstant as a string, which
  // can be further formatted with a custom beginning, middle, and end.
  def getDateTime(prefixString: String, centerString: String, postfixString: String): String = {
    prefixString + getDate + centerString + getTime + postfixString
  }

  override def toString: String = {
    getDateTime
  }

}

object TimeInstant {

  // Get the current time as a TimeInstant object.
  def getNow: TimeInstant = {
    val now: DateTime = DateTime.now()
    new TimeInstant(
      now.year().get(),
      now.monthOfYear().get(),
      now.dayOfMonth().get(),
      now.hourOfDay().get(),
      now.minuteOfHour().get(),
      now.secondOfMinute().get()
    )
  }

  // Formats time and date integers so that single digit integers
  // are represented with a "0" before them. This also converts the
  // integers into Strings.
  private def formatInt(num: Int): String = {
    val strNum: String = num.toString
    if(strNum.length <= 1) { "0" + num }
    else { strNum }
  }

  // Get a formatted string representing a time.
  def formatTime(hr: Int, min: Int, sec: Int): String = {
    formatInt(hr) + ":" + formatInt(min) + ":" + formatInt(sec)
  }

  // Get a formatted string representing a date.
  def formatDate(yr: Int, mth: Int, day: Int): String = {
    formatInt(mth) + "/" + formatInt(day) + "/" + formatInt(yr)
  }

  // Get the current time as a string.
  def getCurrentTime: String = {
    TimeInstant.getNow.getTime
  }

  // Get the current date as a string.
  def getCurrentDate: String = {
    TimeInstant.getNow.getDate
  }

  // Get the current date and time as a string.
  def getCurrentDateTime: String = {
    TimeInstant.getNow.getDateTime
  }

  // Get the current date and time as a string, which can be further
  // formatted with a custom beginning, middle, and end.
  def getCurrentDateTime(prefixString: String, centerString: String, postfixString: String): String = {
    TimeInstant.getNow.getDateTime(prefixString, centerString, postfixString)
  }

}
