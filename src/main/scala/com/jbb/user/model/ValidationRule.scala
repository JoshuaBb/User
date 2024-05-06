package com.jbb.user.model

import cats.Semigroup
import cats.data.Validated


trait ValidationRule[Error: Semigroup, A] {
  def validate(a: A): Validated[Error, A]
}
