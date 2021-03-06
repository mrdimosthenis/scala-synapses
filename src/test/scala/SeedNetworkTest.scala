import org.junit.Test
import org.junit.Assert.*
import scala.util.chaining._
import synapses.lib._

class SeedNetworkTest:

  val layers = List(4, 6, 5, 3)

  val neuralNetwork: Net = Net(layers, 1000)

  val neuralNetworkJson = """[[{"activationF":"sigmoid","weights":[-0.4203698112641414,-0.1496727007713341,-0.8928384189584146,0.9211880913772268,0.027180243817137795]},{"activationF":"sigmoid","weights":[-0.3161167802991376,-0.9489930079469029,-0.26015677306584273,-0.6978873003833059,0.2874994065396639]},{"activationF":"sigmoid","weights":[-0.21496286920606544,-0.9357227175449083,-0.9155202030305005,-0.9129309852279106,-0.6978999469719491]},{"activationF":"sigmoid","weights":[0.25806133542967125,0.47588465535587576,-0.5681549645809776,0.9815364553014789,-0.6175472916357287]},{"activationF":"sigmoid","weights":[0.8131999422319118,-0.18065888278217646,-0.8943132277876955,0.294939737903549,-0.13946653126709108]},{"activationF":"sigmoid","weights":[0.3867614051417534,0.4437184379986423,0.2690674140331535,0.32810615530021336,0.17323902449022643]}],[{"activationF":"sigmoid","weights":[-0.20162813099768573,-0.10075233916843396,0.7276149322065253,0.8063910194692945,0.6688055519864093,-0.975106575568284,0.09221847729601618]},{"activationF":"sigmoid","weights":[-0.9446600359811137,-0.5964794872043058,0.7903102784527913,-0.9051888424151342,-0.09122610135386999,0.6535569969369026,-0.22748365874317633]},{"activationF":"sigmoid","weights":[0.09017935830009893,0.6208093403826789,0.33538576542866516,0.753380386023317,-0.2055709809859254,0.9488128412675767,-0.07338684978278609]},{"activationF":"sigmoid","weights":[-0.07576012821021783,-0.35625015691520523,-0.06294831714455151,-0.5599640923034961,-0.35867440290460384,0.5199823465293354,0.6691922308539804]},{"activationF":"sigmoid","weights":[0.07030263961505323,-0.26545912729664556,0.37560221411197214,0.2923244847426285,-0.8896329641128919,0.5311148426965802,0.6182392962661842]}],[{"activationF":"sigmoid","weights":[0.10852652658514339,0.45388143955720217,-0.13615987609385183,-0.7968643692136412,-0.8999692624692617,-0.20406638996276305]},{"activationF":"sigmoid","weights":[-0.11406885695371116,-0.18786441034717605,-0.5055219662230839,0.04524373070462362,-0.11891872316176122,-0.9069098420319777]},{"activationF":"sigmoid","weights":[-0.49189194418627435,0.9655404131465519,-0.7461112874103033,-0.2337494582987365,0.03348554207672061,0.5612844635907202]}]]"""

  @Test def `neural network of/to json`(): Unit =
    assertEquals(
      neuralNetwork.json(),
      neuralNetwork.json().pipe(Net.apply).json()
    )

  @Test def `neural network of json`(): Unit =
    assertEquals(neuralNetwork, Net(neuralNetworkJson))

  val inputValues = List(1.0, 0.5625, 0.511111, 0.47619)

  val prediction: List[Double] =
    neuralNetwork.predict(inputValues)

  @Test def `neural network prediction`(): Unit =
    assertEquals(
      List(0.291094231333837, 0.2908897735203388, 0.4852741175830821),
      prediction
    )

  val learningRate = 0.99

  val expectedOutput = List(0.4, 0.05, 0.9)

  @Test def `neural network normal errors`(): Unit =
    assertEquals(
      List(-0.024340361539339307, -0.013691453365878362, -0.012440626526733252, -0.011590636761417986),
      neuralNetwork.errors(inputValues, expectedOutput)
    )

  @Test def `neural network zero errors`(): Unit =
    assertEquals(
      List(0, 0, 0, 0),
      neuralNetwork.errors(inputValues, prediction)
    )

  val fitNetwork: Net =
    neuralNetwork.fit(learningRate, inputValues, expectedOutput)

  @Test def `fit neural network prediction`(): Unit =
    assertEquals(
      List(0.3000848628242431, 0.26517382555679964, 0.548974781113718),
      fitNetwork.predict(inputValues)
    )
