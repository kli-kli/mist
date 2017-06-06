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
    val amazonS3URI = new AmazonS3URI(urlStr)
    val regionStr  = amazonS3URI.getRegion
    val bucket = amazonS3URI.getBucket
    val key = amazonS3URI.getKey

    var region = Regions.DEFAULT_REGION

    //TODO Dunno
    if (regionStr != null) {
      region = Regions.fromName(regionStr)
    }

    val s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .withForceGlobalBucketAccessEnabled(true)
                    .build()

    val s3Object = s3Client.getObject(new GetObjectRequest(bucket, key))
    println("Content-Type: " + s3Object.getObjectMetadata.getContentType)
  }

}
