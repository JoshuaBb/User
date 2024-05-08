package com.jbb.user.store

import doobie.util.transactor.Transactor

trait UserStore[F[_]]

object UserStore:
  def impl[F[_]](transactor: Transactor[F]): UserStore[F] = new UserStore[F] {
    
  }
