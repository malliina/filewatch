package com.mle.template

import java.nio.file.Paths

import com.mle.file.Watcher

/**
 *
 * @author Michael
 */
object Hello {
  def main(args: Array[String]) {
    val path = Paths.get("E:\\test")
    val events = Watcher.fileEvents(path)
    events.foreach(println)
  }
}
