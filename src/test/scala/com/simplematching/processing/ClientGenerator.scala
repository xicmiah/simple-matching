package com.simplematching.processing

import com.simplematching.models.{Client, _}
import org.scalacheck.Gen

import scala.collection.immutable.SortedMap

object ClientGenerator {
  val equities = Seq("A", "B", "C", "D")

  val genClient: Gen[Client] = for {
    name <- Gen.alphaNumStr
    balance <- Gen.posNum[Money]
    assets <- Gen.containerOfN[Seq, Size](equities.size, Gen.posNum[Size]).map(equities.zip(_).toMap)
  } yield Client(name, balance, assets)

  val genAccounts: Gen[ClientAccounts] = for {
    clients <- Gen.nonEmptyContainerOf[Seq, Client](ClientGenerator.genClient)
  } yield ClientAccounts(SortedMap(clients.map(c => c.name -> c):_*))
}
