enablePlugins(DockerPlugin)

name := "scripted-check-label"

organization := "sbtdocker"

version := "0.1.0"

val labelValue = "b=c 'd|!@#$%^&*(\")e"

dockerfile in docker := {
  new Dockerfile {
    from("busybox")
    label("com.example.key" -> labelValue, "b" -> "value")
  }
}

val check = taskKey[Unit]("Check")
check := {
  val imagesWithLabel = Process("docker", Seq("images", "--filter", s"""label=com.example.key="${labelValue.replace("\"", "\\\"")}""""))
  val out = imagesWithLabel.!!
  val firstImageName = (imageNames in docker).value.head
  if (out.toString.contains(firstImageName.toString)) {
    sys.error("Unexpected output: " + out)
  }
}
