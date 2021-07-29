package synapses.model.net_elems.network

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import scala.util.chaining._
import synapses.model.net_elems.layer.LayerSerialized

type NetworkSerialized = List[LayerSerialized]

object NetworkSerialized:

  private def serialized(network: Network): NetworkSerialized =
    network
      .map(LayerSerialized.serialized)
      .toList

  private def deserialized(networkSerialized: NetworkSerialized): Network =
    networkSerialized
      .to(LazyList)
      .map(LayerSerialized.deserialized)

  def toJson(network: Network): String =
    serialized(network)
      .asJson
      .spaces2

  def ofJson(s: String): Network =
    decode[NetworkSerialized](s)
      .toOption
      .get
      .pipe(deserialized)
