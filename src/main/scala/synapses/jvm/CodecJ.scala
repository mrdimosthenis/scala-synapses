package synapses.jvm

import scala.jdk.StreamConverters._
import scala.jdk.FunctionConverters._
import scala.jdk.CollectionConverters._
import scala.util.chaining._
import synapses.custom.Tuple
import synapses.lib.Codec

case class CodecJ(codec: Codec):

  def encode(datapoint: java.util.Map[String, String]): Array[Double] =
    datapoint
      .asScala
      .toMap
      .pipe(codec.encode)
      .toArray

  def decode(encodedValues: Array[Double]): java.util.Map[String, String] =
    encodedValues
      .toList
      .pipe(codec.decode)
      .asJava

  def json(): String =
    codec.json()

object CodecJ:

  def apply(keysWithFlags: Array[Tuple[String, Boolean]],
            datapoints: java.util.stream.Stream[java.util.Map[String, String]]): CodecJ =
    val keysFlags =
      keysWithFlags
        .map { t =>
          (t.x, t.y)
        }
        .toList
    val points =
      datapoints
        .toScala(Iterator)
        .map(_.asScala.toMap)
    Codec(keysFlags, points)
      .pipe(CodecJ.apply)

  def apply(json: String): CodecJ =
    json
      .pipe(Codec.apply)
      .pipe(CodecJ.apply)
