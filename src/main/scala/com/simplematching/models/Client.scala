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

case class Client(name: String, balance: Money, assets: Map[Equity, Size]) {
  def applyChange(balanceChange: Money, equity: Equity, equityChange: Size): Client = {
    copy(balance = balance + balanceChange, assets = updateAssets(equity, equityChange))
  }

  private def updateAssets(equity: Equity, equityChange: Size) = {
    val quantity = assets.getOrElse(equity, 0)
    val newQuantity = quantity + equityChange
    assets.updated(equity, newQuantity)
  }

  def toTsvLine = s"$name\t$balance\t${assets("A")}\t${assets("B")}\t${assets("C")}\t${assets("D")}"
}
