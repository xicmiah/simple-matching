package com.simplematching.processing

import com.simplematching.models._
import org.scalacheck._

class ZeroSumTest extends Properties("ZeroSum") {
  private def genOrder(genClient: Gen[String]): Gen[Order] = {
    for {
      client <- genClient
      action <- Gen.oneOf(Action.Buy, Action.Sell)
      equity <- Gen.oneOf(ClientGenerator.equities)
      price <- Gen.posNum[Price]
      size <- Gen.posNum[Size]
    } yield Order(client, action, equity, price, size)
  }

  val genEvents: Gen[(ClientAccounts, Seq[Order])] = for {
    initialAccounts <- ClientGenerator.genAccounts
    orders <- Gen.containerOfN[Seq, Order](1000, genOrder(Gen.oneOf(initialAccounts.clients.keys.toSeq)))
    if hasTrades(initialAccounts, orders)
  } yield initialAccounts -> orders

  private def hasTrades(initial: ClientAccounts, orders: Seq[Order]) = {
    orders.view
      .scanLeft(OrderBooks() -> Seq.empty[Trade]) { case ((books, _), order) => books.addOrderEmitTrades(order) }
      .exists(_._2.nonEmpty)
  }

  private def runOrders(initial: ClientAccounts, orders: Seq[Order]) = {
    orders.view
      .scanLeft(OrderBooks() -> Seq.empty[Trade]) { case ((books, _), order) => books.addOrderEmitTrades(order) }
      .flatMap(_._2)
      .foldLeft(initial)(_ applyTrade _)
  }

  property("balanceZeroSum") = Prop.forAll(genEvents) {
    case (initial, orders) =>
      val updated = runOrders(initial, orders)
      ClientStats.totalBalance(initial) == ClientStats.totalBalance(updated)
  }

  property("assetsZeroSum") = Prop.forAll(genEvents) {
    case (initial, orders) =>
      val updated = runOrders(initial, orders)
      ClientStats.totalAssets(initial) == ClientStats.totalAssets(updated)
  }
}
