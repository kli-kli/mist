package io.hydrosphere.mist.jobs.resolvers

import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.{AmazonS3ClientBuilder, AmazonS3URI}
import com.amazonaws.services.s3.model.GetObjectRequest

/**
  * Created by mironova on 6/1/2017.
  */
object SomeObjectTestUrl {

  val urlStr = "https://s3.amazonaws.com/nutrition001/006.html"

  def main(args: Array[String]): Unit = {
    val s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.DEFAULT_REGION)
                    .withForceGlobalBucketAccessEnabled(true)
                    .build()

    val amazonS3URI = new AmazonS3URI(urlStr)
    println(amazonS3URI.getRegion)
    println(amazonS3URI.getBucket)
    println(amazonS3URI.getKey)
    val s3Object = s3Client.getObject(new GetObjectRequest(amazonS3URI.getBucket, amazonS3URI.getKey))
    println("Content-Type: " + s3Object.getObjectMetadata.getContentType)
  }

}
