name := """play-demo"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    jdbc,
    cache,
    ws,
    "com.typesafe.play"       %% "play-slick"           % "2.1.0",
    "com.h2database"          %  "h2"                   % "1.4.194",
    "com.wix"                 %% "accord-core"          % "0.6.1",
    "org.slf4j"               %  "slf4j-nop"            % "1.7.24",
    "org.scalatest"           %% "scalatest"            % "3.0.1"          % Test,
    "org.scalatestplus.play"  %% "scalatestplus-play"   % "2.0.0"          % Test
  )
}

javaOptions in Test += "-Dconfig.resource=application.test.conf"
