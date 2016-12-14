package com.simplematching.models

object Client {
  def parseFromLine(line: String): Option[Client] = PartialFunction.condOpt(line.split('\t').toList) {
    case Seq(name, balance, aQuantity, bQuantity, cQuantity, dQuantity) =>
      val assets = Map(
        "A" -> aQuantity.toInt,
        "B" -> bQuantity.toInt,
        "C" -> cQuantity.toInt,
        "D" -> dQuantity.toInt)
      Client(name, balance.toLong, assets)
  }
}
case class Client(name: String, balance: Money, assets: Map[Equity, Quantity])
