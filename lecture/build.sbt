name := "lecture"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  val akkaV = "2.3.11"
  Seq(
    "com.typesafe.akka"            %% "akka-actor"        % akkaV     withSources(),
    "com.typesafe.akka"            %% "akka-cluster"      % akkaV     withSources(),

    "com.ning"                     %  "async-http-client" % "1.9.22"  withSources(),
    "org.jsoup"                    %  "jsoup"             % "1.8.2"   withSources(),
    "ch.qos.logback"               %  "logback-classic"   % "1.1.3"   withSources(),
    //
    "com.typesafe.akka"            %% "akka-testkit"      % akkaV     % "test" withSources(),
    "org.specs2"                   %% "specs2"            % "2.3.12"  % "test" withSources(), // do not update, sensitive!
    "org.scalatest"                %% "scalatest"         % "2.2.3"   % "test" withSources()
  )
}

retrieveManaged := true