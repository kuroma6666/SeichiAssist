package com.github.unchama.seichiassist.subsystems.seichilevelupgift.domain

case class GiftBundle(map: Map[Gift, Int]) {
  require(map.forall { case (_, count) => count >= 1 })

  def combinePair(gift: Gift, count: Int): GiftBundle = GiftBundle {
    map.updatedWith(gift) {
      case Some(value) => Some(value + count)
      case None        => Some(count)
    }
  }

  def combine(bundle: GiftBundle): GiftBundle =
    bundle.map.toList.foldLeft(this)((bundle, pair) => bundle.combinePair(pair._1, pair._2))

  def gifts: Set[Gift] = map.keys.toSet
}

object GiftBundle {

  val empty: GiftBundle = GiftBundle(Map.empty)

  def ofSinglePair(gift: Gift, count: Int): GiftBundle = empty.combinePair(gift, count)

}
