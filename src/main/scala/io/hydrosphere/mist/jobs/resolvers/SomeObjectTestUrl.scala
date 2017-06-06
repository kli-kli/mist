package io.hydrosphere.mist.jobs.resolvers

import java.nio.file.Paths

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3ClientBuilder, AmazonS3URI}
import com.amazonaws.services.s3.model.GetObjectRequest

/**
  * Created by mironova on 6/1/2017.
  */
object SomeObjectTestUrl {

  val urlStr = "https://s3.amazonaws.com/nutrition001/006.html"
  val targetDir: String = "/tmp"

  def main(args: Array[String]): Unit = {
    val resolver = new S3Resolver(urlStr)
    resolver.resolve()
  }

}
