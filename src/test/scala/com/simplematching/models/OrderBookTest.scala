package com.simplematching.models

import com.simplematching.models.Action.{Buy, Sell}
import utest._

object OrderBookTest extends TestSuite {
  val tests = this {
    def genOrder(action: Action, price: Price, size: Size) = Order("C1", action, "A", price, size)

    val empty = OrderBook.empty
    "adds buy orders to bids" - {
      val order = genOrder(Buy, 10, 1)
      val (updated, trades) = empty.addOrderAndMatch(order)
      assert(updated.bids.values.flatten.toSeq.contains(order))
      assert(trades.isEmpty)
    }

    "adds sell orders to asks" - {
      val order = genOrder(Sell, 10, 1)
      val (updated, trades) = empty.addOrderAndMatch(order)
      assert(updated.asks.values.flatten.toSeq.contains(order))
      assert(trades.isEmpty)
    }

    "returns matches" - {
      val buy = genOrder(Buy, 10, 1)
      val sell = genOrder(Sell, 10, 1).copy(client = "C2")
      val (book, _) = empty.addOrderAndMatch(sell)

      val (result, trades) = book.addOrderAndMatch(buy)
      assert(trades == Seq(Trade("C1", "C2", "A", 10, 1)))
      assert(result == empty)
    }
  }
}
