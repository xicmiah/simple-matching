package com.simplematching.processing

import com.simplematching.models._
import org.scalacheck._

import scala.collection.immutable.SortedMap

object TradeProcessingTest extends Properties("Clients") {
  private def equities = ClientGenerator.equities

  val genAccounts: Gen[ClientAccounts] = for {
    clients <- Gen.nonEmptyContainerOf[Seq, Client](ClientGenerator.genClient)
  } yield ClientAccounts(SortedMap(clients.map(c => c.name -> c):_*))

  private def genTrade(buyer: String, seller: String): Gen[Trade] = for {
    equity <- Gen.oneOf(equities)
    price <- Gen.posNum[Price]
    quantity <- Gen.posNum[Size]
  } yield Trade(buyer, seller, equity, price, quantity)

  val genTradeUpdate: Gen[(ClientAccounts, Trade, ClientAccounts)] = for {
    accounts <- genAccounts
    clientNames = accounts.clients.keySet.toSeq
    buyer <- Gen.oneOf(clientNames)
    seller <- Gen.oneOf(clientNames)
    trade <- genTrade(buyer, seller)
  } yield (accounts, trade, accounts.applyTrade(trade))

  val genNonSelfTradeUpdate = genTradeUpdate.suchThat {
    case (_, trade, _) => trade.buyer != trade.seller
  }

  property("decreasesBuyerBalance") = Prop.forAll(genNonSelfTradeUpdate) {
    case (old, trade, updated) =>
      old.clients(trade.buyer).balance == updated.clients(trade.buyer).balance + trade.sum
  }

  property("increasesSellerBalance") = Prop.forAll(genNonSelfTradeUpdate) {
    case (old, trade, updated) =>
      old.clients(trade.seller).balance + trade.sum == updated.clients(trade.seller).balance
  }

  property("increasesBuyerAssets") = Prop.forAll(genNonSelfTradeUpdate) {
    case (old, trade, updated) =>
      def assetValue(accounts: ClientAccounts) = accounts.clients(trade.buyer).assets(trade.equity)
      assetValue(old) + trade.size == assetValue(updated)
  }

  property("decreasesSellerAssets") = Prop.forAll(genNonSelfTradeUpdate) {
    case (old, trade, updated) =>
      def assetValue(accounts: ClientAccounts) = accounts.clients(trade.seller).assets(trade.equity)
      assetValue(old) == assetValue(updated) + trade.size
  }

  property("balanceZeroSum") = Prop.forAll(genTradeUpdate) {
    case (old, _, updated) =>
      old.clients.values.map(_.balance).sum == updated.clients.values.map(_.balance).sum
  }

  property("assetsZeroSum") = Prop.forAll(genTradeUpdate) {
    case (old, _, updated) =>
      def totalAssets(clients: ClientAccounts): Map[Equity, Size] = {
        equities.map(equity => equity -> old.clients.values.flatMap(c => c.assets.get(equity)).sum).toMap
      }
      totalAssets(old) == totalAssets(updated)
  }
}
