package com.github.windymelt.tfs.infrastructure

import akka.actor.ActorSystem
import scala.concurrent.ExecutionContext

trait ActorSystemComponent
    extends com.github.windymelt.tfs.application.ActorSystemComponent
    with ExecutionContextProviderComponent:
  implicit val system: ActorSystem = ActorSystem("StandardActorSystem")
  implicit val ec: ExecutionContext = system.dispatcher
