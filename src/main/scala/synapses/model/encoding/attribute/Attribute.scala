package synapses.model.encoding.attribute

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import scala.util.chaining._

enum Attribute(val key: String):
  case Discrete(s: String, values: LazyList[String]) extends Attribute(s)
  case Continuous(s: String, min: Double, max: Double) extends Attribute(s)

object Attribute:

  def realize(attribute: Attribute): Attribute =
    attribute match
      case Discrete(_, values) => values.force
      case Continuous(_, _, _) => ()
    attribute

  def parse(s: String): Double =
    s.trim.toDouble

  def updated(datapoint: Map[String, String])
             (attribute: Attribute): Attribute =
    attribute match

      case Discrete(s, values) =>
        val v = datapoint(s)
        val updatedValues =
          if values.contains(v) then
            values
          else
            values.prepended(v)
        Discrete(s, updatedValues)

      case Continuous(s, min, max) =>
        val v = s.pipe(datapoint).pipe(parse)
        Continuous(
          s,
          Math.min(v, min),
          Math.max(v, max)
        )

  def encode(v: String)(attribute: Attribute): LazyList[Double] =
    attribute match

      case Discrete(_, values) =>
        values.map(str => if str == v then 1.0 else 0.0)

      case Continuous(_, min, max) =>
        if min == max then
          LazyList(0.5)
        else
          LazyList(
            (parse(v) - min) / (max - min)
          )

  def decode(encodedValues: LazyList[Double])(attribute: Attribute): String =
    attribute match

      case Discrete(_, values) =>
        values
          .zip(encodedValues)
          .reduceLeft { case it@((_, accNum), (_, num)) =>
            if num > accNum then it._2 else it._1
          }
          ._1

      case Continuous(_, min, max) =>
        if min == max then
          min.asJson.noSpaces
        else
          val v = encodedValues.head * (max - min) + min
          v.asJson.noSpaces
