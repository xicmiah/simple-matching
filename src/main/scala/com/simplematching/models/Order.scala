package com.simplematching.models

object Order {
  def parseFromLine(line: String): Option[Order] = PartialFunction.condOpt(line.split('\t').toList) {
    case Seq(client, actionRaw, equity, price, quantity) =>
      val action = actionRaw match {
        case "b" => Action.Buy
        case "s" => Action.Sell
      }
      Order(client, action, equity, price.toLong, quantity.toInt)
  }
}
case class Order(client: String, action: Action, equity: Equity, price: Price, quantity: Quantity)
