package com.jbb.user.model

import cats.Semigroup
import cats.data.Validated

/**
 * An interface typically associated with the request api model for aggregating errors in a request body
 *
 * @tparam Error
 * @tparam A
 */
trait ValidationRule[Error: Semigroup, A] {
  def validate(a: A): Validated[Error, A]
}
