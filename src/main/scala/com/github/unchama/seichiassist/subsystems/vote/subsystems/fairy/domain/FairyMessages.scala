package com.github.unchama.seichiassist.subsystems.vote.subsystems.fairy.domain

case class FairyMessages(messages: FairyMessage*) {
  require(messages.nonEmpty)
}
