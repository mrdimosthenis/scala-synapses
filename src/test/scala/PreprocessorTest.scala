import com.github.tototoshi.csv._
import java.io.File
import org.junit.Test
import org.junit.Assert.*
import scala.io.Source
import scala.util.chaining._
import scala.util.Random
import synapses.lib._
import synapses.model.encoding.attribute.Attribute.{Discrete, Continuous}

class PreprocessorTest:

  val csvReader: CSVReader = CSVReader.open(new File("test-resources/mnist.csv"))

  val csvIterator: Iterator[Seq[String]] = csvReader.iterator

  val (headersMulti, rows) = csvIterator.splitAt(1)

  val headers = headersMulti.next()

  val datapoints: Iterator[Map[String, String]] = rows.map { row =>
    val ksVs = headers.zip(row)
    Map.from(ksVs)
  }

  val pixelKeysWithFlags: List[(String, Boolean)] =
    (0 to 783)
      .map(i => ("pixel" ++ i.toString, false))
      .toList

  val keysWithDiscreteFlags: List[(String, Boolean)] =
    pixelKeysWithFlags.prepended(("label", true))

  val preprocessor: Codec = Codec(keysWithDiscreteFlags, datapoints)

  val preprocessorJsonSource: Source = Source.fromFile("test-resources/preprocessor.json")

  val preprocessorJson: String = preprocessorJsonSource.getLines.mkString

  @Test def `preprocessor of/to json`(): Unit =
    assertEquals(
      preprocessor.json(),
      preprocessor.json().pipe(Codec.apply).json()
    )

  @Test def `preprocessor of json`(): Unit =
    assertEquals(preprocessor, Codec(preprocessorJson))

  val rnd = Random(1000)

  val randomDatapoint: Map[String, String] =
    preprocessor
      .attributes
      .map { attribute =>
        attribute match
          case Discrete(s, values) =>
            values
              .length
              .pipe(rnd.nextInt)
              .toString
              .pipe((s, _))
          case Continuous(s, min, max) =>
            val v = min + (max - min) * rnd.nextDouble()
            (s, v.toString)
      }
      .pipe(Map.from)

  val randomEncodedDatapoint: List[Double] =
    preprocessor.encode(randomDatapoint)

  @Test def `encode decode random datapoint`(): Unit =
    assertEquals(
      randomDatapoint,
      preprocessor.decode(randomEncodedDatapoint)
    )

  @Test def `random encoded datapoint`(): Unit =
    assertEquals(
      List(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.7132244875459751, 0.23509154628934603, 0.9772998238781296, 0.2967158400678649, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.7309465896806235, 0.8450283462623699, 0.7524087032071788, 0.3231975497809124, 0.9329963692112381, 0.3744272779384149, 0.10031576413407928, 0.9404458373522719, 0.39279174960918517, 0.5027547620623471, 0.45997733690758136, 0.014503541189752611, 0.18815649470302198, 0.9584091241371622, 0.30351958356664055, 0.8541156975864668, 0.20316455263357036, 0.14015589838191578, 0.06704685990192727, 0.5026655200957296, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.0576393854519307, 0.8624031281472566, 0.7121809745640141, 0.7948818199334238, 0.41961499251199763, 0.8596161996234889, 0.23565464186964769, 0.04753038814422106, 0.9547684482457709, 0.7992844765550283, 0.12654690728903328, 0.5737084687059103, 0.8054910236155156, 0.7039710371782213, 0.5822144262904075, 0.4401387643423398, 0.3146454030194783, 0.20985814859650898, 0.6245927474788721, 0.792546138424125, 0.863822130457396, 0.4578414800297429, 0.303521789360899, 0.6554035428070688, 0.5, 0.5, 0.5, 0.5, 0.7863072925557698, 0.420378125158008, 0.6314328469599423, 0.8783298122507406, 0.0010672905476456496, 0.9195844557605864, 0.2780939923142727, 0.4613847620249778, 0.39229953140783735, 0.30747391478276087, 0.8850478746325293, 0.018539054601921734, 0.5767091348767349, 0.8422756957364779, 0.5926213953829722, 0.35291186730882285, 0.06768392856874916, 0.3617924808671258, 0.696042715649967, 0.21820076687878087, 0.2303066112266432, 0.32786332610298485, 0.5770181316289112, 0.9085609858976418, 0.7475179325516867, 0.5, 0.5, 0.17862883058651058, 0.9198495961453347, 0.6062803419144348, 0.3890203300405841, 0.7992614676750874, 0.1494650937799631, 0.3590287871687132, 0.23709953984897458, 0.8563235066336407, 0.6438175299187856, 0.5310708377878923, 0.5785566497596617, 0.10785834038621822, 0.7150534301519001, 0.8590133771167145, 0.7602153825026867, 0.3292405484349833, 0.04836203612262102, 0.7207597383502553, 0.8383176249145983, 0.5964127596262496, 0.9657899126265925, 0.1389143359531645, 0.08909027545978443, 0.884068661373336, 0.021610904381485052, 0.5, 0.5, 0.5, 0.23116192321402473, 0.7353772271707111, 0.6609792518273673, 0.6252963766045857, 0.8571250952449645, 0.9084498431145069, 0.4966259913998994, 0.5933475070143892, 0.9555574951526185, 0.11374812865920168, 0.3687773775402957, 0.1568499175556327, 0.8185396847561119, 0.676646706106301, 0.4578621506779761, 0.24440927219535735, 0.04113485238155057, 0.4159803178409607, 0.7323349216184359, 0.13708292593409788, 0.1874202213568733, 0.5555170299148556, 0.4622136771337806, 0.9834663474185552, 0.9336733635647205, 0.25279144943924814, 0.5, 0.7578996926544577, 0.126365643674466, 0.08944505491614929, 0.9612808806710923, 0.1607262878203196, 0.4084361968057125, 0.02835250900006026, 0.4311869263825935, 0.4487926636420255, 0.9393889710626897, 0.4101970489793375, 0.24821553379011363, 0.0798876724383416, 0.2630769467728208, 0.5357896054953022, 0.3357001043486799, 0.3565760521839356, 0.561607191113699, 0.26030992324806834, 0.7890397852551848, 0.9628894903523042, 0.3746206023298717, 0.8117802065024583, 0.5456807005842929, 0.40159626073332355, 0.3602713995520276, 0.7384651755992266, 0.5, 0.8602780840085725, 0.8275505526972048, 0.48852475262892137, 0.43337121048696814, 0.9612007575713458, 0.5791685645723296, 0.6301286532750795, 0.6208662698087913, 0.18297314798460695, 0.07003293156111956, 0.1313750088069452, 0.6880309247293094, 0.4812464316081968, 0.008426023186689722, 0.4367377027936902, 0.0625707525463911, 0.78121385304039, 0.30530925954867405, 0.30040034989770314, 0.39026629524209, 0.5664287503528005, 0.7117502232040009, 0.4667170554503376, 0.4006128247891988, 0.7464663180410591, 0.5752889154541377, 0.3241735095937718, 0.15063118995872715, 0.35021296039917293, 0.8931306470439082, 0.601604457721266, 0.8818654997915065, 0.36207389447880534, 0.24337243388688867, 0.024056461067298307, 0.3320286309686905, 0.19514867781087397, 0.23504048316187898, 0.6005675215423733, 0.10545258351584863, 0.8784312254079095, 0.8335371373753158, 0.8772684277397491, 0.6254448246911509, 0.02434890658586819, 0.2901648546280313, 0.05659282612590044, 0.2668482907777101, 0.038463655279686204, 0.4327161825529031, 0.3570165361583071, 0.4985471457716564, 0.8349149145396002, 0.7586884612089211, 0.36762870903483175, 0.2533562115026745, 0.5291929450406244, 0.11350226769073568, 0.43797096779378114, 0.2866770099730115, 0.19408817615195884, 0.7331733178350507, 0.33596291973909775, 0.9854989665611834, 0.785170623111337, 0.08420734197292379, 0.505365442120062, 0.9457691413009538, 0.7689105807479358, 0.2430581850329806, 0.25911626789948483, 0.6488498367667305, 0.8195603959961851, 0.5233095116251232, 0.15600444820573478, 0.736728190987547, 0.19017421241520105, 0.6808107392832523, 0.6255338940887626, 0.7108373543253753, 0.008650718173191296, 0.30531840133626, 0.6294586010702626, 0.36079431043043086, 0.8014419521824646, 0.9417639969266203, 0.7783548556116759, 0.8670544755985244, 0.00557855013333497, 0.07509026294118504, 0.857729055747615, 0.3154286431614277, 0.09392003749849553, 0.3596677937106414, 0.1605417817931829, 0.31160324251048044, 0.20904623123329125, 0.3776239482433368, 0.37166255647543134, 0.4902500175676823, 0.8779641201083075, 0.7699871837764062, 0.004651522364585103, 0.9760064993086414, 0.4628118182278729, 0.800369194407329, 0.5523980614031722, 0.27255437913353864, 0.49199957738677336, 0.2829784037152685, 0.5805282527442874, 0.6312175794584319, 0.9117349130037818, 0.33507824194299507, 0.9661266170948992, 0.6819477149351075, 0.5996378938976608, 0.598573989823877, 0.3642001163050259, 0.9972595070291064, 0.19015878486634885, 0.41623234546951315, 0.7704451177463298, 0.3036242699260523, 0.3757700097993806, 0.9451083125855526, 0.7412608152702387, 0.25502484341934395, 0.3684102074038389, 0.3430740110166304, 0.430585913964451, 0.03982075559248033, 0.13241787917073577, 0.9739146024162553, 0.10801720797967373, 0.6047673288980029, 0.33319818035944915, 0.04227666161264054, 0.23380286877643522, 0.9489783728672084, 0.6851257085726683, 0.19182481265957096, 0.4836843316174916, 0.7658167999862423, 0.52769285967803, 0.37828257236671503, 0.3401955798469847, 0.6921433424307122, 0.010671887951292593, 0.7636036546392829, 0.9969090646051485, 0.5620483756941869, 0.22706599281752682, 0.46834066503653227, 0.3582527902539401, 0.3561376227279939, 0.9968831214511157, 0.766347800576669, 0.8599035720808348, 0.987637386187761, 0.7752737727102381, 0.7092751448498354, 0.6833962225135394, 0.6730308143024832, 0.33533162868270383, 0.22788184995141145, 0.6934699200311106, 0.3976311784599009, 0.4588640109609188, 0.473016442910996, 0.08780632759174356, 0.9054446104012417, 0.8576971102782069, 0.5311820327615197, 0.3161971132552197, 0.7178689373056425, 0.9435880444779219, 0.6779254991152748, 0.2998107291075959, 0.9172210830252266, 0.8665744451365505, 0.016858924886182902, 0.36111157764927515, 0.8187811404630332, 0.44959947729008454, 0.6929428712536013, 0.5611172493458053, 0.19898579930043647, 0.2615084034097749, 0.86573066309702, 0.33306264746460323, 0.44759951054788316, 0.09102029840542236, 0.2815937126811432, 0.40017214082232666, 0.5, 0.5110366481061321, 0.5935176592501574, 0.9701241450826644, 0.363508732467017, 0.05565603788008111, 0.5428800222760993, 0.10992520885445156, 0.9127947696962925, 0.7715223615154883, 0.7396900255331208, 0.02229326378354768, 0.5702717004582928, 0.550641209758768, 0.056091320820698676, 0.6682829033646019, 0.1992324976425358, 0.7035577022774137, 0.5824993584994845, 0.810768116721941, 0.7908586619217959, 0.6897959664476323, 0.6865649563651616, 0.0895306257850812, 0.012337005081167973, 0.7867190791202222, 0.9622256243786866, 0.18108514023583366, 0.5, 0.5, 0.7594275875873764, 0.4450054410516868, 0.14861349320043382, 0.18345603568720914, 0.544670607129438, 0.7133976476466244, 0.7094804854624294, 0.46169352112976236, 0.5583486081247953, 0.0413007609284034, 0.8312755872619626, 0.4528944866091873, 0.10692942913393455, 0.3031765652137006, 0.7498444985295757, 0.29130073247124244, 0.3563372024523307, 0.4942837788691473, 0.9669486899087941, 0.6653358086669304, 0.6774709428996296, 0.47561507108525813, 0.944480418846182, 0.8379997692501183, 0.43561404215191757, 0.6872071705047196, 0.5, 0.7182329965891994, 0.61255846641287, 0.04701789323694072, 0.8191782880698101, 0.8239778811177222, 0.8035889778713965, 0.8511965179637693, 0.9786552597412819, 0.9044250967573169, 0.9410422044035397, 0.9964567562720742, 0.14255436667983534, 0.3219384553898892, 0.5619759071817341, 0.2563941600026416, 0.3185466508807495, 0.033411180516030314, 0.5595544481854002, 0.6609731875604888, 0.14158619286001184, 0.1563127828912949, 0.13607609167385293, 0.2520411869232331, 0.7563680923571143, 0.6564452119226623, 0.11013882927604901, 0.617610031028673, 0.5, 0.6524332033359649, 0.8212611705593089, 0.6437787069368217, 0.29002406046960794, 0.1962328108639636, 0.6223449355280233, 0.8655527375172225, 0.8418571774026381, 0.1594489906638913, 0.3536872707759614, 0.646449559060611, 0.9086902418848364, 0.03201146753834683, 0.25181954262322415, 0.1716616533584565, 0.4214236664122878, 0.022766014653430533, 0.46227103617977827, 0.9006816364578588, 0.7876882407607669, 0.8407951868811733, 0.6974255535953564, 0.6926770093895163, 0.4529329721912543, 0.9317917999997377, 0.6089594750128221, 0.2915344944972309, 0.9812242414563862, 0.7455649624685776, 0.12892023450868295, 0.23737713914982117, 0.6856944061427289, 0.8187668478155605, 0.3743242023594, 0.5574898635498644, 0.38252247269123985, 0.07118643845711592, 0.7072235334839838, 0.7819718743473592, 0.5425256325426285, 0.7344636693812912, 0.19551174581242348, 0.9280678823608141, 0.9708850081977705, 0.7588356897081414, 0.02930815315640889, 0.10201175810139385, 0.15822483199577175, 0.3765347323767283, 0.41358124357060266, 0.27835002219065474, 0.6152091893149175, 0.7397145755117419, 0.07974242452643143, 0.6340949271885188, 0.5, 0.19629068746506073, 0.8372602420636244, 0.5386898693633999, 0.953493660673513, 0.8188370217422377, 0.5209172276054996, 0.9306523857560265, 0.010498981114076433, 0.25968984449352506, 0.2801504811194814, 0.3478933697158608, 0.11786458009341871, 0.8696672323746587, 0.4039882377855911, 0.32109324605804135, 0.27963412827559364, 0.288320452445765, 0.7784145600356189, 0.18814932260499595, 0.32465294168887426, 0.39039126715058303, 0.6325648103894933, 0.6425909641521894, 0.8702952526169094, 0.5561569535760208, 0.44597476907103706, 0.4899918546282458, 0.5, 0.7226030980814677, 0.7611766407699825, 0.1537921833651542, 0.7119196650198935, 0.5678711772643094, 0.17425540440410803, 0.4455756085593372, 0.15390361315048862, 0.4831865890284329, 0.9605495049641449, 0.9947289079910909, 0.09478806526599204, 0.6944802326185883, 0.6334998287215076, 0.51854545961094, 0.192091615405253, 0.010174369485599353, 0.2317214641401728, 0.8062157168887932, 0.36346501076412796, 0.19211190890087626, 0.2826933645150179, 0.88878148707608, 0.15216217316271052, 0.8733651585646497, 0.2010303833574589, 0.37201298531074495, 0.2819310925494395, 0.2796690522306764, 0.3451236324288921, 0.32030081229401264, 0.6666135438035696, 0.75048048795597, 0.07800455765926029, 0.3970376532665987, 0.9649386274046083, 0.2011741523257694, 0.9566090119526034, 0.1905974109720744, 0.7524399326125695, 0.21964243201810896, 0.6081872885681711, 0.7849023075885364, 0.5201213971809604, 0.44857528735875896, 0.9434271869789566, 0.66970107122496, 0.003442764811105059, 0.26895318201219975, 0.10292674951825831, 0.028064466178342018, 0.7953901622147023, 0.5707947724481435, 0.9369742672578719, 0.28723700024371424, 0.8038432202753373, 0.573957990676697, 0.8781625867911315, 0.5738666602402865, 0.8761451132148252, 0.24480174181868464, 0.7821864408152371, 0.37576699018556436, 0.832727346190573, 0.8437727190398449, 0.6842401549055418, 0.27974913592443307, 0.22683642972449813, 0.534489928193459, 0.44675490913745775, 0.12869374305443182, 0.37885487290289954, 0.78665716148794, 0.9587840192922987, 0.32091598725129744, 0.6214041914888664, 0.5131417874959773, 0.8881655461010421, 0.26100928751499064, 0.2353630854717641, 0.49581540333854834, 0.9846616624570019, 0.1637363107744727, 0.5, 0.5, 0.5855945465379118, 0.5111689607502381, 0.19389697304202802, 0.30620440928848147, 0.28506884019786116, 0.4681612609073804, 0.6964244840669924, 0.8135504342809223, 0.6936912595466129, 0.4723722086804154, 0.4076968669781561, 0.4231323577560101, 0.8377677550491014, 0.032438752832948725, 0.5311028290986199, 0.28682853841691036, 0.02527953949534656, 0.4915311188596958, 0.14317485693429632, 0.8734275544086741, 0.9405092625492265, 0.1434064460532204, 0.9136449814166998, 0.8546463762530255, 0.3200087129694439, 0.5, 0.5, 0.5, 0.8096705084726379, 0.9456449715645273, 0.3500061491557155, 0.5044053311925174, 0.5059855318621088, 0.8838061935056822, 0.44580238504705794, 0.6102934191493135, 0.24413907106284904, 0.6927718494507219, 0.39848849289422805, 0.9467455580072247, 0.9114183524220845, 0.2732853410681141, 0.45529053458302005, 0.9002589038491816, 0.9364748946470854, 0.9941581567376476, 0.1692117623170717, 0.7049901577766928, 0.6333048285006789, 0.33769944340623015, 0.9062083485756166, 0.003214850976448469, 0.2633814984524413, 0.5, 0.5, 0.5, 0.5201941576461326, 0.22582183327387217, 0.3687675208218302, 0.6657911236120558, 0.6375172627156448, 0.08046640333584065, 0.08420931118036556, 0.677200870929757, 0.4760553843545977, 0.499148460804575, 0.7878243173903932, 0.28088865304769384, 0.7526784770722801, 0.03488448976360403, 0.5520465876719169, 0.3886228050419427, 0.04228303009371126, 0.17655758520301612, 0.500752223806079, 0.5187190902111543, 0.30438515448319303, 0.6564439676664504, 0.8048791427558912, 0.06063530571780795, 0.4648708764051991, 0.5, 0.5, 0.5, 0.5, 0.5, 0.3420979828718652, 0.7182572799116776, 0.7037856996937137, 0.17985245180175713, 0.8102412522689886, 0.6187431195269641, 0.0657182679326207, 0.918084113460303, 0.6180470635710811, 0.5547641819271106, 0.09157194047053485, 0.04716196029010189, 0.6985214161619918, 0.14544865952940234, 0.2065404261721967, 0.41244168631909295, 0.1925559453034088, 0.101393486322917, 0.5982184574588258, 0.45281781338815563, 0.48880888836892505, 0.4447671119255686, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.8251295829562302, 0.5236822241106733, 0.5348514499519869, 0.7496640866087585, 0.3359288325070616, 0.7745565409477678, 0.010478803879848897, 0.6682790260884153, 0.7410961252782639, 0.8908751745720281, 0.37053448430421587, 0.24720667684847908, 0.6124282065711737, 0.33354544022595733, 0.8889744839168485, 0.30225379992630075, 0.09254728374784338, 0.8737592262422127, 0.10558761810423789, 0.5, 0.5, 0.5, 0.5),
      randomEncodedDatapoint
    )

  val randomDecodedDatapointValues: List[Double] =
    preprocessor.decode(randomEncodedDatapoint)
      .toList
      .sortBy(_._1)
      .map(_._2.toDouble)

  @Test def `values of random decoded datapoint`(): Unit =
    assertEquals(
      List(7.0, 0.0, 0.0, 0.0, 151.11845582265792, 89.99252616374983, 17.259401785031038, 92.25708262111708, 177.49089249074157, 55.64119555408912, 58.728185862794014, 83.60514815626114, 147.13962356537237, 149.00400168721325, 0.0, 90.44966983875409, 0.0, 0.0, 6.787895562287402, 46.912329403412066, 69.11595897824557, 87.91859458917202, 203.8116742571473, 38.113598913890584, 91.55234072802187, 82.73404055533311, 60.46038266148852, 218.36249419157838, 164.17347012929034, 135.42306363591254, 147.53194568871373, 27.503876798485646, 182.33862468873454, 219.0484111647622, 193.85492253818512, 83.95633985092074, 59.71325275749389, 12.332319211268361, 183.7937332793151, 213.77099435322256, 152.08525370469366, 246.27642771978108, 35.42315566805694, 22.71802024224503, 224.55343998882734, 4.970508007741562, 0.0, 211.096761957676, 0.0, 0.0, 21.96038270533235, 187.52119292853132, 168.54970921597865, 159.45057603416936, 218.56689928746596, 231.65470999419927, 126.63962780697435, 151.30361428866925, 2.670442560610784, 243.6671612639177, 29.00577280809643, 94.0382312727754, 39.99672897668634, 208.72761961280855, 172.54491005710673, 116.7548484228839, 62.32436440981613, 10.489387357295396, 106.07498104944497, 0.0, 186.74540501270116, 34.95614611319496, 47.79215644600269, 141.6568426282882, 117.86448766911406, 250.78391859173158, 236.2193609818743, 4.550246089906467, 0.0, 3.0315987706178307, 0.0, 22.366718930380483, 22.80848900361807, 245.12662457112853, 40.985203394181504, 104.1512301854567, 7.2298897950153656, 109.95266622756134, 114.44212922871651, 239.54418762098587, 104.60024748973106, 0.0, 63.29496111647897, 20.37135647177711, 67.0846214270693, 136.62634940130206, 85.60352660891338, 90.92689330690358, 143.20983373399324, 66.37903042825742, 201.2051452400721, 245.53682003983758, 0.0, 95.52825359411729, 207.00395265812688, 139.1485786489947, 102.4070464869975, 91.50893548621501, 186.83168942660433, 0.0, 110.11559475309728, 210.19784038509002, 124.57381192037495, 0.0, 0.0, 110.50965867417688, 245.1061931806932, 147.68798396594403, 160.68280658514527, 158.32089880124178, 46.658152736074776, 17.858397548085488, 33.50062724577102, 175.4478858059739, 122.71784006009018, 0.0, 2.1486359126058794, 111.36811421239099, 15.955541899329733, 199.20953252529947, 77.85386118491188, 76.6020892239143, 99.51790528673295, 144.43933133996413, 181.49630691702023, 119.01284913983609, 0.0, 102.1562703212457, 190.34891110047008, 146.69867344080512, 63.53800788037927, 7.983453067812539, 89.3043049017891, 227.7483149961966, 153.40913671892284, 224.87570244683417, 92.32884309209535, 0.0, 62.05997064115661, 6.134397572161069, 84.66730089701608, 49.76291284177286, 59.935323206279136, 153.1447179933052, 26.8904087965414, 223.99996247901692, 212.55197003070552, 223.70344907363602, 0.0, 159.48843029624348, 6.208971179396388, 73.99203793014799, 14.431170662104613, 68.04631414831607, 9.808232096319982, 110.34262655099029, 91.0392167203683, 127.12952217177238, 212.90330320759804, 0.0, 192.70686914706596, 69.84945471661803, 46.61754291649211, 134.4150080403186, 28.9430782611376, 111.68259678741418, 73.10263754311794, 49.492484918749504, 186.95919604793792, 85.67054453346992, 0.0, 251.30223647310177, 200.21850889339095, 21.472872203095566, 128.8681877406158, 241.17113103174322, 196.07219809072365, 61.979837183410055, 66.07464831436863, 165.45670837551626, 208.98790097902722, 0.0, 133.44392546440642, 39.78113429246237, 187.8656887018245, 48.49442416587627, 173.60673851722933, 159.51114299263446, 181.2635253529707, 2.2059331341637805, 77.55087393941004, 138.4808922354578, 0.0, 81.53951415727738, 204.36769780652847, 240.14981921628817, 198.48048818097737, 221.09889127762372, 1.4225302840004173, 19.148017050002185, 218.72090921564183, 80.43430400616407, 23.94960956211636, 0.0, 91.71528739621355, 40.93815435726164, 79.4588268401725, 53.306788964489265, 96.29410680205088, 94.773951901235, 125.01375447975897, 223.8808506276184, 196.34673186298357, 1.1861382029692014, 0.0, 0.0, 248.88165732370354, 118.01701364810758, 204.0941445738689, 140.8615056578089, 69.50136667905235, 125.4598922336272, 71.8765145436782, 141.06836541686184, 94.68263691876479, 231.58066790296058, 0.0, 85.44495169546374, 246.3622873591993, 173.8966673084524, 152.9076629439035, 152.63636740508863, 92.8710296577816, 254.30117429242213, 48.49049014091896, 106.13924809472586, 196.4635050253141, 11.695145434889977, 77.42418883114334, 95.82135249884205, 241.00261970931592, 189.02150789391087, 65.0313350719327, 93.94460288797892, 87.48387280924075, 109.79940806093501, 10.154292676082484, 33.76655918853762, 39.716332274331386, 248.3482236161451, 27.5443880348168, 154.21566886899075, 84.96553599165954, 10.695995387998057, 26.185921302960743, 154.68347477735497, 174.70705568603043, 48.9153272281906, 123.33950456246035, 118.12816640352708, 195.28328399649178, 134.56167921789765, 96.46205595351233, 86.74987286098109, 176.4965523198316, 2.721331427579611, 194.71893193301716, 254.21181147431287, 143.32233580201765, 57.901828168469336, 82.09217764435175, 119.42686958431572, 91.35446151475472, 90.81509379563843, 254.2051959700345, 195.41868914705057, 219.27541088061287, 251.84753347787904, 197.6948120411107, 180.86516193670803, 174.26603674095253, 237.91407414886572, 171.62285764713323, 85.50956531408947, 57.42622618775569, 76.28169120342217, 12.72419771071683, 116.09259477311245, 120.61919294230398, 22.39061353589461, 230.88837565231663, 218.71276312094278, 90.98582853903483, 135.45141835418752, 80.63026388008103, 183.05657901293884, 240.61495134187007, 172.87100227439507, 76.45173592243695, 233.8913761714328, 220.97648350982035, 4.29902584597664, 92.08345230056517, 25.580519854190214, 208.78919081807345, 114.64786670897156, 176.70043216966835, 143.08489858318035, 50.7413788216113, 66.68464286949259, 220.76131908974008, 84.93097510347383, 114.1378751897102, 23.210176093382703, 239.81368852482933, 70.96161559564808, 98.84251878311468, 0.0, 96.07488984395285, 150.75348544954, 247.3816569960794, 92.69472677908934, 14.192289659420684, 138.43440568040532, 28.030928257885147, 0.0, 100.16189615034222, 232.76266627255458, 196.7382021864495, 188.6209565109458, 5.684782264804658, 145.41928361686465, 140.41350848848583, 14.303286809278163, 170.4121403579735, 50.804286898846634, 179.4072140807405, 128.20246432589852, 148.53733641736855, 206.74586976409498, 201.66895879005796, 175.89797144414624, 175.0740638731162, 22.830309575195706, 3.145936295697833, 200.61336517565667, 201.1051554951455, 9.235342152027517, 117.29422091143324, 0.0, 0.0, 192.8946072471936, 113.47638746818014, 37.89644076611062, 46.78128910023833, 138.8910048180067, 181.91640014988923, 180.9175237929195, 117.7318478880894, 3.698403003386916, 142.3788950718228, 10.531694036742868, 211.97527475180047, 115.48809408534277, 27.26700442915331, 77.31002412949366, 191.2103471250418, 74.28168678016682, 90.86598662534432, 126.04236361163257, 47.979906149270604, 246.5719159267425, 169.66063121006727, 172.75509043940556, 121.28184312674082, 240.8425068057764, 213.68994115878016, 111.08158074873899, 130.56936239589672, 0.0, 33.75695083969237, 244.39432665497637, 155.589850468869, 11.989562775419884, 208.89046345780156, 210.11435968501917, 204.91518935720612, 217.05511208076118, 249.55709123402687, 230.6283996731158, 239.96576212290262, 254.09647284937893, 77.39749380949334, 36.351363503358016, 82.09430612442175, 143.3038563313422, 65.3805108006736, 81.22939597459113, 8.51985103158773, 142.68638428727706, 168.54816282792464, 36.10447917930302, 39.8597596372802, 217.799502884549, 34.6994033768325, 64.27050266542443, 192.87386355106415, 167.39352904027888, 27.975262636116447, 137.72703691939407, 0.0, 46.32275743685351, 208.60033732206446, 164.16357026888954, 49.57215084259117, 73.95613541975003, 50.03936677031072, 158.69795855964594, 220.71594806689174, 214.67358023767272, 40.65949261929228, 90.19025404787016, 164.8446375604558, 231.71601168063327, 8.162924222278441, 35.739754087388526, 64.21398336892216, 43.77372160640641, 107.4630349351334, 5.805333736624786, 117.87911422584347, 229.673817296754, 200.86050139399558, 214.4027726546992, 177.84351616681587, 176.63263739432665, 0.0, 12.336622221954617, 115.49790790876985, 237.60690899993313, 154.066747178244, 35.27567383416494, 58.87345448738317, 93.94118527104078, 32.87465979971415, 60.5311704832044, 174.8520735663959, 208.78554619296793, 99.02510745885874, 95.452671601647, 142.1599152052154, 97.54323053626617, 18.15254180656456, 180.34200103841587, 199.40282795857658, 138.34403629837027, 187.28823569222925, 49.855495182167985, 236.6573100020076, 0.0, 247.57567709043147, 193.50310087557605, 7.473579054884267, 26.01299831585543, 40.347332158921795, 96.01635675606572, 105.46321711050368, 70.97925565861696, 156.87834327530396, 188.62721675549417, 0.0, 20.334318254240014, 95.74833400546633, 0.0, 50.05412530359049, 213.50136172622422, 137.36591668766698, 243.14088347174584, 208.80344054427061, 132.8338930394024, 237.31635836778676, 0.0, 2.6772401840894906, 66.22091034584889, 71.43837268546775, 88.71280927754451, 30.055467923821773, 221.76514425553796, 103.01700063532573, 81.87877774480054, 71.30670271027638, 73.52171537367008, 0.0, 198.49571280908282, 47.97807726427397, 82.78650013066294, 99.54977312339868, 161.3040266493208, 163.8606958588083, 221.92528941731192, 141.8200231618853, 112.83161657497237, 62.71895739241546, 0.0, 0.0, 128.62335145850125, 194.10004339634554, 39.21700675811432, 181.53951458007282, 144.80715020239887, 44.43512812304755, 113.62178018263099, 39.245421353374596, 123.2125802022504, 0.0, 244.94012376585695, 253.6558715377282, 24.17095664282797, 177.09245931774, 161.54245632398442, 132.2290922007897, 48.98336192833951, 2.5944642188278353, 59.088973355744066, 205.58500780664227, 3.688920668923565, 92.68357774485263, 48.988536769723446, 72.08680795132958, 226.6392792044004, 38.801354156491186, 222.70811543398568, 51.262747756152024, 18.600649265537246, 9.021794961582064, 29.924588588682376, 25.00969071627044, 88.00652626936748, 81.67670713497323, 169.98645366991025, 191.37252442877235, 19.891162203111374, 101.24460158298267, 246.05934998817511, 51.2994088430712, 243.93529804791388, 48.60233979787897, 0.0, 95.43225059157788, 191.87218281620522, 56.00882016461778, 155.08775858488363, 200.15008843507678, 132.6309562811449, 114.38669827648353, 240.5739326796339, 170.77377316236482, 0.8779050268317901, 68.58306141311094, 101.74487295147824, 26.24632112715587, 7.156438875477215, 202.82449136474906, 145.55266697427658, 235.18054108172586, 11.202243009504855, 24.919139828535457, 5.73957990676697, 223.93145963173853, 146.33599836127306, 98.18990824780745, 223.41700386978044, 62.42444416376458, 199.45754240788548, 95.82058249731891, 212.3454732785961, 215.16204335516045, 174.48123950091315, 71.33602966073043, 57.84328957974702, 136.29493168933203, 219.20213090398966, 113.92250183005173, 32.81690447888011, 96.60799259023938, 200.5975761794247, 244.48992491953618, 81.83357674908085, 158.45806882966093, 130.8511558114742, 226.48221425576574, 66.55736831632261, 60.091933676760156, 60.01758679529985, 125.44129704465273, 221.54887405282543, 11.789014375762035, 0.0, 0.0, 127.07401659872686, 130.3480849913107, 49.44372812571714, 78.08212436856277, 12.12024897677637, 72.69255425045459, 119.38112153138201, 177.58824343708307, 207.4553607416352, 176.89127118438628, 120.45491321350593, 103.9627010794298, 107.89875122778258, 213.63077753752086, 8.271881972401925, 243.46595430267158, 135.4312214201481, 73.14127729631214, 6.446282571313373, 125.34043530922243, 36.509588518245565, 222.72402637421192, 239.82986195005276, 36.5686437435712, 232.97947026125846, 205.96977667697917, 203.81754152153223, 48.00130694541658, 0.0, 0.0, 0.0, 204.8466386435774, 239.2481778058254, 89.25156803470745, 128.62335945409194, 129.02631062483775, 225.37057934394898, 32.26946135870349, 113.67960818699977, 155.62482188307493, 62.25546312102651, 176.65682160993407, 101.61456568802815, 241.4201172918423, 232.41167986763153, 69.68776197236909, 116.09908631867012, 229.5660204815413, 146.29565952000712, 238.80109813500678, 253.51032996810014, 43.148999390853284, 179.77249023305666, 161.49273126767312, 86.11335806858868, 230.17692053820662, 0.774779085324081, 25.811386848339247, 0.0, 0.0, 205.4002110219565, 0.0, 0.0, 21.84815462113757, 57.35874565156353, 94.03571780956669, 169.77673652107424, 162.56690199248942, 20.518932850639366, 21.47337435099322, 172.68622208708803, 179.51261448044642, 121.39412301042242, 127.28285750516662, 200.89520093455025, 71.62660652716193, 191.9330116534314, 8.895544889719028, 140.7718798563388, 99.09881528569538, 10.78217267389637, 45.02218422676911, 148.4646787040539, 127.69181707055014, 132.27336800384433, 77.61821439321422, 167.39321175494484, 157.7563119801547, 7.70068382616161, 48.346571146140704, 0.0, 0.0, 0.0, 112.23538490729665, 0.0, 0.0, 87.23498563232563, 183.1556063774778, 179.465353421897, 45.86237520944807, 206.6115193285921, 157.77949547937587, 16.75815832281828, 234.11144893237727, 80.23457776996696, 157.60200121062567, 141.4648663914132, 23.35084481998639, 12.026299873975981, 178.12296112130792, 37.0894081799976, 52.66780867391016, 105.1726300113687, 49.10176605236924, 25.855339012343837, 53.51382789210979, 152.5457066520006, 114.56290678720337, 13.6866488743299, 26.241259603608547, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 159.27115060711236, 0.0, 146.04793618325274, 120.97059376956554, 135.31741683785268, 190.41467799862465, 85.32592345679365, 197.5119179416808, 2.6720949893614687, 170.4111516525459, 188.97951194595728, 202.0992652981519, 227.1731695158672, 94.48629349757505, 63.037702596362166, 156.1691926756493, 84.72054181739317, 225.7995189148795, 76.47021138135409, 23.414462788204375, 221.93484346552202, 6.546432322462749, 220.27464326663596, 0.0, 0.0, 0.0, 0.0, 116.74957740758444, 0.0, 77.39805628702925, 108.14158456316635, 0.0, 0.0, 0.0, 0.0, 110.86932825036354, 35.31176251327267, 87.76916572743198, 223.97410212393885, 0.0, 0.27215908964964064, 234.49403621894953, 70.91396804013954, 117.65311431636934, 100.03638050899852, 78.40584826960402, 225.68720803129497, 4.727458923490042, 147.06082939356742, 214.78030241280186),
      randomDecodedDatapointValues
    )

  preprocessorJsonSource.close()

  csvReader.close()