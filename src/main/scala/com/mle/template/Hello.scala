package com.mle.template

import java.nio.file.Paths

import com.mle.file.Watcher

/**
 *
 * @author Michael
 */
object Hello {
  def main(args: Array[String]) {
    Watcher.start(Paths.get("E:\\test"))
  }
}
