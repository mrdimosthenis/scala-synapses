import com.github.tototoshi.csv._
import java.io.File
import org.junit.{Ignore, Test}
import org.junit.Assert.*
import scala.io.Source
import scala.util.chaining._
import scala.util.Random
import synapses.lib._

class ScoreExperiment:

  @Ignore
  @Test def `trained network json`(): Unit =

    val selectedNumOfRows = 999999999

    val learningRate = 0.1

    val net = Net(List(784, 128, 64, 10), 1000)

    val startMillis = System.currentTimeMillis()

    val preprocessorJsonSource: Source = Source.fromFile("test-resources/preprocessor.json")

    val preprocessor = preprocessorJsonSource.getLines().mkString.pipe(Codec.apply)

    val csvReaderFirst: CSVReader = CSVReader.open(new File("test-resources/mnist.csv"))

    val csvIteratorFirst: Iterator[Seq[String]] = csvReaderFirst.iterator.take(selectedNumOfRows)

    val csvReaderSecond: CSVReader = CSVReader.open(new File("test-resources/mnist.csv"))

    val csvIteratorSecond: Iterator[Seq[String]] = csvReaderSecond.iterator.take(selectedNumOfRows)

    val (headersMulti, rowsFirst) = csvIteratorFirst.splitAt(1)

    val rowsSecond = csvIteratorSecond.drop(1)

    val headers = headersMulti.next()

    val ysWithXsMultiFirst: Iterator[(List[Double], List[Double])] =
      rowsFirst.map { row =>
        headers.zip(row)
          .pipe(Map.from)
          .pipe(preprocessor.encode)
          .splitAt(10)
      }

    val fitNet = ysWithXsMultiFirst.foldLeft(net) { case (acc, (ys, xs)) =>
      acc.fit(learningRate, xs, ys)
    }

    val outputPairs: Iterator[(List[Double], List[Double])] =
      rowsSecond.map { row =>
        val (ys, xs) =
          headers
            .zip(row)
            .pipe(Map.from)
            .pipe(preprocessor.encode)
            .splitAt(10)
        (ys, fitNet.predict(xs))
      }

    assertEquals(
      0.0,
      Stats.score(outputPairs),
      0.001
    )

    val endMillis = System.currentTimeMillis()

    println(s"${endMillis - startMillis}: the duration of SCORE experiment")

    preprocessorJsonSource.close()

    csvReaderFirst.close()

    csvReaderSecond.close()
