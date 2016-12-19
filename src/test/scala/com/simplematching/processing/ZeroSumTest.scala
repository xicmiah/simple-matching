package com.simplematching.processing

import com.simplematching.models._
import org.scalacheck._

class ZeroSumTest extends Properties("ZeroSum") {
  val genEvents: Gen[(ClientAccounts, Seq[Order])] = for {
    initialAccounts <- Generators.genAccounts
    orders <- Gen.containerOfN[Seq, Order](1000, Generators.genOrder(Gen.oneOf(initialAccounts.clients.keys.toSeq)))
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
