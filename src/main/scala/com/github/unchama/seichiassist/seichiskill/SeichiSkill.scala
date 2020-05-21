package com.github.unchama.seichiassist.seichiskill

import enumeratum.EnumEntry.Snakecase
import enumeratum._

sealed trait SeichiSkill extends Snakecase {
  val name: String
  val range: SkillRange
  val maxCoolDownTicks: Option[Int]
  val manaCost: Int
  val requiredActiveSkillPoint: Int
}

sealed abstract class ActiveSkill(override val name: String,
                                  override val range: ActiveSkillRange,
                                  override val maxCoolDownTicks: Option[Int],
                                  override val manaCost: Int,
                                  override val requiredActiveSkillPoint: Int) extends SeichiSkill

sealed abstract class AssaultSkill(override val name: String,
                                   override val range: AssaultSkillRange,
                                   override val manaCost: Int,
                                   override val requiredActiveSkillPoint: Int) extends SeichiSkill {
  override val maxCoolDownTicks: None.type = None
}

object SeichiSkill extends Enum[SeichiSkill] {
  import ActiveSkillRange._
  import AssaultSkillRange._

  /**
   * Enumeratumの機能により、
   * 各Enum名(をスネークケースにしたもの)がDB上での識別子となる。
   * DBの内部を変更しないままここのobject名を変えないこと。
   */

  case object DualBreak extends ActiveSkill("デュアル・ブレイク", singleArea(1, 2, 1), None, 1, 10)
  case object TrialBreak extends ActiveSkill("トリアル・ブレイク", singleArea(3, 2, 1), None, 3, 20)
  case object Explosion extends ActiveSkill("エクスプロージョン", singleArea(3, 3, 3), None, 12, 30)
  case object MirageFlare extends ActiveSkill("ミラージュ・フレア", singleArea(5, 5, 3), Some(14), 30, 40)
  case object Dockarn extends ActiveSkill("ドッ・カーン", singleArea(7, 7, 5), Some(30), 70, 50)
  case object GiganticBomb extends ActiveSkill("ギガンティック・ボム", singleArea(9, 9, 7), Some(50), 100, 60)
  case object BrilliantDetonation extends ActiveSkill("ブリリアント・デトネーション", singleArea(11, 11, 9), Some(70), 200, 70)
  case object LemuriaImpact extends ActiveSkill("レムリア・インパクト", singleArea(13, 13, 11), Some(100), 350, 80)
  case object EternalVice extends ActiveSkill("エターナル・ヴァイス", singleArea(15, 15, 13), Some(140), 500, 90)

  case object TomBoy extends ActiveSkill("トム・ボウイ", MultiArea(3, 3, 3)(3), Some(12), 28, 40)
  case object Thunderstorm extends ActiveSkill("サンダーストーム", MultiArea(3, 3, 3)(7), Some(28), 65, 50)
  case object StarlightBreaker extends ActiveSkill("スターライト・ブレイカー", MultiArea(5, 5, 5)(3), Some(48), 90, 60)
  case object EarthDivide extends ActiveSkill("アース・ディバイド", MultiArea(5, 5, 5)(5), Some(68), 185, 70)
  case object HeavenGaeBolg extends ActiveSkill("ヘヴン・ゲイボルグ", MultiArea(7, 7, 7)(3), Some(96), 330, 80)
  case object Decision extends ActiveSkill("ディシジョン", MultiArea(7, 7, 7)(7), Some(136), 480, 90)

  case object EbifriDrive extends ActiveSkill("エビフライ・ドライブ", RemoteArea(3, 3, 3), Some(4), 18, 40)
  case object HolyShot extends ActiveSkill("ホーリー・ショット", RemoteArea(5, 5, 3), Some(26), 35, 50)
  case object TsarBomba extends ActiveSkill("ツァーリ・ボンバ", RemoteArea(7, 7, 5), Some(32), 80, 60)
  case object ArcBlast extends ActiveSkill("アーク・ブラスト", RemoteArea(9, 9, 7), Some(54), 110, 70)
  case object PhantasmRay extends ActiveSkill("ファンタズム・レイ", RemoteArea(11, 11, 9), Some(76), 220, 80)
  case object Supernova extends ActiveSkill("スーパー・ノヴァ", RemoteArea(13, 13, 11), Some(110), 380, 90)

  case object WhiteBreath extends AssaultSkill("ホワイト・ブレス", condenseWater(7, 7, 7), 30, 70)
  case object AbsoluteZero extends AssaultSkill("アブソリュート・ゼロ", condenseWater(11, 11, 11), 80, 80)
  case object DiamondDust extends AssaultSkill("ダイヤモンドダスト", condenseWater(15, 15, 15), 160, 90)

  case object LavaCondensation extends AssaultSkill("ラヴァ・コンデンセーション", condenseLava(7, 7, 7), 20, 70)
  case object MoerakiBoulders extends AssaultSkill("モエラキ・ボールダーズ", condenseLava(11, 11, 11), 60, 80)
  case object Eldfell extends AssaultSkill("エルト・フェットル", condenseLava(13, 13, 13), 150, 90)

  case object VenderBlizzard extends AssaultSkill("ヴェンダー・ブリザード", condenseLiquid(11, 11, 11), 170, 110)

  case object AssaultArmor extends AssaultSkill("アサルト・アーマー", armor(11, 11, 11), 600, 0)

  override def values: IndexedSeq[SeichiSkill] = findValues
}
