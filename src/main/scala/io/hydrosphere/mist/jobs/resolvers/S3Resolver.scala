package io.hydrosphere.mist.jobs.resolvers

import java.io.File
import java.net.URI
import java.nio.file.Paths

import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.s3.model.GetObjectRequest
import com.amazonaws.services.s3.{AmazonS3ClientBuilder, AmazonS3URI}


class S3Resolver(
    path: String,
    targetDir: String = "/tmp"
  ) extends JobResolver {

  private val amazonS3URI = new AmazonS3URI(path)

  private val s3Client = AmazonS3ClientBuilder.standard()
    .withRegion(Regions.DEFAULT_REGION)
    .withForceGlobalBucketAccessEnabled(true)
    .build()


  override def exists: Boolean = {
    s3Client.doesObjectExist(amazonS3URI.getBucket, amazonS3URI.getKey)
  }

  override def resolve(): File = {
    //TODO what to do if file exists already?
    if (!exists) {
      val regionStr  = amazonS3URI.getRegion
      val bucket = amazonS3URI.getBucket
      val key = amazonS3URI.getKey
      var regions = Regions.DEFAULT_REGION
      //TODO Dunno
      if (regionStr != null) {
        regions = Regions.fromName(regionStr)
        s3Client.setRegion(Region.getRegion(regions))
      }
      //TODO thinking about name of jars
      val localPath = Paths.get(targetDir, "%s_%s_%s.jar".format(regions, bucket, key))
      val s3Object = s3Client.getObject(new GetObjectRequest(bucket, key), localPath.toFile)
      new File(localPath.toString)
    }
  }
}
