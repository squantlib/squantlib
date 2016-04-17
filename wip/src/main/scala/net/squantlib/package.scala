package net.squantlib

/**
  * Created by ceb on 02/04/16.
  */
package object numerictypes {
  type Integer = Int
  type BigInteger = Long
  type Natural = Double // >= 0
  type Real = Double
  type Decimal = Double
  type size_t = Int // >= 0
  type Time = Double
  type DiscountFactor = Double
  type Rate = Double
  type Spread = Double
  type Volatility = Double
  type Probability = Double
  // type DayCounter =

  object Option extends Enumeration {
    type Option = Value
    val Call, Put = Value
  }
}