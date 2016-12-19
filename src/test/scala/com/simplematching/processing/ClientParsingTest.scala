package com.simplematching.processing

import com.simplematching.models.Client
import org.scalacheck.{Prop, Properties}

class ClientParsingTest extends Properties("Client") {
  property("parsesOwnTsv") = Prop.forAll(Generators.genClient) { client =>
    Client.parseFromLine(client.toTsvLine).contains(client)
  }
}
