package io.hydrosphere.mist.jobs.resolvers

import com.amazonaws.SDKGlobalConfiguration
import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.s3.{AmazonS3Client, AmazonS3ClientBuilder}
import com.amazonaws.services.s3.model.GetObjectRequest

/**
  * Created by mironova on 6/1/2017.
  */
object SomeObjectTest {

  val region = "region"
  val bucketName = "mattress01"
  val key = "004.html"

  def main(args: Array[String]): Unit = {
    val s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(Regions.US_EAST_1)
                    .withForceGlobalBucketAccessEnabled(true)
                    .build()

    val s3Object = s3Client.getObject(new GetObjectRequest(bucketName, key))
    println("Content-Type: " + s3Object.getObjectMetadata.getContentType)
  }

}
