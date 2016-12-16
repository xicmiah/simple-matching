package com.simplematching.models

import com.simplematching.models.Action.{Buy, Sell}
import utest._

object OrderBookTest extends TestSuite {
  val tests = this {
    def genOrder(action: Action, price: Price, size: Size) = Order("C1", action, "A", price, size)

    val empty = OrderBook.empty
    "adds buy orders to bids" - {
      val order = genOrder(Buy, 10, 1)
      val updated = empty.addOrder(order)
      assert(updated.bids.values.flatten.toSeq.contains(order))
    }

    "adds sell orders to asks" - {
      val order = genOrder(Sell, 10, 1)
      val updated = empty.addOrder(order)
      assert(updated.asks.values.flatten.toSeq.contains(order))
    }

    "with same price/size buy+sell orders" - {
      val buyOrder = genOrder(Buy, 10, 5)
      val unmatched = genOrder(Sell, 5, 9)
      val filled = empty
        .addOrder(buyOrder)
        .addOrder(buyOrder.copy(action = Sell))
        .addOrder(unmatched)

      "has trades" - {
        val trades = filled.possibleTrades
        assert(
          trades.size == 1,
          trades.head == Trade("C1", "C1", "A", 10, 5))
      }

      "removes orders by trade" - {
        val afterMatching = filled.removeMatchedOrders(filled.possibleTrades.head)
        assert(afterMatching == empty.addOrder(unmatched))
      }
    }
  }
}
