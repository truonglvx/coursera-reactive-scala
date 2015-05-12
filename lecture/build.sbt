name := "lecture"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  val akkaV = "2.3.11"
  Seq(
    "com.typesafe.akka"            %% "akka-actor"   % akkaV   withSources(),
    "org.scalautils"               %% "scalautils"   % "2.1.7" withSources(),
    //
    "com.typesafe.akka"            %% "akka-testkit" % akkaV    % "test" withSources(),
    "org.specs2"                   %% "specs2"       % "2.3.12" % "test" withSources(), // do not update, sensitive!
    "org.scalatest"                %% "scalatest"    %  "2.2.3" % "test" withSources(),
    "junit"                        %  "junit"        % "4.12"   % "test" // Scala IDE requires this; IntelliJ IDEA does not
  )
}