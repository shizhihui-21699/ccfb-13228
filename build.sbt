ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "seal-forensics",
    version := "1.0.0",
    libraryDependencies ++= Seq(
      guice,
      // OpenCV（openpnp 打包版，自带各平台原生库，含 macOS arm64）
      "org.openpnp" % "opencv" % "4.9.0-0"
    )
  )
