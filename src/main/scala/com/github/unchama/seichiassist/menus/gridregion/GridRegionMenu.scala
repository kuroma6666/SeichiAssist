package com.github.unchama.seichiassist.menus.gridregion

import cats.effect.IO
import com.github.unchama.itemstackbuilder.IconItemStackBuilder
import com.github.unchama.menuinventory.slot.button.action.{
  ClickEventFilter,
  FilteredButtonEffect,
  LeftClickButtonEffect
}
import com.github.unchama.menuinventory.slot.button.{Button, RecomputedButton}
import com.github.unchama.menuinventory.{
  LayoutPreparationContext,
  Menu,
  MenuFrame,
  MenuSlotLayout
}
import com.github.unchama.minecraft.actions.OnMinecraftServerThread
import com.github.unchama.seichiassist.subsystems.gridregion.GridRegionAPI
import com.github.unchama.seichiassist.subsystems.gridregion.domain.{
  Direction,
  RelativeDirection
}
import com.github.unchama.targetedeffect.player.FocusedSoundEffect
import com.github.unchama.targetedeffect.{DeferredEffect, SequentialEffect}
import org.bukkit.ChatColor._
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.{Material, Sound}

object GridRegionMenu extends Menu {

  class Environment(
    implicit val onMainThread: OnMinecraftServerThread[IO],
    implicit val layoutPreparationContext: LayoutPreparationContext,
    val gridRegionAPI: GridRegionAPI[IO, Player]
  )

  override val frame: MenuFrame =
    MenuFrame(Right(InventoryType.DISPENSER), s"${LIGHT_PURPLE}グリッド式保護設定メニュー")

  override def computeMenuLayout(player: Player)(
    implicit environment: Environment
  ): IO[MenuSlotLayout] = ???

  case class computeButtons(player: Player)(implicit environment: Environment) {
    import environment._

    def toggleUnitPerClickButton(): IO[Button] = RecomputedButton {
      for {
        currentRegionUnit <- gridRegionAPI.unitPerClick(player)
      } yield {
        val iconItemStack = new IconItemStackBuilder(Material.STAINED_GLASS_PANE, 1)
          .title(s"${GREEN}拡張単位の変更")
          .lore(
            List(
              s"${GREEN}現在のユニット指定量",
              s"$AQUA${currentRegionUnit.units}${GREEN}ユニット($AQUA${currentRegionUnit.computeBlockAmount}${GREEN}ブロック)/1クリック",
              s"$RED${UNDERLINE}クリックで変更"
            )
          )
          .build()

        val leftClickEffect = LeftClickButtonEffect {
          SequentialEffect(
            DeferredEffect(IO(gridRegionAPI.toggleUnitPerClick)),
            FocusedSoundEffect(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f)
          )
        }

        Button(iconItemStack, leftClickEffect)
      }
    }

    private def gridLore(direction: Direction): IO[List[String]] = for {
      currentRegionUnit <- gridRegionAPI.unitPerClick(player)
    } yield List(
      s"${GREEN}左クリックで増加",
      s"${RED}右クリックで減少",
      s"$GRAY---------------",
      s"${GRAY}方向：$AQUA${direction.uiLabel}",
      s"${GRAY}現在の指定方向のユニット数：$AQUA${currentRegionUnit.units}$GRAY($AQUA${currentRegionUnit.computeBlockAmount}${GRAY}ブロック)"
    )

    def regionUnitExpansionButton(relativeDirection: RelativeDirection): IO[Button] =
      RecomputedButton {
        val yaw = player.getEyeLocation.getYaw
        val direction = Direction.relativeDirection(yaw)(relativeDirection)
        for {
          gridLore <- gridLore(direction)
          regionUnits <- gridRegionAPI.regionUnits(player)
          currentPerClickRegionUnit <- gridRegionAPI.unitPerClick(player)
        } yield {
          val worldName = player.getEyeLocation.getWorld.getName
          val expandedRegionUnits =
            regionUnits.expansionRegionUnits(relativeDirection, currentPerClickRegionUnit)
          val contractedRegionUnits =
            regionUnits.contractRegionUnits(relativeDirection, currentPerClickRegionUnit)

          val lore = gridLore ++ {
            if (gridRegionAPI.isWithinLimits(expandedRegionUnits, worldName))
              List(s"$RED${UNDERLINE}これ以上拡張できません")
            else if (gridRegionAPI.isWithinLimits(contractedRegionUnits, worldName))
              List(s"$RED${UNDERLINE}これ以上縮小できません")
            else
              List.empty
          }

          val relativeDirectionString = relativeDirection match {
            case RelativeDirection.Ahead  => "前へ"
            case RelativeDirection.Behind => "後ろへ"
            case RelativeDirection.Left   => "左へ"
            case RelativeDirection.Right  => "右へ"
          }

          val itemStack =
            new IconItemStackBuilder(Material.STAINED_GLASS_PANE, 1)
              .title(s"$DARK_GREEN${relativeDirectionString}ユニット増やす/減らす")
              .lore(lore)
              .build()

          val leftClickButtonEffect = FilteredButtonEffect(ClickEventFilter.LEFT_CLICK) { _ =>
            SequentialEffect(
              DeferredEffect(IO(gridRegionAPI.saveRegionUnits(expandedRegionUnits))),
              FocusedSoundEffect(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f)
            )
          }

          val rightClickButtonEffect = FilteredButtonEffect(ClickEventFilter.RIGHT_CLICK) { _ =>
            SequentialEffect(
              DeferredEffect(IO(gridRegionAPI.saveRegionUnits(contractedRegionUnits))),
              FocusedSoundEffect(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1f, 1f)
            )
          }

          Button(itemStack, leftClickButtonEffect, rightClickButtonEffect)
        }
      }

    val openGridRegionSettingMenuButton: Button = {
      val itemStack = new IconItemStackBuilder(Material.CHEST)
        .title(s"${GREEN}設定保存メニュー")
        .lore(List(s"$RED${UNDERLINE}クリックで開く"))
        .build()

      val leftClickButtonEffect = LeftClickButtonEffect {
        // TODO: openGridRegionSettingMenuを開く
        SequentialEffect(FocusedSoundEffect(Sound.BLOCK_STONE_BUTTON_CLICK_ON, 1.0f, 1.0f))
      }

      Button(itemStack, leftClickButtonEffect)
    }

  }

}
