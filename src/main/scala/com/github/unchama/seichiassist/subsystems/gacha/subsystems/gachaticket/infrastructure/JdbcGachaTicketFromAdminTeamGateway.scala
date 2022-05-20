package com.github.unchama.seichiassist.subsystems.gacha.subsystems.gachaticket.infrastructure

import cats.effect.Sync
import com.github.unchama.concurrent.NonServerThreadContextShift
import com.github.unchama.seichiassist.subsystems.gacha.subsystems.gachaticket.domain.GachaTicketFromAdminTeamGateway
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

import java.util.UUID

class JdbcGachaTicketFromAdminTeamGateway[F[_]: Sync: NonServerThreadContextShift]
    extends GachaTicketFromAdminTeamGateway[F] {

  import cats.implicits._

  /**
   * 現在データベース中にある全プレイヤーの「運営からのガチャ券」の枚数を増加させる作用
   */
  override def add(amount: Int): F[Boolean] = {
    NonServerThreadContextShift[F].shift >> Sync[F].delay {
      DB.localTx { implicit session =>
        sql"update playerdata set numofsorryforbug = numofsorryforbug + $amount"
          .execute()
          .apply()
      }
    }
  }

  /**
   * 指定されたUUIDのプレイヤーの「運営からのガチャ券」の枚数を増加させる作用
   */
  override def add(amount: Int, uuid: UUID): F[Boolean] = {
    NonServerThreadContextShift[F].shift >> Sync[F].delay {
      DB.localTx { implicit session =>
        sql"update playerdata set numofsorryforbug = numofsorryforbug + $amount where uuid = ${uuid.toString}"
          .execute()
          .apply()
      }
    }
  }

}
