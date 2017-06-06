package io.hydrosphere.mist.jobs.resolvers

/**
  * Created by mironova on 6/1/2017.
  */
object SomeObjectTestUrl {

  val urlStr = "https://nutrition001.s3.amazonaws.com/006.html"
  val targetDir: String = "/tmp"

  def main(args: Array[String]): Unit = {
    val resolver = new S3Resolver(urlStr)
    resolver.resolve()
  }

}
