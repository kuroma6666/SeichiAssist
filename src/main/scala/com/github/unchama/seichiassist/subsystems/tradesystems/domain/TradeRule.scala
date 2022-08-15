package com.github.unchama.seichiassist.subsystems.tradesystems.domain

/**
 * 特定のアイテムを交換するためのルール（何を何個入れると何が何個得られて何が返却されるか）
 * を定めるオブジェクトの trait
 */
trait TradeRule[ItemStack] {

  /**
   * プレーヤーが入力したアイテムから、交換結果を計算する
   */
  def trade(contents: List[ItemStack]): TradeResult[ItemStack]

}

object TradeRule {

  def apply[ItemStack](implicit ev: TradeRule[ItemStack]): TradeRule[ItemStack] = ev

}
