package com.github.unchama.seichiassist.subsystems.minestack.domain

import com.github.unchama.generic.RefDict

import java.util.UUID

trait MineStackObjectPersistence[F[_]] extends RefDict[F, UUID, Vector[MineStackObject]]
