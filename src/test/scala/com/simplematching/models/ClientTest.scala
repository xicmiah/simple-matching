package com.simplematching.models
import utest._

object ClientTest extends utest.TestSuite {
  override val tests = this {
    val client = Client("C1", 1000, Map("A" -> 130, "B" -> 240, "C" -> 760, "D" -> 320))

    "parses tsv" - {
      val tsv = "C1\t1000\t130\t240\t760\t320"
      assert(Client.parseFromLine(tsv).contains(client))
    }

    "applies balance/asset changes" - {
      val updated = client.applyChange(-10, "A", 5)

      assert(
        updated.balance == 990,
        updated.assets("A") == 135)
    }
  }
}
