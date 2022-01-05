package com.github.windymelt.tfs.application

import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext

trait ActorSystemComponent:
  implicit val system: ActorSystem
  implicit val ec: ExecutionContext
