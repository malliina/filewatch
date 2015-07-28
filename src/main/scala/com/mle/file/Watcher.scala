package com.mle.file

import java.nio.file.StandardWatchEventKinds.{ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY, OVERFLOW}
import java.nio.file._

import rx.lang.scala.schedulers.NewThreadScheduler
import rx.lang.scala.{Observable, Subscriber}

import scala.annotation.tailrec
import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

sealed trait PathEvent {
  def path: Path
}

case class PathCreated(path: Path) extends PathEvent

case class PathDeleted(path: Path) extends PathEvent

case class PathModified(path: Path) extends PathEvent

/**
 * @author Michael
 */
object Watcher {
  def start(path: Path): Unit = {
    val events = fileEvents(path)
    println("subscribing")
    events.foreach(println)
    println("subscribed")
  }

  def fileEvents(path: Path): Observable[PathEvent] = watcherObservable(path).flatMapIterable(toPathEvent(_).toSeq)

  def watcherObservable(path: Path): Observable[WatchEvent[Path]] = {
    Observable[WatchEvent[Path]](subscriber => {
      val watcher = FileSystems.getDefault.newWatchService()
      val registerKey = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE)
      registerKey.reset()
      loopForSubscriber(watcher, subscriber)
    }).subscribeOn(NewThreadScheduler())
  }

  @tailrec
  private def loopForSubscriber(watcher: WatchService, subscriber: Subscriber[WatchEvent[Path]]): Unit = {
    Try(watcher.take()) match {
      case Success(key) =>
        val events = key.pollEvents().toIndexedSeq
          .filterNot(_.kind() == OVERFLOW)
          .map(event => event.asInstanceOf[WatchEvent[Path]])
        events.foreach(subscriber.onNext)
        val isValid = key.reset()
        if (isValid && !subscriber.isUnsubscribed) {
          loopForSubscriber(watcher, subscriber)
        } else {
          subscriber.onCompleted()
        }
      case Failure(t) =>
        subscriber.onError(t)
    }
  }

  private def toPathEvent(watchEvent: WatchEvent[Path]): Option[PathEvent] = {
    val path = watchEvent.context()
    val kind = watchEvent.kind()
    if (kind == ENTRY_CREATE) Some(PathCreated(path))
    else if (kind == ENTRY_DELETE) Some(PathDeleted(path))
    else if (kind == ENTRY_MODIFY) Some(PathModified(path))
    else None
  }
}
