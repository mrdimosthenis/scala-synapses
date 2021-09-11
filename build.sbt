val scala3Version = "3.0.1"

val circeVersion = "0.14.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "synapses",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion
    )
  )

enablePlugins(ScalaJSPlugin)
scalaJSUseMainModuleInitializer := false
