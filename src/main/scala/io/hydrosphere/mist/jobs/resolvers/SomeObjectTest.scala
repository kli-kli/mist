package io.hydrosphere.mist.jobs.resolvers

import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.GetObjectRequest

/**
  * Created by mironova on 6/1/2017.
  */
object SomeObjectTest {

  def main(args: Array[String]): Unit = {
    val s3Client = AmazonS3ClientBuilder.defaultClient()

    Region.getRegion()
    s3Client.setRegion(siterepository)
    val s3Object = s3Client.getObject(new GetObjectRequest("407", "deceased_persons.pdf"))
    println("Content-Type: " + s3Object.getObjectMetadata.getContentType)
  }

}
