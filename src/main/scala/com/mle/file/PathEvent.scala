package com.mle.file

import java.nio.file.Path

/**
 * @author Michael
 */
sealed trait PathEvent {
  def path: Path
}

case class PathCreated(path: Path) extends PathEvent

case class PathDeleted(path: Path) extends PathEvent

case class PathModified(path: Path) extends PathEvent
